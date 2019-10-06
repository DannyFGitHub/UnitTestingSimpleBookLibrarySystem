package library.entities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Add Mockito Extension to JUNIT5
@ExtendWith(MockitoExtension.class)
class PatronUnitTest {


    String lastName = "lastnametest";
    String firstName = "firstnametest";
    String email = "emailtest";
    long phoneNumber = 123456321;
    int id = 123;

    //Mockito
    @Mock Loan mockLoan;
    @Mock Loan mockLoan2;

    @InjectMocks
    Patron patron = new Patron(lastName, firstName, email, phoneNumber, id);

    @BeforeEach
    void setUp() {
        patron = new Patron(lastName, firstName, email, phoneNumber, id);
    }

    @AfterEach
    void tearDown() {
    }

    //Test hasOverDueLoans with no loans to begin with:
    @Test
    void testHasOverdueNoLoans(){
        //Arrange
        boolean expected = false;
        //Act
        boolean actual = patron.hasOverDueLoans();
        //Assert
        assertEquals(expected, actual);
    }
    //Test hasOverDueLoan with a current loan
    @Test
    void testHasOverdueWithCurrentLoan(){
        //Arrange
        assertNotNull(mockLoan);
        patron.loans.put(1, mockLoan);

        when(mockLoan.isOverDue()).thenReturn(false);

        boolean expected = false;
        //Act
        boolean actual = patron.hasOverDueLoans();
        //Assert
        assertEquals(expected, actual);

        //Check whether mockLoan is checking whether its overdue
        verify(mockLoan).isOverDue();
    }
    //Test hasOverDueLoan with over due loan
    @Test
    void testHasOverdueWithOverdueLoan(){
        //Arrange
        assertNotNull(mockLoan);
        patron.loans.put(1, mockLoan);

        when(mockLoan.isOverDue()).thenReturn(true);

        boolean expected = true;
        //Act
        boolean actual = patron.hasOverDueLoans();
        //Assert
        assertEquals(expected, actual);

        //Check whether mockLoan is checking whether its overdue
        verify(mockLoan).isOverDue();
    }
    //Test hasOverDueWith Multiple loans but none is overdue
    @Test
    void testHasOverdueWithMultipleLoansNoneOverdue(){
        //Arrange
        patron.loans.put(1, mockLoan);
        patron.loans.put(2, mockLoan2);

        when(mockLoan.isOverDue()).thenReturn(false);
        when(mockLoan2.isOverDue()).thenReturn(false);

        boolean expected = false;
        //Act
        boolean actual = patron.hasOverDueLoans();
        //Assert
        assertEquals(expected, actual);

        //Check whether mockLoan is checking whether its overdue
        verify(mockLoan).isOverDue();
        verify(mockLoan2).isOverDue();
    }


    //Test takeOutLoan adds loan to current borrowing record when state CAN_BORROW, valid loan, loan does not exist in current borrowing record, loan state is CURRENT
    @Test
    void takeOutLoanAddsLoanWhenAllPreconditionsMet(){
        //Arrange
        patron.state = IPatron.PatronState.CAN_BORROW;
        assertNotNull(mockLoan);
        //Check that its not in the borrowing record
        assertTrue(patron.loans.get(mockLoan.getId()) == null);
        mockLoan.state = ILoan.LoanState.CURRENT;
        //Act
        patron.takeOutLoan(mockLoan);
        //Assert
        //Check whether the borrowing record now holds loan
        assertTrue(patron.loans.get(mockLoan.getId()) != null);
    }
    //Test takeOutLoan throws error when preconditions not met patron state is RESTRICTED, also loan is not added to current borrowing record:
    @Test
    void takeOutLoanPreconditionsNotMet(){
        //Arrange
        patron.state = IPatron.PatronState.RESTRICTED;
        assertNotNull(mockLoan);
        //Check that its not in the borrowing record
        assertTrue(patron.loans.get(mockLoan.getId()) == null);
        mockLoan.state = ILoan.LoanState.CURRENT;
        //Act
        //Assert
        //Check whether the borrowing record now holds loan
        assertFalse(patron.loans.get(mockLoan.getId()) != null);

        assertThrows(RuntimeException.class, () -> patron.takeOutLoan(mockLoan));
    }
    //Test takeOutLoan throws error when loan is invalid:
    @Test
    void takeOutLoanPreconditionsNotMetLoanInvalid(){
        //Arrange
        mockLoan = null;
        patron.state = IPatron.PatronState.CAN_BORROW;
        assertNull(mockLoan);
        //Act
        //Assert
        assertThrows(RuntimeException.class, () -> patron.takeOutLoan(mockLoan));
    }
    //Test takeOutLoan throws error when preconditions not met loan is not current:
    @Test
    void takeOutLoanPreconditionsNotMetLoanNotCurrent(){
        //Arrange
        patron.state = IPatron.PatronState.CAN_BORROW;
        assertNotNull(mockLoan);
        //Check that its not in the borrowing record
        assertTrue(patron.loans.get(mockLoan.getId()) == null);
        mockLoan.state = ILoan.LoanState.OVER_DUE;
        //Act
        //Assert
        //Check whether the borrowing record now holds loan
        assertFalse(patron.loans.get(mockLoan.getId()) != null);

        assertThrows(RuntimeException.class, () -> patron.takeOutLoan(mockLoan));
    }
}