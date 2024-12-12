package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionEngineTest {

    private TransactionEngine transactionEngine;
    private Transaction transaction1;
    private Transaction transaction2;

    @BeforeEach
    void setUp() {
        transactionEngine = new TransactionEngine();

        transaction1 = new Transaction();
        transaction1.transactionId = 1;
        transaction1.accountId = 101;
        transaction1.amount = 300;
        transaction1.isDebit = true;

        transaction2 = new Transaction();
        transaction2.transactionId = 2;
        transaction2.accountId = 101;
        transaction2.amount = 100;
        transaction2.isDebit = true;
    }

//    @Test
//    void testAddTransactionAndDetectFraud_NoFraud() {
//        Transaction txn1 = new Transaction();
//        txn1.setTransactionId(1);
//        txn1.setAccountId(1001);
//        txn1.setAmount(300);
//        txn1.setDebit(true);
//
//        // Clear history to ensure no previous transactions affect the result
//        transactionEngine.transactionHistory.clear();
//
//        int fraudScore = transactionEngine.addTransactionAndDetectFraud(txn1);
//        assertEquals(0, fraudScore, "No fraud should be detected for a low amount transaction.");
//    }


    @Test
    void testAddTransactionAndDetectFraud_FraudDetected() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(1001);
        txn1.setAmount(300);
        txn1.setDebit(true);
        transactionEngine.addTransactionAndDetectFraud(txn1);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(1001);
        txn2.setAmount(1000);
        txn2.setDebit(true);

        int fraudScore = transactionEngine.addTransactionAndDetectFraud(txn2);
        assertTrue(fraudScore > 0, "Fraud should be detected when transaction amount is excessively high.");
    }

    @Test
    void testGetAverageTransactionAmountByAccount() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(1001);
        txn1.setAmount(300);
        transactionEngine.addTransactionAndDetectFraud(txn1);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(1001);
        txn2.setAmount(700);
        transactionEngine.addTransactionAndDetectFraud(txn2);

        int avgAmount = transactionEngine.getAverageTransactionAmountByAccount(1001);
        assertEquals(500, avgAmount);
    }

    @Test
    void testGetTransactionPatternAboveThreshold() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(1001);
        txn1.setAmount(1200);
        transactionEngine.addTransactionAndDetectFraud(txn1);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(1001);
        txn2.setAmount(1400);
        transactionEngine.addTransactionAndDetectFraud(txn2);

        int pattern = transactionEngine.getTransactionPatternAboveThreshold(1000);
        assertEquals(200, pattern, "The difference between high transactions should be 200.");
    }

//    @Test
//    public void testDetectFraudulentTransaction_exceedsThreshold() {
//        transactionEngine.addTransactionAndDetectFraud(transaction1); // Add initial transaction
//
//        // Adding a low average transaction to trigger fraud detection
//        Transaction lowTxn = new Transaction();
//        lowTxn.setTransactionId(3);
//        lowTxn.setAccountId(101);
//        lowTxn.setAmount(50);
//        transactionEngine.addTransactionAndDetectFraud(lowTxn);
//
//        int result = transactionEngine.detectFraudulentTransaction(transaction1);
//        assertEquals(100, result, "Fraudulent transaction should return difference amount if it exceeds threshold");
//    }


    @Test
    public void testDetectFraudulentTransaction_withinThreshold() {
        transactionEngine.addTransactionAndDetectFraud(transaction2); // Add initial transaction
        int result = transactionEngine.detectFraudulentTransaction(transaction2);
        assertEquals(0, result, "Transaction within threshold should return 0");
    }

    @Test
    public void testAddTransactionAndDetectFraud_duplicateTransaction() {
        transactionEngine.addTransactionAndDetectFraud(transaction1); // First addition
        int result = transactionEngine.addTransactionAndDetectFraud(transaction1); // Duplicate
        assertEquals(0, result, "Duplicate transaction should return 0");
    }

