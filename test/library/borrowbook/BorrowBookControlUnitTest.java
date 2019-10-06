package library.borrowbook;

import library.entities.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Add Mockito Extension to JUNIT5
@ExtendWith(MockitoExtension.class)
public class BorrowBookControlUnitTest {

    //Mockito
    @Mock
    Library mockLibrary;
    @Mock
    BorrowBookUI mockBorrowBookUI;
    @Mock
    Patron mockPatron;
    @Mock
    Book mockBook;
    @Mock
    ILoan mockLoan;

    @InjectMocks
    BorrowBookControl borrowBookControl = new BorrowBookControl(mockLibrary);

    @BeforeEach
    void setUp() {
        borrowBookControl = new BorrowBookControl(mockLibrary);
        //Inject borrowBookUI into borrowBookControl by using borrowBookUI's constructor
        borrowBookControl.setUI(mockBorrowBookUI);
    }

    @AfterEach
    void tearDown() {
    }

    //Test cardswiped with UI and control in SWIPING state, patronId integer gets patron from library using patronId:
    @Test
    void cardSwipedWithUIandControlinSwipingStateGetsPatronFromLibrary(){
        //Arrange
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.SWIPING;
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.SWIPING;
        int patronId = 1;
        when(mockLibrary.getPatronById(patronId)).thenReturn(mockPatron);
        //Act
        borrowBookControl.cardSwiped(patronId);
        //Assert
        verify(mockLibrary).getPatronById(patronId);
    }
    //Test cardswiped with UI and control in SWIPING state, patronId not found:
    @Test
    void cardSwipedWithUIandControlinSwipingStatePatronIDNotFound(){
        //Arrange
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.SWIPING;
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.SWIPING;
        int patronId = 1;
        when(mockLibrary.getPatronById(patronId)).thenReturn(null);
        //Act
        borrowBookControl.cardSwiped(patronId);
        //Assert
        verify(mockBorrowBookUI).display("Invalid patronId");
        assertTrue(borrowBookControl.controlState == IBorrowBookControl.BorrowControlState.SWIPING);
    }
    //Test cardswiped with UI and control in SWIPING state, patronId integer checks borrowing restrictions:
    @Test
    void cardSwipedWithUIandControlinSwipingStateCheckBorrowingRestrictions(){
        //Arrange
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.SWIPING;
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.SWIPING;
        int patronId = 1;
        when(mockLibrary.getPatronById(patronId)).thenReturn(mockPatron);
        when(mockLibrary.patronCanBorrow(mockPatron)).thenReturn(true);
        //Act
        borrowBookControl.cardSwiped(patronId);
        //Assert
        verify(mockLibrary).patronCanBorrow(mockPatron);
    }
    //Test cardswiped with UI and control in SWIPING state, patronId integer when restricted from borrowing control and UI change to restricted:
    @Test
    void cardSwipedWithUIandControlinSwipingStateBorrowingNotAllowedCheckState(){
        //Arrange
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.SWIPING;
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.SWIPING;
        int patronId = 1;
        when(mockLibrary.getPatronById(patronId)).thenReturn(mockPatron);
        when(mockLibrary.patronCanBorrow(mockPatron)).thenReturn(false);
        //Act
        borrowBookControl.cardSwiped(patronId);
        //Assert
        verify(mockLibrary).patronCanBorrow(mockPatron);
        assertTrue(borrowBookControl.controlState == IBorrowBookControl.BorrowControlState.RESTRICTED);
        verify(mockBorrowBookUI).setRestricted();
    }
    //Test cardswiped with ui and control Swiping, patron can borrow, UI State to Scanning and control to Scanning
    @Test
    void cardSwipedWithUIandControlInSwipingStatePatronCanBorrow(){
        //Arrange
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.SWIPING;
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.SWIPING;
        int patronId = 1;
        when(mockLibrary.getPatronById(patronId)).thenReturn(mockPatron);
        when(mockLibrary.patronCanBorrow(mockPatron)).thenReturn(true);
        //Act
        borrowBookControl.cardSwiped(patronId);
        //Assert
        assertTrue(borrowBookControl.controlState == IBorrowBookControl.BorrowControlState.SCANNING);
        verify(mockBorrowBookUI).setScanning();
        assertTrue(borrowBookControl.currentPatron == mockPatron);
    }



