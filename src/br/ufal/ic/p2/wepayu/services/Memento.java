package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.LinkedHashMap;

public class Memento {

    private LinkedHashMap<String, Empregado> empregadosSnapshot;
    private Empregado empregadoSnapshot;


    public Memento(LinkedHashMap<String, Empregado> empregados){
        empregadosSnapshot = new LinkedHashMap<>();

        empregados.forEach((id, emp) -> {
            empregadosSnapshot.put(id, emp.clone());
        });
    }

    public Memento(Empregado empregado){
        empregadoSnapshot = empregado.clone();
    }

    public LinkedHashMap<String, Empregado> getEmpregadosSnapshot() {
        return empregadosSnapshot;
    }

    public Empregado getEmpregadoSnapshot() {
        return empregadoSnapshot;
    }
}
