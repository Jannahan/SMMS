package test;

import main.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MainTest {

    @Mock
    private Scanner scanner;

    @Test
    void testIsValidEmail_ValidEmail() {
        assertTrue(Main.isValidEmail("test@example.com"));
        assertTrue(Main.isValidEmail("user.name@domain.com"));
    }

    @Test
    void testIsValidEmail_NullEmail() {
        assertFalse(Main.isValidEmail(null));
    }

    @Test
    void testIsValidName_ValidName() {
        assertTrue(Main.isValidName("John"));
        assertTrue(Main.isValidName("Mary Jane"));
        assertTrue(Main.isValidName("Artificial Intelligence"));
    }

    @Test
    void testIsValidName_InvalidNameWithNumbers() {
        assertFalse(Main.isValidName("John123"));
        assertFalse(Main.isValidName("Room 101"));
        assertFalse(Main.isValidName("AI2"));
    }

    @Test
    void testIsValidName_NullName() {
        assertFalse(Main.isValidName(null));
    }

    @Test
    void testGetValidChoice_ValidInput() {
        String input = "5\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        int choice = Main.getValidChoice(scanner);
        assertEquals(5, choice);
    }

    @Test
    void testGetValidChoice_InvalidThenValidInput() {
        String input = "abc\n3\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        int choice = Main.getValidChoice(scanner);
        assertEquals(3, choice);
    }
}