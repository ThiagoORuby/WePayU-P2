package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exceptions.DataInicialPosteriorException;
import br.ufal.ic.p2.wepayu.services.Settings;
import br.ufal.ic.p2.wepayu.services.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmpregadoComissionado extends Empregado {

    private Double comissao;
    private List<ResultadoDeVenda> vendas;

    public EmpregadoComissionado(){
        super();
    }
    public EmpregadoComissionado(String nome, String endereco, String tipo, Double salario, Double comissao) throws Exception {
        super(nome, endereco, tipo, salario);
        setComissao(comissao);
        this.vendas = new ArrayList<>();
        setAgendaPagamento(new AgendaPagamento("semanal 2 5"));
    }

    public Double getComissao() {
        return comissao;
    }

    public void setComissao(Double comissao) {
        this.comissao = comissao;
    }

    public List<ResultadoDeVenda> getVendas() {
        return vendas;
    }
    public void setVendas(List<ResultadoDeVenda> vendas) {
        this.vendas = vendas;
    }

    public void setVenda(ResultadoDeVenda venda){
        this.vendas.add(venda);
    }

    /**
     * Retorna o valor total de vendas realizadas em um intervalo de dias
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return valor total de vendas realizadas
     * @throws Exception se falhar em alguma verificação
     */
    public Double getVendasRealizadas(String dataInicial, String dataFinal) throws Exception
    {
        Double valorTotal = 0d;

        LocalDate dInicial = Utils.formatarData(dataInicial, "inicial");
        LocalDate dFinal = Utils.formatarData(dataFinal, "final");

        if(dInicial.isAfter(dFinal))
            throw new DataInicialPosteriorException();

        if(dInicial.isEqual(dFinal))
            return valorTotal;

        for(ResultadoDeVenda venda: vendas)
        {
            LocalDate data = LocalDate.parse(venda.getData(), Settings.formatter);
            if(data.isEqual(dInicial))
            {
                valorTotal += venda.getValor();
            }
            else{
                if(data.isAfter(dInicial) && data.isBefore(dFinal)) {
                    valorTotal += venda.getValor();
                }
            }
        }

        return valorTotal;
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
            total = membro.getTaxasServico(dataInicial, dataFinal) +
                    dias*membro.getTaxaSindical();
        }

        return total;
    }

    /**
     * Retorna salário fixo com base na agenda de pagamento
     * @return salário fixo do comissionado
     */
    public Double getSalarioFixo(){
        AgendaPagamento agenda = getAgendaPagamento();

        if(agenda.getTipo().equals("mensal"))
            return getSalario();
        else{
            int semana = Math.max(agenda.getSemana(), 1);
            return Math.floor((getSalario()*12D/52D)*semana * 100)/100F;
        }
    }

    /**
     * Retorna valor total das comissões em um intervalo de dias
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return valor total das comissões
     * @throws Exception se falhar em alguma verificação
     */
    public Double getComissoes(String dataInicial, String dataFinal) throws Exception{
        Double percentual = getVendasRealizadas(dataInicial, dataFinal) * getComissao();
        return Math.floor(percentual*100)/100F;
    }

    /**
     * Retorna o salário bruto em um intervalo de dias
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return valor do salário bruto
     * @throws Exception se falhar em alguma verificação
     */
    public Double getSalarioBruto(String dataInicial, String dataFinal) throws Exception{
        return  getSalarioFixo() + getComissoes(dataInicial, dataFinal);
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
        valores.add(getSalarioFixo());
        valores.add(getVendasRealizadas(dataInicial, data));
        valores.add(getComissoes(dataInicial, data));
        valores.add(getSalarioBruto(dataInicial, data));
        valores.add(getDescontos(dataInicial, data));
        valores.add(valores.get(3) - valores.get(4));

        // Cria strings dos dados numéricos para inserção na folha de pagamento
        String fixo = Utils.doubleToString(valores.get(0), false);
        String vendas = Utils.doubleToString(valores.get(1), false);
        String comissoes = Utils.doubleToString(valores.get(2), false);
        String bruto = Utils.doubleToString(valores.get(3), false);
        String descontos = Utils.doubleToString(valores.get(4), false);
        String liquido = Utils.doubleToString(valores.get(5), false);

        // Cria String da linha correspondente aos dados na folha de pagamento
        String linha = String.format("%-21s %8s %8s %8s %13s %9s %15s %s", getNome(),
                fixo, vendas, comissoes, bruto, descontos, liquido, getDadosPagamento());

        return new Object[]{linha, valores};
    }

    @Override
    public EmpregadoComissionado clone() {
        EmpregadoComissionado clone = (EmpregadoComissionado) super.clone();

        clone.vendas = new ArrayList<>();

        for(ResultadoDeVenda venda: this.vendas){
            clone.vendas.add(venda.clone());
        }

        return clone;
    }
}
