package org.example.overall.write;

import java.io.File;
import java.util.ArrayList;

import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.write.KMyMoneyWritableAccount;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.base.basetypes.simple.KMMAcctID;

// This program will "thin-out" a KMyMoney file, i.e.
// batch-delete a number of objects. 
// 
// You might need that when you want to take a real-world
// file as a template and experiment with a smaller/
// less complicated version of it (for test purposes, say).
//
// The author is currently working on a full-fledged tool
// that does this in a more sophisticated and generally-
// applicable way (not published yet). What you see here is 
// a simplified version of it.
// 
// Apart from that, this example essentially demonstrates 
// how to delete objects (as opposed to all the other examples 
// that only read, generate or update objects). And since this 
// is a non-trivial exercise (you have to take into account 
// the dependencies between the objects, and there is some
// API-internal bookkeeping involved), it merits an example
// program of its own.
public class ThinOutKMMFile {
	private static final int NOF_ITER_MAX = 10;

	// -----------------------------------------------------------------

	// BEGIN Example data -- adapt to your needs
	private static String kmmInFileName  = "example_in.kmy";
	private static String kmmOutFileName = "example_out.kmy";

	// Fill the following list with IDs of accounts to be *kept*.
	// Transactions referencing these accounts will be kept as well.
	// You only have to specifiy the highest-level account in a branch
	// of the account tree -- all its children will be kept as well.
	private static ArrayList<KMMAcctID> lofAcctIDs = new ArrayList<KMMAcctID>();
	// END Example data

	// -----------------------------------------------------------------

	public static void main(String[] args) {
		try {
			ThinOutKMMFile tool = new ThinOutKMMFile();
			tool.kernel();
		} catch (Exception exc) {
			System.err.println("Execution exception. Aborting.");
			exc.printStackTrace();
			System.exit(1);
		}
	}

	protected void kernel() throws Exception {
		KMyMoneyWritableFile kmmFile = new KMyMoneyWritableFileImpl(new File(kmmInFileName), true);

		expandExcludeList(kmmFile);

		// ---
		// BEGIN CORE

		System.out.println("");
		System.out.println("----------------------------------------------");
		System.out.println("Transactions");
		deleteTransactions(kmmFile);

		System.out.println("");
		System.out.println("----------------------------------------------");
		System.out.println("Accounts");
		deleteAcctsIter(kmmFile);

		// END CORE
		// ---

		// Write thinned-out data to file
		System.out.print("Writing file: ");
		kmmFile.writeFile(new File(kmmOutFileName));
		System.out.println("OK");
	}

	// -----------------------------------------------------------------

	// Expand list of accounts to be kept with children of accounts
	// initially specified above. All the way down to the leaves.
	private void expandExcludeList(KMyMoneyWritableFile kmmFile) {
		ArrayList<KMMAcctID> newExclList = new ArrayList<KMMAcctID>();
		for ( KMMAcctID acctID : lofAcctIDs ) {
			newExclList.add(acctID);
			KMyMoneyAccount acct = kmmFile.getAccountByID(acctID);
			for ( KMyMoneyAccount subAcct : acct.getChildren() )
				newExclList.add(subAcct.getID().getStdID());
		}

		lofAcctIDs = newExclList;

		System.out.println("Expanded exlude list: " + lofAcctIDs);
	}

	private void deleteTransactions(KMyMoneyWritableFile kmmFile) {
		for ( KMyMoneyWritableTransaction trx : kmmFile.getWritableTransactions() ) {
			if ( toBeDeletedTrx(kmmFile, trx) ) {
				kmmFile.removeTransaction(trx);
			}
		}
	}

	private void deleteAcctsIter(KMyMoneyWritableFile kmmFile) {
		int nofIter = 0;
		while ( nofIter < NOF_ITER_MAX ) { // trivial exist of iteration. This can be improved, of course
			System.out.println("");
			System.out.println("-----------------------");
			System.out.println("Iteration " + (nofIter + 1));

			doAcctIteration(kmmFile);

			nofIter++;
		}
	}

	private void doAcctIteration(KMyMoneyWritableFile kmmFile) {
		for ( KMyMoneyWritableAccount acct : kmmFile.getWritableAccounts() ) {
			if ( toBeDeletedAcct(kmmFile, acct) ) {
				acct.remove();
			}
		}
	}

	// ---------------------------------------------------------------

	private boolean toBeDeletedTrx(KMyMoneyWritableFile kmmFile, KMyMoneyTransaction trx) {
		boolean result = true;

		// Do not delete transactions pointing to one of the
		// accounts in the exclude-list
		for ( KMyMoneyTransactionSplit splt : trx.getSplits() ) {
			if ( lofAcctIDs.contains( splt.getAccountID() ) ) {
				result = false;
				continue;
			}
		}
		
		// More criteria, if you want, e.g.: date, action, etc.
		// ...

		return result;
	}

	private boolean toBeDeletedAcct(KMyMoneyWritableFile kmmFile, KMyMoneyAccount acct) {
		// Do not delete the top-level accounts (obviously)
		if ( acct.isRootAccount() )
			return false; 

		// Do not delete accounts that have children
		if ( acct.getChildren() != null ) {
			if ( acct.getChildren().size() > 0 )
				return false;
		}

		// Do not delete accounts that are in the exclude-list
		if ( lofAcctIDs.contains( acct.getID().getStdID() ) )
			return false; 

		// And of course: Do not delete accounts that have transactions 
		// pointing to them
		if ( acct.hasTransactions() )
			return false;
		
		// More criteria, if you want, e.g.: account type.
		// ...

		return true;
	}

}
