export interface EstudianteListadoDTO {
  idEstudiante: number;
  nroDoc: string;
  nombres: string;
  apellidos: string;
  fechaNacimiento: string;
  sexo: string;

  // Apoderado principal
  nombresTutor: string | null;
  celularTutor: string | null;

  // Estado calculado: NUEVO | CONTINUADOR | PRE_INSCRITO | YA_MATRICULADO
  condicion: string;

  // Datos de matricula actual (si YA_MATRICULADO)
  codMatriculaActual?: string | null;
  estadoMatriculaActual?: string | null;
  gradoAulaActual?: number | null;
  seccionAulaActual?: string | null;

  // Datos de inscripcion previa (si PRE_INSCRITO)
  codInscripcion?: string | null;
  estadoInscripcion?: string | null;
}
