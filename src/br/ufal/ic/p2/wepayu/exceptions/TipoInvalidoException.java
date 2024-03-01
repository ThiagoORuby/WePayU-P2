package br.ufal.ic.p2.wepayu.exceptions;

public class TipoInvalidoException extends Exception {

    public TipoInvalidoException(){
        super("Tipo invalido.");
    }

    public TipoInvalidoException(String nome){
        super(nome + " invalido.");
    }
}
