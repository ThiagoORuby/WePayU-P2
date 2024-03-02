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

    /**
     * Cria uma cópia do Resultado de Venda
     * @return {@link ResultadoDeVenda} cópia
     */
    @Override
    public ResultadoDeVenda clone() {
        try {
            return (ResultadoDeVenda) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
