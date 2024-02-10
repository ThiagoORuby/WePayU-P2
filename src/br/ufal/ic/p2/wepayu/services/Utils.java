package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.exceptions.*;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
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
     * @return
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
                throw new Exception("Empregado nao recebe em banco.");
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
            } else throw new Exception(nome + " invalido.");
        }
        return atributo;
    }

    public static String validarAtributo(String atributo, String nome, String sufix) throws Exception{
        if(atributo != null)
            if(atributo.isEmpty()) throw new ValorNuloException(nome, sufix);

        return atributo;
    }

    public static String validarHoras(String horas) throws Exception{
        if(Double.parseDouble(horas.replace(",", ".")) <= 0){
            throw new Exception("Horas devem ser positivas.");
        }
        return horas;
    }

    public static Double validarValor(String valor) throws Exception{
        valor = validarAtributo(valor, "Taxa sindical", "a");
        Double valorConvertido = Double.parseDouble(valor.replace(",", "."));
        if(valorConvertido <= 0){
            throw new Exception("Valor deve ser positivo.");
        }

        return valorConvertido;
    }

    public static void checarTipo(String tipo) throws Exception {
        if(!Settings.TIPOS.contains(tipo)){
            throw  new TipoInvalidoException();
        }
        throw new TipoNaoAplicavelException();
    }

    public static void checarTipo(String tipo, String comparador) throws Exception {
        if(!tipo.equals(comparador))
            throw new TipoEmpregadoInvalidoException(comparador);

    }

    public static int getDias(String dataInicial, String dataFinal){
        LocalDate dInicial = LocalDate.parse(dataInicial, Settings.formatter);
        LocalDate dFinal = LocalDate.parse(dataFinal, Settings.formatter);

        return (int) ChronoUnit.DAYS.between(dInicial, dFinal);
    }


    public static String getUltimaSexta(String data) {
        LocalDate dataParse = LocalDate.parse(data, Settings.formatter);
        LocalDate dataInicial = dataParse.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));

        return dataInicial.format(Settings.formatter);
    }

    public static String getProximaSexta(String data) {
        LocalDate dataParse = LocalDate.parse(data, Settings.formatter);
        LocalDate dataInicial = dataParse.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));

        return dataInicial.format(Settings.formatter);
    }

    public static boolean ehSexta(String data){
        LocalDate dataParse = LocalDate.parse(data, Settings.formatter);

        return dataParse.getDayOfWeek() == DayOfWeek.FRIDAY;
    }

    public static boolean ehUltimoDiaMes(String data){
        LocalDate dataParse = LocalDate.parse(data, Settings.formatter);
        LocalDate ultimo = dataParse.with(TemporalAdjusters.lastDayOfMonth());

        return ultimo.equals(dataParse);
    }

    public static String getPrimeiroDiaMes(String data){
        LocalDate dataParse = LocalDate.parse(data, Settings.formatter);

        return dataParse.with(TemporalAdjusters.firstDayOfMonth()).
                format(Settings.formatter);
    }

    public static boolean ehDiaDePagamentoComissionado(String data) {
        LocalDate dataParse = LocalDate.parse(data, Settings.formatter);

        // Obtém dia da semana da data
        DayOfWeek diaDaSemana = dataParse.getDayOfWeek();

        // Verifica se é sexta-feira
        if (diaDaSemana != DayOfWeek.FRIDAY) {
            return false;
        }

        // Calcula a data da contratação (1/1/2005)
        LocalDate dataPrimeiroPagamento = LocalDate.of(2005, 1, 1);

        // Calcula a diferença em dias entre a data e a data da contratação
        long diferencaEmDias = ChronoUnit.DAYS.between(dataPrimeiroPagamento, dataParse);

        // Verifica se a diferença em dias é um múltiplo de 14 (dias entre pagamentos)
        return (diferencaEmDias + 1) % Settings.DIAS_ENTRE_PAGAMENTOS == 0;
    }

    public static String getUltimoPagamentoComissionado(String data) {
        LocalDate dataParse = LocalDate.parse(data, Settings.formatter);
        LocalDate dataInicial = dataParse.minusDays(13);
        return dataInicial.format(Settings.formatter);
    }

    public static List<Double> somarListas(List<Double> lista1, List<Double> lista2) throws Exception{
        if(lista1.isEmpty()) return lista2;
        if(lista2.isEmpty()) return lista1;
        if(lista1.size() != lista2.size()) throw new ListasDiferentesException();

        List<Double> soma = new ArrayList<>();

        for(int i = 0; i < lista1.size(); i++){
            soma.add(lista1.get(i) + lista2.get(i));
        }

        return soma;
    }


}
