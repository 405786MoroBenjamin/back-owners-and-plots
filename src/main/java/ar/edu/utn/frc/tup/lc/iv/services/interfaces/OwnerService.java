package ar.edu.utn.frc.tup.lc.iv.services.interfaces;

import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetOwnerDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetOwnerTypeDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetTaxStatusDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.post.PostOwnerDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.put.PutOwnerDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OwnerService {

    GetOwnerDto createOwner(PostOwnerDto postOwnerDto);
    GetOwnerDto updateOwner(Integer ownerId, PutOwnerDto putOwnerDto);
    List<GetTaxStatusDto> getTaxStatus();
    List<GetOwnerTypeDto> getOwnerTypes();
    List<GetOwnerDto> getAllOwners();
}
