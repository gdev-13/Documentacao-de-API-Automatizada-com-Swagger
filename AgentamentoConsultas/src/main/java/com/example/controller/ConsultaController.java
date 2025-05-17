package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Consulta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/consultas")
@Tag(name = "Consultas Médicas", description = "Agendamento e gerenciamento de consultas")
public class ConsultaController {

    private final List<Consulta> consultas = new ArrayList<>();
    private Long nextId = 1L;

    @Operation(
        summary = "Listar todas as consultas",
        description = "Retorna todas as consultas agendadas, filtradas por status se fornecido"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de consultas retornada com sucesso"),
        @ApiResponse(responseCode = "204", description = "Nenhuma consulta encontrada")
    })
    @GetMapping
    public ResponseEntity<List<Consulta>> listar(
        @Parameter(description = "Filtrar por status (AGENDADA, CANCELADA, REALIZADA)", example = "AGENDADA")
        @RequestParam(required = false) String status) {
        
        List<Consulta> resultado = status != null ?
            consultas.stream().filter(c -> c.getStatus().equals(status)).toList() :
            consultas;
        
        return resultado.isEmpty() ?
            ResponseEntity.noContent().build() :
            ResponseEntity.ok(resultado);
    }

    @Operation(
        summary = "Agendar nova consulta",
        description = "Cria um novo agendamento de consulta médica"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Consulta agendada com sucesso",
            content = @Content(schema = @Schema(implementation = Consulta.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<Consulta> agendar(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados da consulta a ser agendada",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    value = "{\"paciente\": \"Maria Oliveira\", \"especialidade\": \"Dermatologia\", \"dataHora\": \"2025-11-20T10:00:00\"}"
                )
            )
        )
        @RequestBody Consulta consulta) {
        
        // Validação simples
        if (consulta.getPaciente() == null || consulta.getDataHora() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        consulta.setId(nextId++);
        consulta.setStatus("AGENDADA");
        consultas.add(consulta);
        return ResponseEntity.status(201).body(consulta);
    }

    @Operation(
        summary = "Cancelar consulta",
        description = "Atualiza o status de uma consulta para CANCELADA"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta cancelada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(
        @Parameter(description = "ID da consulta a ser cancelada", example = "1")
        @PathVariable Long id) {
        
        Optional<Consulta> consulta = consultas.stream()
            .filter(c -> c.getId().equals(id))
            .findFirst();
        
        if (consulta.isPresent()) {
            consulta.get().setStatus("CANCELADA");
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}