package com.devrod.matriucla.services;

import com.devrod.matriucla.dto.PagoRequestDTO;
import com.devrod.matriucla.dto.PagoResponseDTO;
import com.devrod.matriucla.entity.ComprobantePago;
import com.devrod.matriucla.entity.FichaMatricula;
import com.devrod.matriucla.repository.ComprobantePagoRepository;
import com.devrod.matriucla.repository.FichaInscripcionRepository;
import com.devrod.matriucla.repository.FichaMatriculaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

    private final FichaMatriculaRepository matriculaRepository;
    private final FichaInscripcionRepository inscripcionRepository;
    private final ComprobantePagoRepository comprobantePagoRepository;

    /**
     * Registra un pago transaccionalmente y actualiza el estado de la matrícula.
     */
    @Transactional
    public PagoResponseDTO registrarPago(PagoRequestDTO requestDTO) {
        log.info("Iniciando proceso de pago para la matrícula: {}", requestDTO.getCodMatricula());

        // 1. Buscar la Matrícula
        FichaMatricula matricula = matriculaRepository.findByCodMatricula(requestDTO.getCodMatricula())
                .orElseThrow(() -> new RuntimeException("No se encontró la matrícula con código: " + requestDTO.getCodMatricula()));

        // 2. Validar que no esté cancelada ya
        if ("CANCELADO".equalsIgnoreCase(matricula.getEstadoMatricula())) {
            throw new RuntimeException("La matrícula " + requestDTO.getCodMatricula() + " ya se encuentra CANCELADA / PAGADA.");
        }

        // 3. Validar montos exactos (No permitimos pagos parciales)
        if (requestDTO.getImportePagado().compareTo(matricula.getMontoTotal()) != 0) {
            throw new RuntimeException("El importe pagado (S/ " + requestDTO.getImportePagado() + 
                                       ") no coincide con el monto total de la matrícula (S/ " + matricula.getMontoTotal() + ")");
        }

        // 4. Generar número de comprobante correlativo institucional (ECU-CUS-02 paso 12: Ej. BOL-001-000458)
        String serie = "BOLETA".equalsIgnoreCase(requestDTO.getTipoComprobante()) ? "BOL-001-" : "REC-001-";
        String ultimoComprobante = comprobantePagoRepository.findMaxNroComprobanteByPrefijo(serie).orElse(null);
        int correlativo = 1;
        if (ultimoComprobante != null && ultimoComprobante.startsWith(serie)) {
            try {
                String sub = ultimoComprobante.substring(serie.length());
                correlativo = Integer.parseInt(sub) + 1;
            } catch (NumberFormatException e) {
                correlativo = (int) (comprobantePagoRepository.count() + 1);
            }
        } else {
            correlativo = (int) (comprobantePagoRepository.count() + 1);
        }
        String nroComprobante = String.format("%s%06d", serie, correlativo);

        // 5. Crear el Comprobante
        log.info("Generando comprobante de pago: {}", nroComprobante);
        ComprobantePago comprobante = new ComprobantePago();
        comprobante.setNroComprobante(nroComprobante);
        comprobante.setFechaEmision(LocalDateTime.now());
        comprobante.setTipoComprobante(requestDTO.getTipoComprobante().toUpperCase());
        comprobante.setMedioPago(requestDTO.getMedioPago().toUpperCase());
        comprobante.setNroOperacion(requestDTO.getNroOperacion());
        comprobante.setImportePagado(requestDTO.getImportePagado());
        comprobante.setFichaMatricula(matricula);
        
        comprobante = comprobantePagoRepository.save(comprobante);

        // 6. Actualizar el estado de la matrícula a CANCELADO y consolidar la inscripción
        log.info("Actualizando estado de la matrícula a CANCELADO...");
        matricula.setEstadoMatricula("CANCELADO");
        matriculaRepository.save(matricula);

        if (matricula.getFichaInscripcion() != null) {
            matricula.getFichaInscripcion().setEstadoInscripcion("CONSOLIDADA");
            inscripcionRepository.save(matricula.getFichaInscripcion());
            log.info("Ficha de Inscripción {} CONSOLIDADA con éxito tras el pago.", matricula.getFichaInscripcion().getCodInscripcion());
        }

        String dni = matricula.getFichaInscripcion().getEstudiante().getNroDoc();

        return PagoResponseDTO.builder()
                .nroComprobante(comprobante.getNroComprobante())
                .fechaEmision(comprobante.getFechaEmision())
                .tipoComprobante(comprobante.getTipoComprobante())
                .medioPago(comprobante.getMedioPago())
                .nroOperacion(comprobante.getNroOperacion())
                .importePagado(comprobante.getImportePagado())
                .codMatricula(matricula.getCodMatricula())
                .dniEstudiante(dni)
                .build();
    }

    /**
     * Obtiene la lista de todos los pagos registrados.
     */
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarPagos() {
        return comprobantePagoRepository.findAll().stream().map(comprobante -> {
            FichaMatricula matricula = comprobante.getFichaMatricula();
            String dni = matricula.getFichaInscripcion().getEstudiante().getNroDoc();
            
            return PagoResponseDTO.builder()
                    .nroComprobante(comprobante.getNroComprobante())
                    .fechaEmision(comprobante.getFechaEmision())
                    .tipoComprobante(comprobante.getTipoComprobante())
                    .medioPago(comprobante.getMedioPago())
                    .nroOperacion(comprobante.getNroOperacion())
                    .importePagado(comprobante.getImportePagado())
                    .codMatricula(matricula.getCodMatricula())
                    .dniEstudiante(dni)
                    .build();
        }).collect(Collectors.toList());
    }
}
