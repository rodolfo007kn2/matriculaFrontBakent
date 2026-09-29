package com.devrod.matriucla.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "carpeta_documentaria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarpetaDocumentaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carpeta")
    private Integer idCarpeta;

    @Column(name = "nro_expediente", length = 20, nullable = false, unique = true)
    private String nroExpediente;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(name = "tiene_dni_menor")
    private Boolean tieneDniMenor;

    @Column(name = "tiene_dni_tutor")
    private Boolean tieneDniTutor;

    @Column(name = "tiene_cartilla_vacunas")
    private Boolean tieneCartillaVacunas;

    @Column(name = "tiene_tamizaje_hemog")
    private Boolean tieneTamizajeHemog;

    @Column(name = "tiene_ficha_siagie")
    private Boolean tieneFichaSiagie;

    @Column(name = "tiene_certif_estudios")
    private Boolean tieneCertifEstudios;

    @Column(name = "tiene_partida_nac")
    private Boolean tienePartidaNac;

    @Column(name = "estado_doc", length = 20, nullable = false)
    private String estadoDoc; // 'COMPLETO', 'OBSERVADO', 'SUBSANADO'

    @Column(name = "detalle_observacion", columnDefinition = "TEXT")
    private String detalleObservacion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;
}