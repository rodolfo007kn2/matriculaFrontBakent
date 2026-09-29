export interface InscripcionResponseDTO {
  codInscripcion: string;
  anioLectivo: number;
  fechaInscripcion: string;
  estadoInscripcion: string;
  nroExpediente: string;
  estadoDoc: string;
  dniEstudiante: string;
  nombresEstudiante: string;
  idAula?: number | null;
  nombreAula?: string | null;
  mensaje?: string;
}
