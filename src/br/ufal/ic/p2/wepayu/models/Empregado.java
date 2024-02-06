package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.util.*;

public class Empregado implements Serializable{

    private String id;
    private String nome;
    private String endereco;
    private String tipo;

    private MembroSindicato membroSindicato;
    private MetodoPagamento metodoPagamento;

    public Empregado(){}
    public Empregado(String nome, String endereco, String tipo) throws Exception {
        setId(UUID.randomUUID().toString());
        setNome(nome);
        setEndereco(endereco);
        setTipo(tipo);
        setMetodoPagamento(new MetodoPagamento("emMaos"));
    }

    public String getId() { return id; }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTipo() {
        return tipo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) throws Exception {
        if(nome.isEmpty()){
            throw new Exception("Nome nao pode ser nulo.");
        }
        this.nome = nome;
    }

    public void setEndereco(String endereco) throws Exception {
        if(endereco.isEmpty()) {
            throw new Exception("Endereco nao pode ser nulo.");
        }
        this.endereco = endereco;
    }

    public void setTipo(String tipo) throws Exception {
        this.tipo = tipo;
    }

    public boolean getSindicalizado(){
        return (membroSindicato != null);
    }

    public MembroSindicato getMembroSindicato() {
        return membroSindicato;
    }

    public void setMembroSindicato(MembroSindicato membroSindicato) {
        this.membroSindicato = membroSindicato;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }
}
