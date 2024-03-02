package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.exceptions.TipoEmpregadoInvalidoException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.ResultadoDeVenda;
import br.ufal.ic.p2.wepayu.services.DBManager;
import br.ufal.ic.p2.wepayu.services.Utils;

/**
 * Data Access Object (DAO) para gerenciamento de Resultados de Vendas
 * @author thomruby
 */
public class VendaDao {

    private final DBManager session;

    /**
     * Cria uma instância de {@link VendaDao} com base em uma sessão
     * @param session {@link DBManager} da sessão do banco de dados
     */
    public VendaDao(DBManager session) {
        this.session = session;
    }

    /**
     * Cria um novo {@link ResultadoDeVenda} na base de dados
     * @param empregado empregado relacionado a venda
     * @param data data da venda
     * @param valor valor da venda
     * @throws Exception se falhar em alguma verificação
     */
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
