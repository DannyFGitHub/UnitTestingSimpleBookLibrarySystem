package library.entities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    Book book;
    String author = "Author1";
    String title = "Title1";
    String callNo = "A1";
    int bookId = 1;

    @BeforeEach
    void setUp() {
        book = new Book(author, title, callNo, bookId);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void isAvailableAfterConstruction() {
        //Arrange
        boolean expected = true;
        //Act
        boolean actual = book.isAvailable();
        //Asserts
        assertTrue(expected == actual);
    }

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
}