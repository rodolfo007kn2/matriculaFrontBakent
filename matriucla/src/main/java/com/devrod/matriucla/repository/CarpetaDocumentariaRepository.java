package com.devrod.matriucla.repository;

import com.devrod.matriucla.entity.CarpetaDocumentaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarpetaDocumentariaRepository extends JpaRepository<CarpetaDocumentaria, Integer> {
    Optional<CarpetaDocumentaria> findByEstudianteIdEstudiante(Integer idEstudiante);
    long count();
}