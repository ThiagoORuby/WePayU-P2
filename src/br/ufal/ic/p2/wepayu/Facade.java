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
        Empregado empregado = null;
        switch (tipo) {
            case ("horista") -> {
                empregado = new EmpregadoHorista(nome, endereco, tipo, Utils.salarioFormatter(salario));
            }
            case ("assalariado") -> {
                empregado = new EmpregadoAssalariado(nome, endereco, tipo, Utils.salarioFormatter(salario));
            }
            default -> Utils.checaTipo(tipo);
        }
        empregadoDao.salvarEmpregado(empregado);
        return empregado.getId();
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        EmpregadoComissionado empregado;
        switch (tipo){
            case ("comissionado") ->  empregado = new EmpregadoComissionado(nome, endereco, tipo, Utils.salarioFormatter(salario), Utils.comissaoFormatter(comissao));
            default -> throw new Exception("Tipo nao aplicavel.");
        }

        empregadoDao.salvarEmpregado(empregado);
        return empregado.getId();
    }


    public String getAtributoEmpregado(String emp, String atributo) throws Exception {
        Empregado empregado = empregadoDao.getEmpregadoById(emp);

        return switch (empregado.getClass().getSimpleName())
        {
            case "EmpregadoAssalariado" -> Utils.getAtributoEmpregadoAssalariado((EmpregadoAssalariado) empregado, atributo);
            case "EmpregadoHorista" -> Utils.getAtributoEmpregadoHorista((EmpregadoHorista) empregado, atributo);
            case "EmpregadoComissionado" -> Utils.getAtributoEmpregadoComissionado((EmpregadoComissionado) empregado, atributo);
            default -> throw new AtributoInexistenteException();
        };
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

        CartaoDePonto cartao = new CartaoDePonto(Utils.dataValidate(data), Utils.horasValidate(horas));
        ((EmpregadoHorista) empregado).setCartao(cartao);
        empregadoDao.atualizarEmpregados();
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        Double horas = null;
        Empregado empregado = empregadoDao.getEmpregadoById(emp);
        if(!empregado.getClass().getSimpleName().equals("EmpregadoHorista")) throw new Exception("Empregado nao eh horista.");

        horas = ((EmpregadoHorista) empregado).getHorasNormaisTrabalhadas(dataInicial, dataFinal);
        return Utils.doubleToString(horas, true);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        Double horas = null;
        Empregado empregado = empregadoDao.getEmpregadoById(emp);

        if(!empregado.getClass().getSimpleName().equals("EmpregadoHorista")) throw new Exception("Empregado nao eh horista.");

        horas = ((EmpregadoHorista) empregado).getHorasExtrasTrabalhadas(dataInicial, dataFinal);
        return Utils.doubleToString(horas, true);
    }
}
