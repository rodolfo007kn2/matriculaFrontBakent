package com.devrod.matriucla.mapper;

import com.devrod.matriucla.dto.MatriculaRequestDTO;
import com.devrod.matriucla.entity.AlumnoContinuador;
import com.devrod.matriucla.entity.AlumnoNuevo;
import com.devrod.matriucla.entity.CarpetaDocumentaria;
import com.devrod.matriucla.entity.Estudiante;
import com.devrod.matriucla.entity.Tutor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class MatriculaMapper {

    /**
     * Extrae los datos del DTO y construye la entidad Tutor.
     */
    public Tutor toTutor(MatriculaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Tutor tutor = new Tutor();
        // Si el DTO no envía el tipo, le ponemos un valor por defecto
        tutor.setTipoDoc(dto.getTipoDocTutor() != null ? dto.getTipoDocTutor() : "DNI");
        tutor.setNroDoc(dto.getNroDocTutor());
        tutor.setNombres(dto.getNombresTutor());
        tutor.setApellidos(dto.getApellidosTutor());
        tutor.setParentesco(dto.getParentescoTutor());
        tutor.setCelular(dto.getCelularTutor());
        tutor.setDireccion(dto.getDireccionTutor());
        tutor.setCorreo(dto.getCorreoTutor());
        tutor.setCreatedAt(LocalDateTime.now());
        
        return tutor;
    }
    /**
     * Extrae los datos del DTO y construye la entidad Estudiante (Nuevo o Continuador).
     */
    public Estudiante toEstudiante(MatriculaRequestDTO dto, Tutor tutorGuardado) {
        if (dto == null) {
            return null;
        }

        Estudiante estudiante;
        
        if ("NUEVO".equalsIgnoreCase(dto.getTipoEstudiante())) {
            AlumnoNuevo nuevo = new AlumnoNuevo();
            nuevo.setNroPartidaNac(dto.getNroPartidaNac());
            nuevo.setColegioProcedencia(dto.getColegioProcedencia());
            nuevo.setTieneTraslado(dto.getTieneTraslado());
            estudiante = nuevo;
        } else if ("CONTINUADOR".equalsIgnoreCase(dto.getTipoEstudiante())) {
            AlumnoContinuador continuador = new AlumnoContinuador();
            continuador.setCodEstudiantePrevio(dto.getCodEstudiantePrevio());
            continuador.setAnioIngreso(dto.getAnioIngreso());
            continuador.setObservacionAcademica(dto.getObservacionAcademica());
            estudiante = continuador;
        } else {
            throw new IllegalArgumentException("Tipo de estudiante no válido: " + dto.getTipoEstudiante());
        }

        // Datos comunes del Padre (Estudiante)
        estudiante.setTipoDoc(dto.getTipoDocEstudiante() != null ? dto.getTipoDocEstudiante() : "DNI");
        estudiante.setNroDoc(dto.getNroDocEstudiante());
        estudiante.setNombres(dto.getNombresEstudiante());
        estudiante.setApellidos(dto.getApellidosEstudiante());
        estudiante.setFechaNacimiento(dto.getFechaNacimiento());
        estudiante.setSexo(dto.getSexo());
        estudiante.setTutor(tutorGuardado);
        estudiante.setCreatedAt(LocalDateTime.now());

        return estudiante;
    }
    /**
     * Extrae el checklist de documentos del DTO y construye la Carpeta Documentaria.
     */
    public CarpetaDocumentaria toCarpetaDocumentaria(MatriculaRequestDTO dto, Estudiante estudianteGuardado) {
        if (dto == null || estudianteGuardado == null) {
            return null;
        }

        CarpetaDocumentaria carpeta = new CarpetaDocumentaria();
        // Generar un nro de expediente (Ej: EXP-DNI)
        carpeta.setNroExpediente("EXP-" + estudianteGuardado.getNroDoc());
        carpeta.setFechaRegistro(LocalDate.now());

        // Checklist
        carpeta.setTieneDniMenor(dto.getTieneDniMenor());
        carpeta.setTieneDniTutor(dto.getTieneDniTutor());
        carpeta.setTieneCartillaVacunas(dto.getTieneCartillaVacunas());
        carpeta.setTieneTamizajeHemog(dto.getTieneTamizajeHemog());
        carpeta.setTieneFichaSiagie(dto.getTieneFichaSiagie());
        carpeta.setTieneCertifEstudios(dto.getTieneCertifEstudios());
        carpeta.setTienePartidaNac(dto.getTienePartidaNac());

        // Lógica básica de estado documental
        boolean estaCompleto = Boolean.TRUE.equals(dto.getTieneDniMenor()) &&
                               Boolean.TRUE.equals(dto.getTieneDniTutor()) &&
                               Boolean.TRUE.equals(dto.getTieneCartillaVacunas()) &&
                               Boolean.TRUE.equals(dto.getTieneTamizajeHemog()) &&
                               Boolean.TRUE.equals(dto.getTieneFichaSiagie()) &&
                               Boolean.TRUE.equals(dto.getTieneCertifEstudios()) &&
                               Boolean.TRUE.equals(dto.getTienePartidaNac());

        if (estaCompleto) {
            carpeta.setEstadoDoc("COMPLETO");
            carpeta.setDetalleObservacion("Todos los documentos entregados.");
        } else {
            carpeta.setEstadoDoc("OBSERVADO");
            carpeta.setDetalleObservacion("Faltan documentos en el expediente.");
        }

        carpeta.setEstudiante(estudianteGuardado);

        return carpeta;
    }

    public Tutor toTutor(com.devrod.matriucla.dto.InscripcionRequestDTO dto) {
        if (dto == null) return null;
        Tutor tutor = new Tutor();
        tutor.setTipoDoc(dto.getTipoDocTutor() != null ? dto.getTipoDocTutor() : "DNI");
        tutor.setNroDoc(dto.getNroDocTutor());
        tutor.setNombres(dto.getNombresTutor());
        tutor.setApellidos(dto.getApellidosTutor());
        tutor.setParentesco(dto.getParentescoTutor());
        tutor.setCelular(dto.getCelularTutor());
        tutor.setDireccion(dto.getDireccionTutor());
        tutor.setCorreo(dto.getCorreoTutor());
        tutor.setCreatedAt(LocalDateTime.now());
        return tutor;
    }

    public Estudiante toEstudiante(com.devrod.matriucla.dto.InscripcionRequestDTO dto, Tutor tutorGuardado) {
        if (dto == null) return null;
        AlumnoNuevo nuevo = new AlumnoNuevo();
        nuevo.setNroPartidaNac(dto.getNroPartidaNac());
        nuevo.setColegioProcedencia(dto.getColegioProcedencia());
        nuevo.setTieneTraslado(dto.getTieneTraslado());
        nuevo.setTipoDoc(dto.getTipoDocEstudiante() != null ? dto.getTipoDocEstudiante() : "DNI");
        nuevo.setNroDoc(dto.getNroDocEstudiante());
        nuevo.setNombres(dto.getNombresEstudiante());
        nuevo.setApellidos(dto.getApellidosEstudiante());
        nuevo.setFechaNacimiento(dto.getFechaNacimiento());
        nuevo.setSexo(dto.getSexo());
        nuevo.setTutor(tutorGuardado);
        nuevo.setCreatedAt(LocalDateTime.now());
        return nuevo;
    }

    public CarpetaDocumentaria toCarpetaDocumentaria(com.devrod.matriucla.dto.InscripcionRequestDTO dto, Estudiante estudianteGuardado) {
        if (dto == null || estudianteGuardado == null) return null;
        CarpetaDocumentaria carpeta = new CarpetaDocumentaria();
        carpeta.setNroExpediente("EXP-" + estudianteGuardado.getNroDoc());
        carpeta.setFechaRegistro(LocalDate.now());
        carpeta.setTieneDniMenor(dto.getTieneDniMenor());
        carpeta.setTieneDniTutor(dto.getTieneDniTutor());
        carpeta.setTieneCartillaVacunas(dto.getTieneCartillaVacunas());
        carpeta.setTieneTamizajeHemog(dto.getTieneTamizajeHemog());
        carpeta.setTieneFichaSiagie(dto.getTieneFichaSiagie());
        carpeta.setTieneCertifEstudios(dto.getTieneCertifEstudios());
        carpeta.setTienePartidaNac(dto.getTienePartidaNac());

        boolean estaCompleto = Boolean.TRUE.equals(dto.getTieneDniMenor()) &&
                               Boolean.TRUE.equals(dto.getTieneDniTutor()) &&
                               Boolean.TRUE.equals(dto.getTieneCartillaVacunas()) &&
                               Boolean.TRUE.equals(dto.getTieneTamizajeHemog()) &&
                               Boolean.TRUE.equals(dto.getTieneFichaSiagie()) &&
                               Boolean.TRUE.equals(dto.getTieneCertifEstudios()) &&
                               Boolean.TRUE.equals(dto.getTienePartidaNac());

        if (estaCompleto) {
            carpeta.setEstadoDoc("COMPLETO");
            carpeta.setDetalleObservacion(dto.getDetalleObservacion() != null ? dto.getDetalleObservacion() : "Todos los documentos entregados.");
        } else {
            carpeta.setEstadoDoc("OBSERVADO");
            carpeta.setDetalleObservacion(dto.getDetalleObservacion() != null ? dto.getDetalleObservacion() : "Faltan documentos en el expediente.");
        }
        carpeta.setEstudiante(estudianteGuardado);
        return carpeta;
    }
}
