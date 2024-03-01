package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.exceptions.TipoEmpregadoInvalidoException;
import br.ufal.ic.p2.wepayu.models.CartaoDePonto;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;
import br.ufal.ic.p2.wepayu.services.DBManager;
import br.ufal.ic.p2.wepayu.services.Utils;

public class CartaoDao {

    private final DBManager session;

    public CartaoDao(DBManager session) {
        this.session = session;
    }

    public void create(Empregado empregado, String data, String horas) throws Exception{
        if(!empregado.getTipo().equals("horista"))
            throw new TipoEmpregadoInvalidoException("horista");

        CartaoDePonto cartao = new CartaoDePonto(
                Utils.validarData(data)
                , Utils.validarHoras(horas));

        ((EmpregadoHorista) empregado).setCartao(cartao);
        session.commit();
    }
}
