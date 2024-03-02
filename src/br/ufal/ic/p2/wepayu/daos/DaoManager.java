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

    /**
     * Retorna uma instância do {@link DaoManager} com base em uma sessão
     * @param session {@link DBManager} da sessão do banco de dados
     */
    public DaoManager(DBManager session){
        this.session = session;
    }

    /**
     * Retorna uma instância do DAO de Empregados
     * @return instância de {@link EmpregadoDao}
     */
    public EmpregadoDao getEmpregadoDao() {
        if(empregadoDao == null){
            empregadoDao = new EmpregadoDao(session);
        }
        return empregadoDao;
    }

    /**
     * Retorna uma instância do DAO de Cartões de Ponto
     * @return instância de {@link CartaoDao}
     */
    public CartaoDao getCartaoDao() {
        if(cartaoDao == null){
            cartaoDao = new CartaoDao(session);
        }
        return cartaoDao;
    }

    /**
     * Retorna uma instância do DAO de Resultados de Vendas
     * @return instância de {@link VendaDao}
     */
    public VendaDao getVendaDao() {
        if(vendaDao == null){
            vendaDao = new VendaDao(session);
        }
        return vendaDao;
    }

    /**
     * Retorna uma instância do DAO de Taxas de Serviços
     * @return instância de {@link TaxaDao}
     */
    public TaxaDao getTaxaDao() {
        if(taxaDao == null){
            taxaDao = new TaxaDao(session);
        }
        return taxaDao;
    }

    /**
     * Retorna uma instância do DAO de Membros do Sindicato
     * @return instância de {@link MembroDao}
     */
    public MembroDao getMembroDao(){
        if(membroDao == null){
            membroDao = new MembroDao(session);
        }
        return membroDao;
    }

    /**
     * Retorna uma instância do DAO de Agendas de Pagamento
     * @return instância de {@link AgendaDao}
     */
    public AgendaDao getAgendaDao(){
        if(agendaDao == null){
            agendaDao = new AgendaDao(session);
        }
        return agendaDao;
    }


}
