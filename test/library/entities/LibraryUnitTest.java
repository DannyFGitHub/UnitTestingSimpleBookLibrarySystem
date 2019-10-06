package library.entities;

import library.entities.helpers.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.RuntimeErrorException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Add Mockito Extension to JUNIT5
@ExtendWith(MockitoExtension.class)
public class LibraryUnitTest {

    //Mockito
    @Mock ILoan mockILoan;
    @Mock Loan mockLoan;
    @Mock IPatron mockPatron;
    @Mock Patron mockPatron2;
    @Mock IBook mockBook;
    @Mock Book mockBook2;

    @InjectMocks
    Library testlibrary = new Library(new BookHelper(), new PatronHelper(), new LoanHelper());


    @BeforeEach
    void setUp() {
        testlibrary = new Library(new BookHelper(), new PatronHelper(), new LoanHelper());
        mockLoan = new Loan(mockBook2, mockPatron2);
    }

    @AfterEach
    void tearDown() {

    }


    //Test whether can borrow at max loan limit:
    @Test
    void patronCanBorrowAtMaxLoanLimit(){
        //Arrange
        boolean expected = false;
        int patronsCurrentLoanCount = ILibrary.LOAN_LIMIT;
        when(mockPatron.getNumberOfCurrentLoans()).thenReturn(patronsCurrentLoanCount);
        //Act
        boolean actual = testlibrary.patronCanBorrow(mockPatron);
        //Assert
        assertEquals(expected, actual);
    }
    //Test whether can borrow over max loan limit:
    @Test
    void patronCanBorrowOverMaxLoanLimit(){
        //Arrange
        boolean expected = false;
        int patronsCurrentLoanCount = ILibrary.LOAN_LIMIT + 1;
        when(mockPatron.getNumberOfCurrentLoans()).thenReturn(patronsCurrentLoanCount);
        //Act
        boolean actual = testlibrary.patronCanBorrow(mockPatron);
        //Assert
        assertEquals(expected, actual);
    }
    //Test whether patron can borrow under max loan limit:
    @Test
    void patronCanBorrowUnderMaxLoanLimit(){
        //Arrange
        boolean expected = false;
        int patronsCurrentLoanCount = ILibrary.LOAN_LIMIT - 1;
        when(mockPatron.getNumberOfCurrentLoans()).thenReturn(patronsCurrentLoanCount);
        //Act
        boolean actual = testlibrary.patronCanBorrow(mockPatron);
        //Assert
        assertEquals(expected, actual);
    }
    //Test whether patron can borrow with max fines owed:
    @Test
    void patronCanBorrowWithMaxFinesOwed(){
        //Arrange
        boolean expected = false;
        double mockFines = ILibrary.MAX_FINES_OWED;
        when(mockPatron.getFinesPayable()).thenReturn(mockFines);
        //Act
        boolean actual = testlibrary.patronCanBorrow(mockPatron);
        //Assert
        assertEquals(expected, actual);
    }
    //Test whether can borrow with more than max fines owed:
    @Test
    void patronCanBorrowWithMoreThanMaxFinesOwed(){
        //Arrange
        boolean expected = false;
        double mockFines = ILibrary.MAX_FINES_OWED + 1;
        when(mockPatron.getFinesPayable()).thenReturn(mockFines);
        //Act
        boolean actual = testlibrary.patronCanBorrow(mockPatron);
        //Assert
        assertEquals(expected, actual);
    }
    //Test whether patron can borrow with less than max fines owed:
    @Test
    void patronCanBorrowWithLessThanMaxFinesOwed(){
        //Arrange
        boolean expected = true;
        double mockFines = ILibrary.MAX_FINES_OWED - 1;
        when(mockPatron.getFinesPayable()).thenReturn(mockFines);
        //Act
        boolean actual = testlibrary.patronCanBorrow(mockPatron);
        //Assert
        assertEquals(expected, actual);
    }
    //Test whether patron can borrow with overdue loans:
    @Test
    void patronCanBorrowWithOverDueLoans(){
        //Arrange
        boolean expected = false;
        boolean patronHasOverdueLoans = true;
        when(mockPatron.hasOverDueLoans()).thenReturn(patronHasOverdueLoans);
        //Act
        boolean actual = testlibrary.patronCanBorrow(mockPatron);
        //Assert
        assertEquals(expected, actual);
    }
    //Test whether patron can borrow without overdue loans:
    @Test
    void patronCanBorrowWithoutOverDueLoans(){
        //Arrange
        boolean expected = true;
        boolean patronHasOverdueLoans = false;
        when(mockPatron.hasOverDueLoans()).thenReturn(patronHasOverdueLoans);
        //Act
        boolean actual = testlibrary.patronCanBorrow(mockPatron);
        //Assert
        assertEquals(expected, actual);
    }


