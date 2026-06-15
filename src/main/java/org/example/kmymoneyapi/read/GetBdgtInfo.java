package org.example.kmymoneyapi.read;

import java.io.File;
import java.util.Collection;

import org.kmymoney.api.read.KMyMoneyBudget;
import org.kmymoney.api.read.aux.KMMBudgetAccount;
import org.kmymoney.api.read.aux.KMMBudgetPeriod;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.base.basetypes.simple.KMMBdgtID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class GetBdgtInfo {

	public enum Mode {
		ID,
		NAME
	}

	// -----------------------------------------------------------------

	// BEGIN Example data -- adapt to your needs
	private static String     kmmFileName  = "example_in.kmy";
	private static Mode       mode         = Mode.ID;
	private static KMMBdgtID  bdgtID       = new KMMBdgtID("B000xyz");
	private static String     bdgtName     = "def";
	// END Example data

	// -----------------------------------------------------------------

	public static void main(String[] args) {
		try {
			GetBdgtInfo tool = new GetBdgtInfo();
			tool.kernel();
		} catch (Exception exc) {
			System.err.println("Execution exception. Aborting.");
			exc.printStackTrace();
			System.exit(1);
		}
	}

	protected void kernel() throws Exception {
		KMyMoneyFileImpl gcshFile = new KMyMoneyFileImpl(new File(kmmFileName));

		KMyMoneyBudget bdgt = null;
		if ( mode == Mode.ID ) {
			bdgt = gcshFile.getBudgetByID(bdgtID);
			if ( bdgt == null ) {
				System.err.println("Could not find a budget with this ID.");
				throw new NoEntryFoundException();
			}
		} else if ( mode == Mode.NAME ) {
			Collection<KMyMoneyBudget> secCurrList = gcshFile.getBudgetsByName(bdgtName);
			if ( secCurrList.size() == 0 ) {
				System.err.println("Could not find budgets matching this name.");
				throw new NoEntryFoundException();
			}
			if ( secCurrList.size() > 1 ) {
				System.err.println("Found " + secCurrList.size() + "budgets matching this name.");
				System.err.println("Please specify more precisely.");
				throw new TooManyEntriesFoundException();
			}
			bdgt = secCurrList.iterator().next(); // first element
		}

		// ----------------------------

		try {
			System.out.println("ID:                " + bdgt.getID());
		} catch (Exception exc) {
			System.out.println("D:                 " + "ERROR");
		}

		try {
			System.out.println("toString:          " + bdgt.toString());
		} catch (Exception exc) {
			System.out.println("toString:          " + "ERROR");
		}

		try {
			System.out.println("Name:              '" + bdgt.getName() + "'");
		} catch (Exception exc) {
			System.out.println("Name:              " + "ERROR");
		}

		try {
			System.out.println("Start:             " + bdgt.getStart());
		} catch (Exception exc) {
			System.out.println("Start:             " + "ERROR");
		}

		// ---

		showAccounts(bdgt);
	}

	// -----------------------------------------------------------------

	private void showAccounts(KMyMoneyBudget bdgt) {
		System.out.println("");
		System.out.println("Accounts:");

		System.out.println("");
		System.out.println("Number of accounts: " + bdgt.getAccounts().size());

		System.out.println("");
		for ( KMMBudgetAccount bdgtAcct : bdgt.getAccounts() ) {
			System.out.println(" - " + bdgtAcct.toString());
			System.out.println("   Number of periods: " + bdgtAcct.getPeriods().size());
			for ( KMMBudgetPeriod bdgtPrd : bdgtAcct.getPeriods() ) {
				System.out.println("   o " + bdgtPrd.toString());
			}
		}
	}
}
