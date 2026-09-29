package com.devrod.matriucla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaResponseDTO {
    private String codMatricula;
    private LocalDateTime fechaFormalizacion;
    private BigDecimal montoTotal;
    private String estadoMatricula;
    
    // Datos del Estudiante
    private String dniEstudiante;
    private String nombresEstudiante;
    
    // Datos del Aula
    private Integer gradoAula;
    private String seccionAula;

    // Datos de la Ficha de Inscripción (Fase 1)
    private String codInscripcion;
    private String estadoInscripcion; // "REGISTRADA" o "CONSOLIDADA"

    // Datos de la Carpeta Documentaria
    private String nroExpediente;
    private String estadoDoc; // "COMPLETO" o "OBSERVADO"
}
