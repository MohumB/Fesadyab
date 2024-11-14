package domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
public class TransactionTest {

    private Transaction transaction1;
    private Transaction transaction2;

    @BeforeEach
    public void setUp() {
        transaction1 = new Transaction();
        transaction1.transactionId = 1;
        transaction1.accountId = 101;
        transaction1.amount = 100;
        transaction1.isDebit = true;

        transaction2 = new Transaction();
        transaction2.transactionId = 2;
        transaction2.accountId = 101;
        transaction2.amount = 200;
        transaction2.isDebit = false;
    }

    @Test
    public void testEquals_sameTransactionId() {
        Transaction transaction3 = new Transaction();
        transaction3.transactionId = 1;
        assertTrue(transaction1.equals(transaction3), "Transactions with same transactionId should be equal");
    }

    @Test
    public void testEquals_differentTransactionId() {
        assertFalse(transaction1.equals(transaction2), "Transactions with different transactionIds should not be equal");
    }

    @Test
    public void testEquals_differentClass() {
        assertFalse(transaction1.equals(new Object()), "Transaction should not be equal to an object of a different class");
    }
}