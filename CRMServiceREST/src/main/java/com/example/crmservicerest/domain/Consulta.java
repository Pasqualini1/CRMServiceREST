package com.example.crmservicerest.domain;

import java.time.LocalDateTime;

public class Consulta {
    private Long id;
    private Medico medico;
    private Paciente paciente;
    private LocalDateTime data;
    private boolean ativa = true;
    private String motivoCancelamento;

    public Consulta(Medico medico, Paciente paciente, LocalDateTime data) {
        this.medico = medico;
        this.paciente = paciente;
        this.data = data;
        this.ativa = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Medico getMedico() {
        return medico;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public LocalDateTime getData() {
        return data;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public boolean isCancelada() {
        return !ativa;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void cancelar(String motivo) {
        this.ativa = false;
        this.motivoCancelamento = motivo;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
        this.ativa = false;
    }
}
