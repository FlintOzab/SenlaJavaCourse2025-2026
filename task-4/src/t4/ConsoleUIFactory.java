package t4;

class ConsoleUIFactory implements UIFactory {
    @Override
    public Menu createMenu() {
        return ConsoleMenu.getInstance();
    }
    
    @Override
    public Display createDisplay() {
        return ConsoleDisplay.getInstance();
    }
    
    @Override
    public Input createInput() {
        return ConsoleInput.getInstance();
    }
}