package com.example.projetobase;


import static java.time.LocalDate.now;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class Listas {

    public String nome;
    public List<NewListAdapter.Item> itens;
    public LocalDate datacriacao;

    @RequiresApi(api = Build.VERSION_CODES.O)
    Listas(){
        nome = "";
        itens = new LinkedList<>();
        datacriacao =LocalDate.now();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    Listas(String _nome, List<NewListAdapter.Item> _itens){
        nome = _nome;
        itens = _itens;
        datacriacao = LocalDate.now();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    Listas(String _nome, List<NewListAdapter.Item> _itens, LocalDate Data){
        nome = _nome;
        itens = _itens;
        datacriacao = Data;
    }

    public List<NewListAdapter.Item> get_itens() {
        return itens;
    }

    public String getNome() {
        return nome;
    }

    public String getData() {
        return datacriacao.toString();
    }

    public void set_itens(List<NewListAdapter.Item> _itens) {
        this.itens = _itens;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
