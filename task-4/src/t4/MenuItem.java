package t4;

public class MenuItem {
    private final String name;
    private final Command command;
    
    public MenuItem(String name, Command command) {
        this.name = name;
        this.command = command;
    }
    
    public String getName() {
        return name;
    }
    
    public void execute() {
        if (command != null) {
            command.execute();
        }
    }
    
    public static MenuItem of(String name, Command command) {
        return new MenuItem(name, command);
    }
}
