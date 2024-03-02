package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.exceptions.*;
import br.ufal.ic.p2.wepayu.models.AgendaPagamento;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Métodos para manipulação dos dados da aplicaçãos
 * @author thomruby
 */
public class Utils {

    /**
     * Converte String em Double tratando Exceções
     * @param valor String a ser convertida
     * @param nome Nome associado ao valor
     * @param sufix Sufixo de desinência de gênero
     * @return valor em Double
     * @throws ValorNuloException
     * @throws ValorNaoNumericoException
     * @throws ValorNegativoException
     */
    public static Double formatarValor(String valor, String nome, String sufix) throws Exception {
        Double valorConvertido;
        try{
            valorConvertido = Double.parseDouble(valor.replace(",", "."));
        }
        catch (NumberFormatException e){
            if (valor.isEmpty())
            {
                throw new ValorNuloException(nome, sufix);
            }
            else{
                throw new ValorNaoNumericoException(nome, sufix);
            }
        }

        if(valorConvertido < 0){
            throw new ValorNegativoException(nome, sufix);
        }
        return valorConvertido;
    }

    /**
     * Converter um valor Double para um String
     * @param valor valor a ser convertido
     * @param dynamic booleano que indica a dinamicidade da parte decimal
     * @return valor convertido
     */
    public static String doubleToString(Double valor, boolean dynamic){;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        DecimalFormat formatter = new DecimalFormat(dynamic ? "#.##" : "0.00", symbols);
        return formatter.format(valor);
    }

    /**
     * Valida uma data em relação a quantidade de dias em cada mês
     * @param data String data a ser validada
     * @return String da data caso seja válida
     * @throws DataInvalidaException
     */
    public static String validarData(String data) throws DataInvalidaException {
        Pattern pattern = Pattern.compile("([0-3]?[0-9])/(0?[1-9]|1[0-2])/(\\d{4})");

        Matcher matcher = pattern.matcher(data);

        if(!matcher.matches())
        {
            throw new DataInvalidaException();
        }
        else{
            int dia = Integer.parseInt(matcher.group(1));
            int mes = Integer.parseInt(matcher.group(2));
            int year = Integer.parseInt(matcher.group(3));

            if (mes == 2)
            {
                if((Year.isLeap(year) && dia > 29) || dia > 28) throw new DataInvalidaException();
            }
            else if(mes == 5 || mes == 6 || mes == 9 || mes == 11) {
                if (dia > 30) throw new DataInvalidaException();
            }
            else{
                if(dia > 31) throw new DataInvalidaException();
            }
        }
        return data;
    }

    /**
     * Formata uma data String para LocalDate
     * @param data String data a ser convertida
     * @param nome Nome do atributo para exceção
     * @return {@link LocalDate} Data convertida
     * @throws Exception
     */
    public static LocalDate formatarData(String data, String nome) throws Exception{

        LocalDate dataParse;

        try {dataParse = LocalDate.parse(validarData(data), Settings.formatter);}
        catch (Exception e) {throw new DataInvalidaException(nome);}

        return dataParse;
    }

    /**
     * Verifica a validade de um atributo
     * @param e {@link Empregado}
     * @param atributo atributo a ser verificado
     * @throws Exception
     */
    public static void checarAtributo(Empregado e, String atributo) throws Exception{

        if(atributo.equals("comissao") && !e.getTipo().equals("comissionado"))
            throw new TipoEmpregadoInvalidoException("comissionado");

        if (atributo.equals("banco") || atributo.equals("agencia") || atributo.equals("contaCorrente"))
        {
            if(!e.getMetodoPagamento().getTipo().equals("banco"))
                throw new MetodoPagamentoException();
        }

        if(atributo.equals("idSindicato") || atributo.equals("taxaSindical")){
            if(!e.getSindicalizado())
                throw new TipoEmpregadoInvalidoException("sindicalizado");
        }

    }

    /**
     * Valida atributo comparando com os valores passados
     * @param atributo valor do atributo a ser validado
     * @param valores valores a serem comparados
     * @param nome nome do atributo
     * @param bool booleando que indica se o atributo é booleano
     * @return
     * @throws Exception
     */
    public static String validarAtributo(String atributo, String[] valores,
                                         String nome, boolean bool) throws Exception{
        if(atributo.isEmpty()) throw new ValorNuloException(nome, "o");

        if(!Arrays.asList(valores).contains(atributo)) {
            if (bool) {
                throw new ValorNaoBooleanoException(nome);
            } else throw new TipoInvalidoException("Metodo de pagamento");
        }
        return atributo;
    }

