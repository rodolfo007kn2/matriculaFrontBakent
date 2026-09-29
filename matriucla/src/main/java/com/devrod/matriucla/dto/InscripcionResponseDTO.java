package com.devrod.matriucla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionResponseDTO {
    private String codInscripcion;
    private Integer anioLectivo;
    private LocalDateTime fechaInscripcion;
    private String estadoInscripcion; // "REGISTRADA", "CONSOLIDADA"
    private String nroExpediente;
    private String estadoDoc; // "COMPLETO", "OBSERVADO"
    private String dniEstudiante;
    private String nombresEstudiante;
    private Integer idAula;
    private String nombreAula;
    private String mensaje;
}
