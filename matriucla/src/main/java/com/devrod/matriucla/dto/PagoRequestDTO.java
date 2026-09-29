package com.devrod.matriucla.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequestDTO {

    @NotBlank(message = "El código de matrícula es obligatorio")
    private String codMatricula;
    
    @NotBlank(message = "El tipo de comprobante es obligatorio")
    private String tipoComprobante; // "BOLETA" o "RECIBO"
    
    @NotBlank(message = "El medio de pago es obligatorio")
    private String medioPago; // "EFECTIVO", "TRANSFERENCIA", "TARJETA", "YAPE_PLIN"
    
    private String nroOperacion;
    
    @NotNull(message = "El importe pagado es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto pagado debe ser mayor a cero")
    private BigDecimal importePagado;
}
