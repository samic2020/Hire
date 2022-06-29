package com.samic.hire;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatTextField;
import com.formdev.flatlaf.icons.FlatSearchWithHistoryIcon;
import com.samic.funcoes.BackGroundDeskTopPane;
import com.samic.funcoes.CentralizaTela;
import com.samic.funcoes.Db;
import com.samic.funcoes.Globais;
import com.samic.funcoes.PrinterLister;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.print.PrintService;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 *
 * @author YOGA 510
 */
public class MenuPrincipal extends javax.swing.JFrame {
    private JMenuBar menuBar;
    
    private int searchBarWidth = 300;
    private int searchBarHeigth = 30;
    private Color searchBarFocusColor = new Color(60,98,140);
    private JScrollPane searcBox = new JScrollPane();
    private boolean searchBoxCreated = false;
    private JPopupMenu popupMenu = new JPopupMenu();
    private ButtonGroup bg = new ButtonGroup();
    private FlatTextField searchBar = new FlatTextField();

    private Db conn = Globais.conn;
    
    /**
     * Creates new form MenuPrincipal
     */
    public MenuPrincipal() {
        initComponents();

        setTitle(".:: Hire ::.");
        setIconImage(new ImageIcon(getClass().getResource("/figuras/logo.png")).getImage());
        
        // Setar Menu
        super.setJMenuBar(MontarMenu());

        // Inicializa componentes próprios
        InitSettings();
        
        userName.setText(Globais.userName);
        hostName.setText(Globais.sqlAlias);
    }

