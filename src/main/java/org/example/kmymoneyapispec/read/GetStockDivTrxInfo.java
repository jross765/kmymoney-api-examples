package org.example.kmymoneyapispec.read;

import java.io.File;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.apispec.read.KMyMoneyStockDividendTransaction;
import org.kmymoney.apispec.read.impl.KMyMoneyStockDividendTransactionImpl;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

import xyz.schnorxoborx.base.cmdlinetools.Helper;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GetStockDivTrxInfo {
    // BEGIN Example data -- adapt to your needs
    private static String kmmFileName  = "example_in.kmy";
    private static Helper.Mode mode    = Helper.Mode.ID;
    private static KMMTrxID trxID      = new KMMTrxID("xyz");
    private static String acctName     = "abc";
    // END Example data

    // -----------------------------------------------------------------

	public static void main(String[] args) {
		try {
			GetStockDivTrxInfo tool = new GetStockDivTrxInfo();
			tool.kernel();
		} catch (Exception exc) {
			System.err.println("Execution exception. Aborting.");
			exc.printStackTrace();
			System.exit(1);
		}
	}

	protected void kernel() throws Exception {
		KMyMoneyFileImpl kmmFile = new KMyMoneyFileImpl(new File(kmmFileName));

		KMyMoneyTransaction genTrx = kmmFile.getTransactionByID(trxID);
		if ( genTrx == null ) {
			System.err.println("Error: No (generic) transaction with ID '" + trxID + "' in KMyMoney file");
			System.exit(-1); 
		}

		KMyMoneyStockDividendTransaction specTrx = new KMyMoneyStockDividendTransactionImpl((KMyMoneyTransactionImpl) genTrx);
		if ( specTrx == null ) {
			System.err.println("Error: Transaction with ID '" + trxID + "' does not meet criteria for stock dividend transaction");
			System.exit(-1); 
		}
		
		// ---
		// Inherited from KMyMoneyTransaction:
		
		FixedPointNumber balance = specTrx.getBalance();
		BigFraction balanceRat = specTrx.getBalanceRat();
		
		int nofSplits = specTrx.getSplitsCount();
		KMyMoneyTransactionSplit splt1 = specTrx.getSplits().get(0);

		// ---
		// Inherited from KMyMoneyStockDividendTransaction:
		
		FixedPointNumber grossDiv = specTrx.getGrossDividend();
		FixedPointNumber feeTax = specTrx.getFeesTaxes();
		FixedPointNumber netDiv = specTrx.getNetDividend();

		BigFraction grossDivRat = specTrx.getGrossDividendRat();
		BigFraction feeTaxRat = specTrx.getFeesTaxesRat();
		BigFraction netDivRat = specTrx.getNetDividendRat();
		
		System.out.println("Stock acct. split:      " + specTrx.getStockAccountSplit());
		System.out.println("Income acct. split:     " + specTrx.getIncomeAccountSplit());
		for ( KMyMoneyTransactionSplit splt : specTrx.getExpensesSplits() ) {
			System.out.println("Expenses acct. split:   " + splt);
		}
		System.out.println("Offsetting acct. split: " + specTrx.getOffsettingAccountSplit());
		
		// ---
		
		System.out.println("");
		System.out.println("String rep. (1): " + specTrx.toString() );
		System.out.println("String rep. (2): " + ((KMyMoneyStockDividendTransactionImpl) specTrx).toStringHuman() );
	}

}
