package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.daos.*;
import br.ufal.ic.p2.wepayu.exceptions.AgendaIndisponivelException;
import br.ufal.ic.p2.wepayu.exceptions.TipoEmpregadoInvalidoException;
import br.ufal.ic.p2.wepayu.services.*;
import br.ufal.ic.p2.wepayu.models.*;

public class Facade {

    private DBManager session;

    private final DaoManager daos;
    private final FolhaBuilder folha;
    private final History history;



    public Facade() {
        // inicializa instâncias base do sistema
        session = DBManager.getSession();
        daos = new DaoManager(session);
        folha =  new FolhaBuilder();
        history = new History(session.query());
        // Adiciona estado inicial a pilha do histórico
        history.push();
    }

    public void zerarSistema() throws Exception{
        // limpa as informações locais e do XML
        session.clearAll();
        history.push();
    }

    public void encerrarSistema() throws Exception{
        // Salva as informações no xml
        session.commit();
        session.commitAgendas();
        session.close();
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        Empregado empregado = daos.getEmpregadoDao().create(nome,
                endereco,
                tipo,
                salario);
        history.push();
        return empregado.getId();
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        Empregado empregado = daos.getEmpregadoDao().create(nome,
                endereco,
                tipo,
                salario,
                comissao);
        history.push();
        return empregado.getId();
    }

    public void alteraEmpregado(String emp, String atributo, String valor) throws Exception
    {
        // Verifica se a descrição da agenda existe na base de dados
        if(atributo.equals("agendaPagamento")){
            if(!daos.getAgendaDao().find(valor))
                throw new AgendaIndisponivelException();
        }

        // Chama o método corresponte para alteração do empregado com base no atributo
        if(atributo.equals("metodoPagamento")) {
            daos.getEmpregadoDao().updatePagamentoEmpregado(emp,
                    valor,
                    null,
                    null,
                    null);
        }else {
            daos.getEmpregadoDao().updateAtributoById(emp,
                    atributo,
                    valor,
                    null);
        }
        history.push();
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String comissao) throws Exception{
        daos.getEmpregadoDao().updateAtributoById(emp,
                atributo,
                valor,
                comissao);
        history.push();
    }

    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws Exception{
        daos.getEmpregadoDao().updatePagamentoEmpregado(emp,
                valor1,
                banco,
                agencia,
                contaCorrente);
        history.push();
    }


    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws Exception{

        if(!atributo.equals("sindicalizado")) return;

        idSindicato = Utils.validarAtributo(idSindicato, "Identificacao do sindicato", "a");
        Double taxa = Utils.formatarValor(taxaSindical, "Taxa sindical", "a");

        daos.getEmpregadoDao().checkSindicadoId(idSindicato);

        Empregado empregado = daos.getEmpregadoDao().getById(emp);

        daos.getMembroDao().create(empregado, idSindicato, taxa);
        history.push();
    }

    public String getAtributoEmpregado(String emp, String atributo) throws Exception {
        return daos.getEmpregadoDao().getAtributo(emp, atributo);
    }

    public String getEmpregadoPorNome(String nome, int indice) throws Exception{
        return daos.getEmpregadoDao().getByName(nome).get(indice-1);
    }

    public void removerEmpregado(String emp) throws Exception{
        daos.getEmpregadoDao().deleteById(emp);
        history.push();
    }

    public void lancaCartao(String emp, String data, String horas) throws Exception {
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        daos.getCartaoDao().create(empregado, data, horas);
        history.push();
    }

    public void lancaVenda(String emp, String data, String valor) throws Exception{
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        daos.getVendaDao().create(empregado, data, valor);
        history.push();
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws Exception
    {
        Empregado empregado = daos.getEmpregadoDao().getByIdSindicato(membro);
        daos.getTaxaDao().create(empregado, data, valor);
        history.push();
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        Utils.checarTipo(empregado.getTipo(), "horista");

        Double horas = ((EmpregadoHorista) empregado).getHorasNormaisTrabalhadas(dataInicial, dataFinal);
        return Utils.doubleToString(horas, true);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        Utils.checarTipo(empregado.getTipo(), "horista");

        Double horas = ((EmpregadoHorista) empregado).getHorasExtrasTrabalhadas(dataInicial, dataFinal);
        return Utils.doubleToString(horas, true);
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception{
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        Utils.checarTipo(empregado.getTipo(), "comissionado");

        Double valor = ((EmpregadoComissionado) empregado).getVendasRealizadas(dataInicial, dataFinal);
        return Utils.doubleToString(valor, false);
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception
    {
        Empregado empregado = daos.getEmpregadoDao().getById(emp);

        if(!empregado.getSindicalizado())
            throw new TipoEmpregadoInvalidoException("sindicalizado");

        Double valor = empregado.getMembroSindicato().getTaxasServico(dataInicial,dataFinal);
        return Utils.doubleToString(valor, false);
    }

    public String totalFolha(String data) throws Exception{
        return Utils.doubleToString(folha.getTotalFolha(data),  false);
    }


    public void rodaFolha(String data, String saida) throws Exception{
        folha.geraFolha(data, saida);
        history.push();
    }

    public String getNumeroDeEmpregados(){
        return session.size();
    }

    public void undo() throws Exception{
        Memento m = history.getUndo();
        session.restore(m);
    }

    public void redo() throws Exception{
        Memento m = history.getRedo();
        session.restore(m);
    }

    public void criarAgendaDePagamentos(String descricao) throws Exception{
        daos.getAgendaDao().create(descricao);
    }
}
