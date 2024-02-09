package br.ufal.ic.p2.wepayu.exceptions;

public class ValorNaoBooleanoException extends Exception{

    public ValorNaoBooleanoException(String nome) {
        super(nome + " deve ser true ou false.");
    }
}
