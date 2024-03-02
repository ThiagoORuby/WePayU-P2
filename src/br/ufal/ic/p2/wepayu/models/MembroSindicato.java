package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exceptions.DataInicialPosteriorException;
import br.ufal.ic.p2.wepayu.services.Settings;
import br.ufal.ic.p2.wepayu.services.Utils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MembroSindicato implements Serializable, Cloneable {

    private String idMembro;
    private Double taxaSindical;
    private List<TaxaServico> taxas;
    private HashSet<TaxaServico> taxasExtras;

    public MembroSindicato(){}
    public MembroSindicato(String idMembro, Double taxaSindical) {
        setIdMembro(idMembro);
        setTaxaSindical(taxaSindical);
        this.taxas = new ArrayList<>();
        this.taxasExtras = new HashSet<>();
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

    public List<TaxaServico> getTaxas() {
        return taxas;
    }

    public void setTaxas(List<TaxaServico> taxas) {
        this.taxas = taxas;
    }

    public void setTaxaServico(TaxaServico taxa){
        this.taxas.add(taxa);
    }

    public HashSet<TaxaServico> getTaxasExtras() {
        return taxasExtras;
    }

    public void setTaxasExtras(HashSet<TaxaServico> taxasExtras) {
        this.taxasExtras = taxasExtras;
    }

    public void setTaxaExtra(TaxaServico taxaExtra){
        this.taxasExtras.add(taxaExtra);
    }

    public Double getTaxasServico(String dataInicial, String dataFinal) throws Exception{
        Double valorTotal = 0d;

        LocalDate dInicial = Utils.formatarData(dataInicial, "inicial");
        LocalDate dFinal = Utils.formatarData(dataFinal, "final");

        if(dInicial.isAfter(dFinal))
            throw new DataInicialPosteriorException();

        if(dInicial.isEqual(dFinal))
            return valorTotal;

        for(TaxaServico taxa: taxas){
            LocalDate data = LocalDate.parse(taxa.getData(), Settings.formatter);
            if(data.isEqual(dInicial))
            {
                valorTotal += taxa.getValor();
            }
            else{
                if(data.isAfter(dInicial) && data.isBefore(dFinal)) {
                    valorTotal += taxa.getValor();
                }
            }
        }

        return valorTotal;
    }

    public Double getTotalTaxasExtras() {
        Double valorTotal = 0d;
        for(TaxaServico taxa: taxasExtras){
                valorTotal += taxa.getValor();
        }
        return valorTotal;
    }

    public void clearTaxasExtras(){
        taxasExtras.clear();
    }

    @Override
    public MembroSindicato clone() {
        try {
            MembroSindicato clone = (MembroSindicato) super.clone();

            clone.taxas = new ArrayList<>();
            clone.taxasExtras = new HashSet<>();

            for(TaxaServico taxa: taxas){
                clone.taxas.add(taxa.clone());
            }

            for(TaxaServico taxa: taxasExtras){
                clone.taxasExtras.add(taxa.clone());
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
