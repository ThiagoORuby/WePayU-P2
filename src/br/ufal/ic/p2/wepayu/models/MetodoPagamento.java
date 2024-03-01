package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;

public class MetodoPagamento implements Serializable, Cloneable {

    private String tipo;
    private String banco;
    private String contaCorrente;
    private String agencia;

    public MetodoPagamento(){}
    public MetodoPagamento(String tipo) {
        setTipo(tipo);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    @Override
    public MetodoPagamento clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (MetodoPagamento) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
