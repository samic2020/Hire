package com.samic.cadastros;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.samic.funcoes.DataBasePassWordCrypto;
import com.samic.funcoes.Db;
import com.samic.funcoes.Globais;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;
import com.samic.funcoes.JTextFieldLimit;
import com.samic.funcoes.checkboxtree.*;
import com.samic.genericos.cpoTelefones;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author YOGA 510
 */
public class usuarios extends javax.swing.JInternalFrame {
    private Db conn = Globais.conn;
    private JPanel panel;
    private int _new = -1;
    private int _user = -1;
    private JTree tree;
    
    /**
     * Creates new form usuarios
     */
    private void btnRetornar(ActionEvent e) {
        if (_new > -1) {
            int resp = JOptionPane.showConfirmDialog(this, "Você esta " + (_new == 0 ? "incluindo" : "alterando") + " este registro.\n" +
                    "Deseja cancelar a operação?", "Atenção", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (resp == JOptionPane.NO_OPTION) return;            
        }
        dispose();
    }

    public usuarios() {
        initComponents();
        initSubOpcoes();
        
        // Quando ganhar o foco colocar Clera(x), quando perder o foco desligar o Clear(x)
        
        login.putClientProperty("JTextField.showClearButton", Boolean.valueOf(true));
        senha.putClientProperty("JTextField.showClearButton", Boolean.valueOf(true));
        
        nome.setDocument(new JTextFieldLimit(60));
        login.setDocument(new JTextFieldLimit(20));
        senha.setDocument(new JTextFieldLimit(20));
        dtcadastro.setBackground(nome.getBackground()); dtcadastro.setForeground(nome.getForeground());
        
        if (ReadUser(_user)) {
            btnIncluir.setEnabled(true);
            btnAlterar.setEnabled(true);
            btnExcluir.setEnabled(true);
            btnPesquisar.setEnabled(true);
            btnGravar.setEnabled(false);
            btnRetornar.setEnabled(true);
        } else {
            btnIncluir.setEnabled(true);
            btnAlterar.setEnabled(false);
            btnExcluir.setEnabled(false);
            btnPesquisar.setEnabled(false);
            btnGravar.setEnabled(false);
            btnRetornar.setEnabled(true);            
        }
        FieldsEnable(false);
    }

    private void ClearUser() {
        id.setText("000");
        nome.setText("");
        login.setText("");
        senha.setText("");
        redefine.setSelected(false);
        dtcadastro.setText("");
        menu.setText("");
        telefone.removeAllItems();
    }
    
    private boolean ReadUser(int nId) {
        // Limpa tela
        ClearUser();
        
        String selectSQL = ""; Object[][] param = null; boolean _alguem = false;
        if (nId == -1) {
            selectSQL = "SELECT `id`, `nome`, `login`, `senha`, `dtcadastro`, `resenha`, `foto`, `menu` FROM `usuarios` LIMIT 1;";
        } else {
            selectSQL = "SELECT `id`, `nome`, `login`, `senha`, `dtcadastro`, `resenha`, `foto`, `menu` FROM `usuarios` WHERE `id` = :id LIMIT 1;";
            param = new Object[][] {{"int","id",nId}};
        }
        ResultSet rs = conn.OpenTable(selectSQL, param);
        try {
            while (rs.next()) {
                _alguem = true;
                id.setText(String.format("%03d" ,rs.getInt("id")));
                try {nome.setText(rs.getString("nome"));} catch (SQLException ex) {}
                try {login.setText(rs.getString("login"));} catch (SQLException ex) {}
                try {senha.setText(DataBasePassWordCrypto.decrypt(rs.getString("senha"), DataBasePassWordCrypto.ALGORITMO_AES, DataBasePassWordCrypto.ALGORITMO_AES));} catch (SQLException ex) {}
                try {redefine.setSelected(rs.getBoolean("resenha"));} catch (SQLException ex) {}
                try {dtcadastro.setText(new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("dtcadastro")));} catch (SQLException ex) {}
                try {menu.setText(rs.getString("menu"));} catch (SQLException ex) {}
                String _telefone = null;
                try {_telefone = rs.getString("telefone");} catch (SQLException ex) {}
                if (_telefone == null) telefone.removeAllItems(); else {
                    for (String item : _telefone.split(";")) {
                        telefone.addItem(item);
                    }                        
                }
            }
        } catch (SQLException e) {}
        conn.CloseTable(rs);        
        AcessMenu(menu.getText());
        
        return _alguem;
    }
    
