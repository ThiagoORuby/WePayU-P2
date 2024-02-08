package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.ResultadoDeVenda;
import br.ufal.ic.p2.wepayu.models.TaxaServico;
import br.ufal.ic.p2.wepayu.services.DBManager;
import br.ufal.ic.p2.wepayu.services.Utils;

public class TaxaDao {

    private final DBManager session;

    public TaxaDao(DBManager session) {
        this.session = session;
    }

    public void create(Empregado empregado, String data, String valor) throws Exception{
        TaxaServico taxa = new TaxaServico(
                Utils.validarData(data),
                Utils.validarValor(valor));

        empregado.getMembroSindicato().setTaxaServico(taxa);
        session.commit();
    }

}
