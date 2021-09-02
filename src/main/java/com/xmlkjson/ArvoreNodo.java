package com.xmlkjson;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ArvoreNodo<T> implements Iterable<ArvoreNodo<T>> {

    T Data;
    ArvoreNodo<T> Pai;
    List<ArvoreNodo<T>> Filhos;

    public ArvoreNodo(T data) {
        this.Data = data;
        this.Filhos = new LinkedList<ArvoreNodo<T>>();
    }

    public ArvoreNodo<T> addChild(T child) {
        ArvoreNodo<T> childNode = new ArvoreNodo<T>(child);
        childNode.Pai = this;
        this.Filhos.add(childNode);
        return childNode;
    }


    public T getData()
    {
        return this.Data;
    }

    public ArvoreNodo<T> returnToFather()
    {
        ArvoreNodo<T> fatherNode = this.Pai;
        return fatherNode;
        //return fatherNode.addChild(child);
    }

    @Override
    public Iterator<ArvoreNodo<T>> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super ArvoreNodo<T>> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<ArvoreNodo<T>> spliterator() {
        return Iterable.super.spliterator();
    }
}


