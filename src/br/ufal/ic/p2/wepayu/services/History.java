package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.LinkedHashMap;
import java.util.Stack;

public class History {

    private Stack<Memento> undoStack;
    private Stack<Memento> redoStack;
    private LinkedHashMap<String, Empregado> currentEmpregados;

    public History(LinkedHashMap<String, Empregado> empregados){
        this.currentEmpregados = empregados;
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    public void push(){
        Memento m = new Memento(currentEmpregados);
        undoStack.push(m);

    }

    public Memento getUndo() throws Exception{
        if(undoStack.size() <= 1){
            throw new Exception("Nao ha comando a desfazer.");
        }
        else {
            redoStack.push(undoStack.pop());
            return undoStack.peek();
        }
    }

    public Memento getRedo() throws Exception{
        if(!redoStack.isEmpty()){
            undoStack.push(redoStack.pop());
            return undoStack.peek();
        }
        else{
            throw new Exception("Nao ha comando a refazer.");
        }
    }


}