    private JMenuBar MontarMenu() {
        menuBar = new JMenuBar();  
        
        // Cria menus do Sistema
        SetarMenu();
        
        //Cria o menu Sair  
        JMenu menuSair = new JMenu("Sair");  
        
        // Opção LogOut do menu Sair
        JMenuItem itemSair = new javax.swing.JMenuItem();
        itemSair.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        FlatSVGIcon logoutIcon = new FlatSVGIcon("icons/logout.svg",16,16);
        logoutIcon.setColorFilter( new FlatSVGIcon.ColorFilter( color -> Color.RED ) );        
        itemSair.setIcon((Icon)logoutIcon); 
        itemSair.setText("Logout");
        itemSair.setToolTipText("Logout do sistema...");
        itemSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();

                Globais.conn = null;
                try {
                    login lg = new login();
                    lg.setVisible(true);
                    lg.pack();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        menuSair.add(itemSair);

        // Opção Encerrar o Sistema do menu Sair
        JMenuItem itemFechar = new javax.swing.JMenuItem();
        itemFechar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        FlatSVGIcon exitIcon = new FlatSVGIcon("icons/system-exit.svg",16,16);
        exitIcon.setColorFilter( new FlatSVGIcon.ColorFilter( color -> Color.BLACK ) );        
        itemFechar.setIcon((Icon)exitIcon);
        itemFechar.setText("Encerrar o Sistema");
        itemFechar.setToolTipText("Sai completamente do programa...");
        itemFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.exit(0);
            }
        });
        menuSair.add(itemFechar);
        
        //Adiciona o menu na barra  
        menuBar.add(menuSair);
        
        return menuBar;
    }
    
    // Monta o Menu do usuário
    private void SetarMenu() {    
        String menu = Globais.userMenu;
        String[] amenu = menu.split(";");

        String nivelMenu = "select LPAD(id,3,0) id, nivel, nome, atalho from menu where snivel = 0 order by nivel;";
        ResultSet nivel = conn.OpenTable(nivelMenu, null);
        try {
            while (nivel.next()) {
                String tid = nivel.getString("id");
                String tnome = nivel.getString("nome");
                String tatalho = nivel.getString("atalho");
                
                // Cria menu nivel superior
                JMenu menuItem = new JMenu(tnome);  
                menuItem.setVisible(menuVisible(amenu,tid));
                
                String snivelMenu = "select LPAD(id,3,0) id, nivel, snivel, icone, nome, atalho, chamada from menu where nivel = :nivel and snivel > 0 order by snivel;";
                Object[][] param = {
                    {"int", "nivel", nivel.getInt("nivel")},
                };                
                ResultSet snivel = conn.OpenTable(snivelMenu, param);
                try {
                    while (snivel.next()) {
                        String id = snivel.getString("id");
                        String nome = snivel.getString("nome");
                        String atalho = snivel.getString("atalho");
                        final String rotina = snivel.getString("chamada");
                        String icone = snivel.getString("icone");                        
                        
                        JMenuItem item = new javax.swing.JMenuItem();
                        item.setAccelerator(KeyStroke.getKeyStroke("control alt " + atalho));
                        // Setar Icones
                        if (icone != null) {
                            FlatSVGIcon ico = new FlatSVGIcon(icone,16,16);
                            try{
                                item.setIcon((Icon)ico); 
                            } catch (Exception e) {}
                        }
                        item.setText(nome);
                        item.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                try {  
                                    Class classe = Class.forName(rotina);  
                                    JInternalFrame frame = (JInternalFrame) classe.newInstance();  
                                    WorkArea.add(frame);
                                    WorkArea.getDesktopManager().activateFrame(frame);
                                    
                                    frame.requestFocus();
                                    frame.setSelected(true);
                                    frame.setVisible(true);  
                                    frame.pack();
                                    CentralizaTela.setCentro(frame, WorkArea, 0, 0);
                                } catch (Exception ex) { System.out.println(rotina + " : Erro no cadastro."); }  
                            }
                        });

                        item.setVisible(menuVisible(amenu,id));
                        menuItem.add(item);                        
                    }
                } catch (SQLException ex) {}
                
                // Adiciona na barra de menu
                menuBar.add(menuItem);
                
                conn.CloseTable(snivel);
            }
        } catch (SQLException e) {}
        conn.CloseTable(nivel);
    }
    
    private boolean menuVisible(String[] amenu, String idmenu) {
        boolean _visible = false;
        for (String item : amenu) {
            if (item.subSequence(0, 3).equals(idmenu)) {
                _visible = true;
                break;
            }
        }    
        return _visible;
    }
    
    private void InitSettings() {
        // Search Menu Options
        {
            JCheckBoxMenuItem propCheckBoxMenuItem = new JCheckBoxMenuItem();
            propCheckBoxMenuItem.setText("Proprietários");
            propCheckBoxMenuItem.setMnemonic('P');
            propCheckBoxMenuItem.setSelected(true);
            bg.add(propCheckBoxMenuItem);

            JCheckBoxMenuItem imovCheckBoxMenuItem = new JCheckBoxMenuItem();
            imovCheckBoxMenuItem.setText("Imóveis");
            imovCheckBoxMenuItem.setMnemonic('I');
            imovCheckBoxMenuItem.setSelected(false);
            bg.add(imovCheckBoxMenuItem);

            JCheckBoxMenuItem locaCheckBoxMenuItem = new JCheckBoxMenuItem();
            locaCheckBoxMenuItem.setText("Locatários");
            locaCheckBoxMenuItem.setMnemonic('L');
            locaCheckBoxMenuItem.setSelected(false);
            bg.add(locaCheckBoxMenuItem);

            JCheckBoxMenuItem fiadCheckBoxMenuItem = new JCheckBoxMenuItem();
            fiadCheckBoxMenuItem.setText("Fiadores");
            fiadCheckBoxMenuItem.setMnemonic('F');
            fiadCheckBoxMenuItem.setSelected(false);
            bg.add(fiadCheckBoxMenuItem);

            JCheckBoxMenuItem moviCheckBoxMenuItem = new JCheckBoxMenuItem();
            moviCheckBoxMenuItem.setText("Movimentos");
            moviCheckBoxMenuItem.setMnemonic('I');
            moviCheckBoxMenuItem.setSelected(false);
            bg.add(moviCheckBoxMenuItem);

            popupMenu.add(propCheckBoxMenuItem);
            popupMenu.add(imovCheckBoxMenuItem);
            popupMenu.add(locaCheckBoxMenuItem);
            popupMenu.add(fiadCheckBoxMenuItem);
            popupMenu.addSeparator();
            popupMenu.add(moviCheckBoxMenuItem);
        }        
                
        FlatSVGIcon themeIcon = new FlatSVGIcon("icons/theme.svg",16,16);
        themeIcon.setColorFilter( new FlatSVGIcon.ColorFilter( color -> Color.WHITE ) );
        JToggleButton themeSet = new JToggleButton((Icon)themeIcon);
        themeSet.setToolTipText("Tema Claro ou Escuro.");
        themeSet.addActionListener((e) -> {
            EventQueue.invokeLater(() -> {
                try {
                    String themeFlat = null;
                    if (themeSet.isSelected()) {
                        themeIcon.setColorFilter( new FlatSVGIcon.ColorFilter( color -> Color.BLACK ) );
                        themeFlat = FlatLightLaf.class.getName();
                    } else {
                        themeFlat = FlatDarkLaf.class.getName();
                        themeIcon.setColorFilter( new FlatSVGIcon.ColorFilter( color -> Color.WHITE ) );
                    }
                    UIManager.setLookAndFeel(themeFlat);
                    FlatLaf.updateUI();
                } catch (Exception ex) {}
            });

        });
        
        FlatSVGIcon laserIconPrinter = new FlatSVGIcon("icons/laser.svg",16,16);
        laserIconPrinter.setColorFilter( new FlatSVGIcon.ColorFilter( color -> Color.red ) );
        JToggleButton laserPrinter = new JToggleButton((Icon)laserIconPrinter);                        
        laserPrinter.setToolTipText("Impressora Laser esta Desativada.");
        laserPrinter.addActionListener((e) -> {
            if (laserPrinter.isSelected()) {
                laserIconPrinter.setColorFilter( new FlatSVGIcon.ColorFilter( color -> new Color(50,205,50) ) );
                laserPrinter.setToolTipText("Impressora Laser esta Ativa."); 
            } else {
                laserIconPrinter.setColorFilter( new FlatSVGIcon.ColorFilter( color -> Color.red ) );
                laserPrinter.setToolTipText("Impressora Laser esta Desativada.");
            }
        });
        
        FlatSVGIcon thermalIconPrinter = new FlatSVGIcon("icons/thermal.svg",16,16);
        thermalIconPrinter.setColorFilter( new FlatSVGIcon.ColorFilter( color -> Color.red ) );
        JToggleButton thermalPrinter = new JToggleButton((Icon)thermalIconPrinter);
        thermalPrinter.setToolTipText("Impressora Thermica esta Desativada.");
        thermalPrinter.addActionListener((e) -> {
            if (thermalPrinter.isSelected()) {
                thermalIconPrinter.setColorFilter( new FlatSVGIcon.ColorFilter( color -> new Color(50,205,50) ) );
                thermalPrinter.setToolTipText("Impressora Thermica esta Ativa.");
            } else {
                thermalIconPrinter.setColorFilter( new FlatSVGIcon.ColorFilter( color -> Color.red ) );
                thermalPrinter.setToolTipText("Impressora Thermica esta Desativada.");
            }
        });
        thermalPrinter.addMouseListener(new MouseAdapter() { 
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getModifiers() == MouseEvent.BUTTON3_MASK && e.getClickCount() == 1) {
                JPopupMenu popupMenu = new JPopupMenu();
                
                PrinterLister pLister = new PrinterLister();
                for(PrintService ps : pLister.getPrinters()){
                    popupMenu.add(ps.getName());
		}
                popupMenu.show(thermalPrinter, 0, thermalPrinter.getHeight());
                }
            }
        });
                
        JToolBar printerToolbar = new JToolBar();
        printerToolbar.add(themeSet);
        printerToolbar.addSeparator();
        printerToolbar.add(laserPrinter);
        printerToolbar.add(thermalPrinter);
        printerToolbar.addSeparator();
        
        FlatButton emailButton = new FlatButton();
        emailButton.setIcon((Icon)new FlatSVGIcon("icons/carta.svg",16,16));
        emailButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        emailButton.setFocusable(false);
        emailButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Hello User! How are you?", "User", 1));

        // seachBar Initializacion
        searchBar.setPreferredSize(new Dimension(searchBarWidth, searchBarHeigth));
        searchBar.setMaximumSize(new Dimension(searchBarWidth, searchBarHeigth));
        searchBar.setMinimumSize(new Dimension(searchBarWidth, searchBarHeigth));
        searchBar.setSize(searchBarWidth, searchBarHeigth);
        searchBar.setFocusable(true);
        searchBar.putClientProperty("Component.focusWidth", 0);
        searchBar.putClientProperty("Component.focusColor", searchBarFocusColor);
        
        JButton searchHistoryButton = new JButton((Icon)new FlatSearchWithHistoryIcon(true));
        searchHistoryButton.setToolTipText("Search History");
        searchHistoryButton.addActionListener(e -> {
          popupMenu.show(searchHistoryButton, 0, searchHistoryButton.getHeight());
        });
        searchBar.putClientProperty("JTextField.leadingComponent", searchHistoryButton);
        
        JToggleButton matchCaseButton = new JToggleButton((Icon)new FlatSVGIcon("icons/matchCase.svg"));
        matchCaseButton.setRolloverIcon((Icon)new FlatSVGIcon("icons/matchCaseHovered.svg"));
        matchCaseButton.setSelectedIcon((Icon)new FlatSVGIcon("icons/matchCaseSelected.svg"));
        matchCaseButton.setToolTipText("Caso Sensitivo");
        JToggleButton wordsButton = new JToggleButton((Icon)new FlatSVGIcon("icons/words.svg"));
        wordsButton.setRolloverIcon((Icon)new FlatSVGIcon("icons/wordsHovered.svg"));
        wordsButton.setSelectedIcon((Icon)new FlatSVGIcon("icons/wordsSelected.svg"));
        wordsButton.setToolTipText("Frase Inteira");
    
        searchBar.setPlaceholderText("Busca Globalizada");
        
        JToolBar searchToolbar = new JToolBar();
        searchToolbar.addSeparator();
        searchToolbar.add(matchCaseButton);
        searchToolbar.add(wordsButton);

        searchBar.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (searchBar.getText().trim().length() >= 3) {                    
                    searchBox();
                    repaint();
                }
                searcBox.setVisible(searchBar.getText().trim().length() >= 3);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                
            }
        });
        
        searchBar.putClientProperty("JTextField.trailingComponent", searchToolbar);
        searchBar.putClientProperty("JTextField.showClearButton", Boolean.valueOf(true));
        searchBar.putClientProperty("JTextField.showClearButton", Boolean.valueOf(true));
    
        menuBar.add(Box.createGlue());
        menuBar.add((Component)printerToolbar);
        menuBar.add((Component)searchBar);
        menuBar.add((Component)emailButton);
    }
    
    private void searchBox() {
        if (!searchBoxCreated) {
            final int searchBoxWidth = 600;
            final int searchBoxHeight = 300;
            //JScrollPane searcBox = new JScrollPane();
            Dimension searchBoxDimension = new Dimension(searchBoxWidth, searchBoxHeight);
            searcBox.setSize(searchBoxDimension);
            searcBox.setPreferredSize(searchBoxDimension);
            searcBox.setMaximumSize(searchBoxDimension);
            searcBox.setMinimumSize(searchBoxDimension);

            int posEnd = searchBar.getX();
            int posStart = posEnd + (32) + searchBarWidth  - searchBoxWidth;
            searcBox.setLocation(posStart , -2);

            JTable jtbl = new JTable();
            jtbl.setSize(searchBoxDimension);
            jtbl.setPreferredSize(searchBoxDimension);
            jtbl.setMaximumSize(searchBoxDimension);
            jtbl.setMinimumSize(searchBoxDimension);
            jtbl.setVisible(true);

            searcBox.add(jtbl);

            add(searcBox,0);
            searcBox.setVisible(false);
            searchBoxCreated = true;
        }            
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        WorkArea = new BackGroundDeskTopPane(Globais.backGround);
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        userName = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        hostName = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Hire");
        setMinimumSize(new java.awt.Dimension(800, 600));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Usuário Logado:");

        jLabel3.setText("Local na Rede:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userName, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hostName, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(userName)
                    .addComponent(jLabel3)
                    .addComponent(hostName))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(WorkArea)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(WorkArea, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane WorkArea;
    private javax.swing.JLabel hostName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel userName;
    // End of variables declaration//GEN-END:variables

}
