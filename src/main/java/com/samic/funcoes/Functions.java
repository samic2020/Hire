package com.samic.funcoes;

/**
 *
 * @author YOGA 510
 */
public class Functions {

    static public String join(String[] s, String delimiter) {
        if (s.length <= 0) return "";
        int i = 0;
        String sRet = "";
        for (i=0;i<=s.length - 1;i++) {
            sRet += s[i] + delimiter;
        }
        return sRet.substring(0, sRet.length() - delimiter.length());
    }
    
}
