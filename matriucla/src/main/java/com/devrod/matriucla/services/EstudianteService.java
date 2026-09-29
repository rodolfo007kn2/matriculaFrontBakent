package com.devrod.matriucla.services;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devrod.matriucla.dto.EstudianteConsultaDTO;
import com.devrod.matriucla.dto.EstudianteListadoDTO;
import com.devrod.matriucla.entity.Estudiante;
import com.devrod.matriucla.entity.FichaMatricula;
import com.devrod.matriucla.entity.Tutor;
import com.devrod.matriucla.repository.CarpetaDocumentariaRepository;
import com.devrod.matriucla.repository.EstudianteRepository;
import com.devrod.matriucla.repository.FichaInscripcionRepository;
import com.devrod.matriucla.repository.FichaMatriculaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final FichaMatriculaRepository fichaMatriculaRepository;
    private final FichaInscripcionRepository inscripcionRepository;
    private final CarpetaDocumentariaRepository carpetaRepository;

    @Transactional(readOnly = true)
    public EstudianteConsultaDTO consultarPorNroDoc(String nroDoc) {
        log.info("Consultando estudiante con documento: {}", nroDoc);

        Optional<Estudiante> optEstudiante = estudianteRepository.findByNroDoc(nroDoc);

        if (optEstudiante.isPresent()) {
            Estudiante est = optEstudiante.get();
            Tutor tutor = est.getTutor();

            int anioActual = LocalDate.now().getYear();

            // Buscar si ya tiene matrícula en el año lectivo actual
            Optional<FichaMatricula> optMatriculaActual = fichaMatriculaRepository
                    .findByFichaInscripcionEstudianteNroDoc(nroDoc).stream()
                    .filter(m -> m.getFichaInscripcion() != null &&
                                 m.getFichaInscripcion().getAnioLectivo() != null &&
                                 m.getFichaInscripcion().getAnioLectivo() == anioActual)
                    .findFirst();

            boolean yaMatriculado = optMatriculaActual.isPresent();
            boolean esContinuador = fichaMatriculaRepository.existsByFichaInscripcionEstudianteNroDoc(nroDoc);

            // Buscar si ya tiene Ficha de Inscripción en el año lectivo actual
            Optional<com.devrod.matriucla.entity.FichaInscripcion> optInscripcionActual = inscripcionRepository
                    .findByEstudianteIdEstudianteAndAnioLectivo(est.getIdEstudiante(), anioActual);

            // Buscar si ya tiene Carpeta Documentaria
            Optional<com.devrod.matriucla.entity.CarpetaDocumentaria> optCarpeta = carpetaRepository
                    .findByEstudianteIdEstudiante(est.getIdEstudiante());

            int edad = Period.between(est.getFechaNacimiento(), LocalDate.now()).getYears();

            String condicionCalculada;
            if (yaMatriculado) {
                condicionCalculada = "YA_MATRICULADO";
            } else if (optInscripcionActual.isPresent()) {
                condicionCalculada = "PRE_INSCRITO";
            } else if (esContinuador) {
                condicionCalculada = "CONTINUADOR";
            } else {
                condicionCalculada = "NUEVO";
            }

            EstudianteConsultaDTO.EstudianteConsultaDTOBuilder builder = EstudianteConsultaDTO.builder()
                    .existe(true)
                    .condicion(condicionCalculada)
                    .idEstudiante(est.getIdEstudiante())
                    .nroDoc(est.getNroDoc())
                    .nombres(est.getNombres())
                    .apellidos(est.getApellidos())
                    .fechaNacimiento(est.getFechaNacimiento())
                    .edadCalculada(edad)
                    .sexo(est.getSexo())
                    .idTutor(tutor != null ? tutor.getIdTutor() : null)
                    .nroDocTutor(tutor != null ? tutor.getNroDoc() : null)
                    .nombresTutor(tutor != null ? tutor.getNombres() : null)
                    .apellidosTutor(tutor != null ? tutor.getApellidos() : null)
                    .parentescoTutor(tutor != null ? tutor.getParentesco() : null)
                    .celularTutor(tutor != null ? tutor.getCelular() : null)
                    .yaMatriculadoAnioActual(yaMatriculado);

            if (yaMatriculado) {
                FichaMatricula mat = optMatriculaActual.get();
                builder.codMatriculaActual(mat.getCodMatricula())
                       .estadoMatriculaActual(mat.getEstadoMatricula())
                       .gradoAulaActual(mat.getAula().getGradoEdad())
                       .seccionAulaActual(mat.getAula().getNombreSeccion());
            }

            if (optInscripcionActual.isPresent()) {
                com.devrod.matriucla.entity.FichaInscripcion insc = optInscripcionActual.get();
                builder.tieneInscripcionPrevia(true)
                       .codInscripcion(insc.getCodInscripcion())
                       .estadoInscripcion(insc.getEstadoInscripcion())
                       .anioInscripcion(insc.getAnioLectivo())
                       .idAulaInscripcion(insc.getAula() != null ? insc.getAula().getIdAula() : null);
            }

            if (optCarpeta.isPresent()) {
                com.devrod.matriucla.entity.CarpetaDocumentaria carp = optCarpeta.get();
                builder.nroExpediente(carp.getNroExpediente())
                       .estadoDoc(carp.getEstadoDoc())
                       .detalleObservacion(carp.getDetalleObservacion())
                       .tieneDniMenor(carp.getTieneDniMenor())
                       .tieneDniTutor(carp.getTieneDniTutor())
                       .tieneCartillaVacunas(carp.getTieneCartillaVacunas())
                       .tieneTamizajeHemog(carp.getTieneTamizajeHemog())
                       .tieneFichaSiagie(carp.getTieneFichaSiagie())
                       .tieneCertifEstudios(carp.getTieneCertifEstudios())
                       .tienePartidaNac(carp.getTienePartidaNac());
            }

            return builder.build();
        } else {
            log.info("Estudiante no encontrado. Se registrará como NUEVO.");
            return EstudianteConsultaDTO.builder()
                    .existe(false)
                    .condicion("NUEVO")
                    .nroDoc(nroDoc)
                    .yaMatriculadoAnioActual(false)
                    .build();
        }
    }

    /**
     * Lista todos los estudiantes registrados con su estado calculado
     * en el anio lectivo actual (NUEVO, CONTINUADOR, PRE_INSCRITO, YA_MATRICULADO).
     */
    @Transactional(readOnly = true)
    public List<EstudianteListadoDTO> listarEstudiantes() {
        int anioActual = LocalDate.now().getYear();
        log.info("Listando estudiantes para el anio lectivo: {}", anioActual);

        return estudianteRepository.findAll().stream().map(est -> {
            String nroDoc = est.getNroDoc();

            // Verificar matricula en el anio actual
            Optional<FichaMatricula> optMatricula = fichaMatriculaRepository
                    .findByFichaInscripcionEstudianteNroDoc(nroDoc).stream()
                    .filter(m -> m.getFichaInscripcion() != null &&
                                 m.getFichaInscripcion().getAnioLectivo() != null &&
                                 m.getFichaInscripcion().getAnioLectivo() == anioActual)
                    .findFirst();

            boolean yaMatriculado = optMatricula.isPresent();
            boolean esContinuador = fichaMatriculaRepository
                    .existsByFichaInscripcionEstudianteNroDoc(nroDoc);

            // Verificar inscripcion previa en el anio actual
            Optional<com.devrod.matriucla.entity.FichaInscripcion> optInscripcion = inscripcionRepository
                    .findByEstudianteIdEstudianteAndAnioLectivo(est.getIdEstudiante(), anioActual);

            // Calcular condicion
            String condicion;
            if (yaMatriculado) {
                condicion = "YA_MATRICULADO";
            } else if (optInscripcion.isPresent()) {
                condicion = "PRE_INSCRITO";
            } else if (esContinuador) {
                condicion = "CONTINUADOR";
            } else {
                condicion = "NUEVO";
            }

            EstudianteListadoDTO.EstudianteListadoDTOBuilder builder = EstudianteListadoDTO.builder()
                    .idEstudiante(est.getIdEstudiante())
                    .nroDoc(nroDoc)
                    .nombres(est.getNombres())
                    .apellidos(est.getApellidos())
                    .fechaNacimiento(est.getFechaNacimiento())
                    .sexo(est.getSexo())
                    .condicion(condicion)
                    .nombresTutor(est.getTutor() != null ? est.getTutor().getNombres() : null)
                    .celularTutor(est.getTutor() != null ? est.getTutor().getCelular() : null);

            if (yaMatriculado) {
                FichaMatricula mat = optMatricula.get();
                builder.codMatriculaActual(mat.getCodMatricula())
                       .estadoMatriculaActual(mat.getEstadoMatricula())
                       .gradoAulaActual(mat.getAula() != null ? mat.getAula().getGradoEdad() : null)
                       .seccionAulaActual(mat.getAula() != null ? mat.getAula().getNombreSeccion() : null);
            }

            if (optInscripcion.isPresent()) {
                com.devrod.matriucla.entity.FichaInscripcion insc = optInscripcion.get();
                builder.codInscripcion(insc.getCodInscripcion())
                       .estadoInscripcion(insc.getEstadoInscripcion());
            }

            return builder.build();
        }).collect(Collectors.toList());
    }
}
