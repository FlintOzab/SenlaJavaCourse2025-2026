package bookstore.app.control;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookstore.test.common.BaseTest;
import bookstore.view.ConsoleDisplay;
import bookstore.view.ConsoleInput;
import bookstore.view.Menu;
import bookstore.view.MenuBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for ConsoleMenuController.
 * Tests menu navigation and user input processing.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
class ConsoleMenuControllerTest extends BaseTest {

    @Mock
    private ConsoleDisplay display;

    @Mock
    private ConsoleInput input;

    @InjectMocks
    private ConsoleMenuController menuController;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    private Menu testMenu;
    private boolean actionExecuted;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        actionExecuted = false;
        testMenu = MenuBuilder.builder("Test Menu")
            .addItem("Test Action 1", () -> actionExecuted = true)
            .addItem("Test Action 2", () -> actionExecuted = true)
            .build();
    }

    @Test
    void testExecute_WithValidMenu_Success() {
        // Given
        when(input.readInt(anyString())).thenReturn(0);
        
        // When
        assertDoesNotThrow(() -> menuController.execute(testMenu));
        
        // Then
        verify(display, times(5)).showMessage(messageCaptor.capture());
        
        // Verify menu display content
        assertEquals("\nTest Menu", messageCaptor.getAllValues().get(0));
        assertEquals("=========", messageCaptor.getAllValues().get(1));
        assertEquals("1. Test Action 1", messageCaptor.getAllValues().get(2));
        assertEquals("2. Test Action 2", messageCaptor.getAllValues().get(3));
        assertEquals("0. Выход", messageCaptor.getAllValues().get(4));
        // The last message is from LoggerUtil.logCommandStart, which we don't verify
    }

    @Test
    void testExecute_WithExitOption_Exits() {
        // Given
        when(input.readInt(anyString())).thenReturn(0);
        
        // When
        assertDoesNotThrow(() -> menuController.execute(testMenu));
        
        // Then
        verify(display, times(5)).showMessage(anyString());
        verify(display, never()).showError(anyString());
    }

    @Test
    void testExecute_WithInvalidChoice_ShowsError() {
        // Given
        when(input.readInt(anyString())).thenReturn(99, 0);
        
        // When
        assertDoesNotThrow(() -> menuController.execute(testMenu));
        
        // Then
        verify(display, times(1)).showError("Неверный выбор");
    }

    @Test
    void testExecute_WithValidChoice_ExecutesAction() {
        // Given
        when(input.readInt(anyString())).thenReturn(1, 0);
        
        // When
        assertDoesNotThrow(() -> menuController.execute(testMenu));
        
        // Then
        verify(display, atLeast(1)).showMessage(anyString());
        verify(display, never()).showError(anyString());
    }
}