package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.Exception.AtributoInexistenteException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Utils;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoAssalariado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;

public class EmpregadoDao {

    private static EmpregadoDao instance;
    private LinkedHashMap<String, Empregado> empregados;

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

    public Empregado criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception
    {
        Empregado empregado = null;
        switch (tipo) {
            case ("horista") -> {
                empregado = new EmpregadoHorista(nome, endereco, tipo, Utils.formatarValor(salario, "Salario", "o"));
            }
            case ("assalariado") -> {
                empregado = new EmpregadoAssalariado(nome, endereco, tipo, Utils.formatarValor(salario, "Salario", "o"));
            }
            default -> Utils.checaTipo(tipo);
        }
        return empregado;
    }

    public Empregado criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception{
        EmpregadoComissionado empregado;
        switch (tipo){
            case ("comissionado") ->  empregado = new EmpregadoComissionado(nome, endereco, tipo, Utils.formatarValor(salario, "Salario", "o"), Utils.formatarValor(comissao, "Comissao", "a"));
            default -> throw new Exception("Tipo nao aplicavel.");
        }

        return empregado;
    }


    public void salvarEmpregado(Empregado empregado)  {
        XMLEncoder encoder;
        empregados.put(empregado.getId(), empregado);
        try (BufferedOutputStream file = new BufferedOutputStream(new FileOutputStream(FILE_PATH))) {
            encoder = new XMLEncoder(file);
            empregados.forEach((id, emp) -> {
                encoder.writeObject(emp);
            });
            encoder.close();
        } catch (IOException e) {
        }
    }

    public void atualizarEmpregados(){
        XMLEncoder encoder;
        try (BufferedOutputStream file = new BufferedOutputStream(new FileOutputStream(FILE_PATH))) {
            encoder = new XMLEncoder(file);
            empregados.forEach((id, empregado) -> {
                encoder.writeObject(empregado);
            });
            encoder.close();
        } catch (IOException e) {
        }
    }


