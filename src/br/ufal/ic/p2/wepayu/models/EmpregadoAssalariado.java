package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.services.Utils;

import java.io.Serializable;

public class EmpregadoAssalariado extends Empregado implements Serializable {

    public EmpregadoAssalariado(){}
    public EmpregadoAssalariado(String nome, String endereco, String tipo, Double salario) throws Exception {
        super(nome, endereco, tipo, salario);
    }

    public Double getSalarioBruto(String data){

        if(Utils.ehUltimoDiaUtilMes(data)){
            return getSalario();
        }
        return 0d;
    }

}
