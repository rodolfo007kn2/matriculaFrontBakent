package com.devrod.matriucla.repository;

import com.devrod.matriucla.entity.FichaInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FichaInscripcionRepository extends JpaRepository<FichaInscripcion, Integer> {
    Optional<FichaInscripcion> findByCodInscripcion(String codInscripcion);
    boolean existsByEstudianteIdEstudianteAndAnioLectivo(Integer idEstudiante, Integer anioLectivo);
    Optional<FichaInscripcion> findByEstudianteIdEstudianteAndAnioLectivo(Integer idEstudiante, Integer anioLectivo);
    long countByAnioLectivo(Integer anioLectivo);
}