package library.entities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Add Mockito Extension to JUNIT5
@ExtendWith(MockitoExtension.class)
public class LoanUnitTest {

    Date mockTodayDate = new Date();
    Date mockTomorrowDueDate = new Date(mockTodayDate.getTime() + (1000 * 60 * 60 * 24));
    Date mockDayAfterTomorrowDueDate = new Date(mockTomorrowDueDate.getTime() + (1000 * 60 * 60 * 24));
    int loanId = 123;

    //Mockito
    @Mock Book mockBook;
    @Mock Patron mockPatron;

    @InjectMocks
    Loan loan = new Loan(mockBook, mockPatron);

    @BeforeEach
    void setUp() {
        mockTodayDate = new Date();
        mockTomorrowDueDate = new Date(mockTodayDate.getTime() + (1000 * 60 * 60 * 24));
        mockDayAfterTomorrowDueDate = new Date(mockTomorrowDueDate.getTime() + (1000 * 60 * 60 * 24));
        loan = new Loan(mockBook, mockPatron);
    }

    @AfterEach
    void tearDown() {
    }


    //Test whether commit with invalid precondition throws RuntimeException:
    @Test
    void commitInvalidPreconditionThrowsException(){
        //Arrange
        //Something other than PENDING as per the preconditions
        loan.state = ILoan.LoanState.CURRENT;
        //Act
        //Assert
        assertThrows(RuntimeException.class, () -> loan.commit(loanId, mockTomorrowDueDate));
    }
    //Test whether commit updates loan state to Current:
    @Test
    void commitUpdatesLoanStateToCurrent(){
        //Arrange
        ILoan.LoanState expected = ILoan.LoanState.CURRENT;
        //Valid Precondition
        loan.state = ILoan.LoanState.PENDING;
        //Act
        loan.commit(loanId, mockTomorrowDueDate);
        //Assert
        assertTrue(loan.state == expected);
    }
    //Test whether calls patron.takeoutloan:
    @Test
    void commitCallsPatronTakeoutLoan(){
        //Arrange
        //Valid Precondition
        loan.state = ILoan.LoanState.PENDING;
        //Act
        loan.commit(loanId, mockTomorrowDueDate);
        //Assert
        //Verifying that the method was called, not whether the method did what it was meant to do
        verify(mockPatron).takeOutLoan(loan);
    }
    //Test whether calls book.borrowFromLibrary:
    @Test
    void commitCallsBookBorrowFromLibrary(){
        //Arrange
        //Valid Precondition
        loan.state = ILoan.LoanState.PENDING;
        //Act
        loan.commit(loanId, mockTomorrowDueDate);
        //Assert
        //Verifying that the method was called, not whether the method did what it was meant to do
        verify(mockBook).borrowFromLibrary();
    }
    //Test whether Loan State is Current:
    @Test
    void commitLoanStateIsCurrent(){
        //Arrange
        ILoan.LoanState expected = ILoan.LoanState.CURRENT;
        //Valid Precondition
        loan.state = ILoan.LoanState.PENDING;
        //Act
        loan.commit(loanId, mockTomorrowDueDate);
        //Assert
        assertTrue(loan.state == expected);
    }


    /*
        Test whether checkOverDueDate does not modify the status of loan when state is CURRENT
        and currentDate is before dueDate:
     */
    @Test
    void checkOverDueDateUpdatesLoanStateWhenNotOverdue(){
        //Arrange
        ILoan.LoanState expected = ILoan.LoanState.CURRENT;
        loan.state = ILoan.LoanState.CURRENT;
        loan.dueDate = mockTomorrowDueDate;
        //Act
        loan.checkOverDue(mockTodayDate);
        //Assert
        assertTrue(loan.state == expected);
    }
    /*
        Test whether checkOverDueDate updates to the OVER_DUE status of loan when state is CURRENT to begin with
        and currentDate is after dueDate:
    */
    @Test
    void checkOverDueDateUpdatesLoanStateWhenOverdue(){
        //Arrange
        ILoan.LoanState expected = ILoan.LoanState.OVER_DUE;
        loan.state = ILoan.LoanState.CURRENT;
        loan.dueDate = mockTomorrowDueDate;
        //Act
        loan.checkOverDue(mockDayAfterTomorrowDueDate);
        //Assert
        assertTrue(loan.state == expected);
    }
    /*
        Test whether checkOverDueDate does not update the status of loan when state is not CURRENT (PENDING) to begin with
        and currentDate is after dueDate (testing the AND operator of the checkOverDueDate specification):
    */
    @Test
    void checkOverDueDateUpdatesLoanStateNotCurrent(){
        //Arrange
        ILoan.LoanState expected = ILoan.LoanState.PENDING;
        loan.state = ILoan.LoanState.PENDING;
        loan.dueDate = mockTomorrowDueDate;
        //Act
        loan.checkOverDue(mockDayAfterTomorrowDueDate);
        //Assert
        assertTrue(loan.state == expected);
    }
}
