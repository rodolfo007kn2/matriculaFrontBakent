package com.devrod.matriucla.repository;

import com.devrod.matriucla.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Integer> {
    Optional<Tutor> findByNroDoc(String nroDoc);
}