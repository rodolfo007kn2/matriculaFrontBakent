package com.devrod.matriucla.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devrod.matriucla.dto.EstudianteConsultaDTO;
import com.devrod.matriucla.dto.EstudianteListadoDTO;
import com.devrod.matriucla.services.EstudianteService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;

    @GetMapping("/consultar/{nroDoc}")
    public ResponseEntity<EstudianteConsultaDTO> consultarEstudiante(@PathVariable String nroDoc) {
        EstudianteConsultaDTO dto = estudianteService.consultarPorNroDoc(nroDoc);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<EstudianteListadoDTO>> listarEstudiantes() {
        List<EstudianteListadoDTO> lista = estudianteService.listarEstudiantes();
        return ResponseEntity.ok(lista);
    }
}
