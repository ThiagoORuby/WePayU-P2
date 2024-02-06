package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;

public class TaxaServico implements Serializable {

    private String data;
    private Double valor;

    public TaxaServico(){}
    public TaxaServico(String data, Double valor) {
        setData(data);
        setValor(valor);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
