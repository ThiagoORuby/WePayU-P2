package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.exceptions.RedoException;
import br.ufal.ic.p2.wepayu.exceptions.UndoException;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.LinkedHashMap;
import java.util.Stack;

/**
 * Histórico de armazenamento de estados dos dados do sistema
 * @author thomruby
 */
public class History {

    private Stack<Memento> undoStack;
    private Stack<Memento> redoStack;
    private LinkedHashMap<String, Empregado> currentEmpregados;

    /**
     * Cria um histórico e armazena a referência para o estado atual dos dados
     * @param empregados LinkedHashMap de {@link Empregado}
     */
    public History(LinkedHashMap<String, Empregado> empregados){
        this.currentEmpregados = empregados;
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    /**
     * Adiciona um estado {@link Memento} a pilha de Undo
     */
    public void push(){
        Memento m = new Memento(currentEmpregados);
        undoStack.push(m);

    }

    /**
     * Retorna um estado anterior, se houver
     * @return {@link Memento} que representa um estado anterior dos dados
     * @throws UndoException se não houver mais estados anteriores
     */
    public Memento getUndo() throws UndoException {
        if(undoStack.size() <= 1){
            throw new UndoException();
        }
        else {
            redoStack.push(undoStack.pop());
            return undoStack.peek();
        }
    }

    /**
     * Retorna um estado posterior, se houver
     * @return {@link Memento} que representa um estado posterior dos dados
     * @throws RedoException se não houver mais estados posteriores
     */
    public Memento getRedo() throws RedoException {
        if(!redoStack.isEmpty()){
            undoStack.push(redoStack.pop());
            return undoStack.peek();
        }
        else{
            throw new RedoException();
        }
    }


}
