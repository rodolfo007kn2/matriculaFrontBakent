export interface PagoResponseDTO {
  nroComprobante: string;
  fechaEmision: string;
  tipoComprobante: string;
  medioPago: string;
  nroOperacion: string;
  importePagado: number;
  codMatricula: string;
  dniEstudiante: string;
}
