import java.util.List;

public class InventoryApp {
    private final InventoryManager inventoryManager;
    private final ConsoleUI ui;
    private final InputValidator validator;

    public InventoryApp(InventoryManager inventoryManager, ConsoleUI ui, InputValidator validator) {
        this.inventoryManager = inventoryManager;
        this.ui = ui;
        this.validator = validator;
    }

    public void run() {
        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = promptMenuChoice("Select an option: ", 1, 9,
                    "Invalid option. Enter a number from 1 to 9.");

            switch (choice) {
                case 1:
                    handleAddItem();
                    break;
                case 2:
                    handleUpdateItem();
                    break;
                case 3:
                    handleRemoveItem();
                    break;
                case 4:
                    handleDisplayByCategory();
                    break;
                case 5:
                    handleDisplayAllItems();
                    break;
                case 6:
                    handleSearchItem();
                    break;
                case 7:
                    handleSortItems();
                    break;
                case 8:
                    handleLowStockItems();
                    break;
                case 9:
                    running = false;
                    ui.showMessage("Thank you for using the Inventory Management System.");
                    break;
                default:
                    throw new IllegalStateException("Unexpected menu choice.");
            }
        }
    }

    private void handleAddItem() {
        boolean addAnother = true;
        while (addAnother) {
            ui.showMessage("\nAdd Item");

            Category category = promptCategory("cancel");
            if (category == null) {
                return;
            }

            String id;
            while (true) {
                id = promptScopedItemId(category);
                if (id == null) {
                    return;
                }
                if (inventoryManager.findItemById(id) == null) {
                    break;
                }
                ui.showMessage("Item ID " + id + " is already in use.");
            }

            String name = promptName();
            while (inventoryManager.hasDuplicateName(name)) {
                ui.showMenu("An item with this name already exists.",
                        "1 - Continue Anyway",
                        "2 - Enter a Different Name",
                        "0 - Cancel");
                int duplicateChoice = promptMenuChoice("Select an option: ", 0, 2,
                        "Invalid option. Enter 1 to continue, 2 to enter a different name, "
                                + "or 0 to cancel.");
                if (duplicateChoice == 0) {
                    return;
                }
                if (duplicateChoice == 1) {
                    break;
                }
                name = promptName();
            }

            int quantity = promptQuantity("Enter quantity: ");
            double price = promptPrice("Enter price: ");

            Item addedItem = inventoryManager.addItem(category, id, name, quantity, price);
            ui.showMessage("Item added successfully!");
            ui.showItemDetails(addedItem);
            addAnother = askRepeat("Add another item?");
        }
    }

    private void handleUpdateItem() {
        boolean updateAnother = true;
        while (updateAnother) {
            ui.showMessage("\nUpdate Item");
            String id = promptFullItemId("cancel");
            if (id == null) {
                return;
            }

            Item item = inventoryManager.findItemById(id);
            if (item == null) {
                ui.showMessage("Item not found!");
                updateAnother = askRepeat("Try another item ID?");
                continue;
            }

            ui.showItemDetails(item);
            ui.showMenu("What would you like to update?",
                    "1 - Quantity",
                    "2 - Price",
                    "0 - Cancel");
            int updateChoice = promptMenuChoice("Select an option: ", 0, 2,
                    "Invalid option. Enter 1 for Quantity, 2 for Price, or 0 to cancel.");
            if (updateChoice == 0) {
                return;
            }

            if (updateChoice == 1) {
                updateQuantity(item);
            } else {
                updatePrice(item);
            }
            updateAnother = askRepeat("Update another item?");
        }
    }

    private void updateQuantity(Item item) {
        ui.showMessage("Current quantity: " + item.getQuantity());
        while (true) {
            int newQuantity = promptQuantity("Enter new quantity: ");
            if (newQuantity == item.getQuantity()) {
                ui.showMessage("The new quantity is the same as the current quantity.");
                ui.showMessage("Enter a different quantity.");
                continue;
            }

            int oldQuantity = item.getQuantity();
            item.updateQuantity(newQuantity);
            ui.showMessage("Quantity of Item " + item.getName() + " is updated from "
                    + oldQuantity + " to " + newQuantity);
            return;
        }
    }

    private void updatePrice(Item item) {
        ui.showMessage("Current price: " + ui.formatPrice(item.getPrice()));
        while (true) {
            double newPrice = promptPrice("Enter new price: ");
            if (Double.compare(newPrice, item.getPrice()) == 0) {
                ui.showMessage("The new price is the same as the current price.");
                ui.showMessage("Enter a different price.");
                continue;
            }

            double oldPrice = item.getPrice();
            item.updatePrice(newPrice);
            ui.showMessage("Price of Item " + item.getName() + " is updated from "
                    + ui.formatPrice(oldPrice) + " to " + ui.formatPrice(newPrice));
            return;
        }
    }

    private void handleRemoveItem() {
        boolean removeAnother = true;
        while (removeAnother) {
            ui.showMessage("\nRemove Item");
            String id = promptFullItemId("cancel");
            if (id == null) {
                return;
            }

            Item item = inventoryManager.findItemById(id);
            if (item == null) {
                ui.showMessage("Item not found!");
                removeAnother = askRepeat("Try another item ID?");
                continue;
            }

            ui.showItemDetails(item);
            ui.showMenu("Remove this item?",
                    "1 - Yes",
                    "2 - No",
                    "0 - Cancel");
            int confirmation = promptMenuChoice("Select an option: ", 0, 2,
                    "Invalid option. Enter 1 to remove the item, 2 to keep it, or 0 to cancel.");
            if (confirmation == 0) {
                return;
            }
            if (confirmation == 1) {
                inventoryManager.removeItem(item);
                ui.showMessage("Item " + item.getName() + " has been removed from the inventory");
            } else {
                ui.showMessage("Item was not removed.");
            }
            removeAnother = askRepeat("Remove another item?");
        }
    }

    private void handleDisplayByCategory() {
        ui.showMessage("\nDisplay Items by Category");
        Category category = promptCategory("go back");
        if (category == null) {
            return;
        }

        List<Item> items = inventoryManager.getItemsByCategory(category);
        if (items.isEmpty()) {
            ui.showMessage("No " + category.getDisplayName() + " items found.");
        } else {
            ui.showItemsTable(items, false);
        }
        ui.pause();
    }

    private void handleDisplayAllItems() {
        List<Item> items = inventoryManager.getAllItems();
        if (items.isEmpty()) {
            ui.showMessage("Inventory is empty.");
        } else {
            ui.showItemsTable(items, true);
        }
        ui.pause();
    }

    private void handleSearchItem() {
        boolean searchAnother = true;
        while (searchAnother) {
            ui.showMessage("\nSearch Item");
            String id = promptFullItemId("return to the Main Menu");
            if (id == null) {
                return;
            }

            Item item = inventoryManager.findItemById(id);
            if (item == null) {
                ui.showMessage("Item not found!");
            } else {
                ui.showItemDetails(item);
            }
            searchAnother = askRepeat("Search another item?");
        }
    }

    private void handleSortItems() {
        if (inventoryManager.getAllItems().isEmpty()) {
            ui.showMessage("Inventory is empty.");
            ui.pause();
            return;
        }

        ui.showMenu("Sort Items",
                "1 - Quantity",
                "2 - Price",
                "0 - Back");
        int sortField = promptMenuChoice("Select a sort field: ", 0, 2,
                "Invalid option. Enter 1 for Quantity, 2 for Price, or 0 to go back.");
        if (sortField == 0) {
            return;
        }

        ui.showMenu("Sort Direction",
                "1 - Ascending",
                "2 - Descending",
                "0 - Back");
        int direction = promptMenuChoice("Select a direction: ", 0, 2,
                "Invalid option. Enter 1 for Ascending, 2 for Descending, or 0 to go back.");
        if (direction == 0) {
            return;
        }

        List<Item> sortedItems = inventoryManager.getSortedItems(sortField, direction == 1);
        ui.showItemsTable(sortedItems, true);
        ui.pause();
    }

    private void handleLowStockItems() {
        List<Item> items = inventoryManager.getLowStockItems();
        if (items.isEmpty()) {
            ui.showMessage("No low stock items found.");
        } else {
            ui.showItemsTable(items, true);
        }
        ui.pause();
    }

    private void showMainMenu() {
        ui.showMenu("Menu",
                "1 - Add Item",
                "2 - Update Item",
                "3 - Remove Item",
                "4 - Display Items by Category",
                "5 - Display All Items",
                "6 - Search Item",
                "7 - Sort Items",
                "8 - Display Low Stock Items",
                "9 - Exit");
    }

    private int promptMenuChoice(String prompt, int minimum, int maximum, String errorMessage) {
        while (true) {
            String raw = ui.readLine(prompt);
            try {
                return validator.parseMenuChoice(raw, minimum, maximum);
            } catch (IllegalArgumentException exception) {
                ui.showMessage(errorMessage);
            }
        }
    }

    private Category promptCategory(String zeroAction) {
        while (true) {
            String raw = ui.readLine("Enter category (Clothing, Electronics, Entertainment), or 0 to "
                    + zeroAction + ": ");
            if (isCancelCommand(raw)) {
                return null;
            }

            Category category = Category.fromInput(raw);
            if (category != null) {
                return category;
            }
            ui.showMessage("Category " + raw.trim() + " does not exist!");
        }
    }

    private String promptFullItemId(String zeroAction) {
        while (true) {
            String raw = ui.readLine("Enter Item ID, or 0 to " + zeroAction + ": ");
            if (isCancelCommand(raw)) {
                return null;
            }
            try {
                return validator.normalizeFullItemId(raw);
            } catch (IllegalArgumentException exception) {
                ui.showMessage(exception.getMessage());
            }
        }
    }

    private String promptScopedItemId(Category category) {
        while (true) {
            ui.showMessage("Enter " + category.getDisplayName() + " Item Number, or 0 to cancel");
            String raw = ui.readLine(category.getPrefix() + "-[____]: ");
            if (isCancelCommand(raw)) {
                return null;
            }
            try {
                return validator.normalizeScopedItemId(raw, category);
            } catch (IllegalArgumentException exception) {
                ui.showMessage(exception.getMessage());
            }
        }
    }

    private String promptName() {
        while (true) {
            String raw = ui.readLine("Enter item name: ");
            try {
                return validator.normalizeName(raw);
            } catch (IllegalArgumentException exception) {
                ui.showMessage(exception.getMessage());
            }
        }
    }

    private int promptQuantity(String prompt) {
        while (true) {
            String raw = ui.readLine(prompt);
            try {
                return validator.parseQuantity(raw);
            } catch (IllegalArgumentException exception) {
                ui.showMessage(exception.getMessage());
            }
        }
    }

    private double promptPrice(String prompt) {
        while (true) {
            String raw = ui.readLine(prompt);
            try {
                return validator.parsePrice(raw);
            } catch (IllegalArgumentException exception) {
                ui.showMessage(exception.getMessage());
            }
        }
    }

    private boolean askRepeat(String question) {
        ui.showMenu(question,
                "1 - Yes",
                "0 - Main Menu");
        return promptMenuChoice("Select an option: ", 0, 1,
                "Invalid option. Enter 1 for Yes or 0 for Main Menu.") == 1;
    }

    private boolean isCancelCommand(String raw) {
        return raw != null && InputValidator.CANCEL_COMMAND.equals(raw.trim());
    }
}
