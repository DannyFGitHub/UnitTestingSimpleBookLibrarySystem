package library.entities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookUnitTest {


    String author = "Author1";
    String title = "Title1";
    String callNo = "A1";
    int bookId = 1;

    Book book = new Book(author, title, callNo, bookId);

    @BeforeEach
    void setUp() {
        book = new Book(author, title, callNo, bookId);
    }

    @AfterEach
    void tearDown() {
    }

    //Test book is available after constructed
    @Test
    void isAvailableAfterConstruction() {
        //Arrange
        boolean expected = true;
        //Act
        boolean actual = book.isAvailable();
        //Asserts
        assertTrue(expected == actual);
    }

    //Test book is available when on loan
    @Test
    void testIsAvailableWhenOnLoan(){
        //Arrange
        book.borrowFromLibrary();
        boolean expected = false;
        //Act
        boolean actual = book.isAvailable();
        //Asserts
        assertTrue(expected == actual);
    }

    //Test book is available when damaged:
    @Test
    void testIsAvailableWHenDamaged(){
        //Arrange
        book.borrowFromLibrary();
        book.returnToLibrary(true);
        boolean expected = false;
        //Act
        boolean actual = book.isAvailable();

        //Asserts
        assertTrue(expected == actual);
    }

    //Test book is available when package is damaged:
    @Test
    void testIsAvailableWHenDamagedPackage(){
        //Arrange
        book.state = IBook.BookState.DAMAGED;
        boolean expected = false;

        //Act
        boolean actual = book.isAvailable();

        //Asserts
        assertTrue(expected == actual);
    }

    //Test book is available after being repaired:
    @Test
    void testIsAvailableAfterRepaired(){
        //Arrange
        book.state = IBook.BookState.DAMAGED;
        boolean expected = true;

        //Act
        book.repair();
        boolean actual = book.isAvailable();

        //Asserts
        assertTrue(expected == actual);
    }


    //Test borrowFromLibrary when book is available does not throw RuntimeException:
    @Test
    void borrowFromLibraryAvailableStateNoThrow(){
        //Arrange
        book.state = IBook.BookState.AVAILABLE;
        //Act
        //Asserts
        assertDoesNotThrow(() -> book.borrowFromLibrary());
    }
    //Test borrowFromLibrary when book is not available throws RuntimeException:
    @Test
    void borrowFromLibraryWhenBookIsNotAvailable(){
        //Arrange
        book.state = IBook.BookState.ON_LOAN;
        //Act
        //Asserts
        assertThrows(RuntimeException.class, () -> book.borrowFromLibrary());
    }
    //Test borrowFromLibrary when book is available that book state changes to ON_LOAN:
    @Test
    void borrowFromLibraryWhenBookIsAvailableStateChanges(){
        //Arrange
        book.state = IBook.BookState.AVAILABLE;
        //Act
        book.borrowFromLibrary();
        //Asserts
        assertTrue(book.state == IBook.BookState.ON_LOAN);
    }

    //Test borrowFromLibrary when book is not available and not ON_LOAN that book state does not change to ON_LOAN:
    @Test
    void borrowFromLibraryWhenBookIsNotAvailableStateChanges(){
        //Arrange
        book.state = IBook.BookState.DAMAGED;
        //Act
        assertThrows(RuntimeException.class, () -> book.borrowFromLibrary());
        //Asserts
        assertTrue(book.state != IBook.BookState.ON_LOAN);
    }
}