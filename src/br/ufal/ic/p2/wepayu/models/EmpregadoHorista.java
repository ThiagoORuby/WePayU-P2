package br.ufal.ic.p2.wepayu.models;
import br.ufal.ic.p2.wepayu.exceptions.DataInicialPosteriorException;
import br.ufal.ic.p2.wepayu.services.Settings;
import br.ufal.ic.p2.wepayu.services.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class EmpregadoHorista extends Empregado {

    private List<CartaoDePonto> cartoes;

    public EmpregadoHorista(){
        super();
    }
    public EmpregadoHorista(String nome, String endereco, String tipo, Double salario) throws Exception {
        super(nome, endereco, tipo, salario);
        this.cartoes = new ArrayList<>();
        setAgendaPagamento(new AgendaPagamento("semanal 5"));
    }

    public List<CartaoDePonto> getCartoes(){
        return cartoes;
    }

    public void setCartoes(List<CartaoDePonto> cartoes){
        this.cartoes = cartoes;
    }

    public void setCartao(CartaoDePonto cartao){
        cartoes.add(cartao);
    }

    private void setTaxaExtra(String data, Double valor){
        if(getSindicalizado()){
            TaxaServico extra = new TaxaServico(data,
                    valor);
            getMembroSindicato().setTaxaExtra(extra);
        }
    }

    /**
     * Retorna o total de horas normais trabalhadas em um intervalo de dias
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return total de horas normais
     * @throws Exception se falhar em alguma verficação
     */
    public Double getHorasNormaisTrabalhadas(String dataInicial, String dataFinal) throws Exception{
        Double horas = 0d;

        LocalDate dInicial = Utils.formatarData(dataInicial, "inicial");
        LocalDate dFinal = Utils.formatarData(dataFinal, "final");

        if(dInicial.isAfter(dFinal))
            throw new DataInicialPosteriorException();

        if(dInicial.isEqual(dFinal))
            return horas;

        for(CartaoDePonto cartao: this.cartoes)
        {
            Double horasCartao = Double.parseDouble(cartao.getHora().replace(",", "."));
            LocalDate data = LocalDate.parse(cartao.getData(), Settings.formatter);
            if(data.isEqual(dInicial))
            {
                horas += (horasCartao <= 8d ? horasCartao : 8d);
            }
            else{
                if(data.isAfter(dInicial) && data.isBefore(dFinal)) {
                    horas += (horasCartao <= 8d ? horasCartao : 8d);
                }
            }
        }

        return horas;
    }

    /**
     * Retorna total de horas extras (excendentes) trabalhas em um intervalo de dias
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return total de horas extras
     * @throws Exception se falhar em alguma verificação
     */
    public Double getHorasExtrasTrabalhadas(String dataInicial, String dataFinal) throws Exception{

        Double horas = 0d;

        LocalDate dInicial = Utils.formatarData(dataInicial, "inicial");
        LocalDate dFinal = Utils.formatarData(dataFinal, "final");

        if(dInicial.isAfter(dFinal))
            throw new DataInicialPosteriorException();

        if(dInicial.isEqual(dFinal))
            return horas;


        for(CartaoDePonto cartao: this.cartoes)
        {
            Double horasCartao = Double.parseDouble(cartao.getHora().replace(",", "."));
            LocalDate data = LocalDate.parse(cartao.getData(), Settings.formatter);
            if(data.isEqual(dInicial))
            {
                horas += (horasCartao > 8d ? horasCartao - 8d: 0d);
            }
            else{
                if(data.isAfter(dInicial) && data.isBefore(dFinal)) {
                    horas += (horasCartao > 8d ? horasCartao - 8d : 0d);
                }
            }
        }

        return horas;
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

        int dias = Utils.getDias(dataInicial, dataFinal);

        if (getSindicalizado()) {
            MembroSindicato membro = getMembroSindicato();
            total = membro.getTaxasServico(dataInicial, dataFinal) +
                    dias*membro.getTaxaSindical();
        }

        return total;
    }

    /**
     * Retorna o salário bruto em um intervalo de dias
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return valor do salário bruto
     * @throws Exception se falhar em alguma verificação
     */
    public Double getSalarioBruto(String dataInicial, String dataFinal) throws Exception{
        Double horasNormais = getHorasNormaisTrabalhadas(dataInicial, dataFinal);
        Double horasExtras = getHorasExtrasTrabalhadas(dataInicial, dataFinal);

        return horasNormais*getSalario() + horasExtras*1.5*getSalario();
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
        valores.add(getHorasNormaisTrabalhadas(dataInicial, data));
        valores.add(getHorasExtrasTrabalhadas(dataInicial, data));
        valores.add(getSalarioBruto(dataInicial, data));

        // Confere se há salario suficiente para retirar os descontos acumulados
        Double desconto = getDescontos(dataInicial,data);
        Double descontoTotal = desconto +
                ((getSindicalizado()) ? getMembroSindicato().getTotalTaxasExtras() : 0D);

        if(valores.get(2) < descontoTotal) {
            valores.add(0D);
            String dataCobranca = Utils.getProximoDiaDePagamento(data, getAgendaPagamento());
            setTaxaExtra(dataCobranca, desconto);
        }
        else {
            if(getSindicalizado()) getMembroSindicato().clearTaxasExtras();
            valores.add(descontoTotal);
        }

        // Adiciona dado de salário liquido
        valores.add(valores.get(2) - valores.get(3));

        // Cria strings dos dados numéricos para inserção na folha de pagamento
        String normais = Utils.doubleToString(valores.get(0), true);
        String extras = Utils.doubleToString(valores.get(1), true);
        String bruto = Utils.doubleToString(valores.get(2), false);
        String descontos = Utils.doubleToString(valores.get(3), false);
        String liquido = Utils.doubleToString(valores.get(4), false);

        // Cria String da linha correspondente aos dados na folha de pagamento
        String linha = String.format("%-36s %5s %5s %13s %9s %15s %s", getNome(),
                normais, extras, bruto, descontos, liquido, getDadosPagamento());

        return new Object[]{linha, valores};
    }

    @Override
    public EmpregadoHorista clone() {
        EmpregadoHorista clone = (EmpregadoHorista) super.clone();

        clone.cartoes = new ArrayList<>();

        for(CartaoDePonto cartao: this.cartoes){
            clone.cartoes.add(cartao.clone());
        }

        return clone;
    }
}
