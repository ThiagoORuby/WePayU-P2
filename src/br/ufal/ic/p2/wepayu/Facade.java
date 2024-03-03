package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.daos.*;
import br.ufal.ic.p2.wepayu.exceptions.*;
import br.ufal.ic.p2.wepayu.services.*;
import br.ufal.ic.p2.wepayu.models.*;

public class Facade {

    private DBManager session;

    private final DaoManager daos;
    private final FolhaBuilder folha;
    private final History history;


    /**
     * Cria o Facade que reune todas as operações do sistema
     */
    public Facade() {
        // inicializa instâncias base do sistema
        session = DBManager.getSession();
        daos = new DaoManager(session);
        folha =  new FolhaBuilder();
        history = new History(session.query());
        // Adiciona estado inicial a pilha do histórico
        history.push();
    }

    /**
     * Limpa os dados do sistema
     */
    public void zerarSistema() throws Exception{
        // limpa as informações locais e do XML
        session.clearAll();
        history.push();
    }

    /**
     * Salva os dados atuais do sistema e fecha a conexão com o banco de dados
     */
    public void encerrarSistema() {
        // Salva as informações no xml
        session.commit();
        session.commitAgendas();
        session.close();
    }


    /**
     * Cria um novo {@link EmpregadoHorista} ou {@link EmpregadoAssalariado}
     * @param nome nome do empregado
     * @param endereco endereço do empregado
     * @param tipo tipo ('horista' ou 'assalariado')
     * @param salario valor do salário
     * @return {@link Empregado} criado
     * @throws Exception se falhar em alguma verificação
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        Empregado empregado = daos.getEmpregadoDao().create(nome,
                endereco,
                tipo,
                salario);
        history.push();
        return empregado.getId();
    }

    /**
     * Cria um novo {@link EmpregadoComissionado}
     * @param nome nome do empregado
     * @param endereco endereço do empregado
     * @param tipo tipo ('comissionado')
     * @param salario valor do salário
     * @param comissao valor da comissão
     * @return {@link Empregado} criado
     * @throws Exception se falhar em alguma verificação
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        Empregado empregado = daos.getEmpregadoDao().create(nome,
                endereco,
                tipo,
                salario,
                comissao);
        history.push();
        return empregado.getId();
    }

    /**
     * Atualiza o valor de um atributo pelo id do empregado
     * @param emp id do empregado
     * @param atributo nodo do atributo a ser alterado
     * @param valor novo valor do atributo
     * @throws Exception se falahar em alguma verificação
     */
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

    /**
     * Atualiza o valor de um atributo pelo id do empregado (especificamente, o tipo do empregado)
     * @param emp id do empregado
     * @param atributo nodo do atributo a ser alterado
     * @param valor novo valor do atributo
     * @param comissao nova comissao, caso altere para comissionado
     * @throws Exception se falhar em alguma verificação
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String comissao) throws Exception{
        daos.getEmpregadoDao().updateAtributoById(emp,
                atributo,
                valor,
                comissao);
        history.push();
    }

    /**
     * Atualiza método de pagamento do empregado
     * @param emp id do empregado
     * @param atributo nome do atributo (especificamente, metodoPagamento)
     * @param valor1 tipo do método de pagamento
     * @param banco nome do banco, se for pagamento em banco
     * @param agencia número da agência, se for pagamento em banco
     * @param contaCorrente número da conta, se for pagamento em banco
     * @throws Exception se falhar em alguma verificação
     */
    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws Exception{
        // verifica se as condições são favoráveis
        if(!atributo.equals("metodoPagamento")) return;

        daos.getEmpregadoDao().updatePagamentoEmpregado(emp,
                valor1,
                banco,
                agencia,
                contaCorrente);
        history.push();
    }


    /**
     * Altera um empregado, relacionando-o com o sindicato
     * @param emp id do mepregado
     * @param atributo nome do atributo (especificamente, sindicalizado)
     * @param valor valor do atributo (especificamente, true)
     * @param idSindicato id do empregado no sindicato
     * @param taxaSindical taxa sindical
     * @throws Exception se falhar em alguma verificação
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws Exception{
        // verifica se as condições são favoráveis
        if(!atributo.equals("sindicalizado")) return;

        // realiza as formatações necessárias
        idSindicato = Utils.validarAtributo(idSindicato, "Identificacao do sindicato", "a");
        Double taxa = Utils.formatarValor(taxaSindical, "Taxa sindical", "a");

        // Verifica existencia do id na base
        daos.getEmpregadoDao().checkSindicadoId(idSindicato);

        // Relaciona o empregado ao sindicato
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        daos.getMembroDao().create(empregado, idSindicato, taxa);
        history.push();
    }

    /**
     * Retorna o valor de um atributo do empregado
     * @param emp id do empregado
     * @param atributo atributo buscado
     * @return valor do atributo buscado
     * @throws Exception se falhar em alguma verificação
     */
    public String getAtributoEmpregado(String emp, String atributo) throws Exception {
        return daos.getEmpregadoDao().getAtributo(emp, atributo);
    }

    /**
     * Retorna um empregado com base no nome
     * @param nome nome do empregado
     * @param indice posição de escolha, em caso de multiplos empregados
     * @return {@link Empregado} correspondente
     * @throws EmpregadoNaoExisteException se o empregado não existir
     */
    public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoExisteException {
        return daos.getEmpregadoDao().getByName(nome).get(indice-1);
    }

