package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EmpregadoDao {

    private static EmpregadoDao instance;
    private List<Empregado> empregados;
    private static final String FILE_PATH = "empregados.xml";

    private EmpregadoDao(){
        this.empregados = listarEmpregados();
    }

    public static EmpregadoDao getInstance() {
        if(instance == null)
        {
            instance = new EmpregadoDao();
        }
        return instance;
    }

    public static EmpregadoDao resetInstance(){
        instance = new EmpregadoDao();
        return instance;
    }

    public void salvarEmpregado(Empregado empregado)  {
        XMLEncoder encoder;
        empregados.add(empregado);
        try (BufferedOutputStream file = new BufferedOutputStream(new FileOutputStream(FILE_PATH))) {
            encoder = new XMLEncoder(file);
            for(Empregado emp: empregados)
            {
                encoder.writeObject(emp);
            }
            encoder.close();
        } catch (IOException e) {
        }
    }

    public void atualizarEmpregados(){
        XMLEncoder encoder;
        try (BufferedOutputStream file = new BufferedOutputStream(new FileOutputStream(FILE_PATH))) {
            encoder = new XMLEncoder(file);
            for(Empregado emp: empregados)
            {
                encoder.writeObject(emp);
            }
            encoder.close();
        } catch (IOException e) {
        }
    }


    public List<Empregado> listarEmpregados(){
        XMLDecoder decoder = null;
        List<Empregado> empregados = new ArrayList<>();
        try(BufferedInputStream file = new BufferedInputStream(new FileInputStream(FILE_PATH))){
            decoder = new XMLDecoder(file);
            while(true){
                try{
                    empregados.add((Empregado) decoder.readObject());
                }catch (Exception e) {
                    break;
                }
            }
            decoder.close();
        }catch (IOException e){
        }
        return empregados;
    }

    public Empregado getEmpregadoById(String id) throws Exception {

        if(id.isEmpty())
        {
            throw new Exception("Identificacao do empregado nao pode ser nula.");
        }

        for(Empregado empregado: empregados){
            if(empregado.getId().contains(id)){
                return empregado;
            }
        }
        throw new EmpregadoNaoExisteException();
    }

    public List<String> getEmpregadoByName(String nome) throws Exception{
        List<String> empregadosPorNome = new ArrayList<>();

        for(Empregado empregado: empregados){
            if(empregado.getNome().equals(nome)){
                empregadosPorNome.add(empregado.getId());
            }
        }

        if (empregadosPorNome.isEmpty()) throw new Exception("Nao ha empregado com esse nome.");

        return empregadosPorNome;
    }

    public void deleteEmpregadoById(String id) throws Exception {

        if(id.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");

        for(Empregado empregado: empregados){
            if(empregado.getId().equals(id)){
                empregados.remove(empregado);
                atualizarEmpregados();
                return;
            }
        }

        throw new Exception("Empregado nao existe.");


    }


}
