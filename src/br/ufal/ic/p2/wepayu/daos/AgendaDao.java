package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.models.AgendaPagamento;
import br.ufal.ic.p2.wepayu.services.DBManager;

public class AgendaDao {

    private final DBManager session;

    public AgendaDao(DBManager session){
        this.session = session;
    }
    public void create(String descricao) throws Exception {
        AgendaPagamento agenda = new AgendaPagamento(descricao);
        if(find(agenda)) throw new Exception("Agenda ja existe.");
        session.addAgenda(agenda);
        //session.commitAgendas();
    }

    public boolean find(AgendaPagamento agenda){
        for(AgendaPagamento ag: session.queryAgendas()){
            if(ag.equals(agenda)) return true;
        }
        return false;
    }
}
