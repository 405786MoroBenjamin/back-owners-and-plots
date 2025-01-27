package ar.edu.utn.frc.tup.lc.iv.services.implementations;

import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetDniTypeDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetOwnerAndPlot;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetOwnerDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetOwnerTypeDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetOwnerWithHisPlots;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetPlotDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetTaxStatusDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.OwnerDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.post.PostOwnerDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.put.PutOwnerDto;
import ar.edu.utn.frc.tup.lc.iv.entities.DniTypeEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.FileEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.FileOwnerEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.OwnerEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.OwnerTypeEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.PlotEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.PlotOwnerEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.TaxStatusEntity;
import ar.edu.utn.frc.tup.lc.iv.repositories.DniTypeRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.OwnerRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.OwnerTypeRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.PlotOwnerRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.PlotRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.PlotStateRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.TaxStatusRepository;
import ar.edu.utn.frc.tup.lc.iv.restTemplate.FileManagerClient;
import ar.edu.utn.frc.tup.lc.iv.restTemplate.RestUser;
import ar.edu.utn.frc.tup.lc.iv.restTemplate.users.GetUserDto;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.FileService;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.OwnerService;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.PlotService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de la interfaz OwnerService,
 * contiene toda la lógica de propietarios.
 */
@Service
@Data
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    /**
     * Repositorio para manejar Owner entities.
     */
    private final OwnerRepository ownerRepository;

    /**
     * Repositorio para manejar TaxStatus entities.
     */
    private final TaxStatusRepository taxStatusRepository;

    /**
     * Repositorio para manejar DniTypes entities.
     */
    private final DniTypeRepository dniTypeRepository;

    /**
     * Repositorio para manejar OwnerType entities.
     */
    private final OwnerTypeRepository ownerTypeRepository;

    /**
     * Repositorio para manejar PlotOwner entities.
     */
    private final PlotOwnerRepository plotOwnerRepository;

    /**
     * Repositorio para manejar PlotState entities.
     */
    private final PlotStateRepository plotStateRepository;

    /**
     * Mapper para mapear entidades a DTOs.
     */
    private final ModelMapper modelMapper;

    /**
     * Servicio para manejar la comunicación con el api de usuarios.
     */
    private final RestUser restUser;

    /**
     * Repositorio para manejar Plot entities.
     */
    private final PlotRepository plotRepository;

    /**
     * Servicio para manejar la lógica de lotes.
     */
    private final PlotService plotService;

    /**
     * Servicio para manejar la lógica de archivos.
     */
    private final FileService fileService;

    /**
     * Servicio para manejar la comunicación con el api de archivos.
     */
    private final FileManagerClient fileManagerClient;

    /**
     * Crea un propietario y el usuario del propietario.
     *
     * @param postOwnerDto el DTO con la información del propietario y su usuario.
     * @throws ResponseStatusException si ocurre un error al crear el usuario.
     * @return el DTO con la información del propietario creado.
     */
    @Override
    @Transactional
    public GetOwnerDto createOwner(PostOwnerDto postOwnerDto) {
        if (postOwnerDto.getDni_type_id() == null) {
            postOwnerDto.setDni_type_id(1);
        }
        OwnerEntity ownerEntity = createOwnerEntity(postOwnerDto);
        uploadFiles(postOwnerDto.getFiles(), postOwnerDto.getUserCreateId(), ownerEntity);
        OwnerEntity ownerSaved = ownerRepository.save(ownerEntity);
        assignPlots(ownerSaved, postOwnerDto);

        //Aca se crea el usuario
        if (!restUser.createUser(postOwnerDto)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error while creating the user");
        }

        return createGetOwnerDto(ownerSaved);
    }

    /**
     * Crea una entidad Owner a partir de un DTO.
     *
     * @param postOwnerDto el DTO con la información del propietario.
     * @return la entidad creada.
     */
    public OwnerEntity createOwnerEntity(PostOwnerDto postOwnerDto) {
        OwnerEntity ownerEntity = mapPostToOwnerEntity(postOwnerDto);
        ownerEntity.setOwnerType(findOwnerType(postOwnerDto.getOwnerTypeId()));
        ownerEntity.setTaxStatus(findTaxStatus(postOwnerDto.getTaxStatusId()));
        return ownerEntity;
    }

    /**
     * Obtiene un tipo de propietario por su id.
     *
     * @param ownerTypeId el id del tipo de propietario a buscar.
     * @throws EntityNotFoundException si no se encuentra el tipo de propietario.
     * @return el tipo de propietario.
     */
    public OwnerTypeEntity findOwnerType(Integer ownerTypeId) {
        return ownerTypeRepository.findById(ownerTypeId)
                .orElseThrow(() -> new EntityNotFoundException("OwnerType not found"));
    }

    /**
     * Obtiene una situación fiscal por su id.
     *
     * @param taxStatusId el id de la situación fiscal a buscar.
     * @throws EntityNotFoundException si no se encuentra la situación fiscal.
     * @return la situación fiscal.
     */
    public TaxStatusEntity findTaxStatus(Integer taxStatusId) {
        return taxStatusRepository.findById(taxStatusId)
                .orElseThrow(() -> new EntityNotFoundException("TaxStatus not found"));
    }

    /**
     * Asigna lotes a un propietario.
     *
     * @param owner el propietario al que se le asignarán los lotes.
     * @param postOwnerDto el DTO con la información del propietario.
     * @throws EntityNotFoundException si no se encuentra el lote.
     */
    public void assignPlots(OwnerEntity owner, PostOwnerDto postOwnerDto) {
        Integer[] plots = postOwnerDto.getPlotId();
        for (Integer plot : plots) {
            PlotOwnerEntity plotOwnerEntity = mapPlotOwnerEntity(owner, postOwnerDto, plot);
            validatePlot(plotOwnerEntity);
            plotOwnerRepository.save(plotOwnerEntity);
            plotService.changePlotState(plot, postOwnerDto.getUserCreateId());
        }
    }


    /**
     * Crea un DTO con la información de un propietario.
     *
     * @param owner el propietario a mapear.
     * @return el DTO con la información del propietario.
     */
    public GetOwnerDto createGetOwnerDto(OwnerEntity owner) {
        GetOwnerDto dto = mapOwnerEntitytoGet(owner);
        dto.setOwnerType(owner.getOwnerType().getDescription());
        dto.setTaxStatus(owner.getTaxStatus().getDescription());
        return dto;
    }

    /**
     * Valida si el lote no existe y si ya tiene un propieatrio asignado.
     *
     * @param plotOwner entidad de PlotOwner.
     * @throws EntityNotFoundException si no se encuentra un lote con esa id.
     * @throws IllegalArgumentException si ya existe un propietario activo que tenga ese lote.
     */
    public void validatePlot(PlotOwnerEntity plotOwner) {
        if (!plotRepository.existsById(plotOwner.getPlot().getId())) {
            throw new EntityNotFoundException("Plot not found with id: " + plotOwner.getPlot().getId());
        }

        if (plotOwnerRepository.findByPlotId(plotOwner.getPlot().getId()) != null
                && ownerRepository.existsByIdAndActive(plotOwner.getOwner().getId(), true)) {
            throw new IllegalArgumentException("Plot already has an active owner.");
        }
    }

    /**
     * Sube los archivos de un propietario.
     *
     * @param files los archivos a subir.
     * @param userId el id del usuario que sube los archivos.
     * @param ownerEntity la entidad del propietario.
     */
    public void uploadFiles(List<MultipartFile> files, Integer userId, OwnerEntity ownerEntity) {
        if (files != null && !files.isEmpty()) {
            files.forEach(file -> {
                FileEntity fileEntity = createFileEntity(file, userId);
                FileOwnerEntity fileOwnerEntity = createFileOwnerEntity(fileEntity, ownerEntity, userId);
                ownerEntity.getFiles().add(fileOwnerEntity);
            });
        }
    }

    /**
     * Crea un FileEntity que representa un archivo.
     *
     * @param file el archivo a subir.
     * @param userId el id del usuario que guarda el archivo.
     * @return la entidad creada.
     */
    public FileEntity createFileEntity(MultipartFile file, Integer userId) {
        String fileUuid = fileManagerClient.uploadFile(file).getUuid().toString();

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileUuid(fileUuid);
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setCreatedDatetime(LocalDateTime.now());
        fileEntity.setCreatedUser(userId);
        fileEntity.setLastUpdatedDatetime(LocalDateTime.now());
        fileEntity.setLastUpdatedUser(userId);

        return fileEntity;
    }

    /**
     * Crea una entidad FileOwner que representa la relación entre un archivo y un propietario.
     *
     * @param fileEntity la entidad del archivo.
     * @param ownerEntity la entidad del propietario.
     * @param userId el id del usuario que crea la relación.
     * @return la entidad creada.
     */
    public FileOwnerEntity createFileOwnerEntity(FileEntity fileEntity, OwnerEntity ownerEntity, Integer userId) {
        FileOwnerEntity fileOwnerEntity = new FileOwnerEntity();
        fileOwnerEntity.setFile(fileEntity);
        fileOwnerEntity.setOwner(ownerEntity);
        fileOwnerEntity.setCreatedUser(userId);
        fileOwnerEntity.setCreatedDatetime(LocalDateTime.now());
        fileOwnerEntity.setLastUpdatedUser(userId);
        fileOwnerEntity.setLastUpdatedDatetime(LocalDateTime.now());
        return fileOwnerEntity;
    }

    /**
     * Crea la relación entre un propietario y un lote.
     *
     * @param ownerEntity  la entidad del propietario.
     * @param postOwnerDto el DTO con la información del propietario.
     * @param plotId el id del lote a asignar.
     * @return la entidad creada.
     */
    public PlotOwnerEntity mapPlotOwnerEntity(OwnerEntity ownerEntity, PostOwnerDto postOwnerDto, Integer plotId) {
        PlotOwnerEntity plotOwnerEntity = new PlotOwnerEntity();
        plotOwnerEntity.setOwner(ownerEntity);
        PlotEntity plotEntity = plotRepository.findById(plotId).orElseThrow(() -> new EntityNotFoundException("Plot not found"));
        plotOwnerEntity.setPlot(plotEntity);
        plotOwnerEntity.setCreatedUser(postOwnerDto.getUserCreateId());
        plotOwnerEntity.setCreatedDatetime(LocalDateTime.now());
        plotOwnerEntity.setLastUpdatedUser(postOwnerDto.getUserCreateId());
        plotOwnerEntity.setLastUpdatedDatetime(LocalDateTime.now());
        return plotOwnerEntity;
    }

    /**
     * Actualiza un propietario.
     *
     * @param ownerId el id del propietario a actualizar.
     * @param putOwnerDto el DTO con la información del propietario a actualizar.
     * @return el DTO con la información del propietario actualizado.
     */
    @Transactional
    @Override
    public GetOwnerDto updateOwner(Integer ownerId, PutOwnerDto putOwnerDto) {
        OwnerEntity ownerEntity = findOwnerById(ownerId);
        updateOwnerFields(ownerEntity, putOwnerDto);

        ownerEntity.getFiles().clear();
        ownerRepository.save(ownerEntity);

        uploadFiles(putOwnerDto.getFiles(), putOwnerDto.getUserUpdateId(), ownerEntity);

        // Actualizar los plots asociados al propietario
        updatePlotsForOwner(ownerId, ownerEntity, putOwnerDto);

        restUser.updateUser(putOwnerDto);

        OwnerEntity ownerSaved = ownerRepository.save(ownerEntity);
        return createGetOwnerDto(ownerSaved);
    }

    /**
     * Actualiza los plots asociados a un propietario.
     *
     * @param ownerId el id del propietario.
     * @param ownerEntity la entidad del propietario.
     * @param putOwnerDto el DTO con la información de los nuevos plots.
     */
    private void updatePlotsForOwner(Integer ownerId, OwnerEntity ownerEntity, PutOwnerDto putOwnerDto) {
        Integer[] actualPlots = getActualPlots(ownerId);
        Integer[] newPlots = putOwnerDto.getPlotId();
        PostOwnerDto post = modelMapper.map(putOwnerDto, PostOwnerDto.class);

        addNewPlots(ownerEntity, post, newPlots, actualPlots, putOwnerDto.getUserUpdateId());
        removeOldPlots(ownerId, actualPlots, newPlots);
    }

    /**
     * Obtiene los plots actuales asociados a un propietario.
     *
     * @param ownerId el ID del propietario.
     * @return un arreglo de ids de plots actuales.
     */
    private Integer[] getActualPlots(Integer ownerId) {
        return plotOwnerRepository.findAllByOwner_Id(ownerId).stream()
                .map(plotOwner -> plotOwner.getPlot().getId())
                .toArray(Integer[]::new);
    }

    /**
     * Agrega los nuevos plots que no están en los actuales para
     * actualizar.
     *
     * @param ownerEntity la entidad del propietario.
     * @param post el DTO de publicación del propietario.
     * @param newPlots los nuevos plots.
     * @param actualPlots los plots actuales.
     * @param userUpdateId el id del usuario que actualiza.
     */
    private void addNewPlots(OwnerEntity ownerEntity, PostOwnerDto post, Integer[] newPlots, Integer[] actualPlots, Integer userUpdateId) {
        for (Integer plotId : newPlots) {
            if (!Arrays.asList(actualPlots).contains(plotId)) {
                PlotOwnerEntity plotOwnerEntity = mapPlotOwnerEntity(ownerEntity, post, plotId);
                plotOwnerEntity.setCreatedUser(userUpdateId);
                plotOwnerEntity.setLastUpdatedUser(userUpdateId);
                validatePlot(plotOwnerEntity);
                plotOwnerRepository.save(plotOwnerEntity);
                plotService.changePlotState(plotId, post.getUserCreateId());
            }
        }
    }

    /**
     * Elimina los plots actuales que no están en los nuevos para actualizar.
     *
     * @param ownerId el id del propietario.
     * @param newPlots los nuevos plots.
     * @param actualPlots los plots actuales.
     */
    private void removeOldPlots(Integer ownerId, Integer[] actualPlots, Integer[] newPlots) {
        for (Integer plotId : actualPlots) {
            if (!Arrays.asList(newPlots).contains(plotId)) {
                plotOwnerRepository.deleteByOwnerIdAndPlotId(ownerId, plotId);
                changePlotToAvaible(plotId);
            }
        }
    }

    /**
     * Obtiene un propietario por su id.
     *
     * @param ownerId el id del propietario a buscar.
     * @throws EntityNotFoundException si no se encuentra el propietario.
     * @return la entidad del propietario.
     */
    private OwnerEntity findOwnerById(Integer ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found"));
    }

    /**
     * Actualiza los campos de un propietario.
     *
     * @param owner el propietario a actualizar.
     * @param dto el DTO con la información del propietario a actualizar.
     */
    public void updateOwnerFields(OwnerEntity owner, PutOwnerDto dto) {
        owner.setName(dto.getName());
        owner.setLastname(dto.getLastname());
        owner.setDni(dto.getDni());
        owner.setDni_type_id(dniTypeRepository.findById(dto.getDniTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Dni type not found")));
        owner.setDateBirth(dto.getDateBirth());
        owner.setOwnerType(findOwnerType(dto.getOwnerTypeId()));
        owner.setTaxStatus(findTaxStatus(dto.getTaxStatusId()));
        owner.setBusinessName(dto.getBusinessName());
        owner.setLastUpdatedDatetime(LocalDateTime.now());
        owner.setLastUpdatedUser(dto.getUserUpdateId());
    }

    /**
     * Obtiene un propietario por su id.
     *
     * @param ownerId el id del propietario a buscar.
     * @throws EntityNotFoundException si no se encuentra el propietario.
     * @return el DTO con la información del propietario.
     */
    @Override
    public GetOwnerDto getById(Integer ownerId) {
        OwnerEntity ownerEntity = ownerRepository.findById(ownerId).orElseThrow(() ->
                new EntityNotFoundException("Owner not found"));

        GetOwnerDto getOwnerDto = mapOwnerEntitytoGet(ownerEntity);
        getOwnerDto.setFiles(fileService.getOwnerFiles(ownerId));
        return getOwnerDto;
    }

    /**
     * Mapea el DTO de entrada a una entidad Owner.
     *
     * @param postOwnerDto el DTO de entrada con la información del propietario.
     * @return la entidad Owner correspondiente.
     */
    public OwnerEntity mapPostToOwnerEntity(PostOwnerDto postOwnerDto) {
        OwnerEntity ownerEntity = new OwnerEntity();
        ownerEntity.setName(postOwnerDto.getName());
        ownerEntity.setLastname(postOwnerDto.getLastname());
        ownerEntity.setDni(postOwnerDto.getDni());
        ownerEntity.setDni_type_id(dniTypeRepository.findById(postOwnerDto.getDni_type_id())
                .orElseThrow(() -> new EntityNotFoundException("DniType not found with id: " + postOwnerDto.getDni_type_id())));
        ownerEntity.setDateBirth(postOwnerDto.getDateBirth());
        ownerEntity.setBusinessName(postOwnerDto.getBusinessName());
        ownerEntity.setActive(postOwnerDto.getActive());
        ownerEntity.setCreatedDatetime(LocalDateTime.now());
        ownerEntity.setCreatedUser(postOwnerDto.getUserCreateId());
        ownerEntity.setLastUpdatedDatetime(LocalDateTime.now());
        ownerEntity.setLastUpdatedUser(postOwnerDto.getUserCreateId());
        return ownerEntity;
    }

    /**
     * Mapea una entidad Owner a un DTO de salida.
     *
     * @param ownerEntity la entidad Owner a mapear.
     * @return  el DTO de salida con la información del propietario.
     */
    public GetOwnerDto mapOwnerEntitytoGet(OwnerEntity ownerEntity) {
        GetOwnerDto getOwnerDto = new GetOwnerDto();
        getOwnerDto.setId(ownerEntity.getId());
        getOwnerDto.setName(ownerEntity.getName());
        getOwnerDto.setLastname(ownerEntity.getLastname());
        getOwnerDto.setDni(ownerEntity.getDni());
        getOwnerDto.setDni_type(ownerEntity.getDni_type_id().getDescription());
        getOwnerDto.setDateBirth(ownerEntity.getDateBirth());
        getOwnerDto.setOwnerType(ownerEntity.getOwnerType().getDescription());
        getOwnerDto.setTaxStatus(ownerEntity.getTaxStatus().getDescription());
        getOwnerDto.setBusinessName(ownerEntity.getBusinessName());
        getOwnerDto.setActive(ownerEntity.getActive());
        getOwnerDto.setCreate_date(ownerEntity.getCreatedDatetime().toLocalDate());
        return getOwnerDto;
    }

    /**
     * Obtiene los tipos de situación fiscal.
     *
     * @return una lista de los tipos de situación fiscal.
     */
    @Override
    public List<GetTaxStatusDto> getTaxStatus() {
        List<TaxStatusEntity> taxStatusEntities = taxStatusRepository.findAll();
        List<GetTaxStatusDto> taxStatusDtos = new ArrayList<>();
        for (TaxStatusEntity taxStatusEntity : taxStatusEntities) {
            taxStatusDtos.add(modelMapper.map(taxStatusEntity, GetTaxStatusDto.class));
        }
        return taxStatusDtos;
    }

    /**
     * Obtiene todos los tipos de documentos(DNI , Pasaporte , Cuit/Cuil).
     *
     * @return una lista con los tipos de documentos.
     */
    @Override
    public List<GetDniTypeDto> getDniTypes() {
        List<DniTypeEntity> dniTypeEntities = dniTypeRepository.findAll();
        List<GetDniTypeDto> dniTypeDtos = new ArrayList<>();
        for (DniTypeEntity dniTypeEntity : dniTypeEntities) {
            dniTypeDtos.add(modelMapper.map(dniTypeEntity, GetDniTypeDto.class));
        }
        return dniTypeDtos;
    }

    /**
     * Obtiene todos los tipos de propietarios (personas física, jurídica, otros).
     *
     * @return una lista con los tipos de propietarios.
     */
    @Override
    public List<GetOwnerTypeDto> getOwnerTypes() {
        List<OwnerTypeEntity> ownerTypeEntities = ownerTypeRepository.findAll();
        List<GetOwnerTypeDto> ownerTypeDtos = new ArrayList<>();
        for (OwnerTypeEntity ownerTypeEntity : ownerTypeEntities) {
            ownerTypeDtos.add(modelMapper.map(ownerTypeEntity, GetOwnerTypeDto.class));
        }
        return ownerTypeDtos;
    }


    /**
     * Obtiene todos los propietarios.
     *
     * @return una lista con todos los propietarios.
     */
    @Override
    public List<GetOwnerDto> getAllOwners() {
        List<OwnerEntity> ownerEntities = ownerRepository.findAllActives();
        List<GetOwnerDto> ownerDtos = new ArrayList<>();

        for (OwnerEntity ownerEntity : ownerEntities) {
            GetOwnerDto getOwnerDto = mapOwnerEntitytoGet(ownerEntity);
            getOwnerDto.setFiles(fileService.getOwnerFiles(ownerEntity.getId()));
            ownerDtos.add(getOwnerDto);
        }
        return ownerDtos;
    }

    /**
     * Obtiene todos los propietarios activos, junto con sus lotes y su usuario.
     *
     * @return una lista con todos los propietarios activos, su lote y su usuario.
     */
    @Override
    public List<GetOwnerAndPlot> getOwersAndPlots() {
        return ownerRepository.findAllActives()
                .stream()
                .map(this::buildGetOwnerAndPlot)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los propietarios activos, junto con sus lotes (SIN SU USUARIO).
     *
     * @return una lista con todos los propietarios activos y sus lotes
     */
    @Override
    public List<GetOwnerWithHisPlots> getallOwnersWithTheirPlots() {
        return ownerRepository.findAllActives()
                .stream()
                .map(this::buildGetOwnerWithHisPlots)
                .collect(Collectors.toList());
    }


    /**
     * Obtiene la cantidad de propietarios por estado.
     *
     * @return un mapa con la cantidad de propietarios por estado.
     */
    @Override
    public Map<String, Long> getOwnerCountByStatus() {
        return ownerRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        owner -> owner.getActive() ? "Activos" : "Inactivos",
                        Collectors.counting()
                ));
    }



    private GetOwnerWithHisPlots buildGetOwnerWithHisPlots(OwnerEntity ownerEntity) {
        GetOwnerWithHisPlots getOwnerWithHisPlots = new GetOwnerWithHisPlots();

        // Obtener la lista de IDs de los plots
        List<Integer> plotIds = plotOwnerRepository.findAllByOwner_Id(ownerEntity.getId()).stream()
                .map(plotOwner -> plotOwner.getPlot().getId())
                .collect(Collectors.toList());

        OwnerDto ownerDto = mapOwnerEntityToOwnerDto(ownerEntity);

        getOwnerWithHisPlots.setOwner(ownerDto);
        getOwnerWithHisPlots.setPlot(plotIds);

        return getOwnerWithHisPlots;
    }

    /**
     * Obtiene un propietario por su id, junto con su lote y su usuario.
     *
     * @param ownerId el id del propietario a buscar.
     * @return el propietario, su lote y su usuario.
     */
    @Override
    public GetOwnerAndPlot getOwnerAndPlotById(Integer ownerId) {
        return ownerRepository.findById(ownerId)
                .map(this::buildGetOwnerAndPlot)
                .orElse(null);
    }


    /**
     * Obtiene los propietarios de un lote.
     *
     * @param plotId el id del lote a buscar.
     * @return una lista con los propietarios del lote.
     */
    @Override
    public List<OwnerDto> getOwnersByPlotId(Integer plotId) {
        Optional<List<OwnerEntity>> ownerEntitiesOptional = ownerRepository.findByPlotId(plotId);

        if (ownerEntitiesOptional.isEmpty()) {
            throw new EntityNotFoundException("Owners not found");
        }

        return ownerEntitiesOptional.get().stream()
                .map(this::mapOwnerEntityToOwnerDto)
                .collect(Collectors.toList());
    }

    /**
     * Baja lógica de un propietario y su usuario.
     *
     * @param ownerId el id del propietario a dar de baja.
     * @param userIdUpdate el id del usuario que da de baja al propietario.
     * @throws EntityNotFoundException si no se encuentra el propietario
     */
    @Override
    @Transactional
    public void deleteOwner(Integer ownerId, Integer userIdUpdate) {
        OwnerEntity ownerEntity = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found with id: " + ownerId));

        multiplePlotsChangeState(ownerId);
        logicDeleteOwner(ownerEntity, userIdUpdate);
        restUser.deleteUser(ownerEntity.getId(), userIdUpdate);
    }

    /**
     * Metodo que cambia el estado de un lote a disponible.
     *
     * @param plotId el id del lote a dar de baja
     * @throws EntityNotFoundException si no se encuentra el lote con esa id
     */
    public void changePlotToAvaible(Integer plotId) {
        PlotEntity plotEntity = plotRepository.findById(plotId).orElseThrow(() ->
                new EntityNotFoundException("Plot not found with id: " + plotId));
        plotEntity.setPlotState(plotStateRepository.findById(1).get());
        plotRepository.save(plotEntity);
    }

    /**
     * Meotodo que recorre todos los lotes de un owner y los cambia a disponible.
     *
     * @param ownerId el id del propietario.
     */
    public void multiplePlotsChangeState(Integer ownerId) {
        List<PlotOwnerEntity> plotOwnerEntity = plotOwnerRepository.findAllByOwner_Id(ownerId);
        for (PlotOwnerEntity plotOwnerEntities : plotOwnerEntity) {
            changePlotToAvaible(plotOwnerEntities.getId());
        }
    }

    /**
     * Meotodo que da de baja logica a un propietario.
     *
     * @param ownerEntity un entidad de propietario.
     * @param userIdUpdate la id del usuario que da baja logica.
     */
    public void logicDeleteOwner(OwnerEntity ownerEntity, Integer userIdUpdate) {
        ownerEntity.setActive(false);
        ownerEntity.setLastUpdatedDatetime(LocalDateTime.now());
        ownerEntity.setLastUpdatedUser(userIdUpdate);
        ownerRepository.save(ownerEntity);
    }

    /**
     * Mapea una entidad Owner a un DTO de salida.
     *
     * @param ownerEntity la entidad Owner a mapear.
     * @return  el DTO de salida con la información del propietario.
     */
    public OwnerDto mapOwnerEntityToOwnerDto(OwnerEntity ownerEntity) {
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setId(ownerEntity.getId());
        ownerDto.setName(ownerEntity.getName());
        ownerDto.setLastname(ownerEntity.getLastname());
        ownerDto.setDni(ownerEntity.getDni());
        ownerDto.setDni_type(ownerEntity.getDni_type_id().getDescription());
        ownerDto.setDateBirth(ownerEntity.getDateBirth());
        ownerDto.setOwnerType(ownerEntity.getOwnerType().getDescription());
        ownerDto.setBusinessName(ownerEntity.getBusinessName());
        ownerDto.setActive(ownerEntity.getActive());
        ownerDto.setTaxStatus(ownerEntity.getTaxStatus().getDescription());
        return ownerDto;
    }

    private List<GetPlotDto> mapPlotOwnersToGetPlotDtos(List<PlotOwnerEntity> plotOwnerEntities) {
        return plotOwnerEntities.stream()
                .map(plotOwner -> {
                    PlotEntity plotEntity = plotRepository.findById(plotOwner.getPlot().getId()).orElse(null);
                    if (plotEntity == null) {
                        throw new EntityNotFoundException("Plot not found for PlotOwner");
                    }
                    GetPlotDto getPlotDto = new GetPlotDto();
                    plotService.mapPlotEntityToGetPlotDto(plotEntity, getPlotDto);
                    return getPlotDto;
                })
                .collect(Collectors.toList());
    }

    private GetOwnerAndPlot buildGetOwnerAndPlot(OwnerEntity ownerEntity) {
        GetOwnerAndPlot getOwnerAndPlot = new GetOwnerAndPlot();
        List<GetPlotDto> getPlotDtos = mapPlotOwnersToGetPlotDtos(plotOwnerRepository.findAllByOwner_Id(ownerEntity.getId()));

        OwnerDto ownerDto = mapOwnerEntityToOwnerDto(ownerEntity);
        GetUserDto getUserDto;
        if (!getPlotDtos.isEmpty()) {
            //EMA Hice validacion
            getUserDto = restUser.getUser(getPlotDtos.get(0).getId());
            getOwnerAndPlot.setUser(getUserDto);
        }


        getOwnerAndPlot.setOwner(ownerDto);
        getOwnerAndPlot.setPlot(getPlotDtos);


        return getOwnerAndPlot;
    }
    /**
     * Obtiene los lotes de un propietario.
     *
     * @param userId id del propietario.
     * @return lista de lotes del propietario.
     */
    @Override
    public List<GetPlotDto> getPlotByUserId(Integer userId) {
        //TODO aca arreglar
        List<GetPlotDto> plotDtos = new ArrayList<>();
        try {
            List<GetOwnerAndPlot> ownersAndPlot = getOwersAndPlots();
            GetOwnerAndPlot getOwnerAndPlot = ownersAndPlot.stream().filter(m->m.getUser().getId().equals(userId)).findFirst().get();
            plotDtos = getOwnerAndPlot.getPlot();
        }catch (Exception e){
            System.out.println(e);
        }
        return plotDtos;
    }
}
