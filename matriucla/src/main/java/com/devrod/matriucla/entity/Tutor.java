package com.devrod.matriucla.entity;/*package com.colegio.matricula.entity;*/
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tutor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tutor")
    private Integer idTutor;

    @Column(name = "tipo_doc", length = 10, nullable = false)
    @Builder.Default
    private String tipoDoc = "DNI";

    @Column(name = "nro_doc", length = 15, nullable = false, unique = true)
    private String nroDoc;

    @Column(name = "nombres", length = 80, nullable = false)
    private String nombres;

    @Column(name = "apellidos", length = 80, nullable = false)
    private String apellidos;

    @Column(name = "parentesco", length = 30, nullable = false)
    private String parentesco;

    @Column(name = "celular", length = 15, nullable = false)
    private String celular;

    @Column(name = "direccion", length = 150)
    private String direccion;

    @Column(name = "correo", length = 100)
    private String correo;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}