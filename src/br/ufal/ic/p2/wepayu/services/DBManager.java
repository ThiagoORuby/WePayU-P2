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
     * Limpa o conteúdo do banco de dados
     */
    public void clearAll() {
        empregados.clear();
        restoreAgendas();
        try {
            Files.delete(Path.of(Settings.DB_PATH));
            Files.delete(Path.of(Settings.AGENDA_PATH));
        }catch (Exception e){
            System.out.println("Arquivo nao existe");
        }
        isClosed = false;
    }

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
            System.out.println("Arquivo nao encontrado");
            restoreAgendas();
        }
        return agendas;
    }

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
     * @return LinkedHashMap dos empregados
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
            System.out.println("Arquivo nao encontrado");
        }
        return empregados;
    }

    /**
     * Retorna o atributo empregados
     * @return LinkedHashMap dos empregados
     */
    public LinkedHashMap<String, Empregado> query() {
        return empregados;
    }

    public List<AgendaPagamento> queryAgendas(){
        return agendas;
    }

    /**
     * Salva os dados de empregados no XML
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

    public String size(){
        return String.valueOf(this.empregados.size());
    }

    public void restore(Memento snapshot) throws Exception {

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

    public void close(){
        isClosed = true;
    }

}
