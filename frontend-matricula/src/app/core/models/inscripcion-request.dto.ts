export interface InscripcionRequestDTO {
  tipoEstudiante?: string;

  // Estudiante
  tipoDocEstudiante: string;
  nroDocEstudiante: string;
  nombresEstudiante: string;
  apellidosEstudiante: string;
  fechaNacimiento: string;
  sexo: string;
  nroPartidaNac?: string;
  colegioProcedencia?: string;
  tieneTraslado?: boolean;

  // Tutor
  tipoDocTutor: string;
  nroDocTutor: string;
  nombresTutor: string;
  apellidosTutor: string;
  parentescoTutor: string;
  celularTutor?: string;
  direccionTutor?: string;
  correoTutor?: string;

  // Carpeta Documentaria
  tieneDniMenor?: boolean;
  tieneDniTutor?: boolean;
  tieneCartillaVacunas?: boolean;
  tieneTamizajeHemog?: boolean;
  tieneFichaSiagie?: boolean;
  tieneCertifEstudios?: boolean;
  tienePartidaNac?: boolean;
  detalleObservacion?: string;

  // Aula opcional
  idAula?: number | null;
}
