package com.xmlkjson.controllers;

import com.xmlkjson.tree.Arvore;
import com.google.gson.JsonObject;
import com.xmlkjson.enums.EnumDecoder;
import com.xmlkjson.models.TagModel;
import com.xmlkjson.tree.ArvoreNodo;


import java.util.Stack;

public class ParserController {

    private EnumDecoder StatusAtual;
    private int InicioTag;
    private String Texto;
    private Arvore<TagModel> Arvore;
    private Stack<TagModel> TagsAbertas;

    public void decodificar(String texto)
    {
        Texto = texto;
        Arvore = new Arvore<TagModel>();
        StatusAtual = EnumDecoder.PROCURANDO_INICIO_TAG;

        TagsAbertas = new Stack<TagModel>();

        for(int i = 0; i < Texto.length();i++)
        {
            Character C = Texto.charAt(i);
            try {
                executar(C,i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        StringBuilder ret = new StringBuilder();

        vasculhaFilhos(Arvore.getRaiz(),ret);
    }

    private void executar(Character C,int Indice) throws Exception {
        switch (StatusAtual)
        {
            case PROCURANDO_INICIO_TAG:
                if(C == '<')
                {
                    InicioTag = Indice;
                    StatusAtual = EnumDecoder.PROCURANDO_FIM_TAG;
                }
                break;


            case PROCURANDO_FIM_TAG:
                if(C == '>')
                {
                    StatusAtual = EnumDecoder.PROCURANDO_INICIO_TAG;
                    TagModel tagAux = new TagModel(Texto.substring((InicioTag+1),(Indice)),Indice+1);

                    if(tagAux.getNome().contains("/"))
                    {
                        //verficar se tem "/" e olhar na pilha se estamos fechando uma tag aberta
                        TagModel tagPilha = TagsAbertas.pop();
                        if(!tagPilha.getNome().equals(tagAux.getNome().replace("/","")))
                            throw new Exception("Inválido");
                        else
                        {
                            //tagPilha.Nodo.getData().setPosicaoValorFim(Indice);
                            Arvore.retornarParaPai();
                        }
                    }
                    else
                    {
                        //se for tag de abertura, adciona na arvore
                        Arvore.adcionarNodo(tagAux);
                        //tagAux.setNodo(Arvore);
                        TagsAbertas.add(tagAux);
                    }



                }
                break;


            default:

                break;


        }

    }

    private String montaJson()
    {

        StringBuilder ret = new StringBuilder();

        JsonObject objectRet = new JsonObject();

        return null;
    }

    private void vasculhaFilhos(ArvoreNodo<TagModel> Nodo,StringBuilder sb)
    {

        if(Nodo.getData() != null)
            sb.append("\"" + Nodo.getData().getNome() + "\":" );

        if(Nodo.qtdFilhos()>1)
            sb.append("[");

        if(Nodo.qtdFilhos() == 0 && Nodo.getData() != null)
        {
            //recupera valor da string
            sb.append(Texto.substring(Nodo.getData().getPosicaoValorIni(),Texto.indexOf("<",Nodo.getData().getPosicaoValorIni())));
            return;
        }
        else
        {
            for(int i = 0; i < Nodo.getFilhos().size(); i++)
            {
                vasculhaFilhos(Nodo.getFilhos().get(i),sb);

                if(i+1 != Nodo.getFilhos().size())
                {
                    sb.append(",");
                }

            }

            if(Nodo.qtdFilhos()>1)
                sb.append("]" );
        }
    }
}
