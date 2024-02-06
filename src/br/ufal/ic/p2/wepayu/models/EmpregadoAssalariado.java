package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;

import java.io.Serializable;

public class EmpregadoAssalariado extends Empregado implements Serializable {

    private Double salarioMensal;

    public EmpregadoAssalariado(){
        super();
    }
    public EmpregadoAssalariado(String nome, String endereco, String tipo, Double salarioMensal) throws Exception {
        super(nome, endereco, tipo);
        setSalarioMensal(salarioMensal);
    }

    public Double getSalarioMensal() {
        return salarioMensal;
    }

    public void setSalarioMensal(Double salarioMensal) {
        this.salarioMensal = salarioMensal;
    }
}
