package com.example.estoquepicole;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstoqueApp {
    private Estoque estoque;
    private DefaultListModel<Produto> listModel;
    private JList<Produto> showProducts;
    private JTextField txtName;
    private JTextField txtPrice;
    private JTextField txtAmount;

    public EstoqueApp() {
        estoque = new Estoque();
        listModel = new DefaultListModel<>();

        loadProductsFromDatabase();

        JFrame frame = new JFrame("Controle de Estoque");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        showProducts = new JList<>(listModel);
        panel.add(new JScrollPane(showProducts), BorderLayout.CENTER);

        JPanel formsPanel = new JPanel();
        formsPanel.setLayout(new GridLayout(3, 2));

        // Nome do Produto
        formsPanel.add(new JLabel("Nome: "));
        txtName = new JTextField();
        formsPanel.add(txtName);

        // Preço do Produto
        formsPanel.add(new JLabel("Preço: "));
        txtPrice = new JTextField();
        formsPanel.add(txtPrice);

        // Quantidade de Produto
        formsPanel.add(new JLabel("Quantidade: "));
        txtAmount = new JTextField();
        formsPanel.add(txtAmount);

        // Adiciona o painel de formulário ao painel principal
        panel.add(formsPanel, BorderLayout.NORTH);

        // Botão para ADICIONAR um Produto
        JButton btnAdd = new JButton("Adicionar");
        btnAdd.addActionListener(e -> addProduct());

        // Botão para REMOVER um Produto
        JButton btnRemove = new JButton("Remover");
        btnRemove.addActionListener(e -> removeProduct());

        // Botão para EDITAR um Produto
        JButton btnEdit = new JButton("Editar");
        btnEdit.addActionListener(e -> editProduct());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd);
        btnPanel.add(btnRemove);
        btnPanel.add(btnEdit);

        panel.add(btnPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void loadProductsFromDatabase() {
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM produtos");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int amount = rs.getInt("amount");
                Produto produto = new Produto(name, price, amount);
                listModel.addElement(produto);
                estoque.addProducts(produto); // Presumindo que você tenha um método para adicionar produtos no estoque
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao carregar produtos: " + e.getMessage());
        }
    }

    private void addProduct() {
        String name = txtName.getText();
        double price = Double.parseDouble(txtPrice.getText().replace(",", "."));
        int amount = Integer.parseInt(txtAmount.getText());
        Produto produto = new Produto(name, price, amount);
        estoque.addProducts(produto);
        listModel.addElement(produto);
        clearFields();



        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO produtos (name, price, amount) VALUES (?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao adicionar produto: " + e.getMessage());
        }

        clearFields();
    }

    // Função para REMOVER um produto;
    private void removeProduct(){
        int indexSelected = showProducts.getSelectedIndex();
        if (indexSelected != -1) {
            Produto produto = listModel.getElementAt(indexSelected);
            int response = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir este produto?", "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                System.out.println("Removendo produto: " + produto.getName());
                estoque.removeProducts(produto);
                listModel.remove(indexSelected);

                try (Connection connection = Database.getConnection();
                     PreparedStatement stmt = connection.prepareStatement("DELETE FROM produtos WHERE name = ? AND price = ? AND amount = ?")) {
                    stmt.setString(1, produto.getName());
                    stmt.setDouble(2, produto.getPrice());
                    stmt.setInt(3, produto.getAmount());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao remover produto: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um produto para remover.");
        }
    }

    // Função para EDITAR um produto;
    private void editProduct() {
        int indexSelected = showProducts.getSelectedIndex();
        if (indexSelected != -1) {
            Produto selectedProduct = listModel.getElementAt(indexSelected);
            openEditDialog(selectedProduct, indexSelected);
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um produto para alterar.");
        }
    }

    // Função para ABRIR o EditDialog
    private void openEditDialog(Produto produto, int index) {
        JTextField txtName = new JTextField(produto.getName());
        JTextField txtPrice = new JTextField(String.valueOf(produto.getPrice()));
        JTextField txtAmount = new JTextField(String.valueOf(produto.getAmount()));

        Object[] message = {
                "Nome: ", txtName,
                "Preço: ", txtPrice,
                "Quantidade: ", txtAmount
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Alterar Produto", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = txtName.getText();
            double price = Double.parseDouble(txtPrice.getText());
            int amount = Integer.parseInt(txtAmount.getText());
            Produto newProduct = new Produto(name, price, amount);
            estoque.editProducts(index, newProduct);
            listModel.set(index, newProduct);

            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement("UPDATE produtos SET name = ?, price = ?, amount = ? WHERE name = ? AND price = ? AND amount = ?")) {
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setInt(3, amount);
                stmt.setString(4, produto.getName());
                stmt.setDouble(5, produto.getPrice());
                stmt.setInt(6, produto.getAmount());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao editar produto: " + e.getMessage());
            }
        }
    }

    // Função para deixar os campos em brancos;
    private void clearFields() {
        txtName.setText("");
        txtPrice.setText("");
        txtAmount.setText("");
    }

    // Iniciar o projeto
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EstoqueApp());
    }
}
