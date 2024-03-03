package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.exceptions.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.services.DBManager;
import br.ufal.ic.p2.wepayu.services.Settings;
import br.ufal.ic.p2.wepayu.services.Utils;

import java.util.*;

/**
 * Data Access Object (DAO) para gerenciamento de Empregados
 * @author thomruby
 */
public class EmpregadoDao{

    private final DBManager session;

    /**
     * Cria uma instância de {@link EmpregadoDao} com base em uma sessão
     * @param session {@link DBManager} da sessão do banco de dados
     */
    public EmpregadoDao(DBManager session) {
        this.session = session;
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
    public Empregado create(String nome, String endereco, String tipo, String salario) throws Exception {
        Empregado empregado = null;
        switch (tipo) {
            case "horista" ->
                empregado = new EmpregadoHorista(nome,
                        endereco,
                        tipo,
                        Utils.formatarValor(salario, "Salario", "o"));

            case "assalariado" ->
                empregado = new EmpregadoAssalariado(nome,
                        endereco,
                        tipo,
                        Utils.formatarValor(salario, "Salario", "o"));

            default -> Utils.checarTipo(tipo);
        }
        session.add(empregado);
        session.commit();

        return empregado;
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
    public Empregado create(String nome, String endereco, String tipo, String salario, String comissao) throws Exception{
        EmpregadoComissionado empregado = null;
        if (tipo.equals("comissionado"))
            empregado = new EmpregadoComissionado(nome,
                    endereco,
                    tipo,
                    Utils.formatarValor(salario, "Salario", "o"),
                    Utils.formatarValor(comissao, "Comissao", "a"));
        else
            Utils.checarTipo(tipo);

        session.add(empregado);
        session.commit();

        return empregado;
    }

    /**
     * Retorna um empregado com base no id
     * @param id id do empregado
     * @return {@link Empregado} correspondente
     * @throws Exception se falhar em alguma verificação
     */
    public Empregado getById(String id) throws Exception {

        if(id.isEmpty())
        {
            throw new ValorNuloException("Identificacao do empregado", "a");
        }

        Empregado empregado = session.query().get(id);

        if(empregado == null) throw new EmpregadoNaoExisteException();
        return empregado;
    }

    /**
     * Retorna um empregado com base no nome
     * @param nome nome do empregado
     * @return {@link Empregado} correspondente
     * @throws EmpregadoNaoExisteException se o empregado não existir
     */
    public List<String> getByName(String nome) throws EmpregadoNaoExisteException {
        List<String> empregados = new ArrayList<>();

        for(Map.Entry<String, Empregado> emp: session.query().entrySet()) {
            Empregado empregado = emp.getValue();
            if(empregado.getNome().equals(nome)){
                empregados.add(emp.getKey());
            }
        }

        if (empregados.isEmpty()) throw new EmpregadoNaoExisteException("nome");

        return empregados;
    }

    /**
     * Retorna um empregado com base no id do sindicato
     * @param idSindicato id do sindicato
     * @return {@link Empregado} relacionado
     * @throws Exception se falhar em alguma verificação
     */
    public Empregado getByIdSindicato(String idSindicato) throws Exception {

        if(idSindicato.isEmpty()) throw new ValorNuloException("Identificacao do membro", "a");

        for(Map.Entry<String, Empregado> emp: session.query().entrySet()) {
            Empregado empregado = emp.getValue();
            if(empregado.getSindicalizado()) {
                if(empregado.getMembroSindicato().getIdMembro().equals(idSindicato)) {
                    return empregado;
                }
            }
        }
        throw new MembroNaoExisteException();
    }

    /**
     * Retorna o valor de um atributo do empregado
     * @param id id do empregado
     * @param atributo atributo buscado
     * @return valor do atributo buscado
     * @throws Exception se falhar em alguma verificação
     */
    public String getAtributo(String id, String atributo) throws Exception {
        Empregado empregado = getById(id);

        Utils.checarAtributo(empregado, atributo);

        return switch (atributo) {
            case "nome" ->  empregado.getNome();
            case "endereco" -> empregado.getEndereco();
            case "tipo" -> empregado.getTipo();
            case "salario" -> Utils.doubleToString(empregado.getSalario(), false);
            case "comissao" -> Utils.doubleToString(((EmpregadoComissionado) empregado).getComissao(),
                    false);
            case "sindicalizado" -> empregado.getSindicalizado() ? "true" : "false";
            case "metodoPagamento" -> empregado.getMetodoPagamento().getTipo();
            case "banco" ->  empregado.getMetodoPagamento().getBanco();
            case "agencia" ->  empregado.getMetodoPagamento().getAgencia();
            case "contaCorrente" -> empregado.getMetodoPagamento().getContaCorrente();
            case "idSindicato" -> empregado.getMembroSindicato().getIdMembro();
            case "taxaSindical" -> Utils.doubleToString(empregado.getMembroSindicato().getTaxaSindical(),
                    false);
            case "agendaPagamento" -> empregado.getAgendaPagamento().toString();
            default -> throw new AtributoNaoExisteException();
        };
    }

    /**
     * Atualiza o valor de um atributo pelo id do empregado
     * @param id id do empregado
     * @param atributo nome do atributo
     * @param valor novo valor do atributo
     * @param valor1 comissao caso haja atualização de tipo
     * @throws Exception se falhar em alguma verificação
     */
    public void updateAtributoById(String id, String atributo, String valor, String valor1) throws Exception
    {
        Empregado empregado = getById(id);

        Utils.checarAtributo(empregado, atributo);

        switch (atributo) {
            case "nome" -> empregado.setNome(valor);
            case "endereco" -> empregado.setEndereco(valor);
            case "sindicalizado" -> {
                valor = Utils.validarAtributo(valor, Settings.BOOLEANOS, "Valor", true);
                if(valor.equals("false")) empregado.setMembroSindicato(null);
            }
            case "tipo" -> updateTipo(empregado, valor, valor1);
            case "salario" -> empregado.setSalario(Utils.formatarValor(valor, "Salario", "o"));
            case "comissao" -> ((EmpregadoComissionado) empregado).setComissao(
                    Utils.formatarValor(valor, "Comissao", "a"));
            case "agendaPagamento" -> empregado.setAgendaPagamento(valor);
            default -> throw new AtributoNaoExisteException();
        }
        session.commit();
    }

    /**
     * Atualiza tipo do empregado
     * @param e {@link Empregado} a ser atualizado
     * @param tipo novo tipo
     * @param valor valor da comissão ou novo salario
     * @throws Exception se falhar em alguma verificação
     */
    public void updateTipo(Empregado e, String tipo, String valor) throws Exception {
        String id = e.getId();
        Empregado novoEmp;
        switch (tipo)
        {
            case "assalariado" ->
                novoEmp = convertToAssalariado(e, valor);
            case "horista" ->
                novoEmp = convertToHorista(e, valor);
            case "comissionado" ->
                novoEmp = convertToComissionado(e, valor);
            default -> throw new TipoInvalidoException();
        }

        novoEmp.setId(id);
        session.update(id, novoEmp);
    }

    /**
     * Converte um empregado em assalariado
     * @param e {@link Empregado} a ser convertido
     * @param valor valor do novo salário
     * @return {@link EmpregadoAssalariado} criado
     * @throws Exception se falhar em alguma verificação
     */
    public Empregado convertToAssalariado(Empregado e, String valor) throws Exception {

        String salario = Utils.doubleToString(e.getSalario(), false);
        return create(e.getNome(), e.getEndereco(), "assalariado", valor != null ? valor : salario);
    }

    /**
     * Converte um empregado em horista
     * @param e {@link Empregado} a ser convertido
     * @param valor valor do novo salário
     * @return {@link EmpregadoHorista} criado
     * @throws Exception se falhar em alguma verificação
     */
    public Empregado convertToHorista(Empregado e, String valor) throws Exception{
        String salario = Utils.doubleToString(e.getSalario(), false);
        return create(e.getNome(), e.getEndereco(), "horista", valor != null ? valor : salario);
    }

    /**
     * Converte um empregado em comissionado
     * @param e {@link Empregado} a ser convertido
     * @param valor valor da nova comissao ou salário
     * @return {@link EmpregadoComissionado} criado
     * @throws Exception se falhar em alguma verificação
     */
    public Empregado convertToComissionado(Empregado e, String valor) throws Exception{
        String salario = Utils.doubleToString(e.getSalario(), false);
        return create(e.getNome(), e.getEndereco(), "comissionado", salario, valor);
    }

    /**
     * Atualiza método de pagamento do empregado
     * @param id id do empregado
     * @param tipo tipo do método de pagamento
     * @param banco nome do banco, se for pagamento em banco
     * @param agencia número da agência, se for pagamento em banco
     * @param contaCorrente número da conta, se for pagamento em banco
     * @throws Exception se falhar em alguma verificação
     */
    public void updatePagamentoEmpregado(String id, String tipo, String banco, String agencia, String contaCorrente) throws Exception {
        Empregado empregado = getById(id);


        tipo = Utils.validarAtributo(tipo, Settings.METODOS_PAGAMENTO, "Metodo de pagamento", false);
        banco = Utils.validarAtributo(banco, "Banco", "o");
        agencia = Utils.validarAtributo(agencia, "Agencia", "o");
        contaCorrente = Utils.validarAtributo(contaCorrente, "Conta corrente", "o");

        MetodoPagamento metodo = empregado.getMetodoPagamento();
        metodo.setTipo(tipo);
        metodo.setBanco(banco);
        metodo.setAgencia(agencia);
        metodo.setContaCorrente(contaCorrente);

        session.commit();
    }

    /**
     * Remove um empregado com base em seu id
     * @param id id do empregado
     * @throws ValorNuloException se o id for nulo
     * @throws EmpregadoNaoExisteException se o empregado não existir
     */
    public void deleteById(String id) throws ValorNuloException, EmpregadoNaoExisteException {

        if(id.isEmpty())
            throw new ValorNuloException("Identificacao do empregado", "a");

        Empregado empregado = session.query().remove(id);
        session.commit();
        if(empregado == null)
            throw new EmpregadoNaoExisteException();
    }

    /**
     * Verifica se um id de sindicato já existe entre os empregados
     * @param idSindicato id do sindicato a ser verificado
     * @throws EmpregadoJaExisteException se já existir empregado com este id
     */
    public void checkSindicadoId(String idSindicato) throws EmpregadoJaExisteException
    {
        for(Map.Entry<String, Empregado> emp: session.query().entrySet()) {
            Empregado empregado = emp.getValue();
            if (empregado.getSindicalizado()) {
                if (empregado.getMembroSindicato().getIdMembro().equals(idSindicato))
                    throw new EmpregadoJaExisteException();
            }
        }
    }

}
