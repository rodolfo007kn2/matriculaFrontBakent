package com.devrod.matriucla.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comprobante_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobantePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Integer idComprobante;

    @Column(name = "nro_comprobante", length = 20, nullable = false, unique = true)
    private String nroComprobante;

    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;

    @Column(name = "tipo_comprobante", length = 10, nullable = false)
    private String tipoComprobante; // 'BOLETA', 'RECIBO'

    @Column(name = "medio_pago", length = 20, nullable = false)
    private String medioPago; // 'EFECTIVO', 'TRANSFERENCIA', 'TARJETA', 'YAPE_PLIN'

    @Column(name = "nro_operacion", length = 30)
    private String nroOperacion;

    @Column(name = "importe_pagado", precision = 10, scale = 2, nullable = false)
    private BigDecimal importePagado;

    @Column(name = "estado_pago", length = 15, nullable = false)
    @Builder.Default
    private String estadoPago = "EMITIDO";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matricula", nullable = false, unique = true)
    private FichaMatricula fichaMatricula;
}