package com.example.crmservicerest.domain;

public class Endereco {

    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    public Endereco() {
    }

    public Endereco(String rua,
                    String numero,
                    String complemento,
                    String bairro,
                    String cidade,
                    String estado,
                    String cep) {
        this.rua = rua;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    public void atualizar(String rua,
                          String numero,
                          String complemento,
                          String bairro,
                          String cidade,
                          String estado,
                          String cep) {
        if (rua != null) this.rua = rua;
        if (numero != null) this.numero = numero;
        if (complemento != null) this.complemento = complemento;
        if (bairro != null) this.bairro = bairro;
        if (cidade != null) this.cidade = cidade;
        if (estado != null) this.estado = estado;
        if (cep != null) this.cep = cep;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}
