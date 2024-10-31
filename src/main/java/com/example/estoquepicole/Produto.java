package com.example.estoquepicole;

public class Produto {
    private String name;
    private double price;
    private int amount;
    private String brand;
    private String tipo;
    private String cnpj;


    public Produto(String name, double price, int amount, String brand, String tipo, String cnpj) {
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.brand = brand;
        this.tipo = tipo;
        this.cnpj = cnpj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    @Override
    public String toString() {
        return String.format("Nome: %s - Marca: %s - Tipo: %s - Preço: R$ %.2f - Quantidade: %d, CNPJ: %s",
                name, brand, tipo, price, amount, cnpj);
    }
}