package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CartaoDePonto implements Serializable, Cloneable {

    private String data;
    private String hora;

    public CartaoDePonto(){}
    public CartaoDePonto(String data, String hora) {
        setData(data);
        setHora(hora);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    @Override
    public CartaoDePonto clone() {
        try {
            return (CartaoDePonto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
