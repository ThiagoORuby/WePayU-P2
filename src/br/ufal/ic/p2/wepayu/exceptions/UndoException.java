package br.ufal.ic.p2.wepayu.exceptions;

public class UndoException extends Exception {

    public UndoException(){
        super("Nao ha comando a desfazer.");
    }

}
