package com.example.crmservicerest.domain;

public class Paciente {

    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private String cpf;

    private Endereco endereco;

    private boolean ativo = true;

    public Paciente() {
    }

    public Paciente(String nome,
                    String email,
                    String telefone,
                    String cpf,
                    Endereco endereco) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.endereco = endereco;
        this.ativo = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public void desativar() {
        this.ativo = false;
    }

}
