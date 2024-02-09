package br.ufal.ic.p2.wepayu.exceptions;

public class TipoEmpregadoInvalidoException extends Exception {

    public TipoEmpregadoInvalidoException(String tipo){
        super("Empregado nao eh " + tipo + ".");
    }
}