    //Test bookScanned with UI State- Scanning and control - Scanning, with reference to patron
    @Test
    void bookScannedWithNoPreconditions(){
        //Arrange
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.RESTRICTED;
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.SWIPING;
        borrowBookControl.currentPatron = mockPatron;
        //Act
        //Assert
        assertThrows(RuntimeException.class, () -> borrowBookControl.bookScanned(123));
    }
    //Test bookScanned with UI State- Scanning and control - Scanning, with reference to patron
    @Test
    void bookScannedWithMetPreconditionsBookNotFound(){
        //Arrange
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.SCANNING;
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.SCANNING;
        borrowBookControl.currentPatron = mockPatron;
        int bookId = 123;
        when(mockLibrary.getBookById(bookId)).thenReturn(mockBook);
        //Act
        borrowBookControl.bookScanned(bookId);
        //Assert
        verify(mockBorrowBookUI).display("Book cannot be borrowed");
    }
    //Test bookScanned with UI State- Scanning and control - Scanning, with reference to patron - check if bookScanned tests if book is available
    @Test
    void bookScannedWithMetPreconditionsCheckIfBookAvailable(){
        //Arrange
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.SCANNING;
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.SCANNING;
        borrowBookControl.currentPatron = mockPatron;
        int bookId = 123;
        when(mockLibrary.getBookById(bookId)).thenReturn(mockBook);
        //Act
        borrowBookControl.bookScanned(bookId);
        //Assert
        verify(mockBook).isAvailable();
    }
    //Test bookscanned with Scanning UI and Control Scanning, no loan created, book not available, no loan created
    @Test
    void bookScannedNoLoanCreatedBookNotAvailable(){
        //Arrange
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.SCANNING;
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.SCANNING;
        borrowBookControl.currentPatron = mockPatron;
        int bookId = 123;
        when(mockBook.isAvailable()).thenReturn(false);
        when(mockLibrary.getBookById(bookId)).thenReturn(mockBook);
        //Act
        borrowBookControl.bookScanned(bookId);
        //Assert
        verify(mockBook).isAvailable();
        assertTrue(borrowBookControl.pendingLoans == null);
    }
    //Test bookscanned with Scanning UI and Control Scanning, book available, loan created
    @Test
    void bookScannedNoLoanCreatedBookAvailable(){
        //Arrange
        borrowBookControl.pendingLoans = new ArrayList<>();
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.SCANNING;
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.SCANNING;
        borrowBookControl.currentPatron = mockPatron;
        int bookId = 123;
        when(mockBook.isAvailable()).thenReturn(true);
        when(mockLibrary.getBookById(bookId)).thenReturn(mockBook);
        when(mockLibrary.issueLoan(mockBook, mockPatron)).thenReturn(mockLoan);
        //Act
        borrowBookControl.bookScanned(bookId);
        //Assert
        verify(mockBook).isAvailable();
        assertTrue(borrowBookControl.pendingLoans.size() == 1);
    }
    //Test bookscanned with Scanning UI and Control Scanning, if patron is under limit, new loan created, remains SCANNING
    @Test
    void bookScannedBookNewLoanCreatedUnderLimit(){
        //Arrange
        borrowBookControl.pendingLoans = new ArrayList<>();
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.SCANNING;
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.SCANNING;
        borrowBookControl.currentPatron = mockPatron;
        int bookId = 123;
        when(mockBook.isAvailable()).thenReturn(true);
        when(mockLibrary.getBookById(bookId)).thenReturn(mockBook);
        when(mockLibrary.issueLoan(mockBook, mockPatron)).thenReturn(mockLoan);
        when(mockLibrary.patronWillReachLoanMax(mockPatron, 1)).thenReturn(false);
        //Act
        borrowBookControl.bookScanned(bookId);
        //Assert
        verify(mockBook).isAvailable();
        assertTrue(borrowBookControl.controlState == IBorrowBookControl.BorrowControlState.SCANNING);
    }
    //Test bookscanned with Scanning UI and Control Scanning, if patron is at limit, new loan created, remains FINALISING
    @Test
    void bookScannedBookNewLoanCreatedUpToLimit(){
        //Arrange
        borrowBookControl.pendingLoans = new ArrayList<>();
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.SCANNING;
        mockBorrowBookUI.uiState = IBorrowBookUI.BorrowUIState.SCANNING;
        borrowBookControl.currentPatron = mockPatron;
        int bookId = 123;
        when(mockBook.isAvailable()).thenReturn(true);
        when(mockLibrary.getBookById(bookId)).thenReturn(mockBook);
        when(mockLibrary.issueLoan(mockBook, mockPatron)).thenReturn(mockLoan);
        when(mockLibrary.patronWillReachLoanMax(mockPatron, 1)).thenReturn(true);
        //Act
        borrowBookControl.bookScanned(bookId);
        //Assert
        verify(mockBook).isAvailable();
        assertTrue(borrowBookControl.controlState == IBorrowBookControl.BorrowControlState.FINALISING);
    }


    //Test commitLoans for preconditions not met, throws error
    @Test
    void commitLoansPreconditionsNotMet(){
        //Arrange
        borrowBookControl.pendingLoans = new ArrayList<>();
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.SCANNING;
        //Act
        //Assert
        assertThrows(RuntimeException.class, () -> borrowBookControl.commitLoans());
    }
    //Test commitLoans for changing of UI and control from FINALISING state to COMPLETED
    @Test
    void commitLoansPreconditionsMetStateToCompleted(){
        //Arrange
        borrowBookControl.pendingLoans = new ArrayList<>();
        borrowBookControl.controlState = IBorrowBookControl.BorrowControlState.FINALISING;
        //Act
        borrowBookControl.commitLoans();
        //Assert
        assertTrue(borrowBookControl.controlState == IBorrowBookControl.BorrowControlState.COMPLETED);
    }

}