    private void btnTelAdd(ActionEvent e) {
        String _telephone = new cpoTelefones(this).getTelephone();
        if (!_telephone.startsWith("(  )     -    ") || _telephone != null) telefone.addItem(_telephone);
        telefone.setEnabled(telefone.getItemCount() > 0);
    }

    private void btnTelDel(ActionEvent e) {
        if (telefone.getSelectedIndex() == -1) return;
        int resp = JOptionPane.showConfirmDialog(this, "Exclui este telefone?","Atenção", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.NO_OPTION) return;
        telefone.removeItemAt(telefone.getSelectedIndex());
        telefone.setEnabled(telefone.getItemCount() > 0);
    }

    //DataBasePassWordCrypto.encrypt(rs.getString("senha"), DataBasePassWordCrypto.ALGORITMO_AES,DataBasePassWordCrypto.ALGORITMO_AES)
    //DataBasePassWordCrypto.decrypt(rs.getString("senha"), DataBasePassWordCrypto.ALGORITMO_AES, DataBasePassWordCrypto.ALGORITMO_AES);
    private void AcessMenu() {
        // Montagem do menu                        
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        
        String nivelMenu = "select LPAD(id,3,0) id, nivel, nome, atalho from menu where snivel = 0 order by nivel;";
        ResultSet nivel = conn.OpenTable(nivelMenu, null);
        try {
            while (nivel.next()) {
                String tid = nivel.getString("id");
                String tnome = nivel.getString("nome");
                
                // Cria menu nivel superior
                final DefaultMutableTreeNode topMenu = new DefaultMutableTreeNode(tnome);  
                
                String snivelMenu = "select LPAD(id,3,0) id, nivel, snivel, icone, nome, atalho, chamada, habilitar from menu where nivel = :nivel and snivel > 0 order by snivel;";
                Object[][] param = {
                    {"int", "nivel", nivel.getInt("nivel")},
                };                
                ResultSet snivel = conn.OpenTable(snivelMenu, param);
                try {
                    while (snivel.next()) {
                        String id = snivel.getString("id");
                        String nome = snivel.getString("nome");
                        String[] amenu = null;
                        String habilitar = null;
                        try { habilitar = snivel.getString("habilitar"); } catch (SQLException ex) {}
                        if (habilitar != null) {
                            String[] _hab = habilitar.split(";");
                            String _ha = id + ":";
                            for (String item : _hab) {
                               _ha += "0";
                            }
                            amenu = new String[] {_ha};
                        }
                        
                        Object[] mneuvisible = menuVisible(amenu, id);
                        boolean _visible = (boolean)mneuvisible[0];
                        String _string = (String)mneuvisible[1];
                        
                        add(topMenu, nome, _visible, id, _string);
                    }
                } catch (SQLException ex) {}
                conn.CloseTable(snivel);
                root.add(topMenu);
            }
        } catch (SQLException e) {}
        conn.CloseTable(nivel);
        
        final DefaultTreeModel treeModel = new DefaultTreeModel(root);

        tree = new JTree(treeModel);
        FlatSVGIcon logoutIcon = new FlatSVGIcon("icons/show.svg",16,16);
        UIManager.put("Tree.closedIcon", logoutIcon);
        UIManager.put("Tree.openIcon",  logoutIcon);

        final CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
        tree.setCellRenderer(renderer);

        final CheckBoxNodeEditor editor = new CheckBoxNodeEditor(tree);
        tree.setCellEditor(editor);
        tree.setEditable(true);

        // listen for changes in the selection
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(final TreeSelectionEvent e) {
                try {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (node == null) return;
                    if (node instanceof DefaultMutableTreeNode) {
                        CheckBoxNodeData nodeInfo = (CheckBoxNodeData)node.getUserObject();
                        SubOpcoes(nodeInfo.getId(), nodeInfo.getSopcoes());
                    }
                } catch (Exception es) { ClearPanel(); }
            }                
        });

        // show the tree onscreen
        tree.setRootVisible(false);
        
        final JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setSize(711, 217);
        panelTree.add(scrollPane);                
        
        pack();
    }

    private void AcessMenu(String menu) {
        // Montagem do menu                        
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        
        String[] amenu = null;        
        if (!menu.isEmpty()) amenu = menu.split(";");

        String nivelMenu = "select LPAD(id,3,0) id, nivel, nome, atalho from menu where snivel = 0 order by nivel;";
        ResultSet nivel = conn.OpenTable(nivelMenu, null);
        try {
            while (nivel.next()) {
                String tid = nivel.getString("id");
                String tnome = nivel.getString("nome");
                
                // Cria menu nivel superior
                final DefaultMutableTreeNode topMenu = new DefaultMutableTreeNode(tnome);  
                
                String snivelMenu = "select LPAD(id,3,0) id, nivel, snivel, icone, nome, atalho, chamada, habilitar from menu where nivel = :nivel and snivel > 0 order by snivel;";
                Object[][] param = {
                    {"int", "nivel", nivel.getInt("nivel")},
                };                
                ResultSet snivel = conn.OpenTable(snivelMenu, param);
                try {
                    while (snivel.next()) {
                        String id = snivel.getString("id");
                        String nome = snivel.getString("nome");
                        String habilitar = null;
                        try { habilitar = snivel.getString("habilitar"); } catch (SQLException ex) {}
                        Object[] mneuvisible = menuVisible(amenu, id, habilitar);
                        boolean _visible = (boolean)mneuvisible[0];
                        String _string = (String)mneuvisible[1];
                        
                        add(topMenu, nome, _visible, id, _string);
                    }
                } catch (SQLException ex) {}
                conn.CloseTable(snivel);
                root.add(topMenu);
            }
        } catch (SQLException e) {}
        conn.CloseTable(nivel);
        
        final DefaultTreeModel treeModel = new DefaultTreeModel(root);

        tree = new JTree(treeModel);
        FlatSVGIcon logoutIcon = new FlatSVGIcon("icons/show.svg",16,16);
        UIManager.put("Tree.closedIcon", logoutIcon);
        UIManager.put("Tree.openIcon",  logoutIcon);

        final CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
        tree.setCellRenderer(renderer);

        final CheckBoxNodeEditor editor = new CheckBoxNodeEditor(tree);
        tree.setCellEditor(editor);
        tree.setEditable(true);

        // listen for changes in the selection
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(final TreeSelectionEvent e) {
                try {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (node == null) return;
                    if (node instanceof DefaultMutableTreeNode) {
                        CheckBoxNodeData nodeInfo = (CheckBoxNodeData)node.getUserObject();
                        SubOpcoes(nodeInfo.getId(), nodeInfo.getSopcoes());
                    }
                } catch (Exception es) { ClearPanel(); }
            }                
        });

        // show the tree onscreen
        tree.setRootVisible(false);
        
        final JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setSize(711, 217);
        panelTree.add(scrollPane);                
        
        pack();
    }

    private static DefaultMutableTreeNode add(
            final DefaultMutableTreeNode parent, final String text,
            final boolean checked, final String id, String sopcoes) {
            final CheckBoxNodeData data = new CheckBoxNodeData(text, checked, id, sopcoes);            
            final DefaultMutableTreeNode node = new DefaultMutableTreeNode(data);
            parent.add(node);
            return node;
    }

    private Object[] menuVisible(String[] amenu, String idmenu) {
        if (amenu == null) return new Object[] {true,""};
        
        boolean _visible = false;
        String _opcoes = "";
        for (String item : amenu) {
            if (item.subSequence(0, 3).equals(idmenu)) {
                _visible = true;
                _opcoes = item.length() >= 4 ? item.substring(4) : "";
                break;
            }
        }    
        return new Object[] {_visible, _opcoes};
    }

    private Object[] menuVisible(String[] amenu, String idmenu, String habilitar) {
        if (amenu == null && habilitar == null) {
            return new Object[] {true,""};
        } else if (amenu == null && habilitar != null) {
            String[] _hab = habilitar.split(";");
            String _ha = idmenu + ":";
            for (String item : _hab) {
               _ha += "0";
            }
            amenu = new String[] {_ha};
        }
        
        boolean _visible = false;
        String _opcoes = "";
        for (String item : amenu) {
            if (item.subSequence(0, 3).equals(idmenu)) {
                _visible = true;
                _opcoes = item.length() >= 4 ? item.substring(4) : "";
                break;
            }
        }    
        return new Object[] {_visible, _opcoes};
    }

    private void initSubOpcoes() {
        panel = new JPanel();
        panel.setBorder(new TitledBorder("Opc\u00f5es da Tela"));
        panel.setLocation(353, 3);
        panel.setSize(356,210);
        panel.setVisible(false);
        panelTree.add(panel);
    }

    private void SubOpcoes(String id, String sopcoes) {
        for (Component c : panel.getComponents()) {
            panel.remove(c);
        }
        if (!sopcoes.trim().equalsIgnoreCase("")) {
            for (char item : sopcoes.toCharArray()) {
                JCheckBox check = new JCheckBox();
                check.setSelected(item == '0' ? false : true);
                check.setEnabled(_new > 0);
                panel.add(check);
            }
            panel.setVisible(true);
        } else panel.setVisible(false);
    }

    private void ClearPanel() {
        panel.setVisible(false);
        for (Component c : panel.getComponents()) {
            panel.remove(c);
        }
    }
    
    private void nomeFocusGained(FocusEvent e) {
        nome.putClientProperty("JTextField.showClearButton", Boolean.valueOf(_new > -1 ? true : false));
    }

    private void nomeFocusLost(FocusEvent e) {
        nome.putClientProperty("JTextField.showClearButton", Boolean.valueOf(false));
    }

    private void loginFocusGained(FocusEvent e) {
        login.putClientProperty("JTextField.showClearButton", Boolean.valueOf(_new > -1 ? true : false));
    }

    private void loginFocusLost(FocusEvent e) {
        login.putClientProperty("JTextField.showClearButton", Boolean.valueOf(false));
    }

    private void senhaFocusGained(FocusEvent e) {
        senha.putClientProperty("JTextField.showClearButton", Boolean.valueOf(_new > -1 ? true : false));
    }

    private void senhaFocusLost(FocusEvent e) {
        senha.putClientProperty("JTextField.showClearButton", Boolean.valueOf(false));
    }

    private void btnIncluir(ActionEvent e) {
        _new = 0;
        ClearUser();
        FieldsEnable(true);
        AcessMenu();
        
        btnIncluir.setEnabled(false);
        btnAlterar.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnPesquisar.setEnabled(false);
        btnGravar.setEnabled(true);
        btnRetornar.setEnabled(true);
        
        nome.requestFocus();
    }

    private void btnAlterar(ActionEvent e) {
        _new = 1;
        FieldsEnable(true);

        btnIncluir.setEnabled(false);
        btnAlterar.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnPesquisar.setEnabled(false);
        btnGravar.setEnabled(true);
        btnRetornar.setEnabled(true);

        nome.requestFocus();
    }

    private void btnGravar(ActionEvent e) {
        String SQL = ""; Object[][] param = null;
        String _senha = "";
        try { _senha = DataBasePassWordCrypto.encrypt(senha.getText().trim(), DataBasePassWordCrypto.ALGORITMO_AES,DataBasePassWordCrypto.ALGORITMO_AES);} catch (Exception ex) {}

        if (_new == 0) {
            // Inclusao
            SQL = "INSERT INTO `usuarios`(`nome`,`login`,`senha`,`dtcadastro`,`resenha`," +
                    "`telefone`,`menu`) VALUES (:nome,:login,:senha,:dtcadastro,:resenha,:telefone,:menu);";
            
            param = new Object[][] {
                {"string","nome",nome.getText().trim()},
                {"string","login",login.getText().trim()},
                {"string","senha",_senha},
                {"date","dtcadastro",new Date()},
                {"boolean","resenha",redefine.isSelected()},
                {"string","telefone",getTelefones()},
                {"string","menu",menu.getText().trim()}
            };
        } else if (_new == 1) {
            // Alteração
            SQL = "UPDATE `usuarios` SET `nome` = :nome, `login` = :login, `senha` = :senha, " +
                    "`resenha` = :resenha, `telefone` = :telefone, `menu` = :menu " +
                    "WHERE `id` = :id;";
            param = new Object[][] {
                {"string","nome",nome.getText().trim()},
                {"string","login",login.getText().trim()},
                {"string","senha",_senha},
                {"boolean","resenha",redefine.isSelected()},
                {"string","telefone",getTelefones()},
                {"string","menu",menu.getText().trim()},
                {"int","id",Integer.parseInt(id.getText().trim())}
            };
        }
        
        if (conn.CommandExecute(SQL, param) > 0) {
            btnIncluir.setEnabled(true);
            btnAlterar.setEnabled(true);
            btnExcluir.setEnabled(true);
            btnPesquisar.setEnabled(true);
            btnGravar.setEnabled(false);
            btnRetornar.setEnabled(true);
        }
        
        _new = -1;
        FieldsEnable(false);                
    }
    
    private String getTelefones() {
        String _telefone = "";
        for (int i = 0; i < telefone.getItemCount(); i++) {
            _telefone += telefone.getItemAt(i) + ";";
        }
        if (_telefone.length() > 0) _telefone = _telefone.substring(0, _telefone.length() - 1);
        return _telefone;
    }

    private void FieldsEnable(boolean value) {
        id.setEnabled(value);
        nome.setEnabled(value);
        login.setEnabled(value);
        senha.setEnabled(value);
        redefine.setEnabled(value);
        dtcadastro.setEnabled(value);
        telefone.setEnabled(telefone.getItemCount() > 0); btnTelAdd.setEnabled(value); btnTelDel.setEnabled(value);
        // Acessos
        panel.setEnabled(value);
        tree.setEnabled(value);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        label1 = new JLabel();
        id = new JTextField();
        label2 = new JLabel();
        nome = new JTextField();
        foto = new JLabel();
        label4 = new JLabel();
        login = new JTextField();
        label5 = new JLabel();
        senha = new JPasswordField();
        redefine = new JCheckBox();
        panel1 = new JPanel();
        btnIncluir = new JButton();
        btnAlterar = new JButton();
        btnExcluir = new JButton();
        btnPesquisar = new JButton();
        btnGravar = new JButton();
        btnRetornar = new JButton();
        panelTree = new JPanel();
        label9 = new JLabel();
        label6 = new JLabel();
        menu = new JLabel();
        label3 = new JLabel();
        telefone = new JComboBox();
        btnTelAdd = new JButton();
        btnTelDel = new JButton();
        dtcadastro = new JTextField();

        //======== this ========
        setVisible(true);
        setTitle(".:: Cadastro de Usu\u00e1rios.");
        setIconifiable(true);
        setFrameIcon(new ImageIcon(getClass().getResource("/cadastro/usuarios.png")));
        setMinimumSize(new Dimension(740, 470));
        setMaximumSize(new Dimension(740, 470));
        Container contentPane = getContentPane();

        //---- label1 ----
        label1.setText("Id:");

        //---- id ----
        id.setEditable(false);
        id.setFocusable(false);
        id.setFont(id.getFont().deriveFont(id.getFont().getStyle() | Font.BOLD));
        id.setHorizontalAlignment(SwingConstants.CENTER);
        id.setText("0");

        //---- label2 ----
        label2.setText("Nome:");

        //---- nome ----
        nome.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                nomeFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                nomeFocusLost(e);
            }
        });

        //---- foto ----
        foto.setBorder(new BevelBorder(BevelBorder.LOWERED));

        //---- label4 ----
        label4.setText("Login:");

        //---- login ----
        login.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                loginFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                loginFocusLost(e);
            }
        });

        //---- label5 ----
        label5.setText("Senha:");

        //---- senha ----
        senha.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                senhaFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                senhaFocusLost(e);
            }
        });

        //---- redefine ----
        redefine.setText("Redefinir senha.");

        //======== panel1 ========
        {
            panel1.setBorder(new EtchedBorder());

            //---- btnIncluir ----
            btnIncluir.setText("Incluir");
            btnIncluir.setIcon(new ImageIcon(getClass().getResource("/cadastro/user-add.png")));
            btnIncluir.addActionListener(e -> btnIncluir(e));

            //---- btnAlterar ----
            btnAlterar.setText("Alterar");
            btnAlterar.setIcon(new ImageIcon(getClass().getResource("/cadastro/user-edit.png")));
            btnAlterar.addActionListener(e -> btnAlterar(e));

            //---- btnExcluir ----
            btnExcluir.setText("Ecluir");
            btnExcluir.setIcon(new ImageIcon(getClass().getResource("/cadastro/user-delete.png")));

            //---- btnPesquisar ----
            btnPesquisar.setText("Pesquisar");
            btnPesquisar.setIcon(new ImageIcon(getClass().getResource("/cadastro/lupa.png")));

            //---- btnGravar ----
            btnGravar.setText("Gravar");
            btnGravar.setIcon(new ImageIcon(getClass().getResource("/cadastro/save.png")));
            btnGravar.addActionListener(e -> btnGravar(e));

            //---- btnRetornar ----
            btnRetornar.setText("Retornar");
            btnRetornar.setIcon(new ImageIcon(getClass().getResource("/cadastro/sair.png")));
            btnRetornar.addActionListener(e -> {
			btnRetornar(e);
			btnRetornar(e);
			btnRetornar(e);
		});

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnIncluir)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAlterar)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcluir)
                        .addGap(29, 29, 29)
                        .addComponent(btnPesquisar)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGravar)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRetornar)
                        .addContainerGap())
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(btnRetornar)
                            .addComponent(btnGravar)
                            .addComponent(btnIncluir)
                            .addComponent(btnAlterar)
                            .addComponent(btnExcluir)
                            .addComponent(btnPesquisar))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== panelTree ========
        {
            panelTree.setBorder(new EtchedBorder());
            panelTree.setMaximumSize(new Dimension(711, 217));
            panelTree.setMinimumSize(new Dimension(711, 217));

            GroupLayout panelTreeLayout = new GroupLayout(panelTree);
            panelTree.setLayout(panelTreeLayout);
            panelTreeLayout.setHorizontalGroup(
                panelTreeLayout.createParallelGroup()
                    .addGap(0, 707, Short.MAX_VALUE)
            );
            panelTreeLayout.setVerticalGroup(
                panelTreeLayout.createParallelGroup()
                    .addGap(0, 218, Short.MAX_VALUE)
            );
        }

        //---- label9 ----
        label9.setText("Configura\u00e7\u00f5es de Acesso");
        label9.setIcon(new ImageIcon(getClass().getResource("/cadastro/menu.png")));
        label9.setOpaque(true);
        label9.setBackground(SystemColor.windowBorder);

        //---- label6 ----
        label6.setText("Data Cadastro:");

        //---- menu ----
        menu.setVisible(false);

        //---- label3 ----
        label3.setText("Tel.:");

        //---- btnTelAdd ----
        btnTelAdd.setIcon(new ImageIcon(getClass().getResource("/icons/plus.png")));
        btnTelAdd.addActionListener(e -> btnTelAdd(e));

        //---- btnTelDel ----
        btnTelDel.setIcon(new ImageIcon(getClass().getResource("/icons/minus.png")));
        btnTelDel.addActionListener(e -> btnTelDel(e));

        //---- dtcadastro ----
        dtcadastro.setEditable(false);
        dtcadastro.setOpaque(true);
        dtcadastro.setHorizontalAlignment(SwingConstants.CENTER);

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addComponent(foto, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(19, 19, 19)
                                    .addComponent(label1)
                                    .addGap(22, 22, 22)
                                    .addComponent(id, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(label2)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(nome))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label9, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(12, 12, 12)
                                    .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                            .addComponent(label4, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                                            .addGap(12, 12, 12)
                                            .addComponent(login, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(label5, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(senha, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(redefine))
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                            .addComponent(label6, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(dtcadastro, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
                                            .addGap(66, 66, 66)
                                            .addGroup(contentPaneLayout.createParallelGroup()
                                                .addComponent(menu, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                                                .addGroup(contentPaneLayout.createSequentialGroup()
                                                    .addGap(6, 6, 6)
                                                    .addComponent(label3)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(telefone, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnTelAdd, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnTelDel, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))))))
                        .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelTree, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label1)
                                .addComponent(id, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(label2)
                                .addComponent(nome, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label4)
                                .addComponent(senha, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(label5)
                                .addComponent(redefine)
                                .addComponent(login, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnTelDel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnTelAdd, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addComponent(menu, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label6)
                                        .addComponent(label3)
                                        .addComponent(telefone, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(dtcadastro, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                            .addGap(8, 8, 8)
                            .addComponent(label9, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
                        .addComponent(foto, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                    .addComponent(panelTree, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel label1;
    private JTextField id;
    private JLabel label2;
    private JTextField nome;
    private JLabel foto;
    private JLabel label4;
    private JTextField login;
    private JLabel label5;
    private JPasswordField senha;
    private JCheckBox redefine;
    private JPanel panel1;
    private JButton btnIncluir;
    private JButton btnAlterar;
    private JButton btnExcluir;
    private JButton btnPesquisar;
    private JButton btnGravar;
    private JButton btnRetornar;
    private JPanel panelTree;
    private JLabel label9;
    private JLabel label6;
    private JLabel menu;
    private JLabel label3;
    private JComboBox telefone;
    private JButton btnTelAdd;
    private JButton btnTelDel;
    private JTextField dtcadastro;
    // End of variables declaration//GEN-END:variables
}
