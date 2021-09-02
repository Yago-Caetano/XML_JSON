package com.xmlkjson.controllers;

import com.xmlkjson.ArvoreNodo;
import com.xmlkjson.enums.EnumDecoder;
import com.xmlkjson.models.TagModel;

import java.util.Stack;

public class ParserController {

    private EnumDecoder StatusAtual;
    private int InicioTag;
    private String Texto;
    private ArvoreNodo<TagModel> Arvore;
    private Stack<TagModel> TagsAbertas;

    public void decodificar(String texto)
    {
        Texto = texto;
        Arvore = new ArvoreNodo(new TagModel());
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
                            Arvore = tagPilha.Nodo.returnToFather();
                        }
                    }
                    else
                    {
                        //se for tag de abertura, adciona na arvore
                        Arvore = Arvore.addChild(tagAux);
                        tagAux.setNodo(Arvore);
                        TagsAbertas.add(tagAux);
                    }



                }
                break;


            default:

                break;


        }

    }
}
