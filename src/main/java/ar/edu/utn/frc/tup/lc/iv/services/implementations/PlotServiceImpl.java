package ar.edu.utn.frc.tup.lc.iv.services.implementations;

import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetPlotDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetPlotStateDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetPlotTypeDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.post.PostPlotDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.put.PutPlotDto;
import ar.edu.utn.frc.tup.lc.iv.entities.*;
import ar.edu.utn.frc.tup.lc.iv.repositories.PlotRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.PlotStateRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.PlotTypeRepository;
import ar.edu.utn.frc.tup.lc.iv.restTemplate.FileManagerClient;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.PlotService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlotServiceImpl implements PlotService {

    private final PlotRepository plotRepository;

    private final PlotStateRepository plotStateRepository;

    private final PlotTypeRepository plotTypeRepository;

    private final FileManagerClient fileManagerClient;

    private final ModelMapper modelMapper;

    @Autowired
    public PlotServiceImpl(PlotRepository plotRepository, PlotStateRepository plotStateRepository, PlotTypeRepository plotTypeRepository, FileManagerClient fileManagerClient, ModelMapper modelMapper) {
        this.plotRepository = plotRepository;
        this.plotStateRepository = plotStateRepository;
        this.plotTypeRepository = plotTypeRepository;
        this.fileManagerClient = fileManagerClient;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public GetPlotDto createPlot(PostPlotDto postPlotDto) {
        //Validamos si el el plot a crear existe (mismo numero de plot)
        validatePlotNumber(postPlotDto.getPlot_number());
        //Creamos la entity
        PlotEntity plotEntity = new PlotEntity();
        //Mapeamos con metodo
        mapPlotPostToPlotEntity(plotEntity, postPlotDto);

        uploadFiles(postPlotDto.getFiles() , postPlotDto.getUserCreateId(),plotEntity);

        //Guardamos el plot
        PlotEntity savedPlot = plotRepository.save(plotEntity);
        //Mapeamos el entity al GetPlotDto para poder mostrarlo
        GetPlotDto getPlotDto = new GetPlotDto();
        mapPlotEntityToGetPlotDto(savedPlot , getPlotDto );

        //Retornamos el getPlotDto
        return getPlotDto;

    }

    public void uploadFiles(List<MultipartFile> files, Integer userId, PlotEntity plotEntity) {
        if(files != null && !files.isEmpty()){

            for (MultipartFile file : files) {

                String fileUuid = fileManagerClient.uploadFile(file);

                FileEntity fileEntity = new FileEntity();
                fileEntity.setFileUuid(fileUuid);
                //Todo: ver el nombre del archivo
                fileEntity.setName(file.getName());
                fileEntity.setCreatedDatetime(LocalDateTime.now());
                fileEntity.setCreatedUser(userId);
                fileEntity.setLastUpdatedDatetime(LocalDateTime.now());
                fileEntity.setLastUpdatedUser(userId);

                FilePlotEntity filePlotEntity = new FilePlotEntity();
                filePlotEntity.setFile(fileEntity);
                filePlotEntity.setPlot(plotEntity);
                filePlotEntity.setCreatedDatetime(LocalDateTime.now());
                filePlotEntity.setCreatedUser(userId);
                filePlotEntity.setLastUpdatedDatetime(LocalDateTime.now());
                filePlotEntity.setLastUpdatedUser(userId);

                plotEntity.getFiles().add(filePlotEntity);
            }
        }
    }

    @Override
    public List<GetPlotStateDto> getPlotStates() {
        List<PlotStateEntity> plotStateEntities = plotStateRepository.findAll();
        List<GetPlotStateDto> plotStateDtos = new ArrayList<>();
        for (PlotStateEntity plotStateEntity : plotStateEntities) {
            plotStateDtos.add(modelMapper.map(plotStateEntity, GetPlotStateDto.class));
        }
        return plotStateDtos;
    }

    @Override
    public List<GetPlotTypeDto> getPlotTypes() {
        List<PlotTypeEntity> plotTypeEntities = plotTypeRepository.findAll();
        List<GetPlotTypeDto> plotTypeDtos = new ArrayList<>();
        for (PlotTypeEntity plotTypeEntity : plotTypeEntities) {
            plotTypeDtos.add(modelMapper.map(plotTypeEntity, GetPlotTypeDto.class));
        }
        return plotTypeDtos;
    }

    @Override
    @Transactional
    //todo SALE ESE ERROR RARO
    //todo El error que estás viendo en tu operación PUT está relacionado con una limitación en MySQL que evita la
    // modificación de una tabla que ya está siendo utilizada en un trigger
    public GetPlotDto putPlot(PutPlotDto plotDto, Integer plotId) {
        PlotEntity plotEntity = this.plotRepository.findById(plotId).get();

        if(plotEntity == null){
            throw new RuntimeException();
        }

        PlotStateEntity plotStateEntity = this.plotStateRepository.findById(plotDto.getPlot_state_id()).get();
        if(plotStateEntity == null){
            throw new RuntimeException();
        }

        PlotTypeEntity plotTypeEntity = this.plotTypeRepository.findById(plotDto.getPlot_type_id()).get();
        if(plotTypeEntity == null){
            throw new RuntimeException();
        }

        plotEntity.setTotalAreaInM2(plotDto.getTotal_area_in_m2());
        plotEntity.setBuiltAreaInM2(plotDto.getBuilt_area_in_m2());
        plotEntity.setPlotState(plotStateEntity);
        plotEntity.setPlotType(plotTypeEntity);
        plotEntity.setLastUpdatedDatetime(LocalDateTime.now());
        plotEntity.setLastUpdatedUser(plotDto.getUserUpdateId());
        plotEntity = this.plotRepository.save(plotEntity);
        GetPlotDto getPlotDto = new GetPlotDto();
        this.mapPlotEntityToGetPlotDto(plotEntity , getPlotDto);
        return getPlotDto;
    }

    @Override
    public List<GetPlotDto> getAllPlots() {
        List<PlotEntity> plotEntities = plotRepository.findAll();
        List<GetPlotDto> plotDtos = new ArrayList<>();
        for (PlotEntity plotEntity : plotEntities) {
            GetPlotDto getPlotDto = new GetPlotDto();
            mapPlotEntityToGetPlotDto(plotEntity, getPlotDto );
            plotDtos.add(getPlotDto);
        }
        return plotDtos;
    }

    @Override
    public List<GetPlotDto> getAllPlotsAvailables() {
        List<PlotEntity> plotEntities = plotRepository.findPlotsAvailables();
        List<GetPlotDto> plotDtos = new ArrayList<>();
        for (PlotEntity plotEntity : plotEntities) { GetPlotDto getPlotDto = new GetPlotDto();
            mapPlotEntityToGetPlotDto(plotEntity, getPlotDto );
            plotDtos.add(getPlotDto);
        }
        return plotDtos;
    }

    public GetPlotDto getPlotById(Integer plotId) {
        PlotEntity plotEntity = plotRepository.findById(plotId)
                .orElseThrow(() -> new EntityNotFoundException("Plot not found with id: " + plotId));
        GetPlotDto getPlotDto = new GetPlotDto();
        mapPlotEntityToGetPlotDto(plotEntity, getPlotDto);
        return getPlotDto;
    }
    
    //Metodo para validar si existe un plot con ese numero
    public void validatePlotNumber(Integer plotNumber) {
        if (plotRepository.findByPlotNumber(plotNumber) != null) {
            throw new IllegalArgumentException("Error creating plot: Plot already exist.");
        }
    }

    public void mapPlotPostToPlotEntity(PlotEntity plotEntity, PostPlotDto postPlotDto) {
        //Seteamos campos basicos
        plotEntity.setPlotNumber(postPlotDto.getPlot_number());
        plotEntity.setBlockNumber(postPlotDto.getBlock_number());
        plotEntity.setBuiltAreaInM2(postPlotDto.getBuilt_area_in_m2());
        plotEntity.setTotalAreaInM2(postPlotDto.getTotal_area_in_m2());
        plotEntity.setCreatedDatetime(LocalDateTime.now());
        plotEntity.setLastUpdatedDatetime(LocalDateTime.now());
        plotEntity.setCreatedUser(postPlotDto.getUserCreateId());
        plotEntity.setLastUpdatedUser(plotEntity.getLastUpdatedUser());

        //Mapeamos los estados y tipos
        PlotStateEntity state = plotStateRepository.findById(postPlotDto.getPlot_state_id())
                .orElseThrow(() -> new EntityNotFoundException("PlotState not found with id: " + postPlotDto.getPlot_state_id()));
        PlotTypeEntity type = plotTypeRepository.findById(postPlotDto.getPlot_type_id())
                .orElseThrow(() -> new EntityNotFoundException("PlotType not found with id: " + postPlotDto.getPlot_type_id()));

        //Seteamos estados y tipos
        plotEntity.setPlotType(type);
        plotEntity.setPlotState(state);

        //Guardamos en el repositorio
        plotRepository.save(plotEntity);
    }

    @Override
    public void mapPlotEntityToGetPlotDto(PlotEntity plotEntity, GetPlotDto getPlotDto) {
        getPlotDto.setPlot_number(plotEntity.getPlotNumber());
        getPlotDto.setBlock_number(plotEntity.getBlockNumber());
        getPlotDto.setBuilt_area_in_m2(plotEntity.getBuiltAreaInM2());
        getPlotDto.setTotal_area_in_m2(plotEntity.getTotalAreaInM2());
        getPlotDto.setId(plotEntity.getId());
        getPlotDto.setPlot_state(plotEntity.getPlotState().getName());
        getPlotDto.setPlot_type(plotEntity.getPlotType().getName());
    }

}
