package bookstore.view;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bookstore.test.common.BaseTest;

/**
 * Unit tests for MenuBuilder class.
 * Tests menu building functionality including adding items and creating menus.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
class MenuBuilderTest extends BaseTest {

    private MenuBuilder builder;
    private boolean actionExecuted;

    @BeforeEach
    public void setUp() {
        actionExecuted = false;
        builder = MenuBuilder.builder("Test Menu");
    }

    // ==================== builder Tests ====================

    @Test
    void testBuilder_WithValidTitle_CreatesBuilder() {
        assertNotNull(builder);
    }

    @Test
    void testBuilder_WithNullTitle_DoesNotThrow() {
        MenuBuilder nullTitleBuilder = assertDoesNotThrow(() -> MenuBuilder.builder(null));
        assertNotNull(nullTitleBuilder);
    }

    @Test
    void testBuilder_WithEmptyTitle_DoesNotThrow() {
        MenuBuilder emptyTitleBuilder = assertDoesNotThrow(() -> MenuBuilder.builder(""));
        assertNotNull(emptyTitleBuilder);
        
        Menu menu = emptyTitleBuilder.build();
        assertNotNull(menu);
        assertEquals("", menu.getTitle());
    }

    // ==================== addItem Tests ====================

    @Test
    void testAddItem_WithValidItem_AddsToMenu() {
        builder.addItem("Test Action", () -> actionExecuted = true);
        
        Menu menu = builder.build();
        
        assertEquals(1, menu.getItems().size());
        assertEquals("Test Action", menu.getItems().get(0).getName());
    }

    @Test
    void testAddItem_WithNullName_AddsItemWithNullName() {
        assertDoesNotThrow(() -> builder.addItem(null, () -> {}));
        
        Menu menu = builder.build();
        assertNotNull(menu);
        assertEquals(1, menu.getItems().size());
        assertEquals(null, menu.getItems().get(0).getName());
    }

    @Test
    void testAddItem_WithNullAction_AddsItemWithNullCommand() {
        assertDoesNotThrow(() -> builder.addItem("Action", null));
        
        Menu menu = builder.build();
        assertNotNull(menu);
        assertEquals(1, menu.getItems().size());
        assertEquals("Action", menu.getItems().get(0).getName());
        
        assertDoesNotThrow(() -> menu.getItems().get(0).execute());
    }

    @Test
    void testAddItem_WithBothNull_AddsItemWithNullValues() {
        assertDoesNotThrow(() -> builder.addItem(null, null));
        
        Menu menu = builder.build();
        assertNotNull(menu);
        assertEquals(1, menu.getItems().size());
        assertEquals(null, menu.getItems().get(0).getName());
        
        assertDoesNotThrow(() -> menu.getItems().get(0).execute());
    }

    @Test
    void testAddItem_MultipleItems_AddsAll() {
        builder.addItem("Action 1", () -> {});
        builder.addItem("Action 2", () -> {});
        builder.addItem("Action 3", () -> {});
        
        Menu menu = builder.build();
        
        assertEquals(3, menu.getItems().size());
        assertEquals("Action 1", menu.getItems().get(0).getName());
        assertEquals("Action 2", menu.getItems().get(1).getName());
        assertEquals("Action 3", menu.getItems().get(2).getName());
    }

    @Test
    void testAddItem_WithSameNameMultipleTimes_AddsAll() {
        builder.addItem("Same Action", () -> {});
        builder.addItem("Same Action", () -> {});
        builder.addItem("Same Action", () -> {});
        
        Menu menu = builder.build();
        
        assertEquals(3, menu.getItems().size());
        assertEquals("Same Action", menu.getItems().get(0).getName());
        assertEquals("Same Action", menu.getItems().get(1).getName());
        assertEquals("Same Action", menu.getItems().get(2).getName());
    }

    // ==================== addSubmenu Tests ====================

    @Test
    void testAddSubmenu_WithValidSubmenu_AddsToMenu() {
        Menu submenu = MenuBuilder.builder("Submenu").build();
        
        builder.addSubmenu("Submenu Item", submenu);
        
        Menu menu = builder.build();
        assertEquals(1, menu.getItems().size());
        assertEquals("Submenu Item", menu.getItems().get(0).getName());
    }

    @Test
    void testAddSubmenu_WithNullName_AddsItemWithNullName() {
        Menu submenu = MenuBuilder.builder("Submenu").build();
        
        assertDoesNotThrow(() -> builder.addSubmenu(null, submenu));
        
        Menu menu = builder.build();
        assertEquals(1, menu.getItems().size());
        assertEquals(null, menu.getItems().get(0).getName());
    }

    @Test
    void testAddSubmenu_WithNullMenu_AddsItemWithNullCommand() {
        assertDoesNotThrow(() -> builder.addSubmenu("Submenu Item", null));
        
        Menu menu = builder.build();
        assertEquals(1, menu.getItems().size());
        assertEquals("Submenu Item", menu.getItems().get(0).getName());
        assertDoesNotThrow(() -> menu.getItems().get(0).execute());
    }

    @Test
    void testAddSubmenu_WithBothNull_AddsItemWithNullValues() {
        assertDoesNotThrow(() -> builder.addSubmenu(null, null));
        
        Menu menu = builder.build();
        assertEquals(1, menu.getItems().size());
        assertEquals(null, menu.getItems().get(0).getName());
    }

    // ==================== build Tests ====================

    @Test
    void testBuild_ReturnsMenuWithTitle() {
        Menu menu = builder.build();
        
        assertNotNull(menu);
        assertEquals("Test Menu", menu.getTitle());
        assertNotNull(menu.getItems());
        assertEquals(0, menu.getItems().size());
    }

    @Test
    void testBuild_WithoutItems_ReturnsEmptyMenu() {
        Menu menu = builder.build();
        
        assertNotNull(menu);
        assertEquals(0, menu.getItems().size());
    }

    @Test
    void testBuild_AfterAddingItems_ReturnsMenuWithItems() {
        builder.addItem("Item 1", () -> {});
        builder.addItem("Item 2", () -> {});
        
        Menu menu = builder.build();
        
        assertNotNull(menu);
        assertEquals(2, menu.getItems().size());
        assertEquals("Item 1", menu.getItems().get(0).getName());
        assertEquals("Item 2", menu.getItems().get(1).getName());
    }

    @Test
    void testBuild_WithMixedItems_ReturnsMenuWithAllItems() {
        Menu submenu = MenuBuilder.builder("Submenu").build();
        
        builder.addItem("Action 1", () -> {});
        builder.addSubmenu("Submenu 1", submenu);
        builder.addItem("Action 2", () -> {});
        
        Menu menu = builder.build();
        
        assertEquals(3, menu.getItems().size());
        assertEquals("Action 1", menu.getItems().get(0).getName());
        assertEquals("Submenu 1", menu.getItems().get(1).getName());
        assertEquals("Action 2", menu.getItems().get(2).getName());
    }

    // ==================== Command Execution Tests ====================

    @Test
    void testMenuItem_Execute_ExecutesCommand() {
        final boolean[] executed = {false};
        Command command = () -> executed[0] = true;
        
        builder.addItem("Test", command);
        Menu menu = builder.build();
        
        menu.getItems().get(0).execute();
        
        assertEquals(true, executed[0]);
    }

    @Test
    void testMenuItem_WithNullCommand_ExecuteDoesNotThrow() {
        builder.addItem("Test", null);
        Menu menu = builder.build();
        
        assertDoesNotThrow(() -> menu.getItems().get(0).execute());
    }

    @Test
    void testMenuItem_WithCommandThatThrowsException_ThrowsException() {
        Command throwingCommand = () -> {
            throw new RuntimeException("Test exception");
        };
        
        builder.addItem("Test", throwingCommand);
        Menu menu = builder.build();
        
        assertThrows(RuntimeException.class, () -> menu.getItems().get(0).execute());
    }
}