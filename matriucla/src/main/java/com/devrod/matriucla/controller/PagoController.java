package com.devrod.matriucla.controller;

import com.devrod.matriucla.dto.PagoRequestDTO;
import com.devrod.matriucla.dto.PagoResponseDTO;
import com.devrod.matriucla.services.PagoService;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponseDTO> registrarPago(@Valid @RequestBody PagoRequestDTO requestDTO) {
        PagoResponseDTO resultado = pagoService.registrarPago(requestDTO);
        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listarPagos() {
        List<PagoResponseDTO> lista = pagoService.listarPagos();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }
}
