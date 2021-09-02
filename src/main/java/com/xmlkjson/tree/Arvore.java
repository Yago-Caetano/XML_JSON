package com.xmlkjson.tree;

import java.util.ArrayList;

public class Arvore<T> {

    ArvoreNodo<T> Nodo;

    public Arvore() {
        Nodo = new ArvoreNodo<T>(null);
    }

    public ArvoreNodo<T> adcionarNodo(T Dados)
    {
       Nodo = Nodo.addChild(Dados);
       return Nodo;
    }

    public ArvoreNodo<T> retornarParaPai()
    {
        Nodo = Nodo.returnToFather();
        return Nodo;
    }

}
