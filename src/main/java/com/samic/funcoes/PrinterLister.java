package com.samic.funcoes;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 *
 * @author YOGA 510
 */
public class PrinterLister {
    private PrintService[] services = null;	
	
	public PrintService[] getPrinters(){		
		services = PrintServiceLookup.lookupPrintServices(null, null);
		
		//debug code
		//for(PrintService ps : services){
		//	System.out.println(ps.getName());
		//}
		//end of debug code
		
		return services;		
	}
	
	public PrintService[] getInstalledPrinters(){
		return services;		
	}
	
	public static void main(String args[]){
		PrinterLister pLister = new PrinterLister();
		pLister.getPrinters();
	}
}
