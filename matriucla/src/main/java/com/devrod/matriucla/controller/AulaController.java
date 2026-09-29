package com.devrod.matriucla.controller;

import com.devrod.matriucla.entity.Aula;
import com.devrod.matriucla.services.AulaService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/aulas")
@RequiredArgsConstructor
public class AulaController {

    private final AulaService aulaService;

    @GetMapping
    public ResponseEntity<List<Aula>> listarAulas(@RequestParam(required = false) Integer gradoEdad) {
        List<Aula> aulas = aulaService.listarAulas(gradoEdad);
        return ResponseEntity.ok(aulas);
    }
}
