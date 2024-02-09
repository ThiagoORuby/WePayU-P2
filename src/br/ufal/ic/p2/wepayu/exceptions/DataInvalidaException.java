package br.ufal.ic.p2.wepayu.exceptions;

public class DataInvalidaException extends Exception{
    public DataInvalidaException(String nome){
        super("Data " + nome + " invalida.");
    }

    public DataInvalidaException(){
        super("Data invalida.");
    }
}
