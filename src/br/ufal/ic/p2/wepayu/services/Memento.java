package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.LinkedHashMap;

/**
 * Representa um estado dos dados do sistema
 * @author thomruby
 */
public class Memento {

    private LinkedHashMap<String, Empregado> empregadosSnapshot;

    /**
     * Cria um estado clonando os dados dos empregados
     * @param empregados LinkedHashMap de {@link Empregado}
     */
    public Memento(LinkedHashMap<String, Empregado> empregados){
        empregadosSnapshot = new LinkedHashMap<>();

        empregados.forEach((id, emp) -> {
            empregadosSnapshot.put(id, emp.clone());
        });
    }

    /**
     * Retorna o estado dos dados guardado
     * @return LinkedHashMap de {@link Empregado}
     */
    public LinkedHashMap<String, Empregado> getEmpregadosSnapshot() {
        return empregadosSnapshot;
    }
}
