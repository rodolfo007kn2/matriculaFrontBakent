package com.devrod.matriucla.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EstudianteListadoDTO {

    private Integer idEstudiante;
    private String nroDoc;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String sexo;

    private String nombresTutor;
    private String celularTutor;

    private String condicion;

    private String codMatriculaActual;
    private String estadoMatriculaActual;
    private Integer gradoAulaActual;
    private String seccionAulaActual;

    private String codInscripcion;
    private String estadoInscripcion;
}
