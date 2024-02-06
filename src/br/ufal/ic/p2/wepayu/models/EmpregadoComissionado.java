package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Utils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmpregadoComissionado extends Empregado {

    private Double salarioMensal;
    private Double taxaDeComissao;

    private List<ResultadoDeVenda> vendas;

    public EmpregadoComissionado(){
        super();
    }
    public EmpregadoComissionado(String nome, String endereco, String tipo, Double salarioMensal, Double taxaDeComissao) throws Exception {
        super(nome, endereco, tipo);
        setSalarioMensal(salarioMensal);
        setTaxaDeComissao(taxaDeComissao);
        this.vendas = new ArrayList<>();
    }

    public Double getSalarioMensal() {
        return salarioMensal;
    }

    public void setSalarioMensal(Double salarioMensal) {
        this.salarioMensal = salarioMensal;
    }

    public Double getTaxaDeComissao() {
        return taxaDeComissao;
    }

    public void setTaxaDeComissao(Double taxaDeComissao) {
        this.taxaDeComissao = taxaDeComissao;
    }

    public List<ResultadoDeVenda> getVendas() {
        return vendas;
    }
    public void setVendas(List<ResultadoDeVenda> vendas) {
        this.vendas = vendas;
    }

    public void setVenda(ResultadoDeVenda venda){
        this.vendas.add(venda);
    }

    public Double getVendasRealizadas(String dataInicial, String dataFinal) throws Exception
    {
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

        for(ResultadoDeVenda venda: vendas)
        {
            LocalDate data = LocalDate.parse(venda.getData(), formatter);
            if(data.isEqual(dInicial))
            {
                valorTotal += venda.getValor();
            }
            else{
                if(data.isAfter(dInicial) && data.isBefore(dFinal)) {
                    valorTotal += venda.getValor();
                }
            }
        }

        return valorTotal;
    }


}
