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

        // Botão para ADICIONAR um Produto
        JButton btnAdd = new JButton("Adicionar");
        btnAdd.addActionListener(e -> openAddProductDialog());

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
                String brand = rs.getString("brand");
                String tipo = rs.getString("tipo");
                String cnpj = rs.getString("cnpj");
                Produto produto = new Produto(name, price, amount, brand, tipo, cnpj);
                listModel.addElement(produto);
                estoque.addProducts(produto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao carregar produtos: " + e.getMessage());
        }
    }

    private void openAddProductDialog() {
        while (true) {
            JTextField txtName = new JTextField();
            JTextField txtPrice = new JTextField();
            JTextField txtAmount = new JTextField();
            JTextField txtBrand = new JTextField("Moleka");
            JTextField txtCNPJ = new JTextField("06.094.363/0001-84");

            JRadioButton chkIsPicole = new JRadioButton("Picole", true);
            JRadioButton chkIsPote = new JRadioButton("Pote de Sorvete");

            ButtonGroup group = new ButtonGroup();
            group.add(chkIsPicole);
            group.add(chkIsPote);

            Object[] message = {
                    "Nome: ", txtName,
                    "Preço: ", txtPrice,
                    "Quantidade: ", txtAmount,
                    "Marca: ", txtBrand,
                    chkIsPicole,
                    chkIsPote,
                    "CNPJ:", txtCNPJ,
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Adicionar Produto", JOptionPane.OK_CANCEL_OPTION);

            // Para o loop ao cancelar
            if (option == JOptionPane.CANCEL_OPTION) {
                break;
            }

            String name = txtName.getText();
            double price;
            int amount;
            String brand = txtBrand.getText();
            String cnpj = txtCNPJ.getText();

            // Validação dos campos;
            if (name.isEmpty() || txtPrice.getText().isEmpty() || txtAmount.getText().isEmpty() || cnpj.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            try {
                price = Double.parseDouble(txtPrice.getText().replace(",", "."));
                amount = Integer.parseInt(txtAmount.getText()); // Quantidade é OBRIGATÓRIO SER INTEIRO;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Quantidade deve ser um número inteiro.", "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // Cria um produto novo, se estiver OK
            String tipo = chkIsPote.isSelected() ? "Pote de Sorvete" : "Picolé";
            Produto produto = new Produto(name, price, amount, brand, tipo, cnpj);
            estoque.addProducts(produto);
            listModel.addElement(produto);

            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement("INSERT INTO produtos (name, price, amount,) VALUES (?, ?, ?)")) {
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setInt(3, amount);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao adicionar produto: " + e.getMessage());
            }
            break;
        }
    }

    private void removeProduct() {
        int indexSelected = showProducts.getSelectedIndex();
        if (indexSelected != -1) {
            Produto produto = listModel.getElementAt(indexSelected);
            int response = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir este produto?", "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
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

    private void editProduct() {
        int indexSelected = showProducts.getSelectedIndex();
        if (indexSelected != -1) {
            Produto selectedProduct = listModel.getElementAt(indexSelected);
            openEditDialog(selectedProduct, indexSelected);
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um produto para alterar.");
        }
    }

    private void openEditDialog(Produto produto, int index) {
        while (true) {
            JTextField txtName = new JTextField(produto.getName());
            JTextField txtPrice = new JTextField(String.valueOf(produto.getPrice()));
            JTextField txtAmount = new JTextField(String.valueOf(produto.getAmount()));
            JTextField txtBrand = new JTextField(produto.getBrand());
            JTextField txtCNPJ = new JTextField(produto.getCnpj());
            JRadioButton chkIsPicole = new JRadioButton("Picole", produto.getTipo().equals("Picole"));
            JRadioButton chkIsPote = new JRadioButton("Pote de Sorvete", produto.getTipo().equals("Pote de Sorvete"));

            ButtonGroup group = new ButtonGroup();
            group.add(chkIsPicole);
            group.add(chkIsPote);

            Object[] message = {
                    "Nome: ", txtName,
                    "Preço: ", txtPrice,
                    "Quantidade: ", txtAmount,
                    "Marca: ", txtBrand,
                    "CNPJ ", txtCNPJ,
                    chkIsPicole,
                    chkIsPote,
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Alterar Produto", JOptionPane.OK_CANCEL_OPTION);

            // Para o Loop ao CANCELAR
            if (option == JOptionPane.CANCEL_OPTION) {
                break;
            }

            String name = txtName.getText();
            double price;
            int amount;
            String brand = txtBrand.getText();
            String tipo = chkIsPote.isSelected() ? "Pote de Sorvete" : "Picolé";
            String cnpj = txtCNPJ.getText();

            // VALIDAÇÃO DOS CAMPOS
            if (name.isEmpty() || txtPrice.getText().isEmpty() || txtAmount.getText().isEmpty() || cnpj.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            try {
                price = Double.parseDouble(txtPrice.getText().replace(",", "."));
                amount = Integer.parseInt(txtAmount.getText()); // Quantidade é OBRIGATÓRIO SER INTEIRO;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Quantidade deve ser um número inteiro.", "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // COM TUDO CORRETO, ATUALIZA O PRODUTO
            Produto newProduct = new Produto(name, price, amount, brand, tipo, cnpj);
            estoque.editProducts(index, newProduct);
            listModel.set(index, newProduct);

            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement("UPDATE produtos SET name = ?, price = ?, amount = ?, WHERE name = ? AND price = ? AND amount = ?")) {
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setInt(3, amount);
                stmt.setString(7, produto.getName());
                stmt.setDouble(8, produto.getPrice());
                stmt.setInt(9, produto.getAmount());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao editar o produto: " + e.getMessage());
            }
            break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EstoqueApp());
    }
}