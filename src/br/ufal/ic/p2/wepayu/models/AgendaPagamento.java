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

    /**
     * Cria uma nova agenda de pagamento com base na descrição
     * @param descricao descrição da agenda
     * @throws DescricaoInvalidaException se a descrição estiver mal formatada
     */
    public AgendaPagamento(String descricao) throws DescricaoInvalidaException {
        // guarda a descrição
        setDescricao(descricao);

        // quebra nos espaços em branco
        String[] splitted = descricao.split("\\s+");

        // se existir um Tipo
        if(splitted.length >= 1){
            // guarda o tipo se for válido
            String tipo = splitted[0];
            if(!Arrays.asList(Settings.TIPOS_AGENDAS).contains(tipo))
                throw new DescricaoInvalidaException();
            setTipo(tipo);
        }

        // se existir informação do intervalo de semanas
        if(splitted.length >= 3){
            // guarda intervalo de semanas se for válido
            int semana = Integer.parseInt(splitted[1]);
            if(semana > 52 || semana < 1)
                throw new DescricaoInvalidaException();
            setSemana(semana);
        }

        // se existir informação do dia de pagamento
        if(splitted.length >= 3 || splitted.length == 2){
            // se for último dia do mês
            if(splitted[splitted.length - 1].equals("$")){
                setDia(-1);
            }
            else {
                int dia = Integer.parseInt(splitted[splitted.length - 1]);
                // Se for mensal, guarda dia do mẽs
                if(splitted[0].equals("mensal")){
                    if(dia > 28 || dia < 1) throw new DescricaoInvalidaException();
                }
                // se for semanal, guarda o dia da semana
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

    /***
     * Retorna a descrição inline da agenda
     * @return descriçaõ inline da agenda
     */
    @Override
    public String toString() {
        return descricao;
    }

    /**
     * Cria uma cópia da Agenda de Pagamento
     * @return {@link AgendaPagamento} cópia
     */
    @Override
    public AgendaPagamento clone() {
        try {
            return (AgendaPagamento) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
