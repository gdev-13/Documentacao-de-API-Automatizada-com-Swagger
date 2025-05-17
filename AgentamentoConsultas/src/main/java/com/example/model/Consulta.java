package com.example.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Modelo de uma consulta médica agendada")
public class Consulta {
    @Schema(description = "ID da consulta", example = "1")
    private Long id;
    
    @Schema(description = "Nome do paciente", example = "João Silva", required = true)
    private String paciente;
    
    @Schema(description = "Especialidade médica", example = "Cardiologia")
    private String especialidade;
    
    @Schema(description = "Data e hora da consulta", example = "2025-12-15T14:30:00")
    private LocalDateTime dataHora;
    
    @Schema(description = "Status da consulta", example = "AGENDADA")
    private String status = "AGENDADA";
}