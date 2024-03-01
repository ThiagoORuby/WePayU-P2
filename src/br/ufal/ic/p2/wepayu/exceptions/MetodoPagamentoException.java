package br.ufal.ic.p2.wepayu.exceptions;

public class MetodoPagamentoException extends Exception {

    public MetodoPagamentoException(){
        super("Empregado nao recebe em banco.");
    }
}
