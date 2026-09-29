export interface MatriculaResponseDTO {
  codMatricula: string;
  fechaFormalizacion: string;
  montoTotal: number;
  estadoMatricula: string; // 'PENDIENTE_PAGO' o 'CANCELADO'
  
  // Datos del Estudiante
  dniEstudiante: string;
  nombresEstudiante: string;
  
  // Datos del Aula
  gradoAula: number;
  seccionAula: string;

  // Datos de la Ficha de Inscripción (Fase 1)
  codInscripcion?: string;
  estadoInscripcion?: string; // 'REGISTRADA' o 'CONSOLIDADA'

  // Datos de la Carpeta Documentaria
  nroExpediente?: string;
  estadoDoc?: string; // 'COMPLETO' o 'OBSERVADO'
}
