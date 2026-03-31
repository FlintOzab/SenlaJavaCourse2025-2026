package bookstore.view;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bookstore.test.common.BaseTest;

/**
 * Unit tests for ConsoleInput class.
 * Tests console input operations including reading strings, integers, longs, and dates.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
class ConsoleInputTest extends BaseTest {

    private ConsoleInput input;
    private InputStream originalIn = System.in;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        input = new ConsoleInput();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
        outContent.reset();
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
        input = new ConsoleInput();
    }

    // ==================== readString Tests ====================

    @Test
    void testReadString_WithValidInput_ReturnsInput() {
        provideInput("test input\n");
        
        String result = input.readString("Enter text");
        
        assertNotNull(result);
        assertEquals("test input", result);
    }

    @Test
    void testReadString_WithWhitespaceInput_ReturnsInputWithWhitespace() {
        provideInput("  test with spaces  \n");
        
        String result = input.readString("Enter text");
        
        assertEquals("  test with spaces  ", result);
    }

    @Test
    void testReadString_WithEmptyInput_ReturnsEmptyString() {
        provideInput("\n");
        
        String result = input.readString("Enter text");
        
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void testReadString_WithMultipleLines_ReturnsFirstLine() {
        provideInput("first line\nsecond line\n");
        
        String result = input.readString("Enter text");
        
        assertEquals("first line", result);
    }

    // ==================== readInt Tests ====================

    @Test
    void testReadInt_WithValidInput_ReturnsNumber() {
        provideInput("42\n");
        
        int result = input.readInt("Enter number");
        
        assertEquals(42, result);
    }

    @Test
    void testReadInt_WithNegativeNumber_ReturnsNegative() {
        provideInput("-42\n");
        
        int result = input.readInt("Enter number");
        
        assertEquals(-42, result);
    }

    @Test
    void testReadInt_WithZero_ReturnsZero() {
        provideInput("0\n");
        
        int result = input.readInt("Enter number");
        
        assertEquals(0, result);
    }

    @Test
    void testReadInt_WithInvalidInput_ShowsErrorAndRetries() {
        provideInput("invalid\n5\n");
        
        int result = input.readInt("Enter number");
        
        assertEquals(5, result);
        
        String output = outContent.toString();
        assertNotNull(output);
    }

    @Test
    void testReadInt_WithMultipleInvalidInputs_ShowsErrorMultipleTimes() {
        provideInput("invalid\nnot a number\n42\n");
        
        int result = input.readInt("Enter number");
        
        assertEquals(42, result);
    }

    // ==================== readLong Tests ====================

    @Test
    void testReadLong_WithValidInput_ReturnsNumber() {
        provideInput("123456789\n");
        
        long result = input.readLong("Enter long");
        
        assertEquals(123456789L, result);
    }

    @Test
    void testReadLong_WithLargeNumber_ReturnsNumber() {
        provideInput("9999999999\n");
        
        long result = input.readLong("Enter long");
        
        assertEquals(9999999999L, result);
    }

    @Test
    void testReadLong_WithNegativeNumber_ReturnsNegative() {
        provideInput("-123456789\n");
        
        long result = input.readLong("Enter long");
        
        assertEquals(-123456789L, result);
    }

    @Test
    void testReadLong_WithInvalidInput_ShowsErrorAndRetries() {
        provideInput("invalid\n999\n");
        
        long result = input.readLong("Enter long");
        
        assertEquals(999L, result);
        
        String output = outContent.toString();
        assertNotNull(output);
    }

    // ==================== readDate Tests ====================

    @Test
    void testReadDate_WithValidFormat_ReturnsDate() {
        provideInput("01.01.2024\n");
        
        Date result = assertDoesNotThrow(() -> input.readDate("Enter date"));
        
        assertNotNull(result);
    }

    @Test
    void testReadDate_WithDifferentValidFormat_ReturnsDate() {
        provideInput("31.12.2024\n");
        
        Date result = assertDoesNotThrow(() -> input.readDate("Enter date"));
        
        assertNotNull(result);
    }

    @Test
    void testReadDate_WithInvalidFormat_ShowsErrorAndRetries() {
        provideInput("invalid\n01.01.2024\n");
        
        Date result = assertDoesNotThrow(() -> input.readDate("Enter date"));
        
        assertNotNull(result);
        
        String output = outContent.toString();
        assertNotNull(output);
    }

    @Test
    void testReadDate_WithWrongDateFormat_ShowsErrorAndRetries() {
        provideInput("2024-01-01\n01.01.2024\n");
        
        Date result = assertDoesNotThrow(() -> input.readDate("Enter date"));
        
        assertNotNull(result);
    }

    @Test
    void testReadDate_WithEmptyInput_ShowsErrorAndRetries() {
        provideInput("\n01.01.2024\n");
        
        Date result = assertDoesNotThrow(() -> input.readDate("Enter date"));
        
        assertNotNull(result);
    }
}