package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.models.Empregado;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public class DBManager {

    private static DBManager session;

    private final LinkedHashMap<String, Empregado> empregados;

    private DBManager() {
        this.empregados = getAllData();
    }

    public static DBManager getSession() {
        if(session == null)
        {
            session = new DBManager();
        }
        return session;
    }

    public void clearALl() throws Exception{
        try {
            Files.delete(Path.of("db.xml"));
        }catch (Exception e){
            throw new Exception("Arquivo nao existe.");
        }
    }

    public DBManager reset() throws Exception{
        session = new DBManager();
        return session;
    }


    private LinkedHashMap<String, Empregado> getAllData()
    {
        LinkedHashMap<String, Empregado> empregados = new LinkedHashMap<>();

        try(BufferedInputStream file = new BufferedInputStream(new FileInputStream(Constants.DB_PATH))){
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

    public LinkedHashMap<String, Empregado> query(){
        return empregados;
    }

    public void commit() throws Exception{
        try (BufferedOutputStream file = new BufferedOutputStream(new FileOutputStream(Constants.DB_PATH))) {
            XMLEncoder encoder = new XMLEncoder(file);
            empregados.forEach((id, empregado) -> {
                encoder.writeObject(empregado);
            });
            encoder.close();
        } catch (IOException e) {
            System.out.println("Arquivo nao encontrado");
        }
    }

    public void add(Empregado empregado) throws Exception{
        empregados.put(empregado.getId(), empregado);
    }

    public void update(String id, Empregado empregado) throws Exception{
        empregados.put(id, empregado);
    }


}
