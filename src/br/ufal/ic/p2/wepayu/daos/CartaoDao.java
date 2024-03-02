package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.exceptions.TipoEmpregadoInvalidoException;
import br.ufal.ic.p2.wepayu.models.CartaoDePonto;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;
import br.ufal.ic.p2.wepayu.services.DBManager;
import br.ufal.ic.p2.wepayu.services.Utils;

/**
 * Data Access Object (DAO) para gerenciamento de Cartões de Ponto
 * @author thomruby
 */
public class CartaoDao {

    private final DBManager session;

    /**
     * Cria uma instância de {@link CartaoDao} com base em uma sessão
     * @param session {@link DBManager} da sessão do banco de dados
     */
    public CartaoDao(DBManager session) {
        this.session = session;
    }

    /**
     * Cria um novo {@link CartaoDePonto} na base de dados
     * @param empregado empregado relacionado ao cartão
     * @param data data de lançamento
     * @param horas horas trabalhadas
     * @throws Exception se falhar em alguma validação
     */
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
