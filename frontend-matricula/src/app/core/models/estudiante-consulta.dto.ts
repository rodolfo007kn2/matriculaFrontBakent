export interface EstudianteConsultaDTO {
  existe: boolean;
  condicion: string; // 'NUEVO', 'CONTINUADOR' o 'YA_MATRICULADO'
  idEstudiante: number | null;
  nroDoc: string;
  nombres: string;
  apellidos: string;
  fechaNacimiento: string;
  edadCalculada: number | null;
  sexo: string;
  idTutor: number | null;
  nroDocTutor: string;
  nombresTutor: string;
  apellidosTutor: string;
  parentescoTutor: string;
  celularTutor: string;

  // Control de Matrícula en el Año Lectivo Actual
  yaMatriculadoAnioActual?: boolean;
  codMatriculaActual?: string | null;
  estadoMatriculaActual?: string | null; // 'PENDIENTE_PAGO' o 'CANCELADO'
  gradoAulaActual?: number | null;
  seccionAulaActual?: string | null;

  // Ficha de Inscripción Previa en el Año Lectivo Actual
  tieneInscripcionPrevia?: boolean;
  codInscripcion?: string | null;
  estadoInscripcion?: string | null; // 'REGISTRADA' o 'CONSOLIDADA'
  anioInscripcion?: number | null;
  idAulaInscripcion?: number | null;

  // Carpeta Documentaria Existente y Requisitos entregados
  nroExpediente?: string | null;
  estadoDoc?: string | null; // 'COMPLETO', 'OBSERVADO', 'SUBSANADO'
  detalleObservacion?: string | null;
  tieneDniMenor?: boolean | null;
  tieneDniTutor?: boolean | null;
  tieneCartillaVacunas?: boolean | null;
  tieneTamizajeHemog?: boolean | null;
  tieneFichaSiagie?: boolean | null;
  tieneCertifEstudios?: boolean | null;
  tienePartidaNac?: boolean | null;
}
