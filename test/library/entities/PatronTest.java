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
class PatronTest {


    String lastName = "lastnametest";
    String firstName = "firstnametest";
    String email = "emailtest";
    long phoneNumber = 123456321;
    int id = 123;

    //Mockito
    @Mock ILoan mockLoan;
    @Mock ILoan mockLoan2;

    @InjectMocks
    Patron patron = new Patron(lastName, firstName, email, phoneNumber, id);

    @BeforeEach
    void setUp() {
        patron = new Patron(lastName, firstName, email, phoneNumber, id);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testHasOverdueNoLoans(){
        //Arrange
        boolean expected = false;
        //Act
        boolean actual = patron.hasOverDueLoans();
        //Assert
        assertEquals(expected, actual);
    }

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
}