    /**
     * Valida nulidade de um atributo
     * @param atributo valor do atributo
     * @param nome nome do atributo
     * @param sufix sufixo de desinência de genero
     * @return o atributo, se não for nulo
     * @throws ValorNuloException se o atributo for nulo
     */
    public static String validarAtributo(String atributo, String nome, String sufix) throws ValorNuloException {
        if(atributo != null)
            if(atributo.isEmpty()) throw new ValorNuloException(nome, sufix);

        return atributo;
    }

    /**
     * Verifica se as horas não são negativas
     * @param horas valor das horas
     * @return horas, se não forem negativas
     * @throws HorasNegativasException se as horas forem negativas
     */
    public static String validarHoras(String horas) throws HorasNegativasException {
        if(Double.parseDouble(horas.replace(",", ".")) <= 0){
            throw new HorasNegativasException();
        }
        return horas;
    }

    /**
     * Verifica se um valor é valido e não negativo e converte para {@link Double}
     * @param valor valor a ser verificado
     * @return valor convertido
     * @throws Exception se falhar nas verificações
     */
    public static Double validarValor(String valor) throws Exception{
        valor = validarAtributo(valor, "Taxa sindical", "a");
        Double valorConvertido = Double.parseDouble(valor.replace(",", "."));
        if(valorConvertido <= 0){
            throw new ValorNegativoException();
        }

        return valorConvertido;
    }

    /**
     * Verifica se um tipo é valido ou aplicável
     * @param tipo tipo a ser verificado
     * @throws TipoInvalidoException se não existir o tipo
     * @throws TipoNaoAplicavelException c.c.
     */
    public static void checarTipo(String tipo) throws TipoInvalidoException, TipoNaoAplicavelException {
        if(!Settings.TIPOS.contains(tipo)){
            throw  new TipoInvalidoException();
        }
        throw new TipoNaoAplicavelException();
    }

    /**
     * Verifica se o tipo de um empregado é corresponde
     * @param tipo tipo a ser comparado
     * @param comparador tipo comparador
     * @throws TipoEmpregadoInvalidoException se os tipos forem diferentes
     */
    public static void checarTipo(String tipo, String comparador) throws TipoEmpregadoInvalidoException {
        if(!tipo.equals(comparador))
            throw new TipoEmpregadoInvalidoException(comparador);

    }

    /**
     * Retorna o total de dias entre duas datas
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return diferença entre as datas
     */
    public static int getDias(String dataInicial, String dataFinal){
        LocalDate dInicial = LocalDate.parse(dataInicial, Settings.formatter);
        LocalDate dFinal = LocalDate.parse(dataFinal, Settings.formatter);

        return (int) ChronoUnit.DAYS.between(dInicial, dFinal);
    }

    /**
     * Verifica se uma data é o último dia do seu mês correspondente
     * @param data data a ser comparada
     * @return true, se for último dia do mês, false, c.c.
     */
    public static boolean ehUltimoDiaMes(String data){
        LocalDate dataParse = LocalDate.parse(data, Settings.formatter);
        LocalDate ultimo = dataParse.with(TemporalAdjusters.lastDayOfMonth());

        return ultimo.equals(dataParse);
    }


    /**
     * Retorna o primeiro dia do mẽs
     * @param data data utilizada
     * @return primeiro dia do mês equivalente a data
     */
    public static String getPrimeiroDiaMes(String data){
        LocalDate dataParse = LocalDate.parse(data, Settings.formatter);

        return dataParse.with(TemporalAdjusters.firstDayOfMonth()).
                format(Settings.formatter);
    }

