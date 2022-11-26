package com.example.projetobase;


import java.util.LinkedList;
import java.util.List;


public class Listas {

    public String nome;
    public List<NewListAdapter.Item> itens;

    Listas(){
        nome = "";
        itens = new LinkedList<>();
    }

    Listas(String _nome, List<NewListAdapter.Item> _itens){
        nome = _nome;
        itens = _itens;
    }

    public List<NewListAdapter.Item> get_itens() {
        return itens;
    }

    public String getNome() {
        return nome;
    }

    public void set_itens(List<NewListAdapter.Item> _itens) {
        this.itens = _itens;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
