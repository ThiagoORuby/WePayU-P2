package br.ufal.ic.p2.wepayu.exceptions;

public class EmpregadoNaoExisteException extends Exception{
    public EmpregadoNaoExisteException(){
        super("Empregado nao existe.");
    }

    public EmpregadoNaoExisteException(String atributo) {
        super("Nao ha empregado com esse " + atributo + ".");
    }
}
