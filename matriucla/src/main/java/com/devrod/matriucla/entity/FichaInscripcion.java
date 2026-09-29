package com.devrod.matriucla.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ficha_inscripcion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FichaInscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscripcion")
    private Integer idInscripcion;

    @Column(name = "cod_inscripcion", length = 25, nullable = false, unique = true)
    private String codInscripcion;

    @Column(name = "anio_lectivo", nullable = false)
    private Integer anioLectivo;

    @Column(name = "fecha_inscripcion")
    private LocalDateTime fechaInscripcion;

    @Column(name = "estado_inscripcion", length = 20, nullable = false)
    @Builder.Default
    private String estadoInscripcion = "REGISTRADA"; // 'REGISTRADA', 'CONSOLIDADA', 'ANULADA'

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aula", nullable = true)
    private Aula aula;
}