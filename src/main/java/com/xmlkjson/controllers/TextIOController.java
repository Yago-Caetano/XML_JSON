package com.xmlkjson.controllers;

import java.io.*;
import java.util.Scanner;

public class TextIOController {

    public static String FetchData(String fileName) throws Exception {
        int qt=0;
        System.out.println("Analisando memória.....");
        File file = new File(fileName);
        Scanner input = null;
        StringBuffer buffer = new StringBuffer();

        try
        {
            input = new Scanner(file);
        } catch (FileNotFoundException e)
        {
            System.out.println("Arquivo não encontrado");
            throw new Exception();
        }
        while (input.hasNextLine())
        {
            String aux = input.nextLine();
            aux+="\r\n";
            buffer.append(aux);
        }
        if(buffer.length()>0)
            System.out.println("Dados recuperados com sucesso!!");
        else
            System.out.println("O arquivo está vazio");

        return buffer.toString();
    }

    public static void write(String s, String Caminho) {

        try(FileWriter fw = new FileWriter(Caminho, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(s);
            //more code
        } catch (IOException e) {
            System.out.println("Erro ao escrever arquivo de dados");
        }
    }
}
