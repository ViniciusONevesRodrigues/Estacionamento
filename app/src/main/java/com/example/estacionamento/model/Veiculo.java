package com.example.estacionamento.model;

import java.io.Serializable;

public class Veiculo implements Serializable {
    private int id_veiculo;
    private String placa;
    private int ano;
    private double mensalidade;
    private int fk_proprietario;

    public int getId_veiculo() {
        return id_veiculo;
    }

    public void setId_veiculo(int id_veiculo) {
        this.id_veiculo = id_veiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public double getMensalidade() {
        return mensalidade;
    }

    public void setMensalidade(double mensalidade) {
        this.mensalidade = mensalidade;
    }

    public int getFk_proprietario() {
        return fk_proprietario;
    }

    public void setFk_proprietario(int fk_proprietario) {
        this.fk_proprietario = fk_proprietario;
    }
}
