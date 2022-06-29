package com.samic.hire;

import com.samic.funcoes.DataBasePassWordCrypto;
import com.samic.funcoes.Db;
import com.samic.funcoes.Globais;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author YOGA 510
 */
public class login extends javax.swing.JFrame {
    private Db conn = null;
    private int tentativas = 1;
    
    /**
     * Creates new form login
     */
    public login() {
        setUndecorated(true);
        setBackground(new Color(0,0,0,0));
        setSize(478, 267);
        initComponents();
        
        
        // background
        URL url = getClass().getResource("/figuras/login.png");
        
        ImageIcon icone = new ImageIcon(url);
        //setIconImage(icone.getImage());
        backGround.setIcon(icone);
        setLocationRelativeTo(null);         

        // Unidades de acesso remoto
        jUnidade.removeAllItems();
        for (int w=0; w < Globais.units.size(); w++) {
            jUnidade.addItem(Globais.units.get(w)[0].toString());
        }
        
        jUnidade.setEnabled(true);
        jUnidade.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jUnidade = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        usuario = new javax.swing.JTextField();
        senha = new javax.swing.JPasswordField();
        backGround = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(".:: Imobilis - Sistema Imobiliário");
        setAlwaysOnTop(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jUnidade.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jUnidadeFocusGained(evt);
            }
        });
        jUnidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jUnidadeKeyPressed(evt);
            }
        });
        getContentPane().add(jUnidade, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 140, 190, -1));

        jLabel4.setText("Usuário:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 172, 50, -1));

        jLabel3.setText("Estação:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 142, -1, -1));

        jLabel5.setText("Senha:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 202, -1, -1));

        usuario.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        usuario.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                usuarioFocusGained(evt);
            }
        });
        usuario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                usuarioKeyPressed(evt);
            }
        });
        getContentPane().add(usuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 170, 190, -1));

        senha.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        senha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                senhaFocusGained(evt);
            }
        });
        senha.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                senhaKeyPressed(evt);
            }
        });
        getContentPane().add(senha, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 200, 190, -1));
        getContentPane().add(backGround, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 267));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jUnidadeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jUnidadeFocusGained
        jUnidade.setEnabled(true);
        usuario.setText("");
        usuario.setEnabled(false);
        senha.setText("");
        senha.setEnabled(false);
    }//GEN-LAST:event_jUnidadeFocusGained

    private void jUnidadeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jUnidadeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            // Fax login no banco de dados
            int pos = jUnidade.getSelectedIndex();
            Object[] selUnit = Globais.units.get(pos);
            String _host = selUnit[1].toString().substring(0,selUnit[1].toString().indexOf(":"));
            int _port = Integer.parseInt(selUnit[1].toString().substring(selUnit[1].toString().indexOf(":") + 1));
            String _dbname = selUnit[2].toString();
            
            Globais.sqlAlias = selUnit[0].toString();
            Globais.sqlHost = _host;
            Globais.sqlPort = _port;
            Globais.sqlDbName = _dbname;
            conn = new Db(this);
            if (conn == null) {
                jUnidade.requestFocus();
                return;
            } else {
                Globais.conn = conn;
            }
            
            jUnidade.setEnabled(true);
            usuario.setEnabled(true);
            senha.setEnabled(false);
            usuario.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }//GEN-LAST:event_jUnidadeKeyPressed

    private void usuarioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_usuarioFocusGained
        jUnidade.setEnabled(true);
        usuario.setText("");
        usuario.setEnabled(true);
        senha.setText("");
        senha.setEnabled(false);
    }//GEN-LAST:event_usuarioFocusGained

    private void usuarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_usuarioKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (usuario.getText().trim().equalsIgnoreCase("")) {
                usuario.requestFocus();
                return;
            }
            
            jUnidade.setEnabled(true);
            usuario.setEnabled(true);
            senha.setEnabled(true);
            senha.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            conn.CloseDb();
            Globais.conn = null;
            usuario.setText("");
            jUnidade.requestFocus();
        }
    }//GEN-LAST:event_usuarioKeyPressed

    private void senhaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_senhaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (senha.getText().trim().equalsIgnoreCase("")) {
                senha.requestFocus();
                return;
            }
                
            String _senha = null;
            try { 
                _senha = DataBasePassWordCrypto.encrypt(senha.getText().trim(), DataBasePassWordCrypto.ALGORITMO_AES,DataBasePassWordCrypto.ALGORITMO_AES);
            } catch (Exception ex) {}
            String selectSQL = "SELECT `id`, `nome`, `resenha`, `foto`, `menu` FROM `usuarios` WHERE `login` = :login AND `senha` = :senha LIMIT 1;";
            Object[][] param = {
              {"string", "login", usuario.getText().trim()},
              {"string", "senha", _senha}
            };
            ResultSet lgrs = conn.OpenTable(selectSQL, param);
            boolean isUser = false;
            try {
                while (lgrs.next()) {
                    isUser = true;
                    Globais.userName = lgrs.getString("nome");
                    Globais.userId = lgrs.getString("id");
                    
                    // Recuperar foto usuário
                    Blob blobFoto = null;
                    try { lgrs.getBlob("foto"); } catch (SQLException e) {}
                    InputStream inputStreamFoto = null; BufferedImage bufferedImageFoto = null;
                    if (blobFoto != null) {
                        inputStreamFoto = blobFoto.getBinaryStream();
                        try {bufferedImageFoto = ImageIO.read(inputStreamFoto);} catch (IOException iox) {}
                    }
                    Globais.userFoto = bufferedImageFoto;
                    
                    Globais.userMenu = lgrs.getString("menu");
                }
            } catch (SQLException e) {}
            conn.CloseTable(lgrs);            
            
            if (isUser) {
                dispose();
                MenuPrincipal pr = new MenuPrincipal();
                pr.setVisible(true);
                pr.pack();
            } else {
                tentativas += 1;
                if (tentativas > 3) {
                    JOptionPane.showMessageDialog(this, "Usuário e/ou Senha inválidos!!!\n\nVocê exedeu o limite máximo de Tentativas!!!\nO Administradosr do sistema recebera um aviso...", "Atenção!!!", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                } else JOptionPane.showMessageDialog(this, "Usuário e/ou Senha inválidos!!!\n\nTente Novamente...", "Atenção!!!", JOptionPane.INFORMATION_MESSAGE);
                usuario.requestFocus();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            senha.setText("");
            usuario.requestFocus();
        }
    }//GEN-LAST:event_senhaKeyPressed

    private void senhaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_senhaFocusGained
        senha.setText("");
    }//GEN-LAST:event_senhaFocusGained

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel backGround;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JComboBox<String> jUnidade;
    private javax.swing.JPasswordField senha;
    private javax.swing.JTextField usuario;
    // End of variables declaration//GEN-END:variables
}
