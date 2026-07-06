package org.example.kmymoneyapi.read;

import java.io.File;
import java.util.Collection;

import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyInstitution;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.base.basetypes.simple.KMMInstID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class GetInstInfo {

	// BEGIN Example data -- adapt to your needs
	private static String      kmmFileName = "example_in.kmy";
	private static Helper.Mode mode        = Helper.Mode.ID;
	private static KMMInstID   instID      = new KMMInstID("I000xyz");
	private static String      isin        = "DE0123456789";
	private static String      secName     = "def";
	// END Example data

	// -----------------------------------------------------------------

	public static void main(String[] args) {
		try {
			GetInstInfo tool = new GetInstInfo();
			tool.kernel();
		} catch (Exception exc) {
			System.err.println("Execution exception. Aborting.");
			exc.printStackTrace();
			System.exit(1);
		}
	}

	protected void kernel() throws Exception {
		KMyMoneyFileImpl gcshFile = new KMyMoneyFileImpl(new File(kmmFileName));

		KMyMoneyInstitution inst = null;
		if ( mode == Helper.Mode.ID ) {
			inst = gcshFile.getInstitutionByID(instID);
			if ( inst == null ) {
				System.err.println("Could not find a institution with this ID.");
				throw new NoEntryFoundException();
			}
		} else if ( mode == Helper.Mode.NAME ) {
			Collection<KMyMoneyInstitution> secCurrList = gcshFile.getInstitutionsByName(secName);
			if ( secCurrList.size() == 0 ) {
				System.err.println("Could not find institution matching this name.");
				throw new NoEntryFoundException();
			}
			if ( secCurrList.size() > 1 ) {
				System.err.println("Found " + secCurrList.size() + "securities matching this name.");
				System.err.println("Please specify more precisely.");
				throw new TooManyEntriesFoundException();
			}
			inst = secCurrList.iterator().next(); // first element
		}

		// ----------------------------

		try {
			System.out.println("ID:                " + inst.getID());
		} catch (Exception exc) {
			System.out.println("ID:                " + "ERROR");
		}

		try {
			System.out.println("toString:          " + inst.toString());
		} catch (Exception exc) {
			System.out.println("toString:          " + "ERROR");
		}

		try {
			System.out.println("Name:              '" + inst.getName() + "'");
		} catch (Exception exc) {
			System.out.println("Name:              " + "ERROR");
		}

		try {
			System.out.println("Sort code:         '" + inst.getSortCode() + "'");
		} catch (Exception exc) {
			System.out.println("Sort code:         " + "ERROR");
		}

		try {
			System.out.println("URL:               " + inst.getURL());
		} catch (Exception exc) {
			System.out.println("URL:               " + "ERROR");
		}

		// ---

		showAccounts(inst);
	}

	// -----------------------------------------------------------------

	private void showAccounts(KMyMoneyInstitution inst) {
		System.out.println("");
		System.out.println("Accounts:");

		System.out.println("");
		System.out.println("Number of accounts: " + inst.getAccounts().size());

		System.out.println("");
		for ( KMyMoneyAccount acct : inst.getAccounts() ) {
			System.out.println(" - " + acct.toString());
		}
	}
}
