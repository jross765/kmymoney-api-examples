package org.example.kmymoneyapi.write.gen;

import java.io.File;
import java.time.LocalDate;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.aux.KMMBudgetAccount;
import org.kmymoney.api.write.KMyMoneyWritableBudget;
import org.kmymoney.api.write.aux.KMMWritableBudgetAccount;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.base.basetypes.simple.KMMAcctID;

public class GenBdgt
{
    // BEGIN Example data -- adapt to your needs
    private static String kmmInFileName  = "example_in.kmy";
    private static String kmmOutFileName = "example_out.kmy";
    
    private static String     name    = "Budget 2026";
    
    private static KMMAcctID  acct1ID = new KMMAcctID("A000abc");
    private static KMMAcctID  acct2ID = new KMMAcctID("A000def");
    private static KMMAcctID  acct3ID = new KMMAcctID("A000ghi");
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
    	try {
    		GenAcct tool = new GenAcct();
    		tool.kernel();
    	} catch (Exception exc) {
    		System.err.println("Execution exception. Aborting.");
    		exc.printStackTrace();
    		System.exit(1);
    	}
    }

    protected void kernel() throws Exception
    {
    	KMyMoneyWritableFileImpl kmmFile = new KMyMoneyWritableFileImpl(new File(kmmInFileName));

    	KMyMoneyWritableBudget bdgt = kmmFile.createWritableBudget(name);
    	bdgt.setStart(LocalDate.of(2026, 1, 1));
    	
    	KMMWritableBudgetAccount bdgtAcct1 = bdgt.createWritableAccount(acct1ID, KMMBudgetAccount.Level.MONTH_BY_MONTH);
    	// Entries for mar, jul 
    	bdgtAcct1.createWritablePeriod(LocalDate.of(2026, 3, 1), BigFraction.of(1010));
    	bdgtAcct1.createWritablePeriod(LocalDate.of(2026, 7, 1), BigFraction.of(1020));
    	
    	KMMWritableBudgetAccount bdgtAcct2 = bdgt.createWritableAccount(acct2ID, KMMBudgetAccount.Level.MONTHLY);
    	// Entries for feb, mar, apr 
    	bdgtAcct2.createWritablePeriod(LocalDate.of(2026, 2, 1), BigFraction.of(2010));
    	bdgtAcct2.createWritablePeriod(LocalDate.of(2026, 3, 1), BigFraction.of(2020));
    	bdgtAcct2.createWritablePeriod(LocalDate.of(2026, 4, 1), BigFraction.of(2030));
    	// etc., until dec
    	
    	KMMWritableBudgetAccount bdgtAcct3 = bdgt.createWritableAccount(acct3ID, KMMBudgetAccount.Level.YEARLY);
    	// One entry for the entire year 
    	bdgtAcct3.createWritablePeriod(LocalDate.of(2026, 1, 1), BigFraction.of(3000));
    	
    	System.out.println("Budget to write: " + bdgt.toString());
    	kmmFile.writeFile(new File(kmmOutFileName));
    	System.out.println("OK");
    }
  
}
