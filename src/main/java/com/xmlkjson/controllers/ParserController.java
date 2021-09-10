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
    private final int JSON_ULTIMO_ITEM_LISTA = 5;
    private final int JSON_ULTIMO_OBJETO = 6;
    private final int JSON_ULTIMA_VARIAVEL = 7;



    public void decodificar(String texto,String NomeArquivoFinal)
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

        TextIOController.write(ret.toString(),NomeArquivoFinal + ".json");
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
        boolean inserirVirgula = true;
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

                case JSON_ULTIMA_VARIAVEL:
                    inserirVirgula = false;
                    break;

                case JSON_ULTIMO_OBJETO:
                    sb.append("}");
                    inserirVirgula = false;
                    break;

                case JSON_ULTIMO_ITEM_LISTA:
                    if(verificaObjetoOuVariavel(nodo) != JSON_OBJETO)
                        sb.append("]");
                    else
                    {
                        sb.append("}]");
                    }
                    inserirVirgula = false;
                    break;

            }
            if(inserirVirgula)
            {
                sb.append(",");
            }
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
            int tipoAux = nodo.getData().getTipo();
            if(tipoAux == JSON_ULTIMO_OBJETO)
                tipoAux = JSON_OBJETO;
            else if(tipoAux == JSON_ULTIMO_ITEM_LISTA)
                tipoAux = JSON_ITEM_LISTA;
            else if(tipoAux == JSON_ULTIMA_VARIAVEL)
                tipoAux = JSON_VARIAVEL;

            switch (tipoAux)
            {
                case JSON_OBJETO:
                    sb.append("\"" + nodo.getData().getNome() + "\": {" );
                    break;


                case JSON_PRIMEIRO_DA_LISTA:
                    sb.append("\"" + nodo.getData().getNome() + "\": [" );
                    //verifica o tipo de filho
                    nodo.getData().setTipo(verificaObjetoOuVariavel(nodo));

                    if(nodo.getData().getTipo() == JSON_OBJETO)
                    {
                        sb.append("{" );
                    }
                    break;

                case JSON_ITEM_LISTA:
                    //analise
                    if(verificaObjetoOuVariavel(nodo) == JSON_OBJETO)
                        sb.append("{" );
                    break;

                case JSON_VARIAVEL:
                    sb.append("\"" + nodo.getData().getNome() + "\": ");
                    insereValor(nodo,sb);
                    break;

            }
        }
        else
        {
            sb.append("{");
        }
    }

    private void insereValor(ArvoreNodo<TagModel> nodo,StringBuilder sb)
    {
        String ValorBruto = Texto.substring(nodo.getData().getPosicaoValorIni(),Texto.indexOf("<",nodo.getData().getPosicaoValorIni()));

        //tenta converter para numero

        if(ValorBruto.matches(".*\\d.*"))
        {
            try{
                if(ValorBruto.contains("."))
                {
                    double Convertido = Double.parseDouble(ValorBruto);
                    sb.append(Convertido);
                }
                else
                {
                    int Convertido = Integer.parseInt(ValorBruto);
                    sb.append(Convertido);
                }
                return;
            }
            catch (Exception e) {}
        }

        sb.append("\"" + ValorBruto + "\"");


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
                    int tipoAux = verificaObjetoOuVariavel(NodoAtual);

                    if(tipoAux == JSON_OBJETO)
                        NodoAtual.getData().setTipo(JSON_ULTIMO_OBJETO);
                    else
                        NodoAtual.getData().setTipo(JSON_ULTIMA_VARIAVEL);
                }

                UltimaTag = NodoAtual.getData().getNome();

            } //ultima posição
            else
            {
                if(NodoAtual.getData().getNome().equals(UltimaTag))
                {
                    NodoAtual.getData().setTipo(JSON_ULTIMO_ITEM_LISTA);
                }
                else
                {
                    int tipoAux = verificaObjetoOuVariavel(NodoAtual);

                    if(tipoAux == JSON_OBJETO)
                        NodoAtual.getData().setTipo(JSON_ULTIMO_OBJETO);
                    else
                        NodoAtual.getData().setTipo(JSON_ULTIMA_VARIAVEL);
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


