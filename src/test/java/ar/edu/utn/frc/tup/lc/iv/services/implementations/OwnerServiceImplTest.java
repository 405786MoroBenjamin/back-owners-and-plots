package ar.edu.utn.frc.tup.lc.iv.services.implementations;

import ar.edu.utn.frc.tup.lc.iv.dtos.get.*;
import ar.edu.utn.frc.tup.lc.iv.dtos.put.PutOwnerDto;
import ar.edu.utn.frc.tup.lc.iv.entities.*;
import ar.edu.utn.frc.tup.lc.iv.helpers.OwnerTestHelper;
import ar.edu.utn.frc.tup.lc.iv.repositories.*;
import ar.edu.utn.frc.tup.lc.iv.restTemplate.RestUser;
import ar.edu.utn.frc.tup.lc.iv.restTemplate.users.GetUserDto;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.FileService;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.OwnerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


@SpringBootTest
class OwnerServiceImplTest {

    @MockBean
    private RestUser restUserMock;

    @MockBean
    private TaxStatusRepository taxStatusRepositoryMock;

    @MockBean
    private FileService fileServiceMock;

    @MockBean
    private OwnerTypeRepository ownerTypeRepositoryMock;

    @MockBean
    private OwnerRepository ownerRepositoryMock;

    @MockBean
    private PlotOwnerRepository plotOwnerRepositoryMock;

    @MockBean
    private PlotRepository plotRepositoryMock;

    @SpyBean
    private OwnerService ownerServiceSpy;

    @MockBean
    private DniTypeRepository dniTypeRepositoryMock;

    @MockBean
    private PlotStateRepository plotStateRepositoryMock;


    @Test
    void updateOwner() {
        // Arrange
        Integer ownerId = 1;
        PutOwnerDto putOwnerDto = new PutOwnerDto();
        putOwnerDto.setName("Manu");
        putOwnerDto.setLastname("Ginóbili");
        putOwnerDto.setDni("45591511");
        putOwnerDto.setDniTypeId(1);
        putOwnerDto.setDateBirth(LocalDate.of(1977, 7, 28));
        putOwnerDto.setBusinessName("Ginóbili Enterprises");
        putOwnerDto.setOwnerTypeId(1);
        putOwnerDto.setTaxStatusId(1);
        putOwnerDto.setUserUpdateId(1);
        putOwnerDto.setFiles(new ArrayList<>());

        Integer[] plots = new Integer[2];
        plots[0] = 123;
        plots[1] = 124;
        putOwnerDto.setPlotId(plots);

        OwnerEntity ownerEntity = OwnerTestHelper.OWNER_ENTITY_1; // Utiliza un propietario de tu test helper
        PlotOwnerEntity plotOwnerEntity = OwnerTestHelper.PLOT_OWNER_ENTITY_1;
        PlotOwnerEntity plotOwnerEntity2 = OwnerTestHelper.PLOT_OWNER_ENTITY_2;

        List<PlotOwnerEntity> listPlotOwnerEntity = new ArrayList<>();
        listPlotOwnerEntity.add(plotOwnerEntity);
        listPlotOwnerEntity.add(plotOwnerEntity2);

        PlotEntity plotEntity = OwnerTestHelper.PLOT_ENTITY_1;
        PlotStateEntity plotStateEntity = OwnerTestHelper.PLOT_STATE_ENTITY;

        when(ownerRepositoryMock.findById(ownerId)).thenReturn(Optional.of(ownerEntity));
        when(dniTypeRepositoryMock.findById(1)).thenReturn(Optional.of(OwnerTestHelper.DNI_TYPE_ENTITY_DNI));
        when(ownerTypeRepositoryMock.findById(1)).thenReturn(Optional.ofNullable(OwnerTestHelper.OWNER_TYPE_FISICA));
        when(taxStatusRepositoryMock.findById(1)).thenReturn(Optional.ofNullable(OwnerTestHelper.TAX_STATUS_RESPONSABLE_INSCRIPTO));
        when(ownerRepositoryMock.save(any(OwnerEntity.class))).thenReturn(ownerEntity); // Simulamos la persistencia
        when(plotOwnerRepositoryMock.findByOwnerId(ownerId)).thenReturn(listPlotOwnerEntity);
        when(plotRepositoryMock.findById(123)).thenReturn(Optional.of(plotEntity));
        when(plotRepositoryMock.findById(124)).thenReturn(Optional.of(plotEntity));
        when(plotRepositoryMock.existsById(1)).thenReturn(true);
        when(plotStateRepositoryMock.findById(1)).thenReturn(Optional.of(plotStateEntity));
        when(plotStateRepositoryMock.findById(2)).thenReturn(Optional.of(plotStateEntity));


        // Act
        GetOwnerDto result = ownerServiceSpy.updateOwner(ownerId, putOwnerDto);

        // Assert
        assertNotNull(result);
        assertEquals("Manu", result.getName());
        assertEquals("Ginóbili", result.getLastname());
        assertEquals("45591511", result.getDni());
        assertEquals("DNI", result.getDni_type());
        assertEquals(LocalDate.of(1977, 7, 28), result.getDateBirth());
        assertEquals("Ginóbili Enterprises", result.getBusinessName());
        verify(ownerRepositoryMock, times(2)).save(any(OwnerEntity.class));
    }

