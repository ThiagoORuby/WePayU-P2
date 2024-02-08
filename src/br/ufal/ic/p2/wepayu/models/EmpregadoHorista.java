package br.ufal.ic.p2.wepayu.models;
import br.ufal.ic.p2.wepayu.services.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class EmpregadoHorista extends Empregado{

    private List<CartaoDePonto> cartoes;

    public EmpregadoHorista(){
        super();
    }
    public EmpregadoHorista(String nome, String endereco, String tipo, Double salario) throws Exception {
        super(nome, endereco, tipo, salario);
        this.cartoes = new ArrayList<>();
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

        if(dInicial.isAfter(dFinal)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

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

        if(dInicial.isEqual(dFinal)) return 0d;

        Double horas = 0d;
        for(CartaoDePonto cartao: this.cartoes)
        {
            Double horasCartao = Double.parseDouble(cartao.getHora().replace(",", "."));
            LocalDate data = LocalDate.parse(cartao.getData(), formatter);
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

    public Double getSalarioLiquido(String data) throws Exception{
        return getSalarioBruto(data) - getDescontos(data);
    }

    public Double getDescontos(String data) throws Exception{
        Double total = 0d;

        if(!Utils.checaehSexta(data)) return 0d;
        String dataInicial = Utils.getUltimaSexta(data);

        if (getSindicalizado()) {
            MembroSindicato membro = getMembroSindicato();
            total = membro.getTaxasServico(dataInicial, data) + 7*membro.getTaxaSindical();
        }

        return total;
    }

    public Double getSalarioBruto(String data) throws Exception{

        if(!Utils.checaehSexta(data)) return 0d;

        String dataInicial = Utils.getUltimaSexta(data);
        Double horasNormais = getHorasNormaisTrabalhadas(dataInicial, data);
        Double horasExtras = getHorasExtrasTrabalhadas(dataInicial, data);

        return horasNormais*getSalario() + horasExtras*1.5*getSalario();
    }

    public String getDadosEmLinha(String data) throws Exception{

        if(!Utils.checaehSexta(data)) return null;

        String dataInicial = Utils.getUltimaSexta(data);

        String normais = Utils.doubleToString(getHorasNormaisTrabalhadas(dataInicial, data), true);
        String extras = Utils.doubleToString(getHorasExtrasTrabalhadas(dataInicial, data), true);
        String bruto = Utils.doubleToString(getSalarioBruto(data), false);
        String descontos = Utils.doubleToString(getDescontos(data), false);
        String liquido = Utils.doubleToString(getSalarioLiquido(data), false);

        MetodoPagamento metodo = getMetodoPagamento();
        String pagamento = "Em maos";
        if(metodo.getTipo().equals("banco")){
            pagamento = String.format("%s, Ag. %s CC %s", metodo.getBanco(), metodo.getAgencia(), metodo.getContaCorrente());
        }

        String linha = String.format("%-36s %5s %5s %13s %9s %15s %s", getNome(), normais, extras, bruto, descontos, liquido, pagamento);
        return linha;
    }


}
