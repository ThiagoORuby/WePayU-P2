package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.models.Empregado;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * Classe de manipulação da base de dados (XML)
 * @author thomruby
 */
public class DBManager {

    private static DBManager session;
    private final LinkedHashMap<String, Empregado> empregados;


    private DBManager() {
        this.empregados = getAllData();
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
        try {
            Files.delete(Path.of(Settings.DB_PATH));
        }catch (Exception e){
            System.out.println("Arquivo nao existe");
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

    /**
     * Adiciona um novo empregado
     * @param empregado {@link Empregado} a ser adicionado
     */
    public void add(Empregado empregado) {
        empregados.put(empregado.getId(), empregado);
    }

    /**
     * Atualiza os dados de um empregado
     * @param id id do empregado
     * @param empregado {@link Empregado} a ser atualizado
     */
    public void update(String id, Empregado empregado) {
        empregados.put(id, empregado);
    }

}