    @Test
    void updateOwner_TaxStatusEntityNotFoundException() {
        // Given
        Integer ownerId = 1;
        PutOwnerDto putOwnerDto = new PutOwnerDto();
        putOwnerDto.setName("UpdatedName");
        putOwnerDto.setLastname("UpdatedLastname");
        putOwnerDto.setDni("12345678");
        putOwnerDto.setDniTypeId(1);
        putOwnerDto.setDateBirth(LocalDate.of(1990, 1, 1));
        putOwnerDto.setOwnerTypeId(1);
        putOwnerDto.setTaxStatusId(1); // Este es el ID que buscamos
        putOwnerDto.setBusinessName("UpdatedBusinessName");
        putOwnerDto.setUserUpdateId(1);
        putOwnerDto.setFiles(new ArrayList<>());

        OwnerEntity existingOwner = new OwnerEntity(); // Crea una instancia de OwnerEntity
        existingOwner.setId(ownerId);
        existingOwner.setName("OldName");
        existingOwner.setLastname("OldLastname");
        // Configura otros campos según sea necesario

        // Simulamos que el owner existe
        when(ownerRepositoryMock.findById(ownerId)).thenReturn(Optional.of(existingOwner));
        // Simulamos que el TaxStatus no se encuentra
        when(taxStatusRepositoryMock.findById(putOwnerDto.getTaxStatusId())).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ownerServiceSpy.updateOwner(ownerId, putOwnerDto);
        });
    }

    @Test
    void updateOwner_OwnerTypeEntityNotFoundException() {
        //Given
        OwnerTypeEntity ownerTypeEntity  = new OwnerTypeEntity();
        ownerTypeEntity.setId(2);
        TaxStatusEntity taxStatusEntity = new TaxStatusEntity();

        OwnerEntity ownerEntity = new OwnerEntity();
        ownerEntity.setId(12);
        ownerEntity.setName("Bruno");
        ownerEntity.setLastname("Diaz");
        ownerEntity.setOwnerType(ownerTypeEntity);
        ownerEntity.setTaxStatus(taxStatusEntity);

        //When
        Mockito.when(ownerTypeRepositoryMock.findById(12)).thenReturn(Optional.of(ownerTypeEntity));
        Mockito.when(ownerTypeRepositoryMock.findById(2)).thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class, () -> ownerServiceSpy.updateOwner(12, new PutOwnerDto()));
        Mockito.verify(ownerRepositoryMock, Mockito.times(1)).findById(12);
        Mockito.verify(taxStatusRepositoryMock, Mockito.times(0)).findById(1);
        Mockito.verify(ownerRepositoryMock, Mockito.times(0)).save(new OwnerEntity());
    }

    @Test
    void updateOwner_OwnerEntityNotFoundException() {
        //When
        Mockito.when(ownerRepositoryMock.findById(10)).thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class, () -> ownerServiceSpy.updateOwner(10, new PutOwnerDto()));
        Mockito.verify(ownerRepositoryMock, Mockito.times(0)).findById(1);
        Mockito.verify(taxStatusRepositoryMock, Mockito.times(0)).findById(1);
        Mockito.verify(ownerRepositoryMock, Mockito.times(0)).save(new OwnerEntity());
    }

    @Test
    void getById_Success() {
        // Given
        OwnerTypeEntity ownerTypeEntity = new OwnerTypeEntity();
        TaxStatusEntity taxStatusEntity = new TaxStatusEntity();

        OwnerEntity ownerEntity = new OwnerEntity();
        ownerEntity.setId(12);
        ownerEntity.setName("Bruno");
        ownerEntity.setLastname("Diaz");
        ownerEntity.setDni_type_id(OwnerTestHelper.DNI_TYPE_ENTITY_PASAPORTE);
        ownerEntity.setOwnerType(ownerTypeEntity);
        ownerEntity.setTaxStatus(taxStatusEntity);
        ownerEntity.setCreatedDatetime(LocalDateTime.now()); // Inicializa el campo aquí

        // When
        List<FileDto> fileDtoList = new ArrayList<>();
        fileDtoList.add(new FileDto("foto de perfil", "123456789"));

        when(ownerRepositoryMock.findById(12)).thenReturn(Optional.of(ownerEntity));
        when(fileServiceMock.getOwnerFiles(12)).thenReturn(fileDtoList);

        // Then
        GetOwnerDto result = ownerServiceSpy.getById(12);

        verify(ownerRepositoryMock, times(1)).findById(12);
        verify(fileServiceMock, times(1)).getOwnerFiles(12);

        assertEquals(ownerEntity.getId(), result.getId());
        assertEquals(ownerEntity.getName(), result.getName());
        assertNotNull(result.getFiles());
    }

    @Test
    void getById_EntityNotFound() {
        when(ownerRepositoryMock.findById(10)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ownerServiceSpy.getById(10));
    }

    @Test
    void getTaxStatus() {
        //Given
        List<TaxStatusEntity> taxStatusEntities = new ArrayList<>();
        TaxStatusEntity taxStatusEntity = new TaxStatusEntity();
        taxStatusEntity.setId(1);
        taxStatusEntity.setDescription("asdf");

        taxStatusEntities.add(taxStatusEntity);
        taxStatusEntities.add(taxStatusEntity);
        taxStatusEntities.add(taxStatusEntity);

        //When
        when(taxStatusRepositoryMock.findAll()).thenReturn(taxStatusEntities);

        //Then
        List<GetTaxStatusDto> response = ownerServiceSpy.getTaxStatus();
        assertEquals(taxStatusEntities.size(), response.size());
        assertEquals(taxStatusEntities.get(0).getDescription(), response.get(0).getDescription());
        verify(taxStatusRepositoryMock, times(1)).findAll();
    }


    @Test
    void getOwnerTypes() {
        //Given
        List<OwnerTypeEntity> ownerTypeEntities = new ArrayList<>();

        OwnerTypeEntity ownerTypeEntity = new OwnerTypeEntity();
        ownerTypeEntity.setId(10);
        ownerTypeEntity.setDescription("Judirico");

        ownerTypeEntities.add(ownerTypeEntity);
        ownerTypeEntities.add(ownerTypeEntity);

        //When
        Mockito.when(ownerTypeRepositoryMock.findAll()).thenReturn(ownerTypeEntities);

        //Then
        List<GetOwnerTypeDto> result = ownerServiceSpy.getOwnerTypes();

        assertNotNull(result);
        assertEquals(ownerTypeEntities.size(), result.size());
        assertEquals("Judirico", result.get(0).getDescription());
    }

    @Test
    void getAllOwners() {
        // Given
        OwnerTypeEntity ownerTypeEntity = new OwnerTypeEntity();
        TaxStatusEntity taxStatusEntity = new TaxStatusEntity();
        List<FileDto> fileDtoList = new ArrayList<>();
        fileDtoList.add(new FileDto("foto de perfil", "123456789"));

        List<OwnerEntity> ownerEntities = new ArrayList<>();

        OwnerEntity ownerEntity = new OwnerEntity();
        ownerEntity.setId(1);
        ownerEntity.setName("Manolo");
        ownerEntity.setDni_type_id(OwnerTestHelper.DNI_TYPE_ENTITY_PASAPORTE);
        ownerEntity.setTaxStatus(taxStatusEntity);
        ownerEntity.setOwnerType(ownerTypeEntity);
        ownerEntity.setCreatedDatetime(LocalDateTime.now()); // Inicializa el campo aquí

        ownerEntities.add(ownerEntity);
        ownerEntities.add(ownerEntity);
        ownerEntities.add(ownerEntity);

        // When
        when(ownerRepositoryMock.findAll()).thenReturn(ownerEntities);
        when(fileServiceMock.getOwnerFiles(1)).thenReturn(fileDtoList);

        // Then
        List<GetOwnerDto> result = ownerServiceSpy.getAllOwners();

        assertEquals(ownerEntities.size(), result.size());
        assertEquals(ownerEntities.get(0).getName(), result.get(0).getName());

        verify(ownerRepositoryMock, times(1)).findAll();
        verify(fileServiceMock, times(3)).getOwnerFiles(1);
    }

    @Test
    void getOwersAndPlots() {
        when(ownerRepositoryMock.findAllActives()).thenReturn(OwnerTestHelper.OWNER_ENTITY_LIST);

        when(plotOwnerRepositoryMock.findByOwnerId(1)).thenReturn(List.of(OwnerTestHelper.PLOT_OWNER_ENTITY_1));
        when(plotOwnerRepositoryMock.findByOwnerId(2)).thenReturn(List.of(OwnerTestHelper.PLOT_OWNER_ENTITY_2));
        when(plotOwnerRepositoryMock.findByOwnerId(3)).thenReturn(List.of(OwnerTestHelper.PLOT_OWNER_ENTITY_3));

        when(plotRepositoryMock.findById(1)).thenReturn(Optional.of(OwnerTestHelper.PLOT_ENTITY_1));
        when(plotRepositoryMock.findById(2)).thenReturn(Optional.of(OwnerTestHelper.PLOT_ENTITY_2));
        when(plotRepositoryMock.findById(3)).thenReturn(Optional.of(OwnerTestHelper.PLOT_ENTITY_3));

        List<GetOwnerAndPlot> result = ownerServiceSpy.getOwersAndPlots();

        assertNotNull(result);
        assertEquals(3, result.size());

        GetOwnerAndPlot ownerAndPlot1 = result.get(0);
        assertEquals("Manu", ownerAndPlot1.getOwner().getName());
        assertEquals(202, ownerAndPlot1.getPlot().get(0).getPlot_number());

        GetOwnerAndPlot ownerAndPlot2 = result.get(1);
        assertEquals("Fabricio", ownerAndPlot2.getOwner().getName());
        assertEquals(203, ownerAndPlot2.getPlot().get(0).getPlot_number());

        verify(ownerRepositoryMock, times(1)).findAllActives();
        verify(plotOwnerRepositoryMock, times(3)).findByOwnerId(anyInt());
        verify(plotRepositoryMock, times(3)).findById(anyInt());
        verify(restUserMock, times(3)).getUser(anyInt());
    }

    @Test
    void getOwersAndPlotsException() {
        when(ownerRepositoryMock.findAllActives()).thenReturn(OwnerTestHelper.OWNER_ENTITY_LIST);

        List<PlotOwnerEntity> plotOwnerEntities = List.of(OwnerTestHelper.PLOT_OWNER_ENTITY_1);
        when(plotOwnerRepositoryMock.findByOwnerId(1)).thenReturn(plotOwnerEntities);

        when(plotRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ownerServiceSpy.getOwersAndPlots();
        });
        assertNotNull(exception);
        assertEquals("Plot not found for PlotOwner", exception.getMessage());
    }

    @Test
    void getOwnerAndPlotById_Success() {
        OwnerTypeEntity ownerTypeEntity = new OwnerTypeEntity();
        TaxStatusEntity taxStatusEntity = new TaxStatusEntity();

        OwnerEntity ownerEntity = new OwnerEntity();
        ownerEntity.setId(1);
        ownerEntity.setDni_type_id(OwnerTestHelper.DNI_TYPE_ENTITY_PASAPORTE);
        ownerEntity.setName("Manolo");
        ownerEntity.setTaxStatus(taxStatusEntity);
        ownerEntity.setOwnerType(ownerTypeEntity);

        PlotStateEntity plotStateEntity = new PlotStateEntity();
        plotStateEntity.setName("Activo");

        PlotTypeEntity plotTypeEntity = new PlotTypeEntity();
        plotTypeEntity.setName("Comercial");

        PlotEntity plotEntity = new PlotEntity();
        plotEntity.setId(10);
        plotEntity.setPlotState(plotStateEntity);
        plotEntity.setPlotType(plotTypeEntity);
        plotEntity.setBlockNumber(2);
        plotEntity.setPlotNumber(152);
        plotEntity.setBuiltAreaInM2(500.0);
        plotEntity.setTotalAreaInM2(500.0);

        PlotOwnerEntity plotOwnerEntity = new PlotOwnerEntity();
        plotOwnerEntity.setId(2);
        plotOwnerEntity.setPlot(plotEntity);
        plotOwnerEntity.setOwner(ownerEntity);

        GetUserDto getUserDto = new GetUserDto();
        getUserDto.setId(1);
        getUserDto.setName("Manolo");

        // When
        Mockito.when(ownerRepositoryMock.findById(1)).thenReturn(Optional.of(ownerEntity));
        Mockito.when(plotOwnerRepositoryMock.findByOwnerId(1)).thenReturn(List.of(plotOwnerEntity));
        Mockito.when(plotRepositoryMock.findById(10)).thenReturn(Optional.of(plotEntity));
        Mockito.when(restUserMock.getUser(10)).thenReturn(getUserDto);

        // Then
        GetOwnerAndPlot result = ownerServiceSpy.getOwnerAndPlotById(1);

        assertNotNull(result);
        assertEquals(plotEntity.getId(), result.getPlot().get(0).getId());
        assertEquals(ownerEntity.getName(), result.getOwner().getName());
        assertEquals(getUserDto.getName(), result.getUser().getName());
    }

    @Test
    void getOwnerAndPlotById_Null(){
        //When
        Mockito.when(ownerRepositoryMock.findById(10)).thenReturn(Optional.empty());

        //Then
        GetOwnerAndPlot result = ownerServiceSpy.getOwnerAndPlotById(10);

        assertNull(result);
        Mockito.verify(plotOwnerRepositoryMock, Mockito.times(0)).findByOwnerId(anyInt());
        Mockito.verify(plotRepositoryMock, Mockito.times(0)).findById(anyInt());
        Mockito.verify(restUserMock, Mockito.times(0)).getUser(anyInt());
    }

    @Test
    void getOwnersByPlotId() {
    }

    @Test
    void getOwnersByPlotId_EntityNotFound() {
        //When
        Mockito.when(ownerRepositoryMock.findByPlotId(10)).thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class, ()-> ownerServiceSpy.getOwnersByPlotId(10));
    }

    @Test
    void deleteOwner_Success() {
        //Given
        OwnerTypeEntity ownerTypeEntity  = new OwnerTypeEntity();
        TaxStatusEntity taxStatusEntity = new TaxStatusEntity();

        OwnerEntity ownerEntity = new OwnerEntity();
        ownerEntity.setId(12);
        ownerEntity.setName("Bruno");
        ownerEntity.setLastname("Diaz");
        ownerEntity.setOwnerType(ownerTypeEntity);
        ownerEntity.setTaxStatus(taxStatusEntity);

        //When
        when(ownerRepositoryMock.findById(12)).thenReturn(Optional.of(ownerEntity));

        //Then
        ownerServiceSpy.deleteOwner(12, 1);

        verify(ownerRepositoryMock, times(1)).save(ownerEntity);
        verify(restUserMock, times(1)).deleteUser(12, 1);
    }

    @Test
    void deleteOwner_EntityNotFound() {
        when(ownerRepositoryMock.findById(12)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ownerServiceSpy.deleteOwner(12, 1));
        verify(ownerRepositoryMock, times(1)).findById(12);
        verify(ownerRepositoryMock, times(0)).save(Mockito.any(OwnerEntity.class));
        verify(restUserMock, times(0)).deleteUser(12, 1);
    }
}


