package com.devrod.matriucla.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devrod.matriucla.entity.Aula;

import jakarta.persistence.LockModeType;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Integer> {
    List<Aula> findByGradoEdad(Integer gradoEdad);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Aula a WHERE a.idAula = :idAula")
    Optional<Aula> findByIdWithLock(@Param("idAula") Integer idAula);
}