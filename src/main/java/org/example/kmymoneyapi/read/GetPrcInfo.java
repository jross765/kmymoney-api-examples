package org.example.kmymoneyapi.read;

import java.io.File;
import java.time.LocalDate;

import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.base.basetypes.complex.KMMPrcID;
import org.kmymoney.base.basetypes.simple.KMMSecID;

public class GetPrcInfo
{
    // -----------------------------------------------------------------

    // BEGIN Example data -- adapt to your needs
    private static String    kmmFileName = "example_in.kmy";
    private static KMMPrcID  prc1ID      = new KMMPrcID("USD", "EUR", "2023-11-01");
    private static KMMPrcID  prc2ID      = new KMMPrcID("E000001", "EUR", "2012-03-05");
    private static String    prc2IDStr   = "E000001:EUR:2012-03-05";
    private static KMMSecID  secID       = new KMMSecID("E000001");
    private static String    isin        = "DE0007164600";
    private static LocalDate date        = LocalDate.of(2012, 3, 5);
    // END Example data

	// -----------------------------------------------------------------

	public static void main(String[] args) {
		try {
			GetPrcInfo tool = new GetPrcInfo();
			tool.kernel();
		} catch (Exception exc) {
			System.err.println("Execution exception. Aborting.");
			exc.printStackTrace();
			System.exit(1);
		}
	}

	protected void kernel() throws Exception {
		KMyMoneyFileImpl kmmFile = new KMyMoneyFileImpl(new File(kmmFileName));

		KMyMoneyPrice prc = null;

	    // 1) By ID
		prc = kmmFile.getPriceByID(prc1ID);
		showPrcInfo(prc);
		
		prc = kmmFile.getPriceByID(prc2ID);
		showPrcInfo(prc);
		
	    prc2ID = KMMPrcID.parse(prc2IDStr); // Just another way of doing it
		prc = kmmFile.getPriceByID(prc2ID);
		showPrcInfo(prc);
		
		// 2) By Security-ID and date
		prc = kmmFile.getPriceBySecIDDate(secID, date);
		showPrcInfo(prc);
		
		// 3) By ISIN and date 
		//    (or whatever security identifier you fill the field code with)
		KMyMoneySecurity sec = kmmFile.getSecurityByCode(isin);
		prc = kmmFile.getPriceBySecIDDate(sec.getID(), date);
		showPrcInfo(prc);
	}

	private void showPrcInfo(KMyMoneyPrice prc) {
		System.out.println("--------------");
		
		try {
			System.out.println("Parent price pair: '" + prc.getParentPricePair() + "'");
		} catch (Exception exc) {
			System.out.println("Parent price pair:  " + "ERROR");
		}

		try {
			System.out.println("toString:          " + prc.toString());
		} catch (Exception exc) {
			System.out.println("toString:          " + "ERROR");
		}

		try {
			System.out.println("From sec/curr:     " + prc.getFromSecCurrQualifID());
		} catch (Exception exc) {
			System.out.println("From sec/curr:     " + "ERROR");
		}

		try {
			System.out.println("To curr:           " + prc.getToCurrencyQualifID());
		} catch (Exception exc) {
			System.out.println("To curr:           " + "ERROR");
		}

		try {
			System.out.println("Date:              " + prc.getDate());
		} catch (Exception exc) {
			System.out.println("Date:              " + "ERROR");
		}

		try {
			System.out.println("Value:             " + prc.getValue());
		} catch (Exception exc) {
			System.out.println("Value:             " + "ERROR");
		}

		try {
			System.out.println("Value (exact):     " + prc.getValueRat());
		} catch (Exception exc) {
			System.out.println("Value (exact):     " + "ERROR");
		}

		try {
			System.out.println("Value (fmt):       " + prc.getValueFormatted());
		} catch (Exception exc) {
			System.out.println("Value (fmt):       " + "ERROR");
		}

		try {
			System.out.println("Source:            " + prc.getSource());
		} catch (Exception exc) {
			System.out.println("Source:            " + "ERROR");
		}
	}
	
}
