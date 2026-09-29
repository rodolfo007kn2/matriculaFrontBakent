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
public class PagoResponseDTO {
    private String nroComprobante;
    private LocalDateTime fechaEmision;
    private String tipoComprobante;
    private String medioPago;
    private String nroOperacion;
    private BigDecimal importePagado;
    
    // Datos de la Matrícula relacionada
    private String codMatricula;
    private String dniEstudiante;
}
