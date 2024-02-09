package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.MembroSindicato;
import br.ufal.ic.p2.wepayu.services.DBManager;

public class MembroDao {

    private final DBManager session;

    public MembroDao(DBManager session){
        this.session = session;
    }

    public void create(Empregado empregado, String idSindicato, Double taxa){
        MembroSindicato membro = new MembroSindicato(idSindicato,
                taxa);

        empregado.setMembroSindicato(membro);
        session.commit();
    }
}
