package library.borrowbook;

import library.entities.*;
import library.entities.helpers.BookHelper;
import library.entities.helpers.LoanHelper;
import library.entities.helpers.PatronHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BorrowBookIntegrationTesting {

    //Patron Stub
    String lastName = "lastnametest";
    String firstName = "firstnametest";
    String email = "emailtest";
    long phoneNumber = 123456321;
    int id = 123;
    String author = "Ben";
    String title = "bookTitle";
    String callNo = "12345";


    Library testlibrary = new Library(new BookHelper(), new PatronHelper(), new LoanHelper());

    Patron testpatron = new Patron(lastName, firstName, email, phoneNumber, id);

    Book testbook = new Book(author, title, callNo, id);

    Loan testloan = new Loan(testbook, testpatron);

    BorrowBookControl testBorrowBookControl = new BorrowBookControl(testlibrary);

    BorrowBookUI mockBorrowBookUI = new BorrowBookUI(testBorrowBookControl);


    @BeforeEach
    void setUp() {
        testbook = new Book(author, title, callNo, id);
        testpatron = new Patron(lastName, firstName, email, phoneNumber, id);
        testlibrary = new Library(new BookHelper(), new PatronHelper(), new LoanHelper());
        testloan = new Loan(testbook, testpatron);
        testBorrowBookControl = new BorrowBookControl(testlibrary);
        mockBorrowBookUI = new BorrowBookUI(testBorrowBookControl);
    }


    @AfterEach
    void tearDown() {
    }

    //Test commitLoans loans added to library current loan list
    @Test
    void commitLoansLoansAddedToLibraryCurrentLoanList(){
        //Arrange
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.FINALISING;
        testBorrowBookControl.controlState = IBorrowBookControl.BorrowControlState.FINALISING;
        testBorrowBookControl.currentPatron = testpatron;
        testBorrowBookControl.pendingLoans = new ArrayList<>();
        testBorrowBookControl.pendingLoans.add(testloan);
        //Act
        testBorrowBookControl.commitLoans();
        //Assert
        assertTrue(testlibrary.getCurrentLoansList().contains(testloan));
    }
    //Test commitLoans loans are added to library full loan list
    @Test
    void commitLoansLoansAddedToLibraryFullLoanList(){
        //Arrange
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.FINALISING;
        testBorrowBookControl.controlState = IBorrowBookControl.BorrowControlState.FINALISING;
        testBorrowBookControl.currentPatron = testpatron;
        testBorrowBookControl.pendingLoans = new ArrayList<>();
        testBorrowBookControl.pendingLoans.add(testloan);
        //Act
        testBorrowBookControl.commitLoans();
        //Assert
        assertTrue(testlibrary.loans.containsValue(testloan));
    }
    //Test commitLoans loans are added to patron borrowing record
    @Test
    void commitLoansLoansAddedToPatronBorrowingRecord(){
        //Arrange
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.FINALISING;
        testBorrowBookControl.controlState = IBorrowBookControl.BorrowControlState.FINALISING;
        testBorrowBookControl.currentPatron = testpatron;
        testBorrowBookControl.pendingLoans = new ArrayList<>();
        testBorrowBookControl.pendingLoans.add(testloan);
        //Act
        testBorrowBookControl.commitLoans();
        //Assert
        assertTrue(testpatron.getLoans().contains(testloan));
    }
    //Test commitLoans loan state is current
    @Test
    void commitLoansLoansStateIsCurrent(){
        //Arrange
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.FINALISING;
        testBorrowBookControl.controlState = IBorrowBookControl.BorrowControlState.FINALISING;
        testBorrowBookControl.currentPatron = testpatron;
        testBorrowBookControl.pendingLoans = new ArrayList<>();
        testBorrowBookControl.pendingLoans.add(testloan);
        //Act
        testBorrowBookControl.commitLoans();
        //Assert
        assertTrue(testloan.state.equals(ILoan.LoanState.CURRENT));
    }
    //Test commitLoans book state is ON_LOAN
    @Test
    void commitLoansBookStateIsOnLoan(){
        //Arrange
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.FINALISING;
        testBorrowBookControl.controlState = IBorrowBookControl.BorrowControlState.FINALISING;
        testBorrowBookControl.currentPatron = testpatron;
        testBorrowBookControl.pendingLoans = new ArrayList<>();
        testBorrowBookControl.pendingLoans.add(testloan);
        //Act
        testBorrowBookControl.commitLoans();
        //Assert
        assertTrue(testbook.isOnLoan());
    }
}
