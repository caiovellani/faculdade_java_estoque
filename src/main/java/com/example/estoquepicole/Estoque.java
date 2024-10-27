package com.example.estoquepicole;

import java.util.ArrayList;
import java.util.List;

public class Estoque {
    private List<Produto> produtos;

    public Estoque() {
        produtos = new ArrayList<>();
    }

    public void addProducts(Produto produto){
        produtos.add(produto);
    }

    public void removeProducts(Produto produto){
        produtos.remove(produto);
    }

    public void editProducts(int index, Produto produto){
        if (index >= 0 && index < produtos.size()) {
            produtos.set(index, produto);
        }
    }

    public List<Produto> getProdutos() {
        return produtos;
    }
}
