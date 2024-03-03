package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.exceptions.SistemaEncerradoException;
import br.ufal.ic.p2.wepayu.models.AgendaPagamento;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Classe de manipulação da base de dados (XML)
 * @author thomruby
 */
public class DBManager {

    private static DBManager session;
    private boolean isClosed;
    private LinkedHashMap<String, Empregado> empregados;
    private List<AgendaPagamento> agendas;

    /**
     * Cria um {@link DBManager} e inicializa os dados da base
     */
    private DBManager() {
        this.empregados = getAllData();
        this.isClosed = false;
        this.agendas = getAllAgendas();
    }

    /**
     * Retorna uma instância única de acesso ao banco de dados
     * @return singleton para acesso ao banco
     */
    public static DBManager getSession() {
        if(session == null)
        {
            session = new DBManager();
        }
        return session;
    }

    /**
     * Limpa o conteúdo do banco de dados e restaura lista de agendas
     */
    public void clearAll() {
        empregados.clear();
        restoreAgendas();
        try {
            Files.delete(Path.of(Settings.DB_PATH));
            Files.delete(Path.of(Settings.AGENDA_PATH));
        }catch (Exception e){
            System.out.println("Arquivo nao existe para ser deletado");
        }
        isClosed = false;
    }

    /**
     * Retorna a lista de agendas de pagamento salvos no XML
     * @return lista de {@link AgendaPagamento}
     */
    private List<AgendaPagamento> getAllAgendas() {
        List<AgendaPagamento> agendas = new ArrayList<>();

        try(BufferedInputStream file = new BufferedInputStream(
                new FileInputStream(Settings.AGENDA_PATH))){
            XMLDecoder decoder = new XMLDecoder(file);
            while (true){
                try{
                    AgendaPagamento ag = (AgendaPagamento) decoder.readObject();
                    agendas.add(ag);
                }catch (Exception e){
                    break;
                }
            }
            decoder.close();
        }
        catch (IOException e){
            System.out.println("Arquivo nao encontrado, será criado em seguida");
            restoreAgendas();
        }
        return agendas;
    }

    /**
     * Restaura lista de agendas com base nas opções padrão
     */
    private void restoreAgendas(){
        try{
            agendas = Utils.getAgendasPadrao();
            commitAgendas();
        }catch (Exception e){
            System.out.println("Erro ou restaurar a agenda");
        }
    }

    /**
     * Retorna o LinkedHashMap com dados dos empregados salvos no XML
     * @return LinkedHashMap de {@link Empregado}
     */
    private LinkedHashMap<String, Empregado> getAllData() {
        LinkedHashMap<String, Empregado> empregados = new LinkedHashMap<>();

        try(BufferedInputStream file = new BufferedInputStream(
                new FileInputStream(Settings.DB_PATH))){
            XMLDecoder decoder = new XMLDecoder(file);
            while(true){
                try{
                    Empregado aux = (Empregado) decoder.readObject();
                    empregados.put(aux.getId(), aux);
                }catch (Exception e) {
                    break;
                }
            }
            decoder.close();
        }catch (IOException e) {
            System.out.println("Arquivo nao encontrado, será criado em seguida");
        }
        return empregados;
    }

    /**
     * Retorna o atributo empregados
     * @return LinkedHashMap de {@link Empregado}
     */
    public LinkedHashMap<String, Empregado> query() {
        return empregados;
    }

    /**
     * Retorna o atributo agendas
     * @return lista de {@link AgendaPagamento}
     */
    public List<AgendaPagamento> queryAgendas(){
        return agendas;
    }

    /**
     * Salva os dados de empregados em XML
     */
    public void commit() {
        try (BufferedOutputStream file = new BufferedOutputStream(
                new FileOutputStream(Settings.DB_PATH))) {
            XMLEncoder encoder = new XMLEncoder(file);
            empregados.forEach((id, empregado) -> {
                encoder.writeObject(empregado);
            });
            encoder.close();
        } catch (IOException e) {
            System.out.println("Arquivo nao encontrado");
        }
    }

    /**
     * Salva os dados de agendas em XML
     */
    public void commitAgendas(){
        try (BufferedOutputStream file = new BufferedOutputStream(
                new FileOutputStream(Settings.AGENDA_PATH))) {
            XMLEncoder encoder = new XMLEncoder(file);
           for(AgendaPagamento agenda: agendas){
               encoder.writeObject(agenda);
           }
            encoder.close();
        } catch (IOException e) {
            System.out.println("Arquivo nao encontrado");
        }
    }

    /**
     * Adiciona um novo empregado
     * @param empregado {@link Empregado} a ser adicionado
     */
    public void add(Empregado empregado) {
        empregados.put(empregado.getId(), empregado);
    }

    /**
     * Adiciona uma nova agenda de pagamento
     * @param agenda {@link AgendaPagamento} a ser adicionada
     */
    public void addAgenda(AgendaPagamento agenda){
        agendas.add(agenda);
    }

    /**
     * Atualiza os dados de um empregado
     * @param id id do empregado
     * @param empregado {@link Empregado} a ser atualizado
     */
    public void update(String id, Empregado empregado) {
        empregados.put(id, empregado);
    }

    /**
     * Retorna o tamanho da base de dados de empregados
     * @return total de empregados
     */
    public String size(){
        return String.valueOf(this.empregados.size());
    }

    /**
     * Restaura os dados de empregados para um determinado estado
     * @param snapshot {@link Memento} que armazena um estado dos dados
     * @throws SistemaEncerradoException se o acesso a base foi fechado
     */
    public void restore(Memento snapshot) throws SistemaEncerradoException {

        if (isClosed) {
            throw new SistemaEncerradoException();
        }
        if (snapshot.getEmpregadosSnapshot() != null){
            this.empregados = new LinkedHashMap<>();
            snapshot.getEmpregadosSnapshot().forEach((id, emp) -> {
                empregados.put(id, emp.clone());
            });
        }
    }

    /**
     * Fecha o acesso a base de dados
     */
    public void close(){
        isClosed = true;
    }

}
