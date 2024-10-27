package com.example.estoquepicole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        // Botão para REMOVER um Produto
        JButton btnRemove = new JButton("Remover");
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeProduct();
            }
        });

        // Botão para EDITAR um Produto
        JButton btnEdit = new JButton("Editar");
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editProduct();
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd);
        btnPanel.add(btnRemove);
        btnPanel.add(btnEdit);

        panel.add(btnPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    // Função para ADICIONAR um produto;
    private void addProduct() {
        String name = txtName.getText();
        double price = Double.parseDouble(txtPrice.getText());
        int amount = Integer.parseInt(txtAmount.getText());
        Produto produto = new Produto(name, price, amount);
        estoque.addProducts(produto);
        listModel.addElement(produto);
        clearFields();
    }

    // Função para REMOVER um produto;
    private void removeProduct(){
        int indexSelected = showProducts.getSelectedIndex();
        if (indexSelected != -1) {
            int response = JOptionPane.showConfirmDialog(
                    null,
                    "Tem certeza que deseja excluir este produto?",
                    "Confirmação de Exclusão",
                    JOptionPane.YES_NO_OPTION
            );

            if (response == JOptionPane.YES_OPTION) {
                estoque.removeProducts(listModel.getElementAt(indexSelected));
                listModel.remove(indexSelected);
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
                "Quantidade: ", txtAmount  // Corrigido para usar o campo correto
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Alterar Produto", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = txtName.getText();
            double price = Double.parseDouble(txtPrice.getText());
            int amount = Integer.parseInt(txtAmount.getText());
            Produto newProduct = new Produto(name, price, amount);
            estoque.editProducts(index, newProduct);
            listModel.set(index, newProduct);
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
        SwingUtilities.invokeLater(EstoqueApp::new);
    }
}