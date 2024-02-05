package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.util.UUID;

public class MembroSindicato implements Serializable {

    private String idMembro;
    private Double taxaSindical;

    public MembroSindicato(){}
    public MembroSindicato(String idMembro, Double taxaSindical) {
        setIdMembro(UUID.randomUUID().toString());
        setTaxaSindical(taxaSindical);
    }

    public String getIdMembro() {
        return idMembro;
    }

    public void setIdMembro(String idMembro) {
        this.idMembro = idMembro;
    }

    public Double getTaxaSindical() {
        return taxaSindical;
    }

    public void setTaxaSindical(Double taxaSindical) {
        this.taxaSindical = taxaSindical;
    }
}
