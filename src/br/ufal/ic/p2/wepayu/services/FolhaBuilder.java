package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoAssalariado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;

import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.*;

/**
 * Builder de geração da Folha de Pagamento
 * @author thomruby
 */
public class FolhaBuilder {

    private final DBManager session;
    private String saida;

    public FolhaBuilder(){
        this.session = DBManager.getSession();
    }

    public String getSaida() {
        return saida;
    }

    public void setSaida(String saida) {
        this.saida = saida;
    }

    public Double getTotalFolha(String data) throws Exception{
        Double total = 0d;
        for(Map.Entry<String, Empregado> emp: session.query().entrySet()){
            Empregado e = emp.getValue();
            switch (e.getTipo())
            {
                case "horista" -> {
                    if(Utils.ehSexta(data)){
                        String dataInicial = Utils.getUltimaSexta(data);
                        total += ((EmpregadoHorista) e).getSalarioBruto(dataInicial, data);
                    }
                }
                case "assalariado" -> {
                    if(Utils.ehUltimoDiaMes(data)){
                        total += e.getSalario();
                    }
                }
                case "comissionado" -> {
                    if(Utils.ehDiaDePagamentoComissionado(data)){
                        String dataInicial = Utils.getUltimoPagamentoComissionado(data);
                        total += ((EmpregadoComissionado) e).getSalarioBruto(dataInicial, data);
                    }
                }
            }
        }
        return total;
    }


    private void geraDadosHoristas(String data) throws Exception{
        // Lê arquivo horista.txt
        SortedSet<String> dadosEmpregados = new TreeSet<>();
        List<Double> somaTotal = Arrays.asList(0D, 0D, 0D, 0D, 0D);

        for(Map.Entry<String, Empregado> emp : session.query().entrySet()){
            Empregado e = emp.getValue();
            if(e.getTipo().equals("horista"))
            {
                String dataInicial;
                if(!Utils.ehSexta(data)) break;
                else dataInicial = Utils.getUltimaSexta(data);
                Object[] dados = ((EmpregadoHorista) e).getDadosEmLinha(dataInicial, data);
                dadosEmpregados.add((String) dados[0]);
                somaTotal = Utils.somarListas(somaTotal,
                        (List<Double>) dados[1]);
            }
        }

        try{
            BufferedReader reader = new BufferedReader(new FileReader(Settings.HEADER_HORISTAS));
            BufferedWriter writer = new BufferedWriter(new FileWriter(saida, true));

            String linha;
            while((linha = reader.readLine()) != null)
            {
                writer.write(linha);
                writer.newLine();
            }

            reader.close();

            for(String dado: dadosEmpregados){
                writer.write(dado);
                writer.newLine();
            }

            String total = String.format("\n%-36s %5s %5s %13s %9s %15s\n", "TOTAL HORISTAS",
                    Utils.doubleToString(somaTotal.get(0), true),
                    Utils.doubleToString(somaTotal.get(1), true),
                    Utils.doubleToString(somaTotal.get(2), false),
                    Utils.doubleToString(somaTotal.get(3), false),
                    Utils.doubleToString(somaTotal.get(4), false)
            );

            writer.write(total);
            writer.close();

        }catch (IOException e)
        {
            e.printStackTrace();
        }
    };

    private void geraDadosAssalariados(String data) throws Exception{
        // Lê arquivo assalariados.txt
        SortedSet<String> dadosEmpregados = new TreeSet<>();
        List<Double> somaTotal = Arrays.asList(0D, 0D, 0D);

        for(Map.Entry<String, Empregado> emp : session.query().entrySet()){
            Empregado e = emp.getValue();
            if(e.getTipo().equals("assalariado"))
            {
                String dataInicial;
                if(!Utils.ehUltimoDiaMes(data)) break;
                else dataInicial = Utils.getPrimeiroDiaMes(data);

                Object[] dados = ((EmpregadoAssalariado) e).getDadosEmLinha(dataInicial, data);
                dadosEmpregados.add((String) dados[0]);
                somaTotal = Utils.somarListas(somaTotal,
                        (List<Double>) dados[1]);
            }
        }

        try{
            BufferedReader reader = new BufferedReader(new FileReader(Settings.HEADER_ASSALARIADOS));
            BufferedWriter writer = new BufferedWriter(new FileWriter(saida, true));

            String linha;
            while((linha = reader.readLine()) != null)
            {
                writer.write(linha);
                writer.newLine();
            }

            reader.close();

            for(String dado: dadosEmpregados){
                writer.write(dado);
                writer.newLine();
            }

            String total = String.format("\n%-48s %13s %9s %15s\n", "TOTAL ASSALARIADOS",
                    Utils.doubleToString(somaTotal.get(0), false),
                    Utils.doubleToString(somaTotal.get(1), false),
                    Utils.doubleToString(somaTotal.get(2), false)
            );

            writer.write(total);
            writer.close();

        }catch (IOException e)
        {
            System.out.println("Arquivos nao encontrados");
        }


    }

    private void geraDadosComissionados(String data) throws Exception{
        SortedSet<String> dadosEmpregados = new TreeSet<>();
        List<Double> somaTotal = Arrays.asList(0D,0D,0D,0D,0D,0D);

        for(Map.Entry<String, Empregado> emp : session.query().entrySet()){
            Empregado e = emp.getValue();
            if(e.getTipo().equals("comissionado"))
            {
                String dataInicial;
                if(!Utils.ehDiaDePagamentoComissionado(data)) break;
                else dataInicial = Utils.getUltimoPagamentoComissionado(data);
                Object[] dados = ((EmpregadoComissionado) e).getDadosEmLinha(dataInicial, data);
                dadosEmpregados.add((String) dados[0]);
                somaTotal = Utils.somarListas(somaTotal,
                        (List<Double>) dados[1]);
            }
        }

        try{
            BufferedReader reader = new BufferedReader(new FileReader(Settings.HEADER_COMISSIONADOS));
            BufferedWriter writer = new BufferedWriter(new FileWriter(saida, true));

            String linha;
            while((linha = reader.readLine()) != null)
            {
                writer.write(linha);
                writer.newLine();
            }

            reader.close();

            for(String dado: dadosEmpregados){
                writer.write(dado);
                writer.newLine();
            }

            String total = String.format("\n%-21s %8s %8s %8s %13s %9s %15s\n", "TOTAL COMISSIONADOS",
                    Utils.doubleToString(somaTotal.get(0), false),
                    Utils.doubleToString(somaTotal.get(1), false),
                    Utils.doubleToString(somaTotal.get(2), false),
                    Utils.doubleToString(somaTotal.get(3), false),
                    Utils.doubleToString(somaTotal.get(4), false),
                    Utils.doubleToString(somaTotal.get(5), false)
            );

            writer.write(total);
            writer.write("\nTOTAL FOLHA: " +
                    Utils.doubleToString(getTotalFolha(data), false));
            writer.close();

        }catch (IOException e)
        {
            System.out.println("Arquivos nao encontrados");
        }

    }

    public void geraFolha(String data, String saida) throws Exception{

        setSaida(saida);
        String dia = saida.substring(6, 16);

        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(saida));

            writer.write("FOLHA DE PAGAMENTO DO DIA " + dia);
            writer.newLine();

            writer.close();
        }catch (IOException e)
        {
            System.out.println("Arquivo nao encontrado");
        }

        geraDadosHoristas(data);
        geraDadosAssalariados(data);
        geraDadosComissionados(data);

    }


}


