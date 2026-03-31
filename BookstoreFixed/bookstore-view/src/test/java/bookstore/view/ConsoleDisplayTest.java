package bookstore.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bookstore.test.common.BaseTest;

/**
 * Unit tests for ConsoleDisplay class.
 * Tests console output operations including messages, errors, and lists.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
class ConsoleDisplayTest extends BaseTest {

    private ConsoleDisplay display;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        display = new ConsoleDisplay();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        outContent.reset();
    }

    // ==================== showMessage Tests ====================

    @Test
    void testShowMessage_WithValidMessage_PrintsMessage() {
        String message = "Hello World";
        display.showMessage(message);
        
        String output = outContent.toString().trim();
        assertEquals(message, output);
    }

    @Test
    void testShowMessage_WithNull_PrintsNull() {
        display.showMessage(null);
        
        String output = outContent.toString().trim();
        assertEquals("null", output);
    }

    @Test
    void testShowMessage_WithEmptyString_PrintsEmptyLine() {
        display.showMessage("");
        
        String output = outContent.toString();
        assertEquals(System.lineSeparator(), output);
    }

    @Test
    void testShowMessage_WithMultipleMessages_PrintsEachOnNewLine() {
        display.showMessage("First");
        display.showMessage("Second");
        display.showMessage("Third");
        
        String output = outContent.toString();
        assertTrue(output.contains("First" + System.lineSeparator() + "Second"));
        assertTrue(output.contains("Second" + System.lineSeparator() + "Third"));
    }

    // ==================== showError Tests ====================

    @Test
    void testShowError_WithValidError_PrintsErrorWithPrefix() {
        String errorMessage = "Error occurred";
        display.showError(errorMessage);
        
        String output = outContent.toString().trim();
        assertEquals("Error: " + errorMessage, output);
    }

    @Test
    void testShowError_WithNull_PrintsErrorWithNull() {
        display.showError(null);
        
        String output = outContent.toString().trim();
        assertEquals("Error: null", output);
    }

    @Test
    void testShowError_WithEmptyString_PrintsErrorPrefix() {
        display.showError("");
        
        String output = outContent.toString().trim();
        assertEquals("Error:", output);
    }

    // ==================== showList Tests ====================

    @Test
    void testShowList_WithValidList_PrintsListItems() {
        List<String> items = Arrays.asList("Item 1", "Item 2", "Item 3");
        
        display.showList(items);
        
        String output = outContent.toString();
        assertTrue(output.contains("Item 1"));
        assertTrue(output.contains("Item 2"));
        assertTrue(output.contains("Item 3"));
    }

    @Test
    void testShowList_WithEmptyList_PrintsEmptyListMessage() {
        display.showList(Arrays.asList());
        
        String output = outContent.toString().trim();
        assertEquals("Список пуст", output);
    }

    @Test
    void testShowList_WithNull_PrintsEmptyListMessage() {
        display.showList(null);
        
        String output = outContent.toString().trim();
        assertEquals("Список пуст", output);
    }

    @Test
    void testShowList_WithSingleItem_PrintsSingleItem() {
        List<String> items = Arrays.asList("Single Item");
        
        display.showList(items);
        
        String output = outContent.toString().trim();
        assertEquals("Single Item", output);
    }

    @Test
    void testShowList_WithNumbers_PrintsNumbers() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        display.showList(numbers);
        
        String output = outContent.toString();
        assertTrue(output.contains("1"));
        assertTrue(output.contains("2"));
        assertTrue(output.contains("3"));
        assertTrue(output.contains("4"));
        assertTrue(output.contains("5"));
    }

    @Test
    void testShowList_WithMixedObjects_PrintsObjectStrings() {
        List<Object> mixed = Arrays.asList("String", 123, 45.67, true);
        
        display.showList(mixed);
        
        String output = outContent.toString();
        assertTrue(output.contains("String"));
        assertTrue(output.contains("123"));
        assertTrue(output.contains("45.67"));
        assertTrue(output.contains("true"));
    }
}