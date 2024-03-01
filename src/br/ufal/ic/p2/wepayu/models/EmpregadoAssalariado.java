package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.services.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EmpregadoAssalariado extends Empregado {

    public EmpregadoAssalariado(){}
    public EmpregadoAssalariado(String nome, String endereco, String tipo, Double salario) throws Exception {
        super(nome, endereco, tipo, salario);
    }

    public Double getDescontos(String dataInicial, String dataFinal) throws Exception{
        Double total = 0d;

        int dias = Utils.getDias(dataInicial, dataFinal) + 1;

        if (getSindicalizado()) {
            MembroSindicato membro = getMembroSindicato();
            total = membro.getTaxasServico(dataInicial, dataFinal) + dias*membro.getTaxaSindical();
        }

        return total;
    }

    public Double getSalarioLiquido(String dataInicial, String dataFinal) throws Exception{
        return getSalario() - getDescontos(dataInicial, dataFinal);
    }

    public Object[] getDadosEmLinha(String dataInicial, String data) throws Exception{

        List<Double> valores = new ArrayList<>();

        // Adiciona os dados numéricos a lista de valores
        valores.add(getSalario());
        valores.add(getDescontos(dataInicial, data));
        valores.add(getSalarioLiquido(dataInicial, data));

        // Cria strings dos dados numéricos para inserção na folha de pagamento
        String bruto = Utils.doubleToString(getSalario(), false);
        String descontos = Utils.doubleToString(getDescontos(dataInicial, data), false);
        String liquido = Utils.doubleToString(getSalarioLiquido(dataInicial, data), false);

        // Cria String da linha correspondente aos dados na folha de pagamento
        String linha = String.format("%-48s %13s %9s %15s %s", getNome(),
                bruto, descontos, liquido, getDadosPagamento());

        return new Object[]{linha, valores};
    }

    @Override
    public EmpregadoAssalariado clone() {
        return (EmpregadoAssalariado) super.clone();
    }
}
