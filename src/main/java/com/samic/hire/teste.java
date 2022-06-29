package com.samic.hire;

import com.samic.funcoes.Db;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;

/**
 *
 * @author YOGA 510
 */
public class teste {
    Db conn = null;
    
    public teste() {
        conn = new Db(new JFrame());
        //System.out.println("INSERT: " + conn.CommandExecute("INSERT INTO teste (nome) VALUES (:nome);", new Object[][] {{"string","nome","Miguel Ferreira e Silva"}}));
        
        //ResultSet rs = conn.OpenTable("SELECT id, nome FROM teste WHERE nome LIKE :nome;", new Object[][] {{"string", "nome","%gton%"}});
        ResultSet rs = conn.OpenTable("SELECT id, nome FROM teste", null);
        if (conn == null) System.out.println("Fechada.");
        try {
            while (rs.next()) {
                System.out.println("Nome: " + rs.getString("nome"));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        conn.CloseTable(rs);
        
        Object[][] dados = null;
        try {
            dados = conn.ReadFieldsTable(new String[] {"id", "nome"}, "teste", "nome LIKE 'Wellington%'");
        } catch (SQLException ex) { ex.printStackTrace(); }
        if (dados != null) {
            System.out.println("Read Field Nome: " + (int)dados[0][3] + " : " + (String)dados[1][3]);
        }
        conn.CloseDb();
    }
}
