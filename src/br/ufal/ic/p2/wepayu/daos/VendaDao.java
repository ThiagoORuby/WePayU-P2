package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.exceptions.TipoEmpregadoInvalidoException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.ResultadoDeVenda;
import br.ufal.ic.p2.wepayu.services.DBManager;
import br.ufal.ic.p2.wepayu.services.Utils;

public class VendaDao {

    private final DBManager session;

    public VendaDao(DBManager session) {
        this.session = session;
    }

    public void create(Empregado empregado, String data, String valor) throws Exception{
        if(!empregado.getTipo().equals("comissionado"))
            throw new TipoEmpregadoInvalidoException("comissionado");

        ResultadoDeVenda venda = new ResultadoDeVenda(
                Utils.validarData(data),
                Utils.validarValor(valor));

        ((EmpregadoComissionado) empregado).setVenda(venda);
        session.commit();
    }
}
