package ar.edu.utn.frc.tup.lc.iv.dtos.get;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDto {

    private Integer id;
    private String name;
    private String surname;
    private String dni;
    private String cuitCuil;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateBirth;
    private Integer contactId;
    private String businessName;
    private Boolean active;

    private OwnerTypeDto ownerType;
    private List<FileDto> files;
}
