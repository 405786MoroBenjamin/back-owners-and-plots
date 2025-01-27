package ar.edu.utn.frc.tup.lc.iv.controllers;

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
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controlador para manejar la lógica de propietarios.
 */
@RestController
@RequestMapping("/owners")
@RequiredArgsConstructor
public class OwnerController {

    /**
     * Servicio para manejar la lógica de propietarios.
     */
    private final OwnerService ownerService;

    /**
     * Guarda un nuevo propietario.
     *
     * @param postOwnerDto datos del propietario a guardar.
     * @return el propietario creado.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GetOwnerDto> postOwner(@ModelAttribute PostOwnerDto postOwnerDto) {
        GetOwnerDto getOwnerDto = ownerService.createOwner(postOwnerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(getOwnerDto);
    }

    /**
     * Actualiza un propietario.
     *
     * @param ownerId id del propietario a actualizar.
     * @param putOwnerDto datos del propietario a actualizar.
     * @return el propietario actualizado.
     */
    @PutMapping(value = "/{ownerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GetOwnerDto> putOwner(@PathVariable Integer ownerId, @ModelAttribute PutOwnerDto putOwnerDto) {
        GetOwnerDto getOwnerDto = ownerService.updateOwner(ownerId, putOwnerDto);
        return ResponseEntity.ok(getOwnerDto);
    }

    /**
     * Obtiene un propietario por id.
     *
     * @param ownerId id del propietario a obtener.
     * @return el propietario.
     */
    @GetMapping("/{ownerId}")
    public ResponseEntity<GetOwnerDto> getOwnerById(@PathVariable Integer ownerId) {
        GetOwnerDto result = ownerService.getById(ownerId);
        return ResponseEntity.ok(result);
    }

    /**
     * Obtiene los tipos de situación fiscal.
     *
     * @return lista de los tipos de situación fiscal.
     */
    @GetMapping("/taxstatus")
    public ResponseEntity<List<GetTaxStatusDto>> getTaxStatus() {
        return ResponseEntity.ok(ownerService.getTaxStatus());
    }

    /**
     * Obtiene los tipos de propietarios.
     *
     * @return lista de los tipos de propietarios.
     */
    @GetMapping("/ownertypes")
    public ResponseEntity<List<GetOwnerTypeDto>> getOwnerTypes() {
        return ResponseEntity.ok(ownerService.getOwnerTypes());
    }

    /**
     * Obtiene los tipos de documentos.
     *
     * @return lista de los tipos de documentos.
     */
    @GetMapping("/dnitypes")
    public ResponseEntity<List<GetDniTypeDto>> getDniTypes() {
        return ResponseEntity.ok(ownerService.getDniTypes());
    }

    /**
     * Obtiene todos los propietarios.
     *
     * @return una lista con todos los propietarios.
     */
    @GetMapping()
    public ResponseEntity<List<GetOwnerDto>> getOwners() {
        List<GetOwnerDto> result = ownerService.getAllOwners();

        if (result == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Obtiene los propietarios de un lote.
     *
     * @param plotId el id del lote a buscar.
     * @return una lista con los propietarios del lote.
     */
    @GetMapping("/plot/{plotId}")
    public ResponseEntity<List<OwnerDto>> getOwnersByPlotId(@PathVariable Integer plotId) {
        List<OwnerDto> owners = ownerService.getOwnersByPlotId(plotId);
        return ResponseEntity.ok(owners);
    }

    /**
     * Obtiene todos los propietarios activos, junto con su lote y su usuario.
     *
     * @return una lista con todos los propietarios activos, su lote y su usuario.
     */
    @GetMapping("/ownersandplots")
    public ResponseEntity<List<GetOwnerAndPlot>> getOwnersPlots() {
        List<GetOwnerAndPlot> result = ownerService.getOwersAndPlots();
        return ResponseEntity.ok(result);
    }

    /**
     * Obtiene todos los propietarios activos, junto con su lote y su usuario.
     *
     * @return una lista con todos los propietarios activos, su lote y su usuario.
     */
    @GetMapping("/allOwnersWithTheirPlots")
    public ResponseEntity<List<GetOwnerWithHisPlots>> getallOwnersWithTheirPlots() {
        return ResponseEntity.ok(ownerService.getallOwnersWithTheirPlots());
    }

    /**
     * Obtiene un propietario por su id, junto con su lote y su usuario.
     *
     * @param ownerId el id del propietario a buscar.
     * @return el propietario, su lote y su usuario.
     */
    @GetMapping("/ownersandplots/{ownerId}")
    public ResponseEntity<GetOwnerAndPlot> getOwnerAndPlotById(@PathVariable Integer ownerId) {
        GetOwnerAndPlot ownerAndPlot = ownerService.getOwnerAndPlotById(ownerId);
        if (ownerAndPlot == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ownerAndPlot);
    }

    /**
     * Meotodo para contar propietarios por estado.
     *
     *
     * @return contador de propietarios por estado.
     */
    @GetMapping("/count-by-status")
    public Map<String, Long>  getOwnersCountByStatus() {
        return ownerService.getOwnerCountByStatus();
    }



    /**
     * Baja lógica de un propietario.
     *
     * @param ownerId id del propietario a dar de baja.
     * @param userIdUpdate id del usuario realiza la baja.
     * @return respuesta vacía.
     */
    @DeleteMapping("/{id}/{userIdUpdate}")
    public ResponseEntity<Void> deleteOwner(@PathVariable("id") Integer ownerId, @PathVariable("userIdUpdate") Integer userIdUpdate) {
        ownerService.deleteOwner(ownerId, userIdUpdate);
        return ResponseEntity.noContent().build();
    }
    /**
     * Obtiene una lista de lotes por ID de usuario.
     *
     * @param userId id del propietario asignado al lote.
     * @return una lista de lotes.
     */
    @GetMapping("getplotbyuserid/{userId}")
    public ResponseEntity<List<GetPlotDto>> getPlotByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(ownerService.getPlotByUserId(userId));
    }
}
