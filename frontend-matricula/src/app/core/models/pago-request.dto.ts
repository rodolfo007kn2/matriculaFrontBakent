export interface PagoRequestDTO {
  codMatricula: string;
  tipoComprobante: string; // 'BOLETA' o 'RECIBO'
  medioPago: string;       // 'EFECTIVO', 'TRANSFERENCIA', 'TARJETA', 'YAPE_PLIN'
  nroOperacion?: string;
  importePagado: number;
}
