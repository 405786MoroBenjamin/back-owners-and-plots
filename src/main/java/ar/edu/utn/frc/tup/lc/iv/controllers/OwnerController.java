package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetOwnerDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetOwnerTypeDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetTaxStatusDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.post.PostOwnerDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.put.PutOwnerDto;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owners")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @PostMapping
    public ResponseEntity<GetOwnerDto> postOwner(@RequestBody PostOwnerDto postOwnerDto) {
        GetOwnerDto getOwnerDto = ownerService.createOwner(postOwnerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(getOwnerDto);
    }

    @PutMapping("/{ownerId}")
    public ResponseEntity<GetOwnerDto> putOwner(@PathVariable Integer ownerId, @RequestBody PutOwnerDto putOwnerDto) {
        GetOwnerDto getOwnerDto = ownerService.updateOwner(ownerId, putOwnerDto);
        return ResponseEntity.ok(getOwnerDto);
    }

    @GetMapping("/taxstatus")
    public ResponseEntity<List<GetTaxStatusDto>> getTaxStatus() {
        return ResponseEntity.ok(ownerService.getTaxStatus());
    }

    @GetMapping("/ownertypes")
    public ResponseEntity<List<GetOwnerTypeDto>> getOwnerTypes() {
        return ResponseEntity.ok(ownerService.getOwnerTypes());
    }

}
