import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InventoryManager {
    public static final int LOW_STOCK_THRESHOLD = 5;
    public static final int SORT_BY_QUANTITY = 1;
    public static final int SORT_BY_PRICE = 2;

    private final List<Item> inventory;

    public InventoryManager() {
        inventory = new ArrayList<Item>();
    }

    public Item addItem(Category category, String id, String name, int quantity, double price) {
        if (category == null) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (findItemById(id) != null) {
            throw new IllegalArgumentException("An item with ID " + id + " already exists.");
        }

        Item item = createItem(category, id, name, quantity, price);
        inventory.add(item);
        return item;
    }

    public Item findItemById(String id) {
        if (id == null) {
            return null;
        }
        for (Item item : inventory) {
            if (item.getId().equalsIgnoreCase(id)) {
                return item;
            }
        }
        return null;
    }

    public boolean removeItem(Item item) {
        return inventory.remove(item);
    }

    public boolean hasDuplicateName(String name) {
        if (name == null) {
            return false;
        }
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public List<Item> getAllItems() {
        return new ArrayList<Item>(inventory);
    }

    public List<Item> getItemsByCategory(Category category) {
        List<Item> matchingItems = new ArrayList<Item>();
        for (Item item : inventory) {
            if (item.getCategory() == category) {
                matchingItems.add(item);
            }
        }
        return matchingItems;
    }

    public List<Item> getSortedItems(final int sortField, final boolean ascending) {
        if (sortField != SORT_BY_QUANTITY && sortField != SORT_BY_PRICE) {
            throw new IllegalArgumentException("Unsupported sort field.");
        }

        List<Item> sortedItems = new ArrayList<Item>(inventory);
        Collections.sort(sortedItems, new Comparator<Item>() {
            @Override
            public int compare(Item first, Item second) {
                int primaryComparison;
                if (sortField == SORT_BY_QUANTITY) {
                    primaryComparison = Integer.compare(first.getQuantity(), second.getQuantity());
                } else {
                    primaryComparison = Double.compare(first.getPrice(), second.getPrice());
                }

                if (!ascending) {
                    primaryComparison = -primaryComparison;
                }
                if (primaryComparison != 0) {
                    return primaryComparison;
                }

                return String.CASE_INSENSITIVE_ORDER.compare(first.getName(), second.getName());
            }
        });
        return sortedItems;
    }

    public List<Item> getLowStockItems() {
        List<Item> lowStockItems = new ArrayList<Item>();
        for (Item item : inventory) {
            if (item.getQuantity() <= LOW_STOCK_THRESHOLD) {
                lowStockItems.add(item);
            }
        }
        return lowStockItems;
    }

    private Item createItem(Category category, String id, String name, int quantity, double price) {
        switch (category) {
            case CLOTHING:
                return new Clothing(id, name, quantity, price);
            case ELECTRONICS:
                return new Electronics(id, name, quantity, price);
            case ENTERTAINMENT:
                return new Entertainment(id, name, quantity, price);
            default:
                throw new IllegalArgumentException("Unsupported category.");
        }
    }
}
