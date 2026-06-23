package org.example.kmymoneyapiext.write.gen;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.apiext.secacct.SecuritiesAccountTransactionManager_BF;
import org.kmymoney.base.basetypes.simple.KMMAcctID;
import org.kmymoney.base.tuples.AcctIDAmountBFPair;

public class GenDepotTrx {
	// BEGIN Example data -- adapt to your needs
	private static String kmmInFileName  = "example_in.kmy";
	private static String kmmOutFileName = "example_out.kmy";

	private static SecuritiesAccountTransactionManager_BF.Type type = SecuritiesAccountTransactionManager_BF.Type.DIVIDEND;

	// CAUTION: The following account IDs are all of type
	// KMMAcctID. Why not KMMComplAcctID? Yes, that would work
	// as well, but we never book to the special top-level
	// accounts. Thus, this is a precautionary measure.
	private static KMMAcctID stockAcctID  = new KMMAcctID( "A000063" );
	private static KMMAcctID incomeAcctID = new KMMAcctID( "A000070" ); // only for dividend, not for buy/sell
	private static List<AcctIDAmountBFPair> expensesAcctAmtList = new ArrayList<AcctIDAmountBFPair>(); // only for dividend, not for buy/sell
	private static KMMAcctID offsetAcctID = new KMMAcctID( "A000004" );
	
	private static BigFraction nofStocks      = BigFraction.of(15); // only for buy/sell, not for dividend
	private static BigFraction stockPrc       = BigFraction.of(23080, 100); // only for buy/sell, not for dividend
	private static BigFraction divDistrGross  = BigFraction.of(11200, 100); // only for dividend, not for buy/sell

	private static LocalDate datPst = LocalDate.of(2024, 3, 1);
	private static String descr = "Dividend payment";
	// END Example data

    // -----------------------------------------------------------------

	public static void main(String[] args) {
		try {
			GenDepotTrx tool = new GenDepotTrx();
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

		for ( AcctIDAmountBFPair elt : expensesAcctAmtList ) {
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
		for ( AcctIDAmountBFPair elt : expensesAcctAmtList ) {
			KMyMoneyAccount expensesAcct = kmmFile.getAccountByID(elt.accountID());
			System.err.println("Account 3." + counter + " name (expenses): '" + expensesAcct.getQualifiedName() + "'");
			counter++;
		}

		System.err.println("Account 4 name (offsetting): '" + offsetAcct.getQualifiedName() + "'");

		// ---

		KMyMoneyWritableTransaction trx = null;
		initExpAccts();
		if ( type == SecuritiesAccountTransactionManager_BF.Type.BUY_STOCK ) {
			trx = SecuritiesAccountTransactionManager_BF
					.genBuyStockTrx(kmmFile, 
									stockAcctID, expensesAcctAmtList, offsetAcctID,
									nofStocks, stockPrc, 
									datPst, descr);
		} else if ( type == SecuritiesAccountTransactionManager_BF.Type.SELL_STOCK ) {
			trx = SecuritiesAccountTransactionManager_BF
					.genSellStockTrx(kmmFile, 
									stockAcctID, expensesAcctAmtList, offsetAcctID,
									nofStocks, stockPrc, 
									datPst, descr);
		} else if ( type == SecuritiesAccountTransactionManager_BF.Type.DIVIDEND ) {
			trx = SecuritiesAccountTransactionManager_BF
					.genDividDistribTrx(kmmFile,
									stockAcctID, incomeAcctID, expensesAcctAmtList, offsetAcctID, 
									KMyMoneyTransactionSplit.Action.DIVIDEND, divDistrGross, datPst, 
									descr);
		} else if ( type == SecuritiesAccountTransactionManager_BF.Type.DISTRIBUTION ) {
			trx = SecuritiesAccountTransactionManager_BF
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
		BigFraction amt1 = divDistrGross.multiply(BigFraction.of(25, 100));
		AcctIDAmountBFPair acctAmtPr1 = new AcctIDAmountBFPair(expAcct1, amt1);
		expensesAcctAmtList.add(acctAmtPr1);
		
		KMMAcctID expAcct2 = new KMMAcctID( "A000027" ); // Soli
		BigFraction amt2 = amt1.multiply(BigFraction.of(55, 1000));
		AcctIDAmountBFPair acctAmtPr2 = new AcctIDAmountBFPair(expAcct2, amt2);
		expensesAcctAmtList.add(acctAmtPr2);
	}
}
