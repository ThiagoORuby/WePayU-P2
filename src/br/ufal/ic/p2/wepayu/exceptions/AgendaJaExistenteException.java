package br.ufal.ic.p2.wepayu.exceptions;

public class AgendaJaExistenteException extends Exception {

    public AgendaJaExistenteException(){
        super("Agenda de pagamentos ja existe");
    }
}
