package com.samic.genericos;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Samic
 */
public class cpoTelefones {
    private String _telephone = null;
    private String _regex = "^\\((?:[14689][1-9]|2[12478]|3[1234578]|5[1345]|7[134579])\\) (?:[2-8]|9[1-9])[0-9]{3}\\-[0-9]{4}$";
    
    public cpoTelefones(JInternalFrame me) {
        FlatSVGIcon phoneIcon = new FlatSVGIcon("icons/phone.svg",32,32);
        phoneIcon.setColorFilter( new FlatSVGIcon.ColorFilter( color -> Color.GREEN ) );  
        final MaskFormatter mask = new MaskFormatter();
        try {mask.setMask("(##)*####-####");} catch (ParseException ex) {}
        
        JPanel painel = new JPanel();
        JLabel label1 = new JLabel(phoneIcon);
        JLabel label2 = new JLabel();        
        JFormattedTextField formattedTextField1 = new JFormattedTextField(mask);        
        JLabel label3 = new JLabel();
        JComboBox<String> comboBox1 = new JComboBox();
        comboBox1.addItem("Residencial"); comboBox1.addItem("Comercial"); comboBox1.addItem("Móvel");

        formattedTextField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (formattedTextField1.getText().equalsIgnoreCase("(  )     -    ")) {formattedTextField1.requestFocus(); return;}
                if (!formattedTextField1.getText().matches(_regex)) {formattedTextField1.requestFocus(); return;}
                
                if (e.getKeyCode() == KeyEvent.VK_ENTER) SendTab();
            }
        });        
        
        comboBox1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) SendTab();
            }
        });        
        
        //======== this ========
        painel.setLayout(new GridBagLayout());
        ((GridBagLayout)painel.getLayout()).columnWidths = new int[] {72, 62, 188, 0};
        ((GridBagLayout)painel.getLayout()).rowHeights = new int[] {22, 26, 8, 0, 0};
        ((GridBagLayout)painel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
        ((GridBagLayout)painel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};

        //---- label1 ----
        painel.add(label1, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

        //---- label2 ----
        label2.setText("Tel.:");
        painel.add(label2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        painel.add(formattedTextField1, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

        //---- label3 ----
        label3.setText("Tipo:");
        painel.add(label3, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        painel.add(comboBox1, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

        Object[] botoes = new Object[] {"Confirmar", "Cancelar"};
        Object[] focus = new Object[] {formattedTextField1};
        int opc = JOptionPane.showOptionDialog(me, painel, "Telefone",  JOptionPane.YES_NO_OPTION,-1,null, botoes, focus[0]);
        if (opc == JOptionPane.NO_OPTION || opc == JOptionPane.CANCEL_OPTION) {
            _telephone = null;
            return;
        }
        _telephone = formattedTextField1.getText() + "," + comboBox1.getSelectedItem().toString();
    }

    public String getTelephone() {
        return _telephone;
    }

    private void SendTab() {
        try {            
            Robot robot = new Robot();
            //robot.delay(5000);
            robot.keyPress(KeyEvent.VK_TAB);           
        } catch (AWTException e) {}        
    }
}
