package org.example.kmymoneyapiext.write.gen;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.apiext.secacct.SecuritiesAccountTransactionManager_FP;
import org.kmymoney.base.basetypes.simple.KMMAcctID;
import org.kmymoney.base.tuples.AcctIDAmountFPPair;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenDepotTrx_FP {
	// CAUTION: The following account IDs are all of type
	// KMMAcctID. Why not KMMComplAcctID? Yes, that would work
	// as well, but we never book to the special top-level
	// accounts. Thus, this is a precautionary measure.

	// BEGIN Example data -- adapt to your needs
    private static String kmmInFileName  = "example_in.kmy";
    private static String kmmOutFileName = "example_out.kmy";

	private static SecuritiesAccountTransactionManager_FP.Type type = SecuritiesAccountTransactionManager_FP.Type.DIVIDEND;

	private static KMMAcctID stockAcctID  = new KMMAcctID( "A000063" );
	private static KMMAcctID incomeAcctID = new KMMAcctID( "A000070" ); // only for dividend, not for buy/sell
	private static List<AcctIDAmountFPPair> expensesAcctAmtList = new ArrayList<AcctIDAmountFPPair>(); // only for dividend, not for buy/sell
	private static KMMAcctID offsetAcctID = new KMMAcctID( "A000004" );
	
	private static FixedPointNumber nofStocks      = new FixedPointNumber(15); // only for buy/sell, not for dividend
	private static FixedPointNumber stockPrc       = new FixedPointNumber("23080/100"); // only for buy/sell, not for dividend
	private static FixedPointNumber divDistrGross  = new FixedPointNumber("11200/100"); // only for dividend, not for buy/sell

	private static LocalDate datPst = LocalDate.of(2024, 3, 1);
	private static String descr = "Dividend payment";
	// END Example data

    // -----------------------------------------------------------------

	public static void main(String[] args) {
		try {
			GenDepotTrx_FP tool = new GenDepotTrx_FP();
			tool.kernel();
		} catch (Exception exc) {
			System.err.println("Execution exception. Aborting.");
			exc.printStackTrace();
			System.exit(1);
		}
	}

	protected void kernel() throws Exception {
		KMyMoneyWritableFileImpl kmmFile = new KMyMoneyWritableFileImpl(new File(kmmInFileName));

		KMyMoneyAccount stockAcct = kmmFile.getAccountByID(stockAcctID);
		if ( stockAcct == null )
			System.err.println("Error: Cannot get account with ID '" + stockAcctID + "'");

		KMyMoneyAccount incomeAcct = null;
		if ( incomeAcctID != null ) {
			incomeAcct = kmmFile.getAccountByID(incomeAcctID);
			if ( incomeAcct == null )
				System.err.println("Error: Cannot get account with ID '" + incomeAcctID + "'");
		}

		for ( AcctIDAmountFPPair elt : expensesAcctAmtList ) {
			KMyMoneyAccount expensesAcct = kmmFile.getAccountByID(elt.accountID());
			if ( expensesAcct == null )
				System.err.println("Error: Cannot get account with ID '" + elt.accountID() + "'");
		}

		KMyMoneyAccount offsetAcct = kmmFile.getAccountByID(offsetAcctID);
		if ( offsetAcct == null )
			System.err.println("Error: Cannot get account with ID '" + offsetAcctID + "'");

		System.err.println("Account 1 name (stock):      '" + stockAcct.getQualifiedName() + "'");
		if ( incomeAcctID != null )
			System.err.println("Account 2 name (income):     '" + incomeAcct.getQualifiedName() + "'");

		int counter = 1;
		for ( AcctIDAmountFPPair elt : expensesAcctAmtList ) {
			KMyMoneyAccount expensesAcct = kmmFile.getAccountByID(elt.accountID());
			System.err.println("Account 3." + counter + " name (expenses): '" + expensesAcct.getQualifiedName() + "'");
			counter++;
		}

		System.err.println("Account 4 name (offsetting): '" + offsetAcct.getQualifiedName() + "'");

		// ---

		KMyMoneyWritableTransaction trx = null;
		initExpAccts();
		if ( type == SecuritiesAccountTransactionManager_FP.Type.BUY_STOCK ) {
			trx = SecuritiesAccountTransactionManager_FP
					.genBuyStockTrx(kmmFile, 
									stockAcctID, expensesAcctAmtList, offsetAcctID,
									nofStocks, stockPrc, 
									datPst, descr);
		} else if ( type == SecuritiesAccountTransactionManager_FP.Type.DIVIDEND ) {
			trx = SecuritiesAccountTransactionManager_FP
					.genDividDistribTrx(kmmFile,
									stockAcctID, incomeAcctID, expensesAcctAmtList, offsetAcctID, 
									KMyMoneyTransactionSplit.Action.DIVIDEND, divDistrGross, datPst, 
									descr);
		} else if ( type == SecuritiesAccountTransactionManager_FP.Type.DISTRIBUTION ) {
			trx = SecuritiesAccountTransactionManager_FP
					.genDividDistribTrx(kmmFile,
									stockAcctID, incomeAcctID, expensesAcctAmtList, offsetAcctID, 
									KMyMoneyTransactionSplit.Action.YIELD, divDistrGross, datPst, // This specific split-action does not really make any difference in KMyMoney --
									descr);                                                       // it will essentially be ignored / changed to DIVIDEND by KMyMoney
		}

		// ---

		System.out.println("Transaction to write: " + trx.toString());
		kmmFile.writeFile(new File(kmmOutFileName));

		System.out.println("OK");
	}
	
	// Example for taxes on a dividend payment in Germany (domestic share).
	// If we had a foreign share (e.g. US), we would have to add a 
	// third entry to the list: "Auslaend. Quellensteuer" (that 
	// account is not in the test file yet).
	private void initExpAccts() {
		KMMAcctID expAcct1 = new KMMAcctID( "A000067" ); // Kapitalertragsteuer
		FixedPointNumber amt1 = divDistrGross.copy().multiply(new FixedPointNumber("25/100"));
		AcctIDAmountFPPair acctAmtPr1 = new AcctIDAmountFPPair(expAcct1, amt1);
		expensesAcctAmtList.add(acctAmtPr1);
		
		KMMAcctID expAcct2 = new KMMAcctID( "A000027" ); // Soli
		FixedPointNumber amt2 = amt1.copy().multiply(new FixedPointNumber("55/1000"));
		AcctIDAmountFPPair acctAmtPr2 = new AcctIDAmountFPPair(expAcct2, amt2);
		expensesAcctAmtList.add(acctAmtPr2);
	}
}
