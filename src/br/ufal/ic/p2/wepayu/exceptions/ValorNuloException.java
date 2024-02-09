package br.ufal.ic.p2.wepayu.exceptions;

public class ValorNuloException extends Exception{

    public ValorNuloException(String nome, String sufix) {
        super(nome + " nao pode ser nul" + sufix + ".");
    }
}
