package br.ufal.ic.p2.wepayu.exceptions;

public class ValorNegativoException extends Exception {

    public ValorNegativoException(String nome, String sufix) {
        super(nome + " deve ser nao-negativ" + sufix + ".");
    }
}
