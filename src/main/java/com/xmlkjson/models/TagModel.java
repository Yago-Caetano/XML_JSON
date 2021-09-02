package com.xmlkjson.models;

import com.xmlkjson.tree.ArvoreNodo;

public class TagModel {

    private String Nome;

    private int PosicaoValorIni;

    private int PosicaoValorFim;

    private boolean tagAbertura;

    public ArvoreNodo<TagModel> Nodo;

    public TagModel()
    {

    }

    public int getPosicaoValorIni() {
        return PosicaoValorIni;
    }

    public void setPosicaoValorIni(int posicaoValorIni) {
        PosicaoValorIni = posicaoValorIni;
    }

    public int getPosicaoValorFim() {
        return PosicaoValorFim;
    }

    public void setPosicaoValorFim(int posicaoValorFim) {
        PosicaoValorFim = posicaoValorFim;
    }

    public TagModel(String nome, int posicaoValorIni) {
        Nome = nome;
        PosicaoValorIni = posicaoValorIni;

    }

    public TagModel(String nome, int posicaoValorIni, int posicaoValorFim, boolean tagAbertura) {
        Nome = nome;
        PosicaoValorIni = posicaoValorIni;
        PosicaoValorFim = posicaoValorFim;
        this.tagAbertura = tagAbertura;
    }

    public void setNodo(ArvoreNodo<TagModel> nodo)
    {
        this.Nodo = nodo;
    }

    public String getNome() {
        return Nome;
    }
}
