package br.ufal.ic.p2.wepayu.exceptions;

public class AgendaIndisponivelException extends Exception {

    public AgendaIndisponivelException(){
        super("Agenda de pagamento nao esta disponivel");
    }
}
