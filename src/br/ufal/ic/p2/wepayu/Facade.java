package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.AtributoInexistenteException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.daos.EmpregadoDao;
import br.ufal.ic.p2.wepayu.models.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Facade {

    private EmpregadoDao empregadoDao;

    public Facade(){
        this.empregadoDao = EmpregadoDao.getInstance();
    }

    public void zerarSistema(){
        try{
            Files.delete(Path.of("empregados.xml"));
            empregadoDao = EmpregadoDao.resetInstance();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void encerrarSistema(){
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        Empregado empregado = empregadoDao.criarEmpregado(nome, endereco, tipo, salario);
        empregadoDao.salvarEmpregado(empregado);
        return empregado.getId();
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        Empregado empregado = empregadoDao.criarEmpregado(nome, endereco, tipo, salario, comissao);
        empregadoDao.salvarEmpregado(empregado);
        return empregado.getId();
    }

    public void alteraEmpregado(String emp, String atributo, String valor) throws Exception
    {
        if(atributo.equals("metodoPagamento"))
            empregadoDao.updatePagamentoEmpregado(emp, valor, null, null, null);
        else
            empregadoDao.updateAtributoEmpregado(emp, atributo, valor, null);
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String comissao) throws Exception{
        empregadoDao.updateAtributoEmpregado(emp, atributo, valor, comissao);
    }

    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws Exception{
        empregadoDao.updatePagamentoEmpregado(emp, valor1, banco, agencia, contaCorrente);
    }


    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws Exception{
        if(!atributo.equals("sindicalizado")) return;

        if(empregadoDao.idSindicadoExiste(idSindicato)){
            throw new Exception("Ha outro empregado com esta identificacao de sindicato");
        }

        Empregado empregado = empregadoDao.getEmpregadoById(emp);

        MembroSindicato membro = new MembroSindicato(Utils.validarAtributo(idSindicato, "Identificacao do sindicato", "a"), Utils.formatarValor(taxaSindical, "Taxa sindical", "a"));
        empregado.setMembroSindicato(membro);
        empregadoDao.atualizarEmpregados();
    }



    public String getAtributoEmpregado(String emp, String atributo) throws Exception {
        Empregado empregado = empregadoDao.getEmpregadoById(emp);

        if (atributo.equals("banco") || atributo.equals("agencia") || atributo.equals("contaCorrente"))
        {
            if(!empregado.getMetodoPagamento().getTipo().equals("banco"))
                throw new Exception("Empregado nao recebe em banco.");
        }

        switch (atributo) {
            case "nome" -> {
                return empregado.getNome();
            }
            case "endereco" -> {
                return empregado.getEndereco();
            }
            case "tipo" -> {
                return empregado.getTipo();
            }
            case "metodoPagamento" -> {
                return empregado.getMetodoPagamento().getTipo();
            }
            case "banco" -> {

                return empregado.getMetodoPagamento().getBanco();
            }
            case "agencia" -> {
                return empregado.getMetodoPagamento().getAgencia();
            }
            case "contaCorrente" -> {
                return empregado.getMetodoPagamento().getContaCorrente();
            }
            case "idSindicato" -> {
                if(!empregado.getSindicalizado()) throw new Exception("Empregado nao eh sindicalizado.");
                return empregado.getMembroSindicato().getIdMembro();
            }
            case "taxaSindical" -> {
                if(!empregado.getSindicalizado()) throw new Exception("Empregado nao eh sindicalizado.");
                return Utils.doubleToString(empregado.getMembroSindicato().getTaxaSindical(), false);
            }
            default -> {
                return switch (empregado.getClass().getSimpleName()) {
                    case "EmpregadoAssalariado" ->
                            Utils.getAtributoEmpregadoAssalariado((EmpregadoAssalariado) empregado, atributo);
                    case "EmpregadoHorista" ->
                            Utils.getAtributoEmpregadoHorista((EmpregadoHorista) empregado, atributo);
                    case "EmpregadoComissionado" ->
                            Utils.getAtributoEmpregadoComissionado((EmpregadoComissionado) empregado, atributo);
                    default -> throw new AtributoInexistenteException();
                };
            }
        }
    }

    public String getEmpregadoPorNome(String nome, int indice) throws Exception{
        return empregadoDao.getEmpregadoByName(nome).get(indice-1);
    }

    public void removerEmpregado(String emp) throws Exception{
        empregadoDao.deleteEmpregadoById(emp);
    }

    public void lancaCartao(String emp, String data, String horas) throws Exception {
        Empregado empregado = empregadoDao.getEmpregadoById(emp);
        if(!empregado.getClass().getSimpleName().equals("EmpregadoHorista")) throw new Exception("Empregado nao eh horista.");

        CartaoDePonto cartao = new CartaoDePonto(Utils.validarData(data), Utils.validarHoras(horas));
        ((EmpregadoHorista) empregado).setCartao(cartao);
        empregadoDao.atualizarEmpregados();
    }

    public void lancaVenda(String emp, String data, String valor) throws Exception{
        Empregado empregado = empregadoDao.getEmpregadoById(emp);
        if(!empregado.getClass().getSimpleName().equals("EmpregadoComissionado")) throw new Exception("Empregado nao eh comissionado.");

        ResultadoDeVenda venda = new ResultadoDeVenda(Utils.validarData(data), Utils.validarValor(valor));
        ((EmpregadoComissionado) empregado).setVenda(venda);
        empregadoDao.atualizarEmpregados();
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws Exception
    {
        Empregado empregado = empregadoDao.getEmpregadoByIdSindicato(membro);

        TaxaServico taxa = new TaxaServico(Utils.validarData(data), Utils.validarValor(valor));
        empregado.getMembroSindicato().setTaxaServico(taxa);
        empregadoDao.atualizarEmpregados();
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        Empregado empregado = empregadoDao.getEmpregadoById(emp);
        if(!empregado.getClass().getSimpleName().equals("EmpregadoHorista")) throw new Exception("Empregado nao eh horista.");

        Double horas = ((EmpregadoHorista) empregado).getHorasNormaisTrabalhadas(dataInicial, dataFinal);
        return Utils.doubleToString(horas, true);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        Empregado empregado = empregadoDao.getEmpregadoById(emp);
        if(!empregado.getClass().getSimpleName().equals("EmpregadoHorista")) throw new Exception("Empregado nao eh horista.");

        Double horas = ((EmpregadoHorista) empregado).getHorasExtrasTrabalhadas(dataInicial, dataFinal);
        return Utils.doubleToString(horas, true);
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception{
        Empregado empregado = empregadoDao.getEmpregadoById(emp);
        if(!empregado.getClass().getSimpleName().equals("EmpregadoComissionado")) throw new Exception("Empregado nao eh comissionado.");

        Double valor = ((EmpregadoComissionado) empregado).getVendasRealizadas(dataInicial, dataFinal);
        return Utils.doubleToString(valor, false);
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception
    {
        Empregado empregado = empregadoDao.getEmpregadoById(emp);

        if(!empregado.getSindicalizado()) throw new Exception("Empregado nao eh sindicalizado.");

        Double valor = empregado.getMembroSindicato().getTaxasServico(dataInicial,dataFinal);
        return Utils.doubleToString(valor, false);
    }

}
