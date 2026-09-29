package com.devrod.matriucla.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alumno_continuador")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoContinuador extends Estudiante {

    @Column(name = "cod_estudiante_previo", length = 20)
    private String codEstudiantePrevio;

    @Column(name = "anio_ingreso")
    private Integer anioIngreso;

    @Column(name = "observacion_academica", length = 200)
    private String observacionAcademica;
}
