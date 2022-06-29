package com.samic.funcoes;

public class Dates {

    public static java.sql.Date toSqlDate(java.util.Date value) {
       java.sql.Date tmpDate = null;
        try {tmpDate = new java.sql.Date(value.getTime());} catch (Exception e) {tmpDate = null;}
        return tmpDate;
    }
    
}
