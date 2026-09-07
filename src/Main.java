public class Main {
    public static void main(String[] args) {
        InventoryManager inventoryManager = new InventoryManager();
        ConsoleUI consoleUI = new ConsoleUI();
        InputValidator inputValidator = new InputValidator();

        InventoryApp application = new InventoryApp(inventoryManager, consoleUI, inputValidator);
        application.run();
    }
}
