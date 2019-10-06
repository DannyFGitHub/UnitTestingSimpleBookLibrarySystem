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

import static org.junit.jupiter.api.Assertions.assertTrue;

//Add Mockito Extension to JUNIT5
@ExtendWith(MockitoExtension.class)
public class LibraryIntegrationTesting {

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
        testbook = new Book(author, title, callNo, id);
        testpatron = new Patron(lastName, firstName, email, phoneNumber, id);
        testlibrary = new Library(new BookHelper(), new PatronHelper(), new LoanHelper());
        testloan = new Loan(testbook, testpatron);
    }

    @AfterEach
    void tearDown() {

    }

    //Test commitLoan that Loan state is current
    @Test
    void commitLoanLoanStateisCurrent(){
        //Arrange
        ILoan.LoanState expected = ILoan.LoanState.CURRENT;
        testloan.state = ILoan.LoanState.PENDING;
        //Act
        testlibrary.commitLoan(testloan);
        //Assert
        assertTrue(testloan.state == expected);
    }
    //Test commitLoan that loan added to patron's borrowing record,
    @Test
    void commitLoanAddedToMemberBorrowingRecord(){
        //Arrange
        testloan.state = ILoan.LoanState.PENDING;
        //Act
        testlibrary.commitLoan(testloan);
        //Assert
        assertTrue(testloan.getPatron().getLoans().indexOf(testloan) != -1);
    }
    //Test commitLoan that Book state is ON_LOAN
    @Test
    void commitLoanBookStateisOnLoan(){
        //Arrange
        boolean expected = true;
        testloan.state = ILoan.LoanState.PENDING;
        //Act
        testlibrary.commitLoan(testloan);
        //Assert
        //Not checking book state rather calling function isOnLoan as integration testing of the book class with the Library class when commiting loan.
        assertTrue(testloan.getBook().isOnLoan() == expected);
    }
}
