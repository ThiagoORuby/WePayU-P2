package br.ufal.ic.p2.wepayu.services;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Constantes de configurações da aplicação
 * @author thomruby
 */
public class Settings {

    public static final String DB_PATH = "db.xml";
    public static final String AGENDA_PATH = "agendas.xml";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
    public static final List<String> TIPOS = Arrays.asList("horista", "comissionado", "assalariado");

    public static final String[] METODOS_PAGAMENTO = {"banco", "emMaos", "correios"};
    public static final String[] BOOLEANOS = {"true", "false"};
    public static final String[] AGENDAS_PADRAO = {"semanal 5", "semanal 2 5", "mensal $"};
    public static final String[] TIPOS_AGENDAS = {"semanal", "mensal"};

    public static final String HEADER_HORISTAS = "templates/horistas.txt";
    public static final String HEADER_ASSALARIADOS = "templates/assalariados.txt";
    public static final String HEADER_COMISSIONADOS = "templates/comissionados.txt";

}
