package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exceptions.DescricaoInvalidaException;
import br.ufal.ic.p2.wepayu.services.Settings;

import java.io.Serializable;
import java.util.Arrays;

public class AgendaPagamento implements Serializable, Cloneable {

    private String descricao;
    private String tipo;
    private int semana;
    private int dia;

    public AgendaPagamento(){}

    public AgendaPagamento(String descricao) throws DescricaoInvalidaException {
        setDescricao(descricao);
        String[] splitted = descricao.split("\\s+");
        if(splitted.length >= 1){
            String tipo = splitted[0];
            if(!Arrays.asList(Settings.TIPOS_AGENDAS).contains(tipo))
                throw new DescricaoInvalidaException();
            setTipo(tipo);
        }

        if(splitted.length >= 3){
            int semana = Integer.parseInt(splitted[1]);
            if(semana > 52 || semana < 1)
                throw new DescricaoInvalidaException();
            setSemana(semana);
        }

        if(splitted.length >= 3 || splitted.length == 2){
            if(splitted[splitted.length - 1].equals("$")){
                setDia(-1);
            }
            else {
                int dia = Integer.parseInt(splitted[splitted.length - 1]);
                if(splitted[0].equals("mensal")){
                    if(dia > 28 || dia < 1) throw new DescricaoInvalidaException();
                }
                else if(dia > 7 || dia < 1)
                    throw new DescricaoInvalidaException();
                setDia(dia);
            }
        }
    }
    public AgendaPagamento(String tipo, int semana, int dia){
        setTipo(tipo);
        setSemana(semana);
        setDia(dia);
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getSemana() {
        return semana;
    }

    public void setSemana(int semana) {
        this.semana = semana;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public AgendaPagamento clone() {
        try {
            return (AgendaPagamento) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
