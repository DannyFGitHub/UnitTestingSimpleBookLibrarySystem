package library.entities;

import library.entities.helpers.BookHelper;
import library.entities.helpers.LoanHelper;
import library.entities.helpers.PatronHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;

//Add Mockito Extension to JUNIT5
@ExtendWith(MockitoExtension.class)
public class LoanIntegrationTesting {

    Date mockTodayDate = new Date();
    Date mockTomorrowDueDate = new Date(mockTodayDate.getTime() + (1000 * 60 * 60 * 24));
    Date mockDayAfterTomorrowDueDate = new Date(mockTomorrowDueDate.getTime() + (1000 * 60 * 60 * 24));


    //Patron Stub
    String lastName = "lastnametest";
    String firstName = "firstnametest";
    String email = "emailtest";
    long phoneNumber = 123456321;
    int id = 123;
    String author = "Ben";
    String title = "bookTitle";
    String callNo = "12345";

    @InjectMocks
    Library testlibrary = new Library(new BookHelper(), new PatronHelper(), new LoanHelper());

    @InjectMocks
    Patron testpatron = new Patron(lastName, firstName, email, phoneNumber, id);

    @InjectMocks
    Book testbook = new Book(author, title, callNo, id);

    @InjectMocks
    Loan testloan = new Loan(testbook, testpatron);



    @BeforeEach
    void setUp() {
        mockTodayDate = new Date();
        mockTomorrowDueDate = new Date(mockTodayDate.getTime() + (1000 * 60 * 60 * 24));
        mockDayAfterTomorrowDueDate = new Date(mockTomorrowDueDate.getTime() + (1000 * 60 * 60 * 24));

        testlibrary = new Library(new BookHelper(), new PatronHelper(), new LoanHelper());

        testpatron = new Patron(lastName, firstName, email, phoneNumber, id);
        testbook = new Book(author, title, callNo, id);
        testloan = new Loan(testbook, testpatron);
    }

    @AfterEach
    void tearDown() {
    }

    /*
        Test whether commit method: Loan was added to patrons current borrowing record:
    */
    @Test
    void commitCheckLoanAddedToPatronsBorrowingRecord(){
        //Arrange
        int loanId = 123;
        testloan.state = ILoan.LoanState.PENDING;
        //Act
        testloan.commit(loanId, mockTomorrowDueDate);
        //Assert
        assertTrue(testloan.getPatron().getLoans().indexOf(testloan) != -1);
    }
    /*
        Test whether commit method: book state updated to ON_LOAN:
    */
    @Test
    void commitBookStateUpdatedtoOnLoan(){
        //Arrange
        int loanId = 123;
        boolean expected = true;
        testloan.state = ILoan.LoanState.PENDING;
        //Act
        testloan.commit(loanId, mockTomorrowDueDate);
        //Assert
        assertTrue(testloan.getBook().isOnLoan() == expected);
    }

}
