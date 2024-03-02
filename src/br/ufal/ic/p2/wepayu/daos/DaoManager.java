package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.MembroSindicato;
import br.ufal.ic.p2.wepayu.services.DBManager;

/**
 * Gerenciador de DAOs da aplicação
 * @author thomruby
 */
public class DaoManager {

    private final DBManager session;
    private EmpregadoDao empregadoDao;
    private CartaoDao cartaoDao;
    private TaxaDao taxaDao;
    private VendaDao vendaDao;
    private MembroDao membroDao;
    private AgendaDao agendaDao;


    public DaoManager(DBManager session){
        this.session = session;
    }

    public EmpregadoDao getEmpregadoDao() {
        if(empregadoDao == null){
            empregadoDao = new EmpregadoDao(session);
        }
        return empregadoDao;
    }

    public CartaoDao getCartaoDao() {
        if(cartaoDao == null){
            cartaoDao = new CartaoDao(session);
        }
        return cartaoDao;
    }

    public VendaDao getVendaDao() {
        if(vendaDao == null){
            vendaDao = new VendaDao(session);
        }
        return vendaDao;
    }

    public TaxaDao getTaxaDao() {
        if(taxaDao == null){
            taxaDao = new TaxaDao(session);
        }
        return taxaDao;
    }

    public MembroDao getMembroDao(){
        if(membroDao == null){
            membroDao = new MembroDao(session);
        }
        return membroDao;
    }

    public AgendaDao getAgendaDao(){
        if(agendaDao == null){
            agendaDao = new AgendaDao(session);
        }
        return agendaDao;
    }


}
