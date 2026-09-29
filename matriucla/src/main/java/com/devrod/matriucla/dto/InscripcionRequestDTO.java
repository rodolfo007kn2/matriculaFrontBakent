package com.devrod.matriucla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionRequestDTO {

    // Identificación
    private String tipoEstudiante; // "NUEVO" o "CONTINUADOR"

    // Datos Estudiante
    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocEstudiante;
    
    @NotBlank(message = "El número de documento del estudiante es obligatorio")
    @Size(max = 15, message = "El número de documento no debe superar los 15 caracteres")
    private String nroDocEstudiante;
    
    @NotBlank(message = "Los nombres del estudiante son obligatorios")
    private String nombresEstudiante;
    
    @NotBlank(message = "Los apellidos del estudiante son obligatorios")
    private String apellidosEstudiante;
    
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;
    
    @NotBlank(message = "El sexo es obligatorio")
    private String sexo;

    private String nroPartidaNac;
    private String colegioProcedencia;
    private Boolean tieneTraslado;

    // Datos Tutor
    @NotBlank(message = "El tipo de documento del tutor es obligatorio")
    private String tipoDocTutor;
    
    @NotBlank(message = "El número de documento del tutor es obligatorio")
    @Size(max = 15, message = "El número de documento no debe superar los 15 caracteres")
    private String nroDocTutor;
    
    @NotBlank(message = "Los nombres del tutor son obligatorios")
    private String nombresTutor;
    
    @NotBlank(message = "Los apellidos del tutor son obligatorios")
    private String apellidosTutor;
    
    @NotBlank(message = "El parentesco del tutor es obligatorio")
    private String parentescoTutor;
    
    private String celularTutor;
    private String direccionTutor;
    private String correoTutor;

    // Checklist Documentos (Carpeta Documentaria)
    private Boolean tieneDniMenor;
    private Boolean tieneDniTutor;
    private Boolean tieneCartillaVacunas;
    private Boolean tieneTamizajeHemog;
    private Boolean tieneFichaSiagie;
    private Boolean tieneCertifEstudios;
    private Boolean tienePartidaNac;

    private String detalleObservacion;

    // Aula preliminar (opcional en fase de inscripción)
    private Integer idAula;
}
