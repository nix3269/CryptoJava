package lab2;

import java.util.*;

/**
 * Ledger defines for each user the balance at a given time in the ledger model
 * of bitcoins and contains methods for checking and updating the ledger
 * including processing a transaction
 */

public class Ledger extends UserAmount {

	public UserAmount MainLedger;

	public Ledger() {
		this.MainLedger = new UserAmount();
	}

	/**
	 *
	 * Task 1: Fill in the method checkUserAmountDeductable You need to replace the
	 * dummy value true by the correct calculation
	 *
	 * Check all items in amountToCheckForDeduction can be deducted from the current
	 * one
	 *
	 * amountToCheckForDeduction is usually obtained from a list of inputs of a
	 * transaction
	 *
	 * Checking that a TransactionOutputList can be deducted will be later done by
	 * first converting that TransactionOutputList into a UserAmount and then using
	 * this method
	 *
	 * A naive check would just check whether each entry of a outputlist of a
	 * Transaction can be deducted
	 *
	 * But there could be an output for the same user Alice of say 10 units twice
	 * where there are not enough funds to deduct it twice but enough funds to
	 * deduct it once The naive check would succeed, but after converting the ouput
	 * list of a Transaction to UserAmount we obtain that for Alice 20 units have to
	 * be deducted so the deduction of the UserAmount created fails.
	 *
	 * One could try for checking that one should actually deduct each entry in
	 * squence but then one has to backtrack again. Converting the
	 * TransactionOutputList into a UserAmount is a better approach since the
	 * outputlist of a Transaction is usually much smaller than the main Ledger.
	 * 
	 *
	 */

	public boolean checkUserAmountDeductable(UserAmount userAmountCheck) {
		// you need to replace then next line by the correct statement
		Set<Map.Entry<String, Integer>> CheckEntries = userAmountCheck.userAmountBase.entrySet();
		for (Map.Entry<String, Integer> entry : CheckEntries) {
			// For each entry.key check reciprocal in MainLedgerEntries
			if (userAmountCheck.getBalance(entry.getKey()) > MainLedger.getBalance(entry.getKey())) {
				return false;
			}
		}
		return true;
	};

	/**
	 *
	 * Task 2: Fill in the method checkEntryListDeductable You need to replace the
	 * dummy value true by the correct calculation
	 *
	 * It checks that an EntryList (which will be inputs of a transactions) can be
	 * deducted from Ledger
	 *
	 * done by first converting the EntryList into a UserAmount and then checking
	 * that the resulting UserAmount can be deducted.
	 * 
	 */

	public boolean checkEntryListDeductable(EntryList txel) {
		// you need to replace then next line by the correct statement
		UserAmount check = new UserAmount(txel);
		return checkUserAmountDeductable(check);
	};

	/**
	 * Task 3: Fill in the methods subtractEntryList and addEntryList.
	 *
	 * Subtract an EntryList (txel, usually transaction inputs) from the ledger
	 *
	 * requires that the list to be deducted is deductable.
	 * 
	 */

	public void subtractEntryList(EntryList txel) {
		// fill in Body
		if (checkEntryListDeductable(txel)) {
			UserAmount userAmountCheck = new UserAmount(txel);
			Set<Map.Entry<String, Integer>> CheckEntries = userAmountCheck.userAmountBase.entrySet();
			for (Map.Entry<String, Integer> entry : CheckEntries) {
				MainLedger.subtractBalance(entry.getKey(), userAmountCheck.getBalance(entry.getKey()));
			}
		} else {
			System.out.println("EntryList is not subtractable!");
		}
		return;
	}

	/**
	 * Add an EntryList (txel, usually transaction outputs) to the current ledger
	 *
	 */

	public void addEntryList(EntryList txel) {
		// fill in Body
		UserAmount userAmountCheck = new UserAmount(txel);
		Set<Map.Entry<String, Integer>> CheckEntries = userAmountCheck.userAmountBase.entrySet();
		for (Map.Entry<String, Integer> entry : CheckEntries) {
			MainLedger.addBalance(entry.getKey(), userAmountCheck.getBalance(entry.getKey()));
		}
		return;
	}

