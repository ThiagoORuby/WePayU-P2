package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.services.DBManager;

public class DaoManager {

    private final DBManager session;
    private EmpregadoDao empregadoDao;
    private CartaoDao cartaoDao;
    private TaxaDao taxaDao;
    private VendaDao vendaDao;


    public DaoManager(DBManager session){
        this.session = session;
    }

    public void reset(DBManager session) {
        this.empregadoDao = new EmpregadoDao(session);
        this.cartaoDao = new CartaoDao(session);
        this.vendaDao = new VendaDao(session);
        this.taxaDao = new TaxaDao(session);
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

}
