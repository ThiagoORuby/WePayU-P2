package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.models.*;

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

    /**
     * Retorna o total da folha de pagamento relativa a data
     * @param data Data da folha de pagamento
     * @return {@link Double} total da folha de pagamento
     * @throws Exception
     */
    public Double getTotalFolha(String data) throws Exception{
        Double total = 0d;
        for(Map.Entry<String, Empregado> emp: session.query().entrySet()){
            Empregado e = emp.getValue();
            AgendaPagamento agenda = e.getAgendaPagamento();
            switch (e.getTipo())
            {
                case "horista" -> {
                    // Checa se é sexta e pega a ultima sexta para calcular o salario dos horistas
                    if(Utils.ehDiaDePagamento(data, agenda)){
                        String dataInicial = Utils.getUltimoDiaDePagamento(data, agenda);
                        total += ((EmpregadoHorista) e).getSalarioBruto(dataInicial, data);
                    }
                }
                case "assalariado" -> {
                    // Checa se é o ultimo dia do mes para calcular o salario dos assalariados
                    if(Utils.ehDiaDePagamento(data, agenda)){
                        total += ((EmpregadoAssalariado)e).getSalarioBruto();
                    }
                }
                case "comissionado" -> {
                    // Checha se está no escopo de a cada 2 sextas, pega o ultimo dia de pagamento e
                    // calcula o salario dos comissionados
                    if(Utils.ehDiaDePagamento(data, agenda)){
                        String dataInicial = Utils.getUltimoDiaDePagamento(data, agenda);
                        total += ((EmpregadoComissionado) e).getSalarioBruto(dataInicial, data);
                    }
                }
            }
        }
        return total;
    }

    /**
     * Gera os dados de pagamento de horistas no txt correspondente
     * @param data Data da folha de pagamento
     * @throws Exception
     */
    private void geraDadosHoristas(String data) throws Exception{
        // Lê arquivo horista.txt
        SortedSet<String> dadosEmpregados = new TreeSet<>();
        List<Double> somaTotal = Arrays.asList(0D, 0D, 0D, 0D, 0D);

        // Percorre os empregados horistas e soma o total de pagamento (caso seja dia de pagamento)
        for(Map.Entry<String, Empregado> emp : session.query().entrySet()){
            Empregado e = emp.getValue();
            AgendaPagamento agenda = e.getAgendaPagamento();
            if(e.getTipo().equals("horista"))
            {
                String dataInicial;
                if(!Utils.ehDiaDePagamento(data, agenda)) break;
                else dataInicial = Utils.getUltimoDiaDePagamento(data, agenda);
                Object[] dados = ((EmpregadoHorista) e).getDadosEmLinha(dataInicial, data);
                dadosEmpregados.add((String) dados[0]);
                somaTotal = Utils.somarListas(somaTotal,
                        (List<Double>) dados[1]);
            }
        }


        try{
            BufferedReader reader = new BufferedReader(new FileReader(Settings.HEADER_HORISTAS));
            BufferedWriter writer = new BufferedWriter(new FileWriter(saida, true));

            // Escreve o header dos horistas
            String linha;
            while((linha = reader.readLine()) != null)
            {
                writer.write(linha);
                writer.newLine();
            }

            reader.close();

            // Escreve os dados de cada horista em ordem alfabetica
            for(String dado: dadosEmpregados){
                writer.write(dado);
                writer.newLine();
            }

            // Escreve o valor total de pagamento dos horistas
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

    /**
     * Gera os dados de pagamento dos assalariados no txt correspondente
     * @param data Data da folha de pagamento
     * @throws Exception
     */
    private void geraDadosAssalariados(String data) throws Exception{
        // Lê arquivo assalariados.txt
        SortedSet<String> dadosEmpregados = new TreeSet<>();
        List<Double> somaTotal = Arrays.asList(0D, 0D, 0D);

        // Percorre os empregados assalarios e soma o total de pagamento (caso seja dia de pagamento)
        for(Map.Entry<String, Empregado> emp : session.query().entrySet()){
            Empregado e = emp.getValue();
            AgendaPagamento agenda = e.getAgendaPagamento();
            if(e.getTipo().equals("assalariado"))
            {
                String dataInicial;
                if(!Utils.ehDiaDePagamento(data, agenda)) break;
                else dataInicial = Utils.getUltimoDiaDePagamento(data, agenda);

                Object[] dados = ((EmpregadoAssalariado) e).getDadosEmLinha(dataInicial, data);
                dadosEmpregados.add((String) dados[0]);
                somaTotal = Utils.somarListas(somaTotal,
                        (List<Double>) dados[1]);
            }
        }

        try{
            BufferedReader reader = new BufferedReader(new FileReader(Settings.HEADER_ASSALARIADOS));
            BufferedWriter writer = new BufferedWriter(new FileWriter(saida, true));

            // Escreve header dos assalariados
            String linha;
            while((linha = reader.readLine()) != null)
            {
                writer.write(linha);
                writer.newLine();
            }

            reader.close();

            // Escreve os dados de cada assalariado em ordem alfabetica
            for(String dado: dadosEmpregados){
                writer.write(dado);
                writer.newLine();
            }

            // Escreve o valor total de pagamento dos assalariados
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
            AgendaPagamento agenda = e.getAgendaPagamento();
            if(e.getTipo().equals("comissionado"))
            {
                String dataInicial;
                if(!Utils.ehDiaDePagamento(data, agenda)) break;
                else dataInicial = Utils.getUltimoDiaDePagamento(data, agenda);
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

    /**
     * Gera o txt com folha de pagamento de um determinado dia
     * @param data Data da folha de pagamento
     * @param saida Nome do arquivo txt
     * @throws Exception
     */
    public void geraFolha(String data, String saida) throws Exception{

        setSaida(saida);
        String dia = saida.substring(6, 16);

        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(saida));

            // Escreve o titulo da folha de pagamento
            writer.write("FOLHA DE PAGAMENTO DO DIA " + dia);
            writer.newLine();

            writer.close();
        }catch (IOException e)
        {
            System.out.println("Arquivo nao encontrado");
        }

        // Escreve os blocos de informação sequencialmente
        geraDadosHoristas(data);
        geraDadosAssalariados(data);
        geraDadosComissionados(data);

    }


}


