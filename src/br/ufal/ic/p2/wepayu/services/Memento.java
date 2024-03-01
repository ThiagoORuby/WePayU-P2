package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoAssalariado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;

import java.util.LinkedHashMap;

public class Memento {

    private LinkedHashMap<String, Empregado> empregadosSnapshot = null;

    public Memento(LinkedHashMap<String, Empregado> empregados){
        empregadosSnapshot = new LinkedHashMap<>();

        empregados.forEach((id, emp) -> {
            empregadosSnapshot.put(id, emp.clone());
        });
    }

    public LinkedHashMap<String, Empregado> getEmpregadosSnapshot() {
        return empregadosSnapshot;
    }
}
