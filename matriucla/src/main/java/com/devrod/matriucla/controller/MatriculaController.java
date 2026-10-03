package com.devrod.matriucla.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.devrod.matriucla.dto.MatriculaRequestDTO;
import com.devrod.matriucla.dto.MatriculaResponseDTO;
import com.devrod.matriucla.services.MatriculaService;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;

    @PostMapping
    public ResponseEntity<MatriculaResponseDTO> procesarMatricula(@Valid @RequestBody MatriculaRequestDTO requestDTO) {
        MatriculaResponseDTO resultado = matriculaService.procesarMatricula(requestDTO);
        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    @PostMapping("/inscripcion")
    public ResponseEntity<com.devrod.matriucla.dto.InscripcionResponseDTO> registrarInscripcion(
            @Valid @RequestBody com.devrod.matriucla.dto.InscripcionRequestDTO requestDTO) {
        com.devrod.matriucla.dto.InscripcionResponseDTO resultado = matriculaService.registrarInscripcion(requestDTO);
        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MatriculaResponseDTO>> listarMatriculas() {
        List<MatriculaResponseDTO> lista = matriculaService.listarMatriculas();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarMatricula(
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String dni) {

        if (codigo != null && !codigo.trim().isEmpty()) {
            MatriculaResponseDTO dto = matriculaService.buscarPorCodigo(codigo.trim());
            return ResponseEntity.ok(dto);
        }

        if (dni != null && !dni.trim().isEmpty()) {
            List<MatriculaResponseDTO> dtos = matriculaService.buscarPorDniEstudiante(dni.trim());
            return ResponseEntity.ok(dtos);
        }

        return ResponseEntity.badRequest().body("Debe proporcionar un código de matrícula o un DNI de estudiante.");
    }
}
