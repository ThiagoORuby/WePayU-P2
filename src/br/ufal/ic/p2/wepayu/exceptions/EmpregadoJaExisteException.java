package br.ufal.ic.p2.wepayu.exceptions;

public class EmpregadoJaExisteException extends Exception{

    public EmpregadoJaExisteException(){
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}
