package com.devrod.matriucla.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devrod.matriucla.entity.FichaMatricula;

@Repository
public interface FichaMatriculaRepository extends JpaRepository<FichaMatricula, Integer> {
    Optional<FichaMatricula> findByCodMatricula(String codMatricula);

    // Método clave para saber si es NUEVO o CONTINUADOR: busca si ya estuvo matriculado en el estudiante
    boolean existsByFichaInscripcionEstudianteNroDoc(String nroDoc);

    List<FichaMatricula> findByFichaInscripcionEstudianteNroDoc(String nroDoc);

    @Query("SELECT MAX(m.codMatricula) FROM FichaMatricula m WHERE m.codMatricula LIKE :prefijo%")
    Optional<String> findMaxCodMatriculaByPrefijo(@Param("prefijo") String prefijo);

    long count();
}