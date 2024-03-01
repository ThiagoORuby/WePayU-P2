package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;

public class ResultadoDeVenda implements Serializable, Cloneable {

    private String data;
    private Double valor;

    public ResultadoDeVenda(){}
    public ResultadoDeVenda(String data, Double valor) {
        setData(data);
        setValor(valor);
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public ResultadoDeVenda clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (ResultadoDeVenda) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
