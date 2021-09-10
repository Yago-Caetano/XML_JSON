package com.xmlkjson;

import com.xmlkjson.controllers.ParserController;
import com.xmlkjson.controllers.TextIOController;

public class Main {

    private static ParserController mParser;



    private static boolean validaArgumentos(String[] args)
    {
        if(args.length == 1)
        {
            if(args[0].trim().length()>1 && args[0].trim().contains(".xml"))
                return true;
            else
                return false;
        }
        return false;
    }


    public static void main(String[] args)
    {
        if(!validaArgumentos(args))
        {
            System.out.println("Argumentos inválidos");
            return;
        }

        String NomeArquivo = args[0];

        mParser = new ParserController();
        String texto = null;
        try {
            texto = TextIOController.FetchData(NomeArquivo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        mParser.decodificar(texto,NomeArquivo.replace(".xml",""));
    }
}
