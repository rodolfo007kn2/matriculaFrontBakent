package com.devrod.matriucla.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EstudianteConsultaDTO {
    private boolean existe;
    private String condicion; // "NUEVO" o "CONTINUADOR"
    private Integer idEstudiante;
    private String nroDoc;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private Integer edadCalculada;
    private String sexo;

    // Datos del apoderado si ya existe
    private Integer idTutor;
    private String nroDocTutor;
    private String nombresTutor;
    private String apellidosTutor;
    private String parentescoTutor;
    private String celularTutor;

    // Control de Matrícula en el Año Lectivo Actual
    private boolean yaMatriculadoAnioActual;
    private String codMatriculaActual;
    private String estadoMatriculaActual; // "PENDIENTE_PAGO" o "CANCELADO"
    private Integer gradoAulaActual;
    private String seccionAulaActual;

    // Ficha de Inscripción Previa en el Año Lectivo Actual
    private boolean tieneInscripcionPrevia;
    private String codInscripcion;
    private String estadoInscripcion; // "REGISTRADA" o "CONSOLIDADA"
    private Integer anioInscripcion;
    private Integer idAulaInscripcion;

    // Carpeta Documentaria Existente y Requisitos entregados
    private String nroExpediente;
    private String estadoDoc; // "COMPLETO", "OBSERVADO", "SUBSANADO"
    private String detalleObservacion;
    private Boolean tieneDniMenor;
    private Boolean tieneDniTutor;
    private Boolean tieneCartillaVacunas;
    private Boolean tieneTamizajeHemog;
    private Boolean tieneFichaSiagie;
    private Boolean tieneCertifEstudios;
    private Boolean tienePartidaNac;
}
