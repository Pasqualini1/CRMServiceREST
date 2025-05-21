package com.example.crmservicerest.dto;

import com.example.crmservicerest.domain.Endereco;
import com.example.crmservicerest.domain.Especialidade;

public class DetalhamentoMedicoDto {

    private String nome;
    private String email;
    private String telefone;
    private String crm;
    private Especialidade especialidade;
    private Endereco endereco;

    public DetalhamentoMedicoDto(String nome, String email, String telefone, String crm,
                                 Especialidade especialidade, Endereco endereco) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.crm = crm;
        this.especialidade = especialidade;
        this.endereco = endereco;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getCrm() { return crm; }
    public Especialidade getEspecialidade() { return especialidade; }
    public Endereco getEndereco() { return endereco; }
}
