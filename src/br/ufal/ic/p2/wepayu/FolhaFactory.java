package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.daos.EmpregadoDao;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;

import java.io.*;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class FolhaFactory {

    private EmpregadoDao empregadoDao;

    private String saida;

    public FolhaFactory(){
    }

    public String getSaida() {
        return saida;
    }
/*
    public void setSaida(String saida) {
        this.saida = saida;
    }

    public void geraDadosHoristas(String data) throws Exception{
        // Lê arquivo horista.txt

        String origem = "templates/horistas.txt";
        SortedSet<String> dadosEmpregados = new TreeSet<>();

        for(Map.Entry<String, Empregado> emp : empregadoDao.getEmpregados().entrySet()){
            Empregado e = emp.getValue();
            if(e.getTipo().equals("horista"))
            {
                String linha = ((EmpregadoHorista) e).getDadosEmLinha(data);
                if(linha != null){
                    dadosEmpregados.add(linha);
                }
            }
        }

        try{

            BufferedReader reader = new BufferedReader(new FileReader(origem));
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

            writer.close();

        }catch (IOException e)
        {
            e.printStackTrace();
        }
    };

    public void geraDadosAssalariados(){
        // Lê arquivo assalariados.txt
    }

    public void geraDadosComissionados(){}

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
            e.printStackTrace();
        }

        geraDadosHoristas(data);
    }

 */
}


