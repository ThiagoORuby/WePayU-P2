package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.daos.*;
import br.ufal.ic.p2.wepayu.services.DBManager;
import br.ufal.ic.p2.wepayu.services.Utils;
import br.ufal.ic.p2.wepayu.models.*;

public class Facade {

    private DBManager session;

    private final DaoManager daos;


    public Facade() {
        session = DBManager.getSession();
        daos = new DaoManager(session);
    }

    public void zerarSistema() throws Exception{
        session.clearALl();
        session = session.reset();
        daos.reset(session);
    }

    public void encerrarSistema(){
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        Empregado empregado = daos.getEmpregadoDao().create(nome,
                endereco,
                tipo,
                salario);
        return empregado.getId();
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        Empregado empregado = daos.getEmpregadoDao().create(nome,
                endereco,
                tipo,
                salario,
                comissao);
        return empregado.getId();
    }

    public void alteraEmpregado(String emp, String atributo, String valor) throws Exception
    {
        if(atributo.equals("metodoPagamento"))
            daos.getEmpregadoDao().updatePagamentoEmpregado(emp,
                    valor,
                    null,
                    null,
                    null);
        else
            daos.getEmpregadoDao().updateAtributoById(emp,
                    atributo,
                    valor,
                    null);
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String comissao) throws Exception{
        daos.getEmpregadoDao().updateAtributoById(emp,
                atributo,
                valor,
                comissao);
    }

    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws Exception{
        daos.getEmpregadoDao().updatePagamentoEmpregado(emp,
                valor1,
                banco,
                agencia,
                contaCorrente);
    }


    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws Exception{
        if(!atributo.equals("sindicalizado")) return;

        idSindicato = Utils.validarAtributo(idSindicato, "Identificacao do sindicato", "a");
        Double taxa = Utils.formatarValor(taxaSindical, "Taxa sindical", "a");

        if(daos.getEmpregadoDao().checkSindicadoId(idSindicato)){
            throw new Exception("Ha outro empregado com esta identificacao de sindicato");
        }

        Empregado empregado = daos.getEmpregadoDao().getById(emp);

        MembroSindicato membro = new MembroSindicato(idSindicato, taxa);
        empregado.setMembroSindicato(membro);
        session.commit();
    }

    public String getAtributoEmpregado(String emp, String atributo) throws Exception {
        return daos.getEmpregadoDao().getAtributo(emp, atributo);
    }

    public String getEmpregadoPorNome(String nome, int indice) throws Exception{
        return daos.getEmpregadoDao().getByName(nome).get(indice-1);
    }

    public void removerEmpregado(String emp) throws Exception{
        daos.getEmpregadoDao().deleteById(emp);
    }

    public void lancaCartao(String emp, String data, String horas) throws Exception {
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        daos.getCartaoDao().create(empregado, data, horas);
    }

    public void lancaVenda(String emp, String data, String valor) throws Exception{
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        daos.getVendaDao().create(empregado, data, valor);
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws Exception
    {
        Empregado empregado = daos.getEmpregadoDao().getByIdSindicato(membro);
        daos.getTaxaDao().create(empregado, data, valor);
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        if(!empregado.getClass().getSimpleName().equals("EmpregadoHorista"))
            throw new Exception("Empregado nao eh horista.");

        Double horas = ((EmpregadoHorista) empregado).getHorasNormaisTrabalhadas(dataInicial, dataFinal);
        return Utils.doubleToString(horas, true);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        if(!empregado.getClass().getSimpleName().equals("EmpregadoHorista"))
            throw new Exception("Empregado nao eh horista.");

        Double horas = ((EmpregadoHorista) empregado).getHorasExtrasTrabalhadas(dataInicial, dataFinal);
        return Utils.doubleToString(horas, true);
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception{
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        if(!empregado.getClass().getSimpleName().equals("EmpregadoComissionado"))
            throw new Exception("Empregado nao eh comissionado.");

        Double valor = ((EmpregadoComissionado) empregado).getVendasRealizadas(dataInicial, dataFinal);
        return Utils.doubleToString(valor, false);
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception
    {
        Empregado empregado = daos.getEmpregadoDao().getById(emp);

        if(!empregado.getSindicalizado())
            throw new Exception("Empregado nao eh sindicalizado.");

        Double valor = empregado.getMembroSindicato().getTaxasServico(dataInicial,dataFinal);
        return Utils.doubleToString(valor, false);
    }

    public String totalFolha(String data) throws Exception{
        return Utils.doubleToString(daos.getEmpregadoDao().getTotalEmpregados(data), false);
    }

    public void rodaFolha(String data, String saida) throws Exception{
        FolhaFactory folha = new FolhaFactory();

        //folha.geraFolha(data, saida);
    }

}
