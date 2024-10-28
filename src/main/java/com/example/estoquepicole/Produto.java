package com.example.estoquepicole;

public class Produto {
    private String name;
    private double price;
    private int amount;

    public Produto(String name, double price, int amount) {
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public String getName(){
        return name;
    }

    public double getPrice(){
        return price;
    }

    public int getAmount(){
        return amount;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }

    @Override
    public String toString(){
        return String.format("Nome: %s - Preço: R$ %.2f - Quantidade: %d", name, price, amount);
    }

}

