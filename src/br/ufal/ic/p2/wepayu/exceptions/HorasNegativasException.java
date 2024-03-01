package br.ufal.ic.p2.wepayu.exceptions;

public class HorasNegativasException extends Exception {

    public HorasNegativasException(){
        super("Horas devem ser positivas.");
    }
}
