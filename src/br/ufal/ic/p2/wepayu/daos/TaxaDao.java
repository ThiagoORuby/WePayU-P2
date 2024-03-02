package br.ufal.ic.p2.wepayu.daos;

import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.ResultadoDeVenda;
import br.ufal.ic.p2.wepayu.models.TaxaServico;
import br.ufal.ic.p2.wepayu.services.DBManager;
import br.ufal.ic.p2.wepayu.services.Utils;

/**
 * Data Access Object (DAO) para gerenciamento de Taxas de Serviço
 * @author thomruby
 */
public class TaxaDao {

    private final DBManager session;

    /**
     * Cria uma instância de {@link TaxaDao} com base em uma sessão
     * @param session {@link DBManager} da sessão do banco de dados
     */
    public TaxaDao(DBManager session) {
        this.session = session;
    }

    /**
     * Cria uma nova {@link TaxaServico} na base de dados
     * @param empregado empregado sindicalizado relacionado a taxa
     * @param data data da criação da taxa
     * @param valor valor da taxa
     * @throws Exception se falhar em alguma verificação
     */
    public void create(Empregado empregado, String data, String valor) throws Exception{
        TaxaServico taxa = new TaxaServico(
                Utils.validarData(data),
                Utils.validarValor(valor));

        empregado.getMembroSindicato().setTaxaServico(taxa);
        session.commit();
    }

}