    //Test issueLoan  returns a loan object with valid book, valid patron, book available and patron can_borrow
    @Test
    void issueLoanReturnsLoanValidPreconditions(){
        //Arrange
        //
        //Act
        Object actual = testlibrary.issueLoan(mockBook, mockPatron2);
        //Assert
        assertTrue(actual instanceof ILoan);
    }
    //Test issueLoan returns a pending loan object with valid book, valid patron, book available and patron can_borrow
    @Test
    void issueLoanReturnsPENDINGLoanValidPreconditions(){
        //Arrange
        //
        //Act
        Loan actual = (Loan) testlibrary.issueLoan(mockBook, mockPatron2);
        //Assert
        assertTrue(actual.state == ILoan.LoanState.PENDING);
    }
    //Test issueLoan with null book only
    @Test
    void issueLoanNullBook(){
        //Arrange
        //
        //Act
        when(mockBook.isAvailable()).thenReturn(true);
        mockPatron2.state = IPatron.PatronState.CAN_BORROW;
        //Assert
        assertThrows(RuntimeException.class, () -> testlibrary.issueLoan(null, mockPatron2));
    }
    //Test issueLoan with null patron only
    @Test
    void issueLoanNullPatron(){
        //Arrange
        //
        //Act
        when(mockBook.isAvailable()).thenReturn(true);
        //Assert
        assertThrows(RuntimeException.class, () -> testlibrary.issueLoan(mockBook, null));
    }
    //Test issueLoan with book not available only, all other met
    @Test
    void issueLoanBookNotAvailable(){
        //Arrange
        //
        //Act
        //Set book to not available:
        when(mockBook.isAvailable()).thenReturn(false);
        mockPatron2.state = IPatron.PatronState.CAN_BORROW;
        //Assert
        assertThrows(RuntimeException.class, () -> testlibrary.issueLoan(mockBook, mockPatron2));
    }
    //Test issueLoan with patron cannot borrow only , all other met
    @Test
    void issueLoanPatronCannotBorrow(){
        //Arrange
        //
        //Act
        //Set patron to restricted state:
        when(mockBook.isAvailable()).thenReturn(true);
        mockPatron2.state = IPatron.PatronState.RESTRICTED;
        //Assert
        assertThrows(RuntimeException.class, () -> testlibrary.issueLoan(mockBook, mockPatron2));
    }
    //Test issueLoan with no preconditions met
    @Test
    void issueLoanWithNoPreconditionsMet(){
        //Arrange
        //
        //Act
        //Assert
        assertThrows(RuntimeException.class, () -> testlibrary.issueLoan(null, null));
    }


    //Test commitLoan with an invalid loan object
    @Test
    void commitLoanInvalidLoanObject(){
        //Arrange
        //
        //Act
        //
        //Assert
        assertThrows(RuntimeException.class, () -> testlibrary.commitLoan(null));
    }
    //Test commitLoan with a valid but not PENDING loan object
    @Test
    void commitLoanReturnsValidPendingLoan(){
        //Arrange
        mockLoan.state = ILoan.LoanState.OVER_DUE;
        //Act
        //
        //Assert
        assertThrows(RuntimeException.class, () ->  testlibrary.commitLoan(mockLoan));
    }
    //Test that Load added to full loan list.
    @Test
    void commitLoanAddedToFullLoanList(){
        //Arrange
        mockLoan.state = ILoan.LoanState.PENDING;
        //Act
        testlibrary.commitLoan(mockLoan);
        //Assert
        assertTrue(testlibrary.loans.get(mockLoan.getId()) != null);
    }
    //Test that Loan added to current loan list
    @Test
    void commitLoanAddedToCurrentLoanList(){
        //Arrange
        mockLoan.state = ILoan.LoanState.PENDING;
        //Act
        testlibrary.commitLoan(mockLoan);
        //Assert
        assertTrue(testlibrary.getCurrentLoansList().indexOf(mockLoan) != -1);
    }
}
