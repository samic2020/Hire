package com.samic.hire;

import com.samic.funcoes.DataBasePassWordCrypto;
import com.samic.funcoes.Db;
import java.util.Date;

/**
 *
 * @author Samic
 */
public class addUser {
    private Db conn = null;
    private String user;
    private String login;
    private String password;

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { 
        String _senha = null;
        try { 
            _senha = DataBasePassWordCrypto.encrypt(password, DataBasePassWordCrypto.ALGORITMO_AES,DataBasePassWordCrypto.ALGORITMO_AES) ;
        } catch (Exception ex) {}
        this.password = _senha; 
    }

    public addUser() {}
    
    public boolean insertUser() {
        conn = new Db(null);
        if (conn == null) return false;
        
        String insertSQL = "INSERT INTO `usuarios`(`nome`, `login`, `senha`, `dtcadastro`, `menu`) " +
                "VALUES (:nome, :login, :senha, :dtcadastro, :menu);";
        String _senha = "";
        Object[][] param = {
            {"string","nome",user.trim()},
            {"string","login",login.trim()},
            {"string","senha",password.trim()},
            {"date","dtcadastro",new Date()},
            {"String","menu","001;002:1111"}
        };
        boolean _retorno = conn.CommandExecute(insertSQL, param) > 0;
        try {conn.CloseDb();} catch (Exception ex) {}
        return _retorno;
    }           
}
