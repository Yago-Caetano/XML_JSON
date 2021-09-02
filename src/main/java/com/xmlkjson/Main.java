package com.xmlkjson;

import com.xmlkjson.controllers.ParserController;
import com.xmlkjson.controllers.TextIOController;

public class Main {

    private static ParserController mParser;

    public static void main(String[] args)
    {
        mParser = new ParserController();
        String texto = null;
        try {
            texto = TextIOController.FetchData("teste.xml");

        } catch (Exception e) {
            e.printStackTrace();
        }
        mParser.decodificar(texto);
    }
}
