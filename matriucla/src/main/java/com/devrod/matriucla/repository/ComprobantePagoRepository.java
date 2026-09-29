package com.devrod.matriucla.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devrod.matriucla.entity.ComprobantePago;

@Repository
public interface ComprobantePagoRepository extends JpaRepository<ComprobantePago, Integer> {
    Optional<ComprobantePago> findByNroComprobante(String nroComprobante);
    Optional<ComprobantePago> findByFichaMatriculaIdMatricula(Integer idMatricula);

    @Query("SELECT MAX(c.nroComprobante) FROM ComprobantePago c WHERE c.nroComprobante LIKE :prefijo%")
    Optional<String> findMaxNroComprobanteByPrefijo(@Param("prefijo") String prefijo);

    long count();
}