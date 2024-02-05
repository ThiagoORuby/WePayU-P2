package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;

import java.io.Serializable;

public class EmpregadoComissionado extends Empregado {

    private Double salarioMensal;
    private Double taxaDeComissao;

    public EmpregadoComissionado(){
        super();
    }
    public EmpregadoComissionado(String nome, String endereco, String tipo, Double salarioMensal, Double taxaDeComissao) throws Exception {
        super(nome, endereco, tipo, null);
        setSalarioMensal(salarioMensal);
        setTaxaDeComissao(taxaDeComissao);
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
}
