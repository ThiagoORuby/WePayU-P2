package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.AtributoInexistenteException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoAssalariado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static Double salarioFormatter(String valor) throws Exception {
        Double salarioConvertido;
        try{
            salarioConvertido = Double.parseDouble(valor.replace(",", "."));
        }
        catch (NumberFormatException e){
            if (valor.isEmpty())
            {
                throw new Exception("Salario nao pode ser nulo.");
            }
            else{
                throw new Exception("Salario deve ser numerico.");
            }
        }

        if(salarioConvertido < 0){
            throw new Exception("Salario deve ser nao-negativo.");
        }

        return salarioConvertido;
    }

    public static Double comissaoFormatter(String valor) throws Exception {
        Double comissaoConvertida;
        try{
            comissaoConvertida = Double.parseDouble(valor.replace(",", "."));
        }
        catch (NumberFormatException e){
            if (valor.isEmpty())
            {
                throw new Exception("Comissao nao pode ser nula.");
            }
            else{
                throw new Exception("Comissao deve ser numerica.");
            }
        }

        if(comissaoConvertida < 0){
            throw new Exception("Comissao deve ser nao-negativa.");
        }

        return comissaoConvertida;
    }

    public static String doubleToString(Double valor, boolean lazy){
        DecimalFormat formatter = new DecimalFormat(lazy ? "#.##" : "0.00");
        return formatter.format(valor);
    }

    public static String dataValidate(String data) throws Exception{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        //TODO: FAZER O MESMO QUE EM EMPREGADOHORISTA
        try {LocalDate dataConvertida = LocalDate.parse(data, formatter);}
        catch (DateTimeParseException e) {
            throw new Exception("Data invalida.");
        }

        return data;
    }

    public static String horasValidate(String horas) throws Exception{
        if(Double.parseDouble(horas.replace(",", ".")) <= 0){
            throw new Exception("Horas devem ser positivas.");
        }
        return horas;
    }

    public static void checaTipo(String tipo) throws Exception {
        List<String> tipos = Arrays.asList("horista", "comissionado", "assalariado");
        if(!tipos.contains(tipo)){
            throw  new Exception("Tipo invalido.");
        }
        throw new Exception("Tipo nao aplicavel.");
    }


    public static String getAtributoEmpregadoAssalariado(EmpregadoAssalariado empregado, String atributo) throws AtributoInexistenteException {
        return switch (atributo){
            case "nome" -> empregado.getNome();
            case "endereco" -> empregado.getEndereco();
            case "tipo" -> empregado.getTipo();
            case "salario" -> Utils.doubleToString(empregado.getSalarioMensal(), false);
            case "sindicalizado" -> empregado.getSindicalizado() ? "true" : "false";
            default -> throw new AtributoInexistenteException();
        };
    }

    public static String getAtributoEmpregadoHorista(EmpregadoHorista empregado, String atributo) throws AtributoInexistenteException {
        return switch (atributo){
            case "nome" -> empregado.getNome();
            case "endereco" -> empregado.getEndereco();
            case "tipo" -> empregado.getTipo();
            case "salario" -> Utils.doubleToString(empregado.getSalarioPorHora(), false);
            case "sindicalizado" -> empregado.getSindicalizado() ? "true" : "false";
            default -> throw new AtributoInexistenteException();
        };
    }

    public static String getAtributoEmpregadoComissionado(EmpregadoComissionado empregado, String atributo) throws AtributoInexistenteException {
        return switch (atributo){
            case "nome" -> empregado.getNome();
            case "endereco" -> empregado.getEndereco();
            case "tipo" -> empregado.getTipo();
            case "salario" -> Utils.doubleToString(empregado.getSalarioMensal(), false);
            case "sindicalizado" -> empregado.getSindicalizado() ? "true" : "false";
            case "comissao" -> Utils.doubleToString(empregado.getTaxaDeComissao(), false);
            default -> throw new AtributoInexistenteException();
        };
    }

}
