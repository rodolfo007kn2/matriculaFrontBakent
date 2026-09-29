package com.devrod.matriucla.services;

import com.devrod.matriucla.entity.Aula;
import com.devrod.matriucla.repository.AulaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AulaService {

    private final AulaRepository aulaRepository;

    @Transactional(readOnly = true)
    public List<Aula> listarAulas(Integer gradoEdad) {
        if (gradoEdad != null) {
            log.info("Consultando aulas para grado: {} años", gradoEdad);
            return aulaRepository.findByGradoEdad(gradoEdad);
        }
        log.info("Listando todas las aulas");
        return aulaRepository.findAll();
    }
}
