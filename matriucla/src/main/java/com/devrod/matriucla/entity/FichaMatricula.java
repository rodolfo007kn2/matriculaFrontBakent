package com.devrod.matriucla.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ficha_matricula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FichaMatricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_matricula")
    private Integer idMatricula;

    @Column(name = "cod_matricula", length = 25, nullable = false, unique = true)
    private String codMatricula;

    @Column(name = "fecha_formalizacion")
    private LocalDateTime fechaFormalizacion;

    @Column(name = "monto_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "estado_matricula", length = 20, nullable = false)
    @Builder.Default
    private String estadoMatricula = "PENDIENTE_PAGO"; // 'PENDIENTE_PAGO', 'CANCELADO', 'ANULADO'

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inscripcion", nullable = false, unique = true)
    private FichaInscripcion fichaInscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aula", nullable = false)
    private Aula aula;
}