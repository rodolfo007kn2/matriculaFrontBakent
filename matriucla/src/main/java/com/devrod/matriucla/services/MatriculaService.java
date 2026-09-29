package com.devrod.matriucla.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devrod.matriucla.dto.MatriculaRequestDTO;
import com.devrod.matriucla.dto.MatriculaResponseDTO;
import com.devrod.matriucla.entity.Aula;
import com.devrod.matriucla.entity.CarpetaDocumentaria;
import com.devrod.matriucla.entity.Estudiante;
import com.devrod.matriucla.entity.FichaInscripcion;
import com.devrod.matriucla.entity.FichaMatricula;
import com.devrod.matriucla.entity.Tutor;
import com.devrod.matriucla.mapper.MatriculaMapper;
import com.devrod.matriucla.repository.AulaRepository;
import com.devrod.matriucla.repository.CarpetaDocumentariaRepository;
import com.devrod.matriucla.repository.EstudianteRepository;
import com.devrod.matriucla.repository.FichaInscripcionRepository;
import com.devrod.matriucla.repository.FichaMatriculaRepository;
import com.devrod.matriucla.repository.TutorRepository;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaMapper matriculaMapper;

    private final TutorRepository tutorRepository;
    private final EstudianteRepository estudianteRepository;
    private final CarpetaDocumentariaRepository carpetaRepository;
    private final AulaRepository aulaRepository;
    private final FichaInscripcionRepository inscripcionRepository;
    private final FichaMatriculaRepository matriculaRepository;

    /**
     * Orquesta el proceso de matrícula de forma transaccional.
     * Si algo falla (ej. error al guardar la carpeta), no se guarda el estudiante
     * ni el tutor.
     */
    /**
     * Registra o actualiza la Ficha de Inscripción preliminar (Fase 1)
     * sin requerir formalizar la matrícula ni descontar vacante de inmediato.
     */
    @Transactional
    public com.devrod.matriucla.dto.InscripcionResponseDTO registrarInscripcion(com.devrod.matriucla.dto.InscripcionRequestDTO requestDTO) {
        log.info("Registrando Ficha de Inscripción preliminar para DNI: {}", requestDTO.getNroDocEstudiante());

        // 1. Crear o actualizar Tutor
        Tutor tutor = tutorRepository.findByNroDoc(requestDTO.getNroDocTutor())
                .orElseGet(() -> tutorRepository.save(matriculaMapper.toTutor(requestDTO)));

        // 2. Crear o actualizar Estudiante
        Estudiante estudiante = estudianteRepository.findByNroDoc(requestDTO.getNroDocEstudiante())
                .orElseGet(() -> estudianteRepository.save(matriculaMapper.toEstudiante(requestDTO, tutor)));

        // 3. Crear o actualizar Carpeta Documentaria
        CarpetaDocumentaria carpeta = carpetaRepository.findByEstudianteIdEstudiante(estudiante.getIdEstudiante())
                .orElseGet(() -> matriculaMapper.toCarpetaDocumentaria(requestDTO, estudiante));

        carpeta.setTieneDniMenor(requestDTO.getTieneDniMenor());
        carpeta.setTieneDniTutor(requestDTO.getTieneDniTutor());
        carpeta.setTieneCartillaVacunas(requestDTO.getTieneCartillaVacunas());
        carpeta.setTieneTamizajeHemog(requestDTO.getTieneTamizajeHemog());
        carpeta.setTieneFichaSiagie(requestDTO.getTieneFichaSiagie());
        carpeta.setTieneCertifEstudios(requestDTO.getTieneCertifEstudios());
        carpeta.setTienePartidaNac(requestDTO.getTienePartidaNac());
        if (requestDTO.getDetalleObservacion() != null && !requestDTO.getDetalleObservacion().isBlank()) {
            carpeta.setDetalleObservacion(requestDTO.getDetalleObservacion());
        }

        boolean estaCompleto = Boolean.TRUE.equals(requestDTO.getTieneDniMenor()) &&
                               Boolean.TRUE.equals(requestDTO.getTieneDniTutor()) &&
                               Boolean.TRUE.equals(requestDTO.getTieneCartillaVacunas()) &&
                               Boolean.TRUE.equals(requestDTO.getTieneTamizajeHemog()) &&
                               Boolean.TRUE.equals(requestDTO.getTieneFichaSiagie()) &&
                               Boolean.TRUE.equals(requestDTO.getTieneCertifEstudios()) &&
                               Boolean.TRUE.equals(requestDTO.getTienePartidaNac());

        carpeta.setEstadoDoc(estaCompleto ? "COMPLETO" : "OBSERVADO");
        carpeta = carpetaRepository.save(carpeta);

        int anioLectivo = java.time.LocalDate.now().getYear();

        // 4. Buscar o crear Ficha de Inscripción para el año actual
        FichaInscripcion inscripcion = inscripcionRepository
                .findByEstudianteIdEstudianteAndAnioLectivo(estudiante.getIdEstudiante(), anioLectivo)
                .orElseGet(() -> {
                    FichaInscripcion nueva = new FichaInscripcion();
                    nueva.setCodInscripcion("INS-" + anioLectivo + "-" + estudiante.getNroDoc());
                    nueva.setAnioLectivo(anioLectivo);
                    nueva.setFechaInscripcion(java.time.LocalDateTime.now());
                    nueva.setEstadoInscripcion("REGISTRADA");
                    nueva.setEstudiante(estudiante);
                    return nueva;
                });

        if (requestDTO.getIdAula() != null) {
            aulaRepository.findById(requestDTO.getIdAula()).ifPresent(inscripcion::setAula);
        }

        inscripcion = inscripcionRepository.save(inscripcion);

        return com.devrod.matriucla.dto.InscripcionResponseDTO.builder()
                .codInscripcion(inscripcion.getCodInscripcion())
                .anioLectivo(inscripcion.getAnioLectivo())
                .fechaInscripcion(inscripcion.getFechaInscripcion())
                .estadoInscripcion(inscripcion.getEstadoInscripcion())
                .nroExpediente(carpeta.getNroExpediente())
                .estadoDoc(carpeta.getEstadoDoc())
                .dniEstudiante(estudiante.getNroDoc())
                .nombresEstudiante(estudiante.getNombres() + " " + estudiante.getApellidos())
                .idAula(inscripcion.getAula() != null ? inscripcion.getAula().getIdAula() : null)
                .nombreAula(inscripcion.getAula() != null ? "Inicial " + inscripcion.getAula().getGradoEdad() + " Años - " + inscripcion.getAula().getNombreSeccion() : null)
                .mensaje("Ficha de Inscripción guardada exitosamente con estado " + inscripcion.getEstadoInscripcion() + ". Expediente: " + carpeta.getEstadoDoc())
                .build();
    }

    /**
     * Orquesta el proceso de formalización de matrícula (Fase 2).
     * Asigna aula, descuenta vacante y emite la Ficha de Matrícula (PENDIENTE_PAGO).
     */
    @Transactional
    public MatriculaResponseDTO procesarMatricula(MatriculaRequestDTO requestDTO) {
        log.info("Iniciando proceso de matrícula para el estudiante DNI: {}", requestDTO.getNroDocEstudiante());

        int anioLectivo = java.time.LocalDate.now().getYear();

        // 1. Crear o buscar Tutor
        Tutor tutor = tutorRepository.findByNroDoc(requestDTO.getNroDocTutor())
                .orElseGet(() -> {
                    log.info("Tutor nuevo. Guardando en BD...");
                    return tutorRepository.save(matriculaMapper.toTutor(requestDTO));
                });

        // 2. Crear o buscar Estudiante vinculado al Tutor
        Estudiante estudiante = estudianteRepository.findByNroDoc(requestDTO.getNroDocEstudiante())
                .orElseGet(() -> {
                    log.info("Estudiante nuevo. Guardando en BD...");
                    return estudianteRepository.save(matriculaMapper.toEstudiante(requestDTO, tutor));
                });

        // Validar si el estudiante ya cuenta con matrícula en el año lectivo actual
        boolean yaTieneMatriculaActual = matriculaRepository.findByFichaInscripcionEstudianteNroDoc(estudiante.getNroDoc()).stream()
                .anyMatch(m -> m.getFichaInscripcion() != null &&
                               m.getFichaInscripcion().getAnioLectivo() != null &&
                               m.getFichaInscripcion().getAnioLectivo() == anioLectivo);

        if (yaTieneMatriculaActual) {
            throw new RuntimeException("El estudiante con documento " + estudiante.getNroDoc() +
                    " ya cuenta con una matrícula formalizada en el año lectivo " + anioLectivo);
        }

        // 3. Crear o actualizar Carpeta Documentaria vinculada al Estudiante
        log.info("Generando/Actualizando Carpeta Documentaria...");
        CarpetaDocumentaria carpeta = carpetaRepository.findByEstudianteIdEstudiante(estudiante.getIdEstudiante())
                .orElseGet(() -> matriculaMapper.toCarpetaDocumentaria(requestDTO, estudiante));

        carpeta.setTieneDniMenor(requestDTO.getTieneDniMenor());
        carpeta.setTieneDniTutor(requestDTO.getTieneDniTutor());
        carpeta.setTieneCartillaVacunas(requestDTO.getTieneCartillaVacunas());
        carpeta.setTieneTamizajeHemog(requestDTO.getTieneTamizajeHemog());
        carpeta.setTieneFichaSiagie(requestDTO.getTieneFichaSiagie());
        carpeta.setTieneCertifEstudios(requestDTO.getTieneCertifEstudios());
        carpeta.setTienePartidaNac(requestDTO.getTienePartidaNac());

        boolean estaCompleto = Boolean.TRUE.equals(requestDTO.getTieneDniMenor()) &&
                               Boolean.TRUE.equals(requestDTO.getTieneDniTutor()) &&
                               Boolean.TRUE.equals(requestDTO.getTieneCartillaVacunas()) &&
                               Boolean.TRUE.equals(requestDTO.getTieneTamizajeHemog()) &&
                               Boolean.TRUE.equals(requestDTO.getTieneFichaSiagie()) &&
                               Boolean.TRUE.equals(requestDTO.getTieneCertifEstudios()) &&
                               Boolean.TRUE.equals(requestDTO.getTienePartidaNac());

        carpeta.setEstadoDoc(estaCompleto ? "COMPLETO" : "OBSERVADO");
        carpetaRepository.save(carpeta);

        // 4. Buscar Aula y descontar vacante con bloqueo pesimista (ECU-CUS-01 Req. Especial 2)
        Aula aula = aulaRepository.findByIdWithLock(requestDTO.getIdAula())
                .orElseThrow(() -> new RuntimeException("Aula no encontrada con ID: " + requestDTO.getIdAula()));

        aula.descontarVacante();
        aulaRepository.save(aula);

        // 5. Reutilizar o crear Ficha de Inscripción vinculada al aula
        log.info("Generando/Vinculando Ficha de Inscripción...");
        FichaInscripcion inscripcion = inscripcionRepository
                .findByEstudianteIdEstudianteAndAnioLectivo(estudiante.getIdEstudiante(), anioLectivo)
                .orElseGet(() -> {
                    FichaInscripcion nueva = new FichaInscripcion();
                    nueva.setCodInscripcion("INS-" + anioLectivo + "-" + estudiante.getNroDoc());
                    nueva.setAnioLectivo(anioLectivo);
                    nueva.setFechaInscripcion(java.time.LocalDateTime.now());
                    nueva.setEstadoInscripcion("REGISTRADA");
                    nueva.setEstudiante(estudiante);
                    return nueva;
                });

        inscripcion.setAula(aula);
        inscripcion = inscripcionRepository.save(inscripcion);

        // 6. Crear Ficha de Matrícula (PENDIENTE_PAGO) con código correlativo (ECU-CUS-01 paso 12: Ej. MAT-2026-0125)
        log.info("Generando Ficha de Matrícula con correlativo institucional...");
        String prefijoMatricula = "MAT-" + anioLectivo + "-";
        String ultimoCodigo = matriculaRepository.findMaxCodMatriculaByPrefijo(prefijoMatricula).orElse(null);
        int nuevoCorrelativo = 1;
        if (ultimoCodigo != null && ultimoCodigo.startsWith(prefijoMatricula)) {
            try {
                String sub = ultimoCodigo.substring(prefijoMatricula.length());
                nuevoCorrelativo = Integer.parseInt(sub) + 1;
            } catch (NumberFormatException e) {
                nuevoCorrelativo = (int) (matriculaRepository.count() + 1);
            }
        } else {
            nuevoCorrelativo = (int) (matriculaRepository.count() + 1);
        }
        String codMatricula = String.format("%s%04d", prefijoMatricula, nuevoCorrelativo);

        FichaMatricula matricula = new FichaMatricula();
        matricula.setCodMatricula(codMatricula);
        matricula.setFechaFormalizacion(java.time.LocalDateTime.now());
        matricula.setMontoTotal(new java.math.BigDecimal("350.00")); // Tarifa oficial de matrícula
        matricula.setEstadoMatricula("PENDIENTE_PAGO");
        matricula.setFichaInscripcion(inscripcion);
        matricula.setAula(aula);
        matricula = matriculaRepository.save(matricula);

        log.info("¡Matrícula procesada correctamente para el estudiante {}! Código: {}",
                estudiante.getNombres(), matricula.getCodMatricula());
        return toResponseDTO(matricula);
    }

    /**
     * Obtiene la lista de todas las matrículas registradas en el sistema.
     */
    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> listarMatriculas() {
        return matriculaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca una matrícula por su código único.
     */
    @Transactional(readOnly = true)
    public MatriculaResponseDTO buscarPorCodigo(String codMatricula) {
        FichaMatricula matricula = matriculaRepository.findByCodMatricula(codMatricula)
                .orElseThrow(() -> new RuntimeException("No se encontró la matrícula con código: " + codMatricula));
        return toResponseDTO(matricula);
    }

    /**
     * Busca todas las matrículas de un estudiante por su DNI.
     */
    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> buscarPorDniEstudiante(String dni) {
        return matriculaRepository.findByFichaInscripcionEstudianteNroDoc(dni).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad FichaMatricula a su DTO de respuesta.
     */
    private MatriculaResponseDTO toResponseDTO(FichaMatricula matricula) {
        FichaInscripcion inscripcion = matricula.getFichaInscripcion();
        Estudiante estudiante = inscripcion.getEstudiante();
        Aula aula = matricula.getAula();

        CarpetaDocumentaria carpeta = carpetaRepository
                .findByEstudianteIdEstudiante(estudiante.getIdEstudiante())
                .orElse(null);

        return MatriculaResponseDTO.builder()
                .codMatricula(matricula.getCodMatricula())
                .fechaFormalizacion(matricula.getFechaFormalizacion())
                .montoTotal(matricula.getMontoTotal())
                .estadoMatricula(matricula.getEstadoMatricula())
                .dniEstudiante(estudiante.getNroDoc())
                .nombresEstudiante(estudiante.getNombres() + " " + estudiante.getApellidos())
                .gradoAula(aula.getGradoEdad())
                .seccionAula(aula.getNombreSeccion())
                .codInscripcion(inscripcion.getCodInscripcion())
                .estadoInscripcion(inscripcion.getEstadoInscripcion())
                .nroExpediente(carpeta != null ? carpeta.getNroExpediente() : "EXP-" + estudiante.getNroDoc())
                .estadoDoc(carpeta != null ? carpeta.getEstadoDoc() : "COMPLETO")
                .build();
    }
}