    /**
     * Remove um empregado com base em seu id
     * @param emp id do empregado
     * @throws ValorNuloException se o id for nulo
     * @throws EmpregadoNaoExisteException se o empregado não existir
     */
    public void removerEmpregado(String emp) throws ValorNuloException, EmpregadoNaoExisteException {
        daos.getEmpregadoDao().deleteById(emp);
        history.push();
    }

    /**
     * Cria um novo {@link CartaoDePonto} na base de dados relacionado a um empregado
     * @param emp id do empregado
     * @param data data de lançamento
     * @param horas horas trabalhadas
     * @throws Exception se falhar em alguma validação
     */
    public void lancaCartao(String emp, String data, String horas) throws Exception {
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        daos.getCartaoDao().create(empregado, data, horas);
        history.push();
    }

    /**
     * Cria um novo {@link ResultadoDeVenda} na base de dados relacionado a um empregado
     * @param emp id do empregado
     * @param data data da venda
     * @param valor valor da venda
     * @throws Exception se falhar em alguma verificação
     */
    public void lancaVenda(String emp, String data, String valor) throws Exception{
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        daos.getVendaDao().create(empregado, data, valor);
        history.push();
    }

    /**
     * Cria uma nova {@link TaxaServico} na base de dados relacionado a um empregado
     * @param membro id de membro do sindicato do empregado
     * @param data data da criação da taxa
     * @param valor valor da taxa
     * @throws Exception se falhar em alguma verificação
     */
    public void lancaTaxaServico(String membro, String data, String valor) throws Exception
    {
        Empregado empregado = daos.getEmpregadoDao().getByIdSindicato(membro);
        daos.getTaxaDao().create(empregado, data, valor);
        history.push();
    }

    /**
     * Retorna o total de horas normais trabalhadas em um intervalo de dias
     * @param emp id do empregado associado
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return total de horas normais
     * @throws Exception se falhar em alguma verficação
     */
    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        // verifica tipo correto do empregado
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        Utils.checarTipo(empregado.getTipo(), "horista");

        Double horas = ((EmpregadoHorista) empregado).getHorasNormaisTrabalhadas(dataInicial, dataFinal);
        return Utils.doubleToString(horas, true);
    }

    /**
     * Retorna total de horas extras (excendentes) trabalhas em um intervalo de dias
     * @param emp id do empregado associado
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return total de horas extras
     * @throws Exception se falhar em alguma verificação
     */
    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        // verifica tipo correto do empregado
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        Utils.checarTipo(empregado.getTipo(), "horista");

        Double horas = ((EmpregadoHorista) empregado).getHorasExtrasTrabalhadas(dataInicial, dataFinal);
        return Utils.doubleToString(horas, true);
    }

    /**
     * Retorna o valor total de vendas realizadas em um intervalo de dias
     * @param emp id do empregado associado
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return valor total de vendas realizadas
     * @throws Exception se falhar em alguma verificação
     */
    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception{
        // verifica tipo correto do empregado
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        Utils.checarTipo(empregado.getTipo(), "comissionado");

        Double valor = ((EmpregadoComissionado) empregado).getVendasRealizadas(dataInicial, dataFinal);
        return Utils.doubleToString(valor, false);
    }

    /**
     * Retorna o valor total das taxas do sindicato em um intervalo de dias
     * @param emp id do empregado associado
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return valor total das taxas no intervalo
     * @throws Exception se falhar em alguma verificação
     */
    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception
    {
        // verifica se empregado é sindicalizado
        Empregado empregado = daos.getEmpregadoDao().getById(emp);
        if(!empregado.getSindicalizado())
            throw new TipoEmpregadoInvalidoException("sindicalizado");

        Double valor = empregado.getMembroSindicato().getTaxasServico(dataInicial,dataFinal);
        return Utils.doubleToString(valor, false);
    }

    /**
     * Retorna o total da folha de pagamento relativa a data
     * @param data Data da folha de pagamento
     * @return {@link Double} total da folha de pagamento
     * @throws Exception se falhar em alguma verificação
     */
    public String totalFolha(String data) throws Exception{
        return Utils.doubleToString(folha.getTotalFolha(data),  false);
    }

    /**
     * Gera o txt com folha de pagamento de um determinado dia
     * @param data Data da folha de pagamento
     * @param saida Nome do arquivo txt
     * @throws Exception se falhar em alguma verificação
     */
    public void rodaFolha(String data, String saida) throws Exception{
        folha.geraFolha(data, saida);
        history.push();
    }

    /**
     * Retorna o total de empregados na base de dados
     * @return total de empregados
     */
    public String getNumeroDeEmpregados(){
        return session.size();
    }

    /**
     * Desfaz a última operação realizada
     * @throws UndoException se não houver operação anterior
     * @throws SistemaEncerradoException se o acesso a base foi fechado
     */
    public void undo() throws UndoException, SistemaEncerradoException {
        Memento m = history.getUndo();
        session.restore(m);
    }

    /**
     * Refaz a última operação realizada que foi desfeita
     * @throws RedoException se não houver operação posterior
     * @throws SistemaEncerradoException se o acesso a base foi fechado
     */
    public void redo() throws RedoException, SistemaEncerradoException {
        Memento m = history.getRedo();
        session.restore(m);
    }

    /**
     * Cria uma nova {@link AgendaPagamento} na base de dados
     * @param descricao descrição da agenda
     * @throws Exception se falhar em alguma verificação
     */
    public void criarAgendaDePagamentos(String descricao) throws Exception{
        daos.getAgendaDao().create(descricao);
    }
}
