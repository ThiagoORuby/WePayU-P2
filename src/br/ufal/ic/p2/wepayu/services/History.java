package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.LinkedHashMap;
import java.util.Stack;

public class History {

    private Stack<Memento> undoStack;
    private Stack<Memento> redoStack;

    public History(){
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    public void push(Empregado empregado){
        Memento m = new Memento(empregado);
        undoStack.push(m);
    }

    public void push(LinkedHashMap<String, Empregado> empregados){
        Memento m = new Memento(empregados);
        redoStack.push(m);
    }




}
