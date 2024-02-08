package br.ufal.ic.p2.wepayu.models;

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

    public Double getVendasRealizadas(String dataInicial, String dataFinal) throws Exception
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        LocalDate dInicial = null;
        LocalDate dFinal = null;

        try {dInicial = LocalDate.parse(Utils.validarData(dataInicial), formatter);}
        catch (Exception e) {throw new Exception("Data inicial invalida.");}

        try{dFinal = LocalDate.parse(Utils.validarData(dataFinal), formatter);}
        catch (Exception e) {throw new Exception("Data final invalida.");}

        if(dInicial.isAfter(dFinal)) throw new Exception("Data inicial nao pode ser posterior aa data final.");

        if(dInicial.isEqual(dFinal)) return 0d;

        Double valorTotal = 0d;

        for(ResultadoDeVenda venda: vendas)
        {
            LocalDate data = LocalDate.parse(venda.getData(), formatter);
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

    public Double getSalarioBruto(String data) throws Exception{

        if(!Utils.checaehSexta(data)) return 0d;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        LocalDate dInicial = LocalDate.parse(data, formatter).minusDays(13);
        LocalDate primeiroDia = LocalDate.of(2005, 1, 1);

        if (dInicial.isBefore(primeiroDia))
        {
            return 0d;
        }

        String dataInicial = dInicial.format(formatter);

        Double percentualComissoes = getVendasRealizadas(dataInicial, data)* getComissao();

        return (getSalario()*12D/52D)*2D + percentualComissoes;
    }


}
