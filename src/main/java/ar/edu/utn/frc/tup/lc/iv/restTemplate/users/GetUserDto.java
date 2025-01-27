package ar.edu.utn.frc.tup.lc.iv.restTemplate.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO que representa una respuesta que contiene la información de
 * un usuario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserDto {

    /**
     * Identificador único del usuario.
     */
    private Integer id;

    /**
     * Nombre real del usuario.
     */
    private String name;

    /**
     * Apellido del usuario.
     */
    private String lastname;

    /**
     * Nombre de usuario utilizado en login.
     */
    private String username;

    /**
     * Contraseña del usuario utilizada en login.
     */
    private String password;

    /**
     * Email del usuario que se utiliza en el login.
     */
    private String email;

    /**
     * Número de teléfono del usuario.
     */
    private String phone_number;

    /**
     * Tipo de DNI del usuario.
     */
    private String dni_type;

    /**
     * Número de DNI del usuario.
     */
    private String dni;

    /**
     * Representa sí el usuario está activo o no.
     */
    private Boolean active;

    /**
     * Dirección URL del avatar asignado al usuario.
     */
    private String avatar_url;

    /**
     * Fecha de nacimiento del usuario.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate datebirth;

    /**
     * Fecha de creación del usuario.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate create_date;

    /**
     * Lista de los roles que tiene el usuario.
     */
    private String[] roles;

    /**
     * Identificador del lote asignado al usuario.
     */
    private Integer[] plot_id;

    /**
     * Identificador de la plataforma telegram utilizada en notificaciones.
     */
    private Long telegram_id;
}
