package com.devrod.matriucla.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aula")
    private Integer idAula;

    @Column(name = "nombre_seccion", length = 50, nullable = false)
    private String nombreSeccion;

    @Column(name = "grado_edad", nullable = false)
    private Integer gradoEdad; // 3, 4 o 5 años

    @Column(name = "turno", length = 10, nullable = false)
    private String turno; // 'MANANA', 'TARDE'

    @Column(name = "aforo_maximo", nullable = false)
    private Integer aforoMaximo;

    @Column(name = "vacantes_disp", nullable = false)
    private Integer vacantesDisp;

    // Métodos de negocio de dominio
    public boolean tieneVacantes() {
        return this.vacantesDisp != null && this.vacantesDisp > 0;
    }

    public void descontarVacante() {
        if (!tieneVacantes()) {
            throw new IllegalStateException("No hay vacantes disponibles en el aula: " + this.nombreSeccion);
        }
        this.vacantesDisp--;
    }

    public void restituirVacante() {
        if (this.vacantesDisp < this.aforoMaximo) {
            this.vacantesDisp++;
        }
    }
}