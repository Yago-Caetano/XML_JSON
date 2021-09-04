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

    private final int JSON_OBJETO = 1;
    private final int JSON_PRIMEIRO_DA_LISTA = 2;
    private final int JSON_ITEM_LISTA = 3;
    private final int JSON_VARIAVEL = 4;


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

            classificaFilhos(Nodo);
            trataIncioAnalise(Nodo,sb);
            for(int i = 0; i < Nodo.getFilhos().size(); i++)
            {
                vasculhaFilhos(Nodo.getFilhos().get(i),sb);
            }
            trataFinalAnalise(Nodo,sb);

    }
    private void trataFinalAnalise(ArvoreNodo<TagModel> nodo,StringBuilder sb)
    {
        if(nodo.getData() != null)
        {
            switch (nodo.getData().getTipo())
            {
                case JSON_OBJETO:
                    sb.append("}");
                    break;

                case JSON_PRIMEIRO_DA_LISTA:
                    sb.append("]");
                    break;

                case JSON_ITEM_LISTA:
                    //analise
                    if(verificaObjetoOuVariavel(nodo) == JSON_OBJETO)
                        sb.append("}");

                    break;

            }
            sb.append(",");
        }
        else
        {
            sb.append("}");
        }
    }



    private void trataIncioAnalise(ArvoreNodo<TagModel> nodo,StringBuilder sb)
    {
        if(nodo.getData() != null)
        {
            switch (nodo.getData().getTipo())
            {
                case JSON_OBJETO:
                    sb.append("\"" + nodo.getData().getNome() + "\": {" );
                    break;

                case JSON_PRIMEIRO_DA_LISTA:
                    sb.append("\"" + nodo.getData().getNome() + "\": [" );
                    break;

                case JSON_ITEM_LISTA:
                    //analise
                    if(verificaObjetoOuVariavel(nodo) == JSON_OBJETO)
                        sb.append("{" );
                    break;

                case JSON_VARIAVEL:
                    sb.append("\"" + nodo.getData().getNome() + "\": ");
                    sb.append(Texto.substring(nodo.getData().getPosicaoValorIni(),Texto.indexOf("<",nodo.getData().getPosicaoValorIni())));
                    break;
            }
        }
        else
        {
            sb.append("{");
        }
    }


    private void classificaFilhos(ArvoreNodo<TagModel> nodo) {

        String UltimaTag = "";

        int Tamanho = nodo.qtdFilhos();

        ArvoreNodo<TagModel> NodoAtual;
        //Verifica todos os filhos

        for(int i = 0; i < Tamanho; i++){
            NodoAtual = nodo.getFilhos().get(i);

            //não é o inicio e nem o final
            if(i > 0 && i < Tamanho-1)
            {
                /* CD:[
                // "1":{},
                // "2":{},]

                CD : [{SDAAS},{ASDAD},{ADASD},{BKJDAHKJD}]

                CD: [10,25,10,36,98,14]
                <cd>10</cd>
                <cd>25</cd>

                */
                //se o nodo for diferente da ultima tag
                if(!NodoAtual.getData().getNome().equals(UltimaTag))
                {
                    ArvoreNodo<TagModel> ProximoNodo = nodo.getFilhos().get(i+1);
                    //se o nodo for igual a proxima tag
                    if(NodoAtual.getData().getNome().equals(ProximoNodo.getData().getNome()))
                        NodoAtual.getData().setTipo(JSON_PRIMEIRO_DA_LISTA);
                    else
                    {
                        NodoAtual.getData().setTipo(verificaObjetoOuVariavel(NodoAtual));
                    }
                    UltimaTag = NodoAtual.getData().getNome();
                }
                else {
                    NodoAtual.getData().setTipo(JSON_ITEM_LISTA);
                }
            }
            else if(i == 0)
            {
                if(Tamanho > 1)
                {
                    ArvoreNodo<TagModel> ProximoNodo = nodo.getFilhos().get(i+1);

                    //verifica se é o inicio de uma lista
                    if(NodoAtual.getData().getNome().equals(ProximoNodo.getData().getNome()))
                    {
                        NodoAtual.getData().setTipo(JSON_PRIMEIRO_DA_LISTA);
                    }
                    else
                    {
                        NodoAtual.getData().setTipo(verificaObjetoOuVariavel(NodoAtual));
                    }
                }
                else
                {
                    NodoAtual.getData().setTipo(verificaObjetoOuVariavel(NodoAtual));
                }

                UltimaTag = NodoAtual.getData().getNome();

            } //ultima posição
            else
            {
                if(NodoAtual.getData().getNome().equals(UltimaTag))
                {
                    NodoAtual.getData().setTipo(JSON_ITEM_LISTA);
                }
                else
                {
                    NodoAtual.getData().setTipo(verificaObjetoOuVariavel(NodoAtual));
                }
                UltimaTag = NodoAtual.getData().getNome();

            }

        }
    }

    private int verificaObjetoOuVariavel(ArvoreNodo<TagModel> n)
    {
        //objeto tem mais de 1 filho
        if(n.getFilhos().size()>1)
            return JSON_OBJETO;
        else
            return  JSON_VARIAVEL;
    }

}


