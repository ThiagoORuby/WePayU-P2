package br.ufal.ic.p2.wepayu.models;
import br.ufal.ic.p2.wepayu.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class EmpregadoHorista extends Empregado{

    private Double salarioPorHora;

    private List<CartaoDePonto> cartoes;

    public EmpregadoHorista(){
        super();
    }
    public EmpregadoHorista(String nome, String endereco, String tipo, Double salarioPorHora) throws Exception {
        super(nome, endereco, tipo);
        setSalarioPorHora(salarioPorHora);
        this.cartoes = new ArrayList<>();
    }

    public Double getSalarioPorHora() {
        return salarioPorHora;
    }

    public void setSalarioPorHora(Double salarioPorHora) {
        this.salarioPorHora = salarioPorHora;
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


    public Double getHorasNormaisTrabalhadas(String dataInicial, String dataFinal) throws Exception{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        LocalDate dInicial = null;
        LocalDate dFinal = null;

        try {dInicial = LocalDate.parse(Utils.validarData(dataInicial), formatter);}
        catch (Exception e) {throw new Exception("Data inicial invalida.");}

        try{dFinal = LocalDate.parse(Utils.validarData(dataFinal), formatter);}
        catch (Exception e) {throw new Exception("Data final invalida.");}

        if(dInicial.isAfter(dFinal)) throw new Exception("Data inicial nao pode ser posterior aa data final.");

        if(dInicial.isEqual(dFinal)) return 0d;

        Double horas = 0d;
        for(CartaoDePonto cartao: this.cartoes)
        {
            Double horasCartao = Double.parseDouble(cartao.getHora().replace(",", "."));
            LocalDate data = LocalDate.parse(cartao.getData(), formatter);
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

    public Double getHorasExtrasTrabalhadas(String dataInicial, String dataFinal) throws Exception{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        LocalDate dInicial = null;
        LocalDate dFinal = null;

        try {dInicial = LocalDate.parse(dataInicial, formatter);}
        catch (Exception e) {throw new Exception("Data inicial invalida.");}

        try{dFinal = LocalDate.parse(dataFinal, formatter);}
        catch (Exception e) {throw new Exception("Data final invalida.");}

        if(dInicial.isAfter(dFinal)) throw new Exception("Data inicial nao pode ser posterior aa data final");

        Double horas = 0d;
        int dias = 0;
        for(CartaoDePonto cartao: this.cartoes)
        {
            Double horasCartao = Double.parseDouble(cartao.getHora().replace(",", "."));
            LocalDate data = LocalDate.parse(cartao.getData(), formatter);
            if(data.isEqual(dInicial))
            {
                horas += horasCartao;
                dias++;
            }
            else{
                if(data.isAfter(dInicial) && data.isBefore(dFinal)) {
                    horas += horasCartao;
                    dias++;
                }
            }
        }

        Double horasExtra = horas - 8d*(dias);
        return horasExtra > 0? horasExtra : 0d;
    }


}