	/**
	 *
	 * Task 4: Fill in the method checkTransactionValid You need to replace the
	 * dummy value true by the correct calculation
	 *
	 * Check a transaction is valid: the sum of outputs is less than or equal the
	 * sum of inputs and the inputs can be deducted from the ledger.
	 *
	 */

	public boolean checkTransactionValid(Transaction tx) {
		// you need to replace then next line by the correct statement
		if (checkEntryListDeductable(tx.toInputs())) {
			return (tx.checkTransactionAmountsValid());
		} else {
			return false;
		}

	};

	/**
	 *
	 * Task 5: Fill in the method processTransaction
	 *
	 * Process a transaction by first deducting all the inputs and then adding all
	 * the outputs.
	 *
	 */

	public void processTransaction(Transaction tx) {
		// fill in Body
		if (checkTransactionValid(tx)) {
			System.out.println("Transaction is Valid!");
			subtractEntryList(tx.toInputs());
			addEntryList(tx.toOutputs());
		} else {
			System.out.println("Transaction is invalid!");
		}
		return;
	};

	/**
	 * Task 6: Fill in the testcases as described in the labsheet
	 * 
	 * Testcase
	 */

	public static void test() {
		// fill in Body
		Ledger l = new Ledger();
		l.MainLedger.addAccount("Alice", 0);
		l.MainLedger.addAccount("Bob", 0);
		l.MainLedger.addAccount("Carol", 0);
		l.MainLedger.addAccount("David", 0);
		System.out.println("After initialising Alice, Bob, Carol, David");
		l.MainLedger.print();
		System.out.println("Set Balance Alice to 20");
		l.MainLedger.setBalance("Alice", 20);
		l.MainLedger.print();
		System.out.println("Set Balance Bob 15");
		l.MainLedger.setBalance("Bob", 15);
		l.MainLedger.print();
		System.out.println("add Balance Alice 15");
		l.MainLedger.addBalance("Alice", 15);
		l.MainLedger.print();
		System.out.println("Subtract Balance 10 from Bob");
		l.MainLedger.subtractBalance("Bob", 10);
		l.MainLedger.print();
		System.out.println("Creating Useramount from EntryList (Alice,15),(Bob,10)");
		EntryList txel1 = new EntryList("Alice", 15, "Bob", 10);
		(new UserAmount(txel1)).print();
		System.out.println("Checking entryList deductable");
		System.out.println(l.checkEntryListDeductable(txel1));
		l.MainLedger.print();
		System.out.println("Creating Useramount from EntryList (Alice,15),(Alice,15),(Bob,5)");
		EntryList txel2 = new EntryList("Alice", 15, "Alice", 15, "Bob", 5);
		(new UserAmount(txel2)).print();
		System.out.println("Checking entryList deductable");
		System.out.println(l.checkEntryListDeductable(txel2));
		l.MainLedger.print();
		System.out.println("Subtracting entryList txel1");
		l.subtractEntryList(txel1);
//
		System.out.println("Adding entryList txel2");
		l.addEntryList(txel2);
		l.MainLedger.print();

		System.out.println("Creating Transaction tx1 from Alice 45 to Bob 5 and Carol 20");
		Transaction tx1 = new Transaction(new EntryList("Alice", 45), new EntryList("Bob", 5, "Carol", 20));
		l.processTransaction(tx1);

		System.out.println("Creating Transaction tx2 from Alice 20 to Bob 5 and Carol 20");
		Transaction tx2 = new Transaction(new EntryList("Alice", 20), new EntryList("Bob", 5, "Carol", 20));
		l.processTransaction(tx2);

		System.out.println("Creating Transaction tx3 from Alice 25 to Bob 10 and Carol 15");
		Transaction tx3 = new Transaction(new EntryList("Alice", 25), new EntryList("Bob", 10, "Carol", 15));
		l.processTransaction(tx3);

		System.out.println("Creating Transaction tx4 from Alice 5 Twice to Bob 10");
		Transaction tx4 = new Transaction(new EntryList("Alice", 5, "Alice", 5), new EntryList("Bob", 10));
		l.processTransaction(tx4);
		l.MainLedger.print();
	}

	/**
	 * main function running test cases
	 */

	public static void main(String[] args) {
		Ledger.test();
	}
}
