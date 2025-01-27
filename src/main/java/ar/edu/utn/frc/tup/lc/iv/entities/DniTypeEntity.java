package ar.edu.utn.frc.tup.lc.iv.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * La clase {@code DniTypeEntity} representa un tipo de dni y
 * hace referencia a la tabla de dnitypes.
 */
@Entity
@Table(name = "dni_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DniTypeEntity {
    /**
     * Identificador único de la entidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Descripción del tipo de dni.
     */
    @Column(name = "description")
    private String description;

    /**
     * Fecha que reprenta cuando se creó la entidad.
     */
    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;

    /**
     * Fecha que reprenta cuando fué la última vez que se modificó la entidad.
     */
    @Column(name = "last_updated_datetime", nullable = false)
    private LocalDateTime lastUpdatedDatetime;

    /**
     * Identificador que representa el usuario que creó la entidad.
     */
    @Column(name = "created_user", nullable = false)
    private Integer createdUser;

    /**
     * Itentificador que representa el usuario que modificó por ultima vez
     * la entidad.
     */
    @Column(name = "last_updated_user", nullable = false)
    private Integer lastUpdatedUser;
}