//    @Test
//    public void testAddTransactionAndDetectFraud_newFraudulentTransaction() {
//        // Add a baseline transaction for average calculation
//        transactionEngine.addTransactionAndDetectFraud(transaction1);
//
//        // Create a new high-value transaction to trigger fraud detection
//        Transaction highTxn = new Transaction();
//        highTxn.setTransactionId(3);
//        highTxn.setAccountId(101); // Same account ID
//        highTxn.setAmount(800);    // Amount that should trigger fraud detection
//        highTxn.setDebit(true);
//
//        int result = transactionEngine.addTransactionAndDetectFraud(highTxn);
//        assertEquals(100, result, "New fraudulent transaction should return difference amount if it exceeds threshold");
//    }


    @Test
    public void testAddTransactionAndDetectFraud_nonFraudulentTransaction() {
        transactionEngine.addTransactionAndDetectFraud(transaction2); // Adding non-fraudulent transaction
        int result = transactionEngine.addTransactionAndDetectFraud(transaction2);
        assertEquals(0, result, "Non-fraudulent transaction should return 0");
    }

    @Test
    void testGetTransactionPatternAboveThreshold_EmptyHistory() {
        int pattern = transactionEngine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, pattern, "Pattern should be 0 when transaction history is empty.");
    }

    @Test
    void testGetTransactionPatternAboveThreshold_AllBelowThreshold() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(1001);
        txn1.setAmount(800);  // Below threshold
        transactionEngine.addTransactionAndDetectFraud(txn1);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(1001);
        txn2.setAmount(900);  // Below threshold
        transactionEngine.addTransactionAndDetectFraud(txn2);

        int pattern = transactionEngine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, pattern, "Pattern should be 0 when all transactions are below the threshold.");
    }

    @Test
    void testGetTransactionPatternAboveThreshold_InconsistentDifference() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(1001);
        txn1.setAmount(1200);
        transactionEngine.addTransactionAndDetectFraud(txn1);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(1001);
        txn2.setAmount(1400);  // Difference of 200
        transactionEngine.addTransactionAndDetectFraud(txn2);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAccountId(1001);
        txn3.setAmount(1700);  // Difference of 300, inconsistent with previous
        transactionEngine.addTransactionAndDetectFraud(txn3);

        int pattern = transactionEngine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, pattern, "Pattern should be 0 when differences are inconsistent.");
    }

    @Test
    void testGetTransactionPatternAboveThreshold_ConsistentDifference() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(1001);
        txn1.setAmount(1200);
        transactionEngine.addTransactionAndDetectFraud(txn1);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(1001);
        txn2.setAmount(1400);  // Difference of 200
        transactionEngine.addTransactionAndDetectFraud(txn2);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAccountId(1001);
        txn3.setAmount(1600);  // Difference of 200, consistent with previous
        transactionEngine.addTransactionAndDetectFraud(txn3);

        int pattern = transactionEngine.getTransactionPatternAboveThreshold(1000);
        assertEquals(200, pattern, "Pattern should be 200 when differences are consistent.");
    }

    @Test
    void testTransactionPatternExactThresholdBoundary() {
        // Transactions exactly at and just around the threshold
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(1001);
        txn1.setAmount(1000);  // Exactly at threshold
        transactionEngine.addTransactionAndDetectFraud(txn1);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(1001);
        txn2.setAmount(1001);  // Just above threshold
        transactionEngine.addTransactionAndDetectFraud(txn2);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAccountId(1001);
        txn3.setAmount(999);   // Just below threshold
        transactionEngine.addTransactionAndDetectFraud(txn3);

        // Pattern should only detect transactions strictly above threshold
        int pattern1 = transactionEngine.getTransactionPatternAboveThreshold(1000);
        assertEquals(1, pattern1, "Transactions at or below threshold should not create a pattern");
    }

    @Test
    void testFraudDetectionStrictBoundaryConditions() {
        // Establish baseline transactions to calculate average
        Transaction baseline1 = new Transaction();
        baseline1.setTransactionId(1);
        baseline1.setAccountId(101);
        baseline1.setAmount(100);
        baseline1.setDebit(true);
        transactionEngine.addTransactionAndDetectFraud(baseline1);

        Transaction baseline2 = new Transaction();
        baseline2.setTransactionId(2);
        baseline2.setAccountId(101);
        baseline2.setAmount(100);
        baseline2.setDebit(true);
        transactionEngine.addTransactionAndDetectFraud(baseline2);

        // Test various boundary scenarios
        Transaction[] boundaryScenarios = new Transaction[4];

        // Scenario 1: Exactly 2x average (should not be fraudulent)
        boundaryScenarios[0] = createTransaction(3, 101, 200, true);

        // Scenario 2: Just below 2x average (should not be fraudulent)
        boundaryScenarios[1] = createTransaction(4, 101, 199, true);

        // Scenario 3: Just above 2x average (should be fraudulent)
        boundaryScenarios[2] = createTransaction(5, 101, 201, true);

        // Scenario 4: Non-debit transaction above 2x average (should not be fraudulent)
        boundaryScenarios[3] = createTransaction(6, 101, 201, false);

        // Expected fraud scores
        int[] expectedScores = {0, 0, 1, 0};

        for (int i = 0; i < boundaryScenarios.length; i++) {
            int fraudScore = transactionEngine.detectFraudulentTransaction(boundaryScenarios[i]);
            assertEquals(expectedScores[i], fraudScore,
                    "Fraud detection failed for scenario " + (i+1) +
                            " with transaction amount " + boundaryScenarios[i].amount);
        }
    }

    @Test
    void testFraudScoreCalculationAccuracy() {
        // Establish baseline transactions to calculate average
        Transaction baseline1 = new Transaction();
        baseline1.setTransactionId(1);
        baseline1.setAccountId(101);
        baseline1.setAmount(100);
        baseline1.setDebit(true);
        transactionEngine.addTransactionAndDetectFraud(baseline1);

        Transaction baseline2 = new Transaction();
        baseline2.setTransactionId(2);
        baseline2.setAccountId(101);
        baseline2.setAmount(100);
        baseline2.setDebit(true);
        transactionEngine.addTransactionAndDetectFraud(baseline2);

        // Create a fraudulent transaction
        Transaction fraudTxn = new Transaction();
        fraudTxn.setTransactionId(3);
        fraudTxn.setAccountId(101);
        fraudTxn.setAmount(301);  // Just above 2x average
        fraudTxn.setDebit(true);

        // Verify exact fraud score calculation
        int expectedFraudScore = fraudTxn.getAmount() - (2 * 100);  // Explicit calculation
        int actualFraudScore = transactionEngine.detectFraudulentTransaction(fraudTxn);

        assertEquals(expectedFraudScore, actualFraudScore,
                "Fraud score calculation must precisely subtract 2x average");

        // Additional test to ensure subtraction, not addition
        assertNotEquals(fraudTxn.getAmount() + (2 * 100), actualFraudScore,
                "Fraud score must use subtraction, not addition");
    }

    // Utility method to create transactions easily
    private Transaction createTransaction(int transactionId, int accountId, int amount, boolean isDebit) {
        Transaction txn = new Transaction();
        txn.setTransactionId(transactionId);
        txn.setAccountId(accountId);
        txn.setAmount(amount);
        txn.setDebit(isDebit);
        return txn;
    }


}
