package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.AtributoInexistenteException;
import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoAssalariado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {


    public static Double formatarValor(String valor, String nome, String sufix) throws Exception {
        Double valorConvertido;
        try{
            valorConvertido = Double.parseDouble(valor.replace(",", "."));
        }
        catch (NumberFormatException e){
            if (valor.isEmpty())
            {
                throw new Exception(nome + " nao pode ser nul" + sufix + ".");
            }
            else{
                throw new Exception(nome + " deve ser numeric" + sufix + ".");
            }
        }

        if(valorConvertido < 0){
            throw new Exception(nome + " deve ser nao-negativ" + sufix + ".");
        }

        return valorConvertido;
    }

    public static String doubleToString(Double valor, boolean lazy){
        DecimalFormat formatter = new DecimalFormat(lazy ? "#.##" : "0.00");
        return formatter.format(valor);
    }

    public static String validarData(String data) throws Exception{
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

    public static String validarAtributo(String atributo, String[] valores, String nome, boolean bool) throws Exception{
        if(atributo.isEmpty()) throw new Exception(nome + " nao pode ser nulo.");

        if(!Arrays.asList(valores).contains(atributo)) {
            if (bool) {
                throw new Exception(nome + " deve ser true ou false.");
            } else throw new Exception(nome + " invalido.");
        }
        return atributo;
    }

    public static String validarAtributo(String atributo, String nome, String sufix) throws Exception{
        if(atributo != null)
            if(atributo.isEmpty()) throw new Exception(nome + " nao pode ser nul" + sufix + ".");

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

    public static void checaTipo(String tipo) throws Exception {
        List<String> tipos = Arrays.asList("horista", "comissionado", "assalariado");
        if(!tipos.contains(tipo)){
            throw  new Exception("Tipo invalido.");
        }
        throw new Exception("Tipo nao aplicavel.");
    }


    public static String getAtributoEmpregadoAssalariado(EmpregadoAssalariado empregado, String atributo) throws Exception {
        return switch (atributo){
            case "salario" -> Utils.doubleToString(empregado.getSalarioMensal(), false);
            case "sindicalizado" -> empregado.getSindicalizado() ? "true" : "false";
            case "comissao" -> throw new Exception("Empregado nao eh comissionado.");
            default -> throw new AtributoInexistenteException();
        };
    }

    public static String getAtributoEmpregadoHorista(EmpregadoHorista empregado, String atributo) throws Exception {
        return switch (atributo){
            case "salario" -> Utils.doubleToString(empregado.getSalarioPorHora(), false);
            case "sindicalizado" -> empregado.getSindicalizado() ? "true" : "false";
            case "comissao" -> throw new Exception("Empregado nao eh comissionado.");
            default -> throw new AtributoInexistenteException();
        };
    }

    public static String getAtributoEmpregadoComissionado(EmpregadoComissionado empregado, String atributo) throws Exception {
        return switch (atributo){
            case "salario" -> Utils.doubleToString(empregado.getSalarioMensal(), false);
            case "sindicalizado" -> empregado.getSindicalizado() ? "true" : "false";
            case "comissao" -> Utils.doubleToString(empregado.getTaxaDeComissao(), false);
            default -> throw new AtributoInexistenteException();
        };
    }

}
