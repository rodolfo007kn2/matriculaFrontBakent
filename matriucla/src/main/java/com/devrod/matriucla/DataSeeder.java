package com.devrod.matriucla;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.devrod.matriucla.entity.Aula;
import com.devrod.matriucla.repository.AulaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AulaRepository aulaRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            jdbcTemplate.execute("ALTER TABLE ficha_inscripcion MODIFY COLUMN id_aula INT NULL");
            log.info("Columna id_aula en ficha_inscripcion verificada como NULLABLE.");
        } catch (Exception e) {
            log.debug("Ajuste de id_aula ya aplicado o no requerido: {}", e.getMessage());
        }

        if (aulaRepository.count() == 0) {
            log.info("Inicializando aulas predeterminadas en la base de datos...");

            List<Aula> aulas = List.of(
                    Aula.builder().nombreSeccion("A-01").gradoEdad(3).turno("MANANA").aforoMaximo(20).vacantesDisp(15).build(),
                    Aula.builder().nombreSeccion("A-02").gradoEdad(3).turno("TARDE").aforoMaximo(20).vacantesDisp(18).build(),
                    Aula.builder().nombreSeccion("B-01").gradoEdad(4).turno("MANANA").aforoMaximo(22).vacantesDisp(12).build(),
                    Aula.builder().nombreSeccion("B-02").gradoEdad(4).turno("TARDE").aforoMaximo(22).vacantesDisp(20).build(),
                    Aula.builder().nombreSeccion("C-01").gradoEdad(5).turno("MANANA").aforoMaximo(25).vacantesDisp(8).build(),
                    Aula.builder().nombreSeccion("C-02").gradoEdad(5).turno("TARDE").aforoMaximo(25).vacantesDisp(22).build()
            );

            aulaRepository.saveAll(aulas);
            log.info("Se crearon {} aulas correctamente.", aulas.size());
        }
    }
}
