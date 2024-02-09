package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.services.Utils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MembroSindicato implements Serializable {

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        LocalDate dInicial = null;
        LocalDate dFinal = null;

        try {dInicial = LocalDate.parse(Utils.validarData(dataInicial), formatter);}
        catch (Exception e) {throw new Exception("Data inicial invalida.");}

        try{dFinal = LocalDate.parse(Utils.validarData(dataFinal), formatter);}
        catch (Exception e) {throw new Exception("Data final invalida.");}

        if(dInicial.isAfter(dFinal)) throw new Exception("Data inicial nao pode ser posterior aa data final.");

        if(dInicial.isEqual(dFinal)) return 0d;

        Double valorTotal = 0d;
        for(TaxaServico taxa: taxas){
            LocalDate data = LocalDate.parse(taxa.getData(), formatter);
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

    public Double getAndClearTaxasExtras() {
        Double valorTotal = 0d;
        for(TaxaServico taxa: taxasExtras){
                valorTotal += taxa.getValor();
        }
        taxasExtras.clear();
        return valorTotal;
    }

}
