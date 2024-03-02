package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.services.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EmpregadoAssalariado extends Empregado {

    public EmpregadoAssalariado(){}
    public EmpregadoAssalariado(String nome, String endereco, String tipo, Double salario) throws Exception {
        super(nome, endereco, tipo, salario);
        setAgendaPagamento(new AgendaPagamento("mensal $"));
    }

    /**
     * Retorna os descontos do sindicato se for sindicalizado em um intervalo de dias
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return valor total de desconto no intervalo
     * @throws Exception se falhar em alguma verficação
     */
    public Double getDescontos(String dataInicial, String dataFinal) throws Exception{
        Double total = 0d;

        int dias = Utils.getDias(dataInicial, dataFinal) + 1;

        if (getSindicalizado()) {
            MembroSindicato membro = getMembroSindicato();
            total = membro.getTaxasServico(dataInicial, dataFinal) + dias*membro.getTaxaSindical();
        }

        return total;
    }

    /**
     * Retorna o salário bruto com base na agenda de pagamento
     * @return sálario bruto
     */
    public Double getSalarioBruto() {
        if(getAgendaPagamento().getTipo().equals("mensal")){
            return getSalario();
        }
        else{
            int semana = getAgendaPagamento().getSemana();
            if (semana == 0) semana = 1;
            return Math.floor(((getSalario()*12D/52D)*semana)*100)/100F;
        }
    }

    /**
     * Retorna o salário líquido em um intervalo de dias
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return salário líquido no intervalo
     * @throws Exception se falhar em alguma verificação
     */
    public Double getSalarioLiquido(String dataInicial, String dataFinal) throws Exception{
        return getSalario() - getDescontos(dataInicial, dataFinal);
    }

    /**
     * Retorna os dados em linha para impressão na folha de pagamento
     * acompanhado dos valores númericos (em um intervalo de dias)
     * @param dataInicial data inicial
     * @param data data final
     * @return {@link Object} (String da linha, valores numéricos)
     * @throws Exception se falhar em alguma verificação
     */
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
