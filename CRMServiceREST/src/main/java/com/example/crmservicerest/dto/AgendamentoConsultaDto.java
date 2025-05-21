package com.example.crmservicerest.dto;

import com.example.crmservicerest.domain.Especialidade;
import com.example.crmservicerest.exceptions.BusinessException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AgendamentoConsultaDto {

    private String crm;
    private Especialidade especialidade;
    private String data;
    private String cpfPaciente;

    public AgendamentoConsultaDto() {
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCpfPaciente() {
        return cpfPaciente;
    }

    public void setCpfPaciente(String cpfPaciente) {
        this.cpfPaciente = cpfPaciente;
    }

    public LocalDateTime getDataHoraFormatada() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return LocalDateTime.parse(this.data, formatter);
        } catch (DateTimeParseException e) {
            throw new BusinessException("Formato de data inválido. Use o formato dd/MM/yyyy HH:mm.");
        }
    }
}
