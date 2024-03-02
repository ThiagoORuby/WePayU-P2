package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.exceptions.AgendaJaExistenteException;
import br.ufal.ic.p2.wepayu.models.AgendaPagamento;
import br.ufal.ic.p2.wepayu.services.DBManager;

public class AgendaDao {

    private final DBManager session;

    public AgendaDao(DBManager session){
        this.session = session;
    }
    public void create(String descricao) throws Exception {
        if(find(descricao)) throw new AgendaJaExistenteException();
        session.addAgenda(new AgendaPagamento(descricao));
        //session.commitAgendas();
    }

    public boolean find(String descricao){
        for(AgendaPagamento ag: session.queryAgendas()){
            if(ag.toString().equals(descricao)) return true;
        }
        return false;
    }
}
