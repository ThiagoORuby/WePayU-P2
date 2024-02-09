package br.ufal.ic.p2.wepayu.exceptions;

public class ValorNaoNumericoException extends Exception{

    public ValorNaoNumericoException(String nome, String sufix) {
        super(nome + " deve ser numeric" + sufix + ".");
    }
}
