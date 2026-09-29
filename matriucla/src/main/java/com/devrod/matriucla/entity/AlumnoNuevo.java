package com.devrod.matriucla.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alumno_nuevo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoNuevo extends Estudiante {

    @Column(name = "nro_partida_nac", length = 30)
    private String nroPartidaNac;

    @Column(name = "colegio_procedencia", length = 100)
    private String colegioProcedencia;

    @Column(name = "tiene_traslado")
    private Boolean tieneTraslado;
}
