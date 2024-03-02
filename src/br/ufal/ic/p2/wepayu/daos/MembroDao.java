package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.MembroSindicato;
import br.ufal.ic.p2.wepayu.services.DBManager;

/**
 * Data Access Object (DAO) para gerenciamento de Membros do Sindicato
 * @author thomruby
 */
public class MembroDao {

    private final DBManager session;

    /**
     * Cria uma instância de {@link MembroDao} com base em uma sessão
     * @param session {@link DBManager} da sessão do banco de dados
     */
    public MembroDao(DBManager session){
        this.session = session;
    }

    /**
     * Cria um {@link MembroSindicato} na base de dados
     * @param empregado empregado que será sindicalizado
     * @param idSindicato id no sindicato
     * @param taxa taxa sindical
     */
    public void create(Empregado empregado, String idSindicato, Double taxa){
        MembroSindicato membro = new MembroSindicato(idSindicato,
                taxa);

        empregado.setMembroSindicato(membro);
        session.commit();
    }
}
