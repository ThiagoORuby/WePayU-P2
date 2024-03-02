package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.exceptions.AgendaJaExistenteException;
import br.ufal.ic.p2.wepayu.models.AgendaPagamento;
import br.ufal.ic.p2.wepayu.services.DBManager;

/**
 * Data Access Object (DAO) para gerenciamento de Agendas de Pagamento
 * @author thomruby
 */
public class AgendaDao {

    private final DBManager session;

    /**
     * Cria uma instância de {@link AgendaDao} com base em uma sessão
     * @param session {@link DBManager} da sessão do banco de dados
     */
    public AgendaDao(DBManager session){
        this.session = session;
    }

    /**
     * Cria uma nova {@link AgendaPagamento} na base de dados
     * @param descricao descrição da agenda
     * @throws Exception se falhar em alguma verificação
     */
    public void create(String descricao) throws Exception {
        if(find(descricao)) throw new AgendaJaExistenteException();
        session.addAgenda(new AgendaPagamento(descricao));
    }

    /**
     * Procura uma {@link AgendaPagamento} com base em uma descrição
     * @param descricao descrição da agenda
     * @return true, se existir na base de dados, false, c.c.
     */
    public boolean find(String descricao){
        for(AgendaPagamento ag: session.queryAgendas()){
            if(ag.toString().equals(descricao)) return true;
        }
        return false;
    }
}
