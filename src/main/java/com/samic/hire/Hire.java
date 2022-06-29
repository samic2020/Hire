package com.samic.hire;

import com.formdev.flatlaf.FlatDarkLaf;
import com.samic.funcoes.DataBasePassWordCrypto;
import com.samic.funcoes.Db;
import com.samic.funcoes.Globais;
import java.awt.Color;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import static java.time.Instant.now;
import java.util.prefs.BackingStoreException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author YOGA 510
 */
public class Hire {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatDarkLaf() ); } catch (UnsupportedLookAndFeelException ex) { ex.printStackTrace(); }
        UIManager.put( "Button.arc", 999 );
        UIManager.put( "Component.arc", 999 );
        UIManager.put( "ProgressBar.arc", 999 );
        UIManager.put( "TextComponent.arc", 999 );
        UIManager.put( "Component.focusWidth", 1 );
        UIManager.put( "Component.focusColor", new Color(60,98,140));
        UIManager.put( "ScrollBar.thumbArc", 999 );
        UIManager.put( "ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ) );
        UIManager.put("PasswordField.showRevealButton", true);
        UIManager.put("TitlePane.menuBarEmbedded", Boolean.valueOf(true));
        
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("--MAKE")) {
                if (args[1].equalsIgnoreCase("--PWD")) {
                    String crypSenha = ""; String crypExpDate = "";
                    try {
                        crypSenha = DataBasePassWordCrypto.encrypt(args[2].toString(), DataBasePassWordCrypto.ALGORITMO_AES,DataBasePassWordCrypto.ALGORITMO_AES);
                        crypExpDate = DataBasePassWordCrypto.encrypt(args[3].toString(), DataBasePassWordCrypto.ALGORITMO_AES,DataBasePassWordCrypto.ALGORITMO_AES);
                    } catch (Exception e) {}
                    Globais.prefs.put("Key", crypSenha);
                    Globais.prefs.put("Licence", crypExpDate);
                } else if (args[1].equalsIgnoreCase("--REMOTE")) {
                    if (args[2].equalsIgnoreCase("ADD")) {
                        String _nn = args[3].toString();
                        String _alias = args[4].toString();
                        String _host = args[5].toString();
                        int _port = Integer.parseInt(args[6].toString());
                        String _database = args[7].toString();
                        
                        System.out.println(args[0].toString() + " " + args[1].toString() + " " + args[2].toString() + " " + _nn + " " +
                                _alias + "," + _host + ":" + _port + "," + _database);
                        Globais.prefs.put("Remote" + _nn, _alias + "," + _host + ":" + _port + "," + _database);
                    } else if (args[2].equalsIgnoreCase("DEL")) {
                        System.out.println(args[0].toString() + " " + args[1].toString() + " " + args[2].toString() + " " + args[3].toString());
                        Globais.prefs.remove("Remote" + args[3].toString());
                    } else {
                        System.out.println("--MAKE --REMOTE commands:");
                        System.out.println("=====================================================");
                        System.out.println("ADD - Add the Remote login parameter");
                        System.out.println("DEL - Delete the Remote login parameter");
                        System.out.println("-----------------------------------------------------");
                        System.out.println("");
                        System.out.println("Command sintaxe:");
                        System.out.println("");
                        System.out.println("--MAKE --REMOTE ADD NN ALIAS HOST PORT DATABASE");
                        System.out.println("--MAKE --REMOTE DEL NN");
                        System.out.println("");
                    }
                } else if (args[1].equalsIgnoreCase("--PARAM")) {
                    if (args[2].equalsIgnoreCase("ADD")) {
                        String _type = args[3].toString().trim().toUpperCase();
                        String _key = args[4].toString();
                        String _value = args[5].toString();
                        switch (_type) {
                            case "STRING":
                                Globais.prefs.put(_key, _value);
                                System.out.println("--MAKE --PARAM ADD " + _type + " " + _key + " " + _value + " as sucessfull aditionated.");
                                break;
                            case "INTEGER":
                                Globais.prefs.putInt(_key, Integer.parseInt(_value));
                                System.out.println("--MAKE --PARAM ADD " + _type + " " + _key + " " + _value + " as sucessfull aditionated.");
                                break;
                            case "BOOLEAN":
                                Globais.prefs.putBoolean(_key, Boolean.valueOf(_value));
                                System.out.println("--MAKE --PARAM ADD " + _type + " " + _key + " " + _value + " as sucessfull aditionated.");
                                break;
                            case "DATE":
                                Globais.prefs.put(_key, _value);
                                System.out.println("--MAKE --PARAM ADD " + _type + " " + _key + " " + _value + " as sucessfull aditionated.");
                                break;
                            default:
                                System.out.println("Tipo desconhecido...");
                        }
                    } else if(args[2].equalsIgnoreCase("DEL")) {
                        String _key = args[3].toString();
                        Globais.prefs.remove(_key);
                        System.out.println("--MAKE --PARAM DEL " + _key + " as sucessifull removed.");
                    } else {
                        System.out.println("--MAKE --PARAM commands:");
                        System.out.println("====================================");
                        System.out.println("ADD - Add parameters for imobilis");
                        System.out.println("DEL - Delete parameters for imobilis");
                        System.out.println("------------------------------------");
                        System.out.println("");
                        System.out.println("Command Sintaxe:");
                        System.out.println("");
                        System.out.println("--MAKE --PARAM [ADD|DEL] [<<TYPE>>] KEY VALUE");
                        System.out.println("");
                        System.out.println("<<TYPE>> - STRING; INTEGER; BOOLEAN; DATE");
                        System.out.println("");
                    }
                } else if (args[1].equalsIgnoreCase("--SUPER")) {
                    if (args.length == 5) {
                        addUser adduser = new addUser();
                        adduser.setUser(args[2].toString());
                        adduser.setLogin(args[3].toString());
                        adduser.setPassword(args[4].toString());
                        if (adduser.insertUser()) {
                            System.out.println("The " + adduser.getLogin() + " as sucessfull added.");
                        } else {
                            System.out.println("The " + adduser.getLogin() + " not sucessfull added.");
                        }
                    } else {
                        System.out.println("=====================================================");
                        System.out.println("--MAKE --SUPER");
                        System.out.println("=====================================================");
                        System.out.println("");
                        System.out.println("Command sintaxe:");
                        System.out.println("");
                        System.out.println("--MAKE --SUPER <user> <login> <password>");
                        System.out.println("");
                        System.out.println("<user> - The name of master user.");
                        System.out.println("<login> - The login of master user.");
                        System.out.println("<password> - The password of master user.");                        
                        System.out.println("");                        
                    }
                }
            } else if (args[0].equalsIgnoreCase("--PARAM")) {
                if (args[1].equalsIgnoreCase("--LIST")) {
                    if (args[2].equalsIgnoreCase("ALL")) {
                        System.out.println("--PARAM --LIST ALL");
                        System.out.println("==============================================");
                        try {
                            String[] keys = Globais.prefs.keys();
                            for (String key : keys) {
                                System.out.println("Key: " + key + " = " + Globais.prefs.get(key, ""));
                            }
                        } catch (BackingStoreException e) {}              
                        System.out.println("==============================================");
                    } else if (args[2].equalsIgnoreCase("SEARCH")) {
                        String _search = args[3].toString();
                        System.out.println("--PARAM --LIST SEARCH " + _search);
                        System.out.println("==============================================");
                        try {
                            String[] keys = Globais.prefs.keys();
                            boolean _found = false;
                            for (String key : keys) {
                                if (key.trim().equalsIgnoreCase(_search)) {
                                    System.out.println("Key: " + key + " = " + Globais.prefs.get(key, ""));
                                    _found = true;
                                }
                            }
                            if (!_found) System.out.println("Key: " + _search + " (Not found).");
                        } catch (BackingStoreException e) {}              
                        System.out.println("==============================================");
                    } else if (args[2].equalsIgnoreCase("TOFILE")) {
                        if (new File(System.getProperty("user.dir") + "/Imobilis.var").exists()) new File(System.getProperty("user.dir") + "/Imobilis.aut").delete();
                        try {
                            FileWriter SiciConfig = new FileWriter(System.getProperty("user.dir") + "/Imobilis.var");
                            PrintWriter SiciParam = new PrintWriter(SiciConfig);

                            SiciParam.println("# Begin read of Variables of Imobilis");
                            SiciParam.println("# Listing in " + now());
                            try {
                                String[] keys = Globais.prefs.keys();
                                for (String key : keys) {
                                    SiciParam.println(key + "=" + Globais.prefs.get(key, ""));
                                }
                            } catch (BackingStoreException e) {}              
                            SiciParam.println("# End of read of Variables of Imobilis");
                            
                            SiciConfig.close();
                        } catch (IOException iox) {iox.printStackTrace();}                        
                    } else if (args[2].equalsIgnoreCase("FROMFILE")) {
                        if (!new File(System.getProperty("user.dir") + "/Imobilis.var").exists()) {
                            System.out.println("Imobilis.var File not found...");
                        } else {
                            try {
                                FileReader SiciConfig = new FileReader(System.getProperty("user.dir") + "/Imobilis.var");
                                BufferedReader SiciParam = new BufferedReader(SiciConfig);
                                String linha;
                                while ((linha = SiciParam.readLine()) != null) {
                                    if (linha.startsWith("#")) continue;
                                    String _key = linha.substring(0,linha.indexOf("="));
                                    String _value = linha.substring(linha.indexOf("=") + 1);
                                    Globais.prefs.put(_key, _value);
                                }
                                SiciParam.close();
                            } catch (IOException iox) {iox.printStackTrace();}
                        }
                    } else if (args[2].equalsIgnoreCase("CLEAN")) {
                        try {
                            String[] keys = Globais.prefs.keys();
                            for (String key : keys) {
                                System.out.println("Removing " + key + ": sucess.");
                                Globais.prefs.remove(key);
                            }
                        } catch (BackingStoreException e) {}              
                        System.out.println("All variables of Imobilis deleted with sucess.");
                    } else {
                        System.out.println("--PARAM --LIST commands:");
                        System.out.println("==================================================");
                        System.out.println("ALL - List all variables of imobilis.");
                        System.out.println("SEARCH - Search the variable in imobilis.");
                        System.out.println("TOFILE - List all variables to imobilis.var");
                        System.out.println("FROMFILE - Read imobilis.var to params of imobilis");
                        System.out.println("CLEAN - Clear all params variables of imobilis");
                        System.out.println("--------------------------------------------------");
                        System.out.println("");
                        System.out.println("Command sintaxe:");
                        System.out.println("");
                        System.out.println("--PARAM --LIST ALL");
                        System.out.println("--PARAM --LIST SEARCH <value>");
                        System.out.println("--PARAM --LIST TOFILE");
                        System.out.println("--PARAM --LIST FROMFILE");
                        System.out.println("--PARAM --LIST CLEAN");
                        System.out.println("");
                    }
                }
            } else if (args[0].equalsIgnoreCase("--SUPER")) {
                
            } else if (args[0].equalsIgnoreCase("--HELP")) {
                System.out.println("=====================================================");
                System.out.println("--MAKE --REMOTE commands:");
                System.out.println("=====================================================");
                System.out.println("ADD - Add the Remote login parameter");
                System.out.println("DEL - Delete the Remote login parameter");
                System.out.println("-----------------------------------------------------");
                System.out.println("");
                System.out.println("Command sintaxe:");
                System.out.println("");
                System.out.println("--MAKE --REMOTE ADD NN ALIAS HOST PORT DATABASE");
                System.out.println("--MAKE --REMOTE DEL NN");
                System.out.println("");
                System.out.println("");

                System.out.println("=====================================================");
                System.out.println("--MAKE --PARAM commands:");
                System.out.println("=====================================================");
                System.out.println("ADD - Add parameters for imobilis");
                System.out.println("DEL - Delete parameters for imobilis");
                System.out.println("-----------------------------------------------------");
                System.out.println("");
                System.out.println("Command Sintaxe:");
                System.out.println("");
                System.out.println("--MAKE --PARAM [ADD|DEL] [<<TYPE>>] KEY VALUE");
                System.out.println("");
                System.out.println("<<TYPE>> - STRING; INTEGER; BOOLEAN; DATE");
                System.out.println("");
                System.out.println("");

                System.out.println("=====================================================");
                System.out.println("--PARAM --LIST commands:");
                System.out.println("=====================================================");
                System.out.println("ALL - List all variables of imobilis.");
                System.out.println("SEARCH - Search the variable in imobilis.");
                System.out.println("TOFILE - List all variables to imobilis.var");
                System.out.println("FROMFILE - Read imobilis.var to params of imobilis");
                System.out.println("CLEAN - Clear all params variables of imobilis");
                System.out.println("-----------------------------------------------------");
                System.out.println("");
                System.out.println("Command sintaxe:");
                System.out.println("");
                System.out.println("--PARAM --LIST ALL");
                System.out.println("--PARAM --LIST SEARCH <value>");
                System.out.println("--PARAM --LIST TOFILE");
                System.out.println("--PARAM --LIST FROMFILE");
                System.out.println("--PARAM --LIST CLEAN");
                System.out.println("");                
                
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("SECRET")) {
                        System.out.println("");

                        System.out.println("=====================================================");
                        System.out.println("--MAKE --PWD");
                        System.out.println("=====================================================");
                        System.out.println("");
                        System.out.println("Command sintaxe:");
                        System.out.println("");
                        System.out.println("--MAKE --PWD <databasepassword> <expirationdate>");
                        System.out.println("");
                        System.out.println("<databasepassword> - The database password to acess.");
                        System.out.println("<expirationdate> - Date to program expirate Licence");                        
                        System.out.println("                   format: yyyy-mm-dd");                        
                        System.out.println("");                        

                        System.out.println("=====================================================");
                        System.out.println("--MAKE --SUPER");
                        System.out.println("=====================================================");
                        System.out.println("");
                        System.out.println("Command sintaxe:");
                        System.out.println("");
                        System.out.println("--MAKE --SUPER <user> <login> <password>");
                        System.out.println("");
                        System.out.println("<user> - The name of master user.");
                        System.out.println("<login> - The login of master user.");
                        System.out.println("<password> - The password of master user.");                        
                        System.out.println("");                        

                        System.out.println("=====================================================");
                        System.out.println("--SUPER --SHOW commands");
                        System.out.println("=====================================================");
                        System.out.println("ALL - Show all users of system. (Except master user.)");
                        System.out.println("MASTER - Show master user of system.");
                        System.out.println("<user> - Show the user of system.");
                        System.out.println("");
                        System.out.println("Command sintaxe:");
                        System.out.println("");
                        System.out.println("--SUPER --SHOW ALL|MASTER|<user>");
                        System.out.println("");
                        System.out.println("--SUPER --SHOW ALL");
                        System.out.println("--SUPER --SHOW MASTER");
                        System.out.println("--SUPER --SHOW <user>");                        
                        System.out.println("");                        
                    }
                }
            }
            System.exit(0);
        }
        
        ReadSettings();

        login lg = new login();
        lg.setVisible(true);
        lg.pack();
    }
    
    private static void ReadSettings() {
        Globais.ReadSiciParameters();
        Globais.units.clear();
        String[] _hostl = Globais.remote.split(",");
        if (_hostl.length > 1) Globais.units.add(new String[] {_hostl[0], _hostl[1], _hostl[2]});
        for (int w=1;w<=99;w++) {
            String remotn = Globais.prefs.get("Remote" + new DecimalFormat("00").format(w),"");
            
            String[] _host1 = null; String alias = "", host = "", dbname = "";
            if (!"".equals(remotn)) {
                _host1 = remotn.split(",");
                if (_host1.length > 1) {
                    alias = _host1[0];
                    host  = _host1[1];
                    dbname  = _host1[2];
                }
            }
            if (!"".equals(host)) Globais.units.add(new String[] {alias, host, dbname});
        }
        
        String crySenha  = Globais.prefs.get("Key", "");
        if (crySenha != "") {
            Globais.sqlPwd = DataBasePassWordCrypto.decrypt(crySenha, DataBasePassWordCrypto.ALGORITMO_AES, DataBasePassWordCrypto.ALGORITMO_AES);
        } else Globais.sqlPwd = crySenha;        
    }    
}