    /**
     * Verifica se é dia de pagamento com base na agenda do empregado
     * @param data data a ser verificada
     * @param agenda {@link AgendaPagamento} utilizada
     * @return true, se for dia de pagamento, false, c.c.
     */
    public static boolean ehDiaDePagamento(String data, AgendaPagamento agenda){
        LocalDate dataParse = LocalDate.parse(data, Settings.formatter);
        // Obtém dia da semana da data
        DayOfWeek diaDaSemana = dataParse.getDayOfWeek();

        if(agenda.getTipo().equals("mensal")){
            // mensalmente
            int dia = agenda.getDia();
            if(dia == -1){
                return ehUltimoDiaMes(data);
            }
            else return dataParse.getDayOfMonth() == dia;
        }
        else{
            int dia = agenda.getDia();
            if(agenda.getSemana() > 0){
                // a cada x semanas

                if(dataParse.getDayOfWeek() != DayOfWeek.of(dia)) return false;

                // Calcula a data da contratação (1/1/2005)
                LocalDate dataContratacao = LocalDate.of(2005, 1, 1);
                long totalSemanas = ChronoUnit.WEEKS.between(dataContratacao, dataParse) + 1;
                return totalSemanas % agenda.getSemana() == 0;
            }
            else{
                // toda semana
                if(diaDaSemana == DayOfWeek.of(dia)) return true;
            }
        }
        return false;
    }

    /**
     * Retorna o último dia em que o empregado foi pago com base na agenda
     * @param data data utilizada
     * @param agenda {@link AgendaPagamento} utilizada
     * @return último dia em que houve pagamento
     */
    public static String getUltimoDiaDePagamento(String data, AgendaPagamento agenda){
        LocalDate dataParse = LocalDate.parse(data, Settings.formatter);
        LocalDate dataInicial;
        int dia = agenda.getDia();
        if(agenda.getTipo().equals("mensal")){
            if(dia == -1) {
                return getPrimeiroDiaMes(data);
            }
            dataInicial = dataParse.minusMonths(1);
        }else{
            if(agenda.getSemana() > 0){
                int semana = agenda.getSemana();
                dataInicial = dataParse.minusDays((semana * 7L) - 1);
            }else{
                dataInicial = dataParse.with(TemporalAdjusters.previous(DayOfWeek.of(dia)));
            }
        }
        return dataInicial.format(Settings.formatter);
    }

    /**
     * Retorna o próximo dia de pagamento de um empregado com base na agenda
     * @param data data utilizada
     * @param agenda {@link AgendaPagamento} utilizada
     * @return data do próximo pagamento
     */
    public static String getProximoDiaDePagamento(String data, AgendaPagamento agenda){
        LocalDate dataParse = LocalDate.parse(data, Settings.formatter);
        LocalDate proximaData;
        int dia = agenda.getDia();
        if(agenda.getTipo().equals("mensal")){
            if(dia == -1) {
                proximaData = YearMonth.from(dataParse).plusMonths(1).atEndOfMonth();
            }else{
                proximaData = dataParse.plusMonths(1);
            }
        }else{
            if(agenda.getSemana() > 0){
                int semana = agenda.getSemana();
                proximaData = dataParse.plusDays((semana * 7L) - 1);
            }else{
                proximaData = dataParse.with(TemporalAdjusters.next(DayOfWeek.of(dia)));
            }
        }
        return proximaData.minusDays(1).format(Settings.formatter);
    }

    /**
     * Soma duas {@link List}
     * @param lista1 lista a ser somada
     * @param lista2 lista a ser somada
     * @return soma das duas listas
     * @throws ListasDiferentesException se as listas tiverem tamanhos divergentes
     */
    public static List<Double> somarListas(List<Double> lista1, List<Double> lista2) throws ListasDiferentesException {
        if(lista1.isEmpty()) return lista2;
        if(lista2.isEmpty()) return lista1;
        if(lista1.size() != lista2.size()) throw new ListasDiferentesException();

        List<Double> soma = new ArrayList<>();

        for(int i = 0; i < lista1.size(); i++){
            soma.add(lista1.get(i) + lista2.get(i));
        }

        return soma;
    }

    /**
     * Retorna lista de {@link AgendaPagamento} com dados padrão
     * @return agendas padrão
     * @throws DescricaoInvalidaException se alguma descrição padrão estiver incorreta
     */
    public static List<AgendaPagamento> getAgendasPadrao() throws DescricaoInvalidaException {
        List<AgendaPagamento> agendas = new ArrayList<>();
        for(String agenda: Settings.AGENDAS_PADRAO){
            agendas.add(new AgendaPagamento(agenda));
        }
        return agendas;
    }


}