    public LinkedHashMap<String, Empregado> listarEmpregados(){
        XMLDecoder decoder = null;
        LinkedHashMap<String, Empregado> empregados = new LinkedHashMap<>();
        try(BufferedInputStream file = new BufferedInputStream(new FileInputStream(FILE_PATH))){
            decoder = new XMLDecoder(file);
            while(true){
                try{
                    Empregado aux = (Empregado) decoder.readObject();
                    empregados.put(aux.getId(), aux);
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

        Empregado empregado = empregados.get(id);

        if(empregado == null) throw new EmpregadoNaoExisteException();
        return empregado;
    }

    public List<String> getEmpregadoByName(String nome) throws Exception{
        List<String> empregadosPorNome = new ArrayList<>();

        for(Map.Entry<String, Empregado> emp: empregados.entrySet()) {
            Empregado empregado = emp.getValue();
            if(empregado.getNome().equals(nome)){
                empregadosPorNome.add(emp.getKey());
            }
        }

        if (empregadosPorNome.isEmpty()) throw new Exception("Nao ha empregado com esse nome.");

        return empregadosPorNome;
    }

    public Empregado getEmpregadoByIdSindicato(String idSindicato) throws Exception{

        if(idSindicato.isEmpty()) throw new Exception("Identificacao do membro nao pode ser nula.");

        for(Map.Entry<String, Empregado> emp: empregados.entrySet()) {
            Empregado empregado = emp.getValue();
            if(empregado.getSindicalizado())
            {
                if(empregado.getMembroSindicato().getIdMembro().equals(idSindicato)) {
                    return empregado;
                }
            }
        }
        throw new Exception("Membro nao existe.");
    }

    public void updateAtributoEmpregado(String id, String atributo, String valor, String valor1) throws Exception
    {
        Empregado empregado = getEmpregadoById(id);
        String[] valores = {"true", "false"};
        switch (atributo) {
            case ("nome") -> empregado.setNome(valor);
            case ("endereco") -> empregado.setEndereco(valor);
            case ("sindicalizado") -> {
                valor = Utils.validarAtributo(valor, valores, "Valor", true);
                if(valor.equals("false")) empregado.setMembroSindicato(null);
            }
            case ("tipo") -> {
                updateTipoEmpregado(empregado, valor, valor1);
            }
            default ->
            {
                switch (empregado.getClass().getSimpleName()) {
                    case ("EmpregadoAssalariado") ->
                            updateAtributoEmpregadoAssalariado((EmpregadoAssalariado) empregado, atributo, valor);
                    case ("EmpregadoHorista") ->
                            updateAtributoEmpregadoHorista((EmpregadoHorista) empregado, atributo, valor);
                    case ("EmpregadoComissionado") ->
                            updateAtributoEmpregadoComissionado((EmpregadoComissionado) empregado, atributo, valor);
                    default -> throw new AtributoInexistenteException();
                }
            }
        }
        atualizarEmpregados();
    }

    public void updateTipoEmpregado(Empregado e, String tipo, String valor) throws Exception{
        String id = e.getId();
        Empregado novoEmp = null;
        switch (tipo)
        {
            case ("assalariado") -> {
                novoEmp = converteParaAssalariado(e, e.getTipo(), valor);
            }
            case ("horista") -> {
                novoEmp = converteParaHorista(e, e.getTipo(), valor);
            }
            case ("comissionado") -> {
                novoEmp = converteParaComissionado(e, e.getTipo(), valor);
            }
            default -> throw new Exception("Tipo invalido.");
        }

        novoEmp.setId(id);
        empregados.put(id, novoEmp);
    }

    public Empregado converteParaAssalariado(Empregado e, String tipo, String valor) throws Exception{
        String salario = null;
        switch (tipo)
        {
            case ("comissionado") -> {
                salario = Utils.doubleToString(((EmpregadoComissionado) e).getSalarioMensal(), false);
            }
            case ("horista") -> {
                salario = Utils.doubleToString(((EmpregadoHorista) e).getSalarioPorHora(), false);
            }
            default -> {
                ((EmpregadoAssalariado) e).setSalarioMensal(Utils.formatarValor(valor, "Salario", "o"));
                return e;
            }
        }
        return criarEmpregado(e.getNome(), e.getEndereco(), "assalariado", valor != null ? valor : salario);
    }

    public Empregado converteParaHorista(Empregado e, String tipo, String valor) throws Exception{
        String salario = null;
        switch (tipo) {
            case ("comissionado") -> {
                salario = Utils.doubleToString(((EmpregadoComissionado) e).getSalarioMensal(), false);
            }
            case ("assalariado") -> {
                salario = Utils.doubleToString(((EmpregadoAssalariado) e).getSalarioMensal(), false);
            }
            default -> {
                ((EmpregadoHorista) e).setSalarioPorHora(Utils.formatarValor(valor, "Salario", "o"));
                return e;
            }
        }
        return criarEmpregado(e.getNome(), e.getEndereco(), "horista", valor != null ? valor : salario);
    }

    public Empregado converteParaComissionado(Empregado e, String tipo, String valor) throws Exception{
        String salario = null;
        switch (tipo)
        {
            case ("horista") -> {
                salario = Utils.doubleToString(((EmpregadoHorista) e).getSalarioPorHora(), false);
            }
            case ("assalariado") -> {
                salario = Utils.doubleToString(((EmpregadoAssalariado) e).getSalarioMensal(), false);
            }
            default -> {
                ((EmpregadoComissionado) e).setTaxaDeComissao(Utils.formatarValor(valor, "Comissao", "a"));
                return e;
            }
        }
        return criarEmpregado(e.getNome(), e.getEndereco(), "comissionado", salario, valor);
    }

    public void updatePagamentoEmpregado(String emp, String tipo, String banco, String agencia, String contaCorrente) throws Exception{
        Empregado empregado = getEmpregadoById(emp);

        String[] tipos = {"banco", "emMaos", "correios"};
        tipo = Utils.validarAtributo(tipo, tipos, "Metodo de pagamento", false);
        banco = Utils.validarAtributo(banco, "Banco", "o");
        agencia = Utils.validarAtributo(agencia, "Agencia", "o");
        contaCorrente = Utils.validarAtributo(contaCorrente, "Conta corrente", "o");

        empregado.getMetodoPagamento().setTipo(tipo);
        empregado.getMetodoPagamento().setBanco(banco);
        empregado.getMetodoPagamento().setAgencia(agencia);
        empregado.getMetodoPagamento().setContaCorrente(contaCorrente);
        atualizarEmpregados();
    }


    private void updateAtributoEmpregadoHorista(EmpregadoHorista empregado, String atributo, String valor) throws Exception {
        switch (atributo){
            case ("salario") -> empregado.setSalarioPorHora(Utils.formatarValor(valor, "Salario", "o"));
            case ("comissao") -> throw new Exception("Empregado nao eh comissionado.");
            default -> throw new AtributoInexistenteException();
        }
    }

    private void updateAtributoEmpregadoComissionado(EmpregadoComissionado empregado, String atributo, String valor) throws Exception{
        switch (atributo){
            case ("salario") -> empregado.setSalarioMensal(Utils.formatarValor(valor, "Salario", "o"));
            case ("comissao") -> empregado.setTaxaDeComissao(Utils.formatarValor(valor, "Comissao", "a"));
            default -> throw new AtributoInexistenteException();
        }
    }

    private void updateAtributoEmpregadoAssalariado(EmpregadoAssalariado empregado, String atributo, String valor) throws Exception {
        switch (atributo){
            case ("salario") -> empregado.setSalarioMensal(Utils.formatarValor(valor, "Salario", "o"));
            case ("comissao") -> throw new Exception("Empregado nao eh comissionado.");
            default -> throw new AtributoInexistenteException();
        }
    }

    public void deleteEmpregadoById(String id) throws Exception {

        if(id.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");

        Empregado empregado = empregados.remove(id);
        if(empregado == null) throw new Exception("Empregado nao existe.");
    }

    public boolean idSindicadoExiste(String idSindicato)
    {
        for(Map.Entry<String, Empregado> emp: empregados.entrySet()) {
            Empregado empregado = emp.getValue();
            if (empregado.getSindicalizado()) {
                if (empregado.getMembroSindicato().getIdMembro().equals(idSindicato))
                    return true;
            }
        }
        return false;
    }

}
