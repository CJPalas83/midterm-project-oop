public enum Category {
    CLOTHING("Clothing", "CLO", 50.00, 300000.00,
            "1", "clo"),
    ELECTRONICS("Electronics", "ELE", 50.00, 1000000.00,
            "2", "ele", "electronic"),
    ENTERTAINMENT("Entertainment", "ENT", 20.00, 200000.00,
            "3", "ent");

    public static final int MIN_ITEM_NUMBER = 1;
    public static final int MAX_ITEM_NUMBER = 9999;

    private final String displayName;
    private final String prefix;
    private final double minimumPrice;
    private final double maximumPrice;
    private final String[] aliases;

    Category(String displayName, String prefix, double minimumPrice, double maximumPrice,
            String... aliases) {
        this.displayName = displayName;
        this.prefix = prefix;
        this.minimumPrice = minimumPrice;
        this.maximumPrice = maximumPrice;
        this.aliases = aliases;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPrefix() {
        return prefix;
    }

    public double getMinimumPrice() {
        return minimumPrice;
    }

    public double getMaximumPrice() {
        return maximumPrice;
    }

    public boolean isPriceAllowed(double price) {
        return price >= minimumPrice && price <= maximumPrice;
    }

    public String formatItemId(int itemNumber) {
        if (itemNumber < MIN_ITEM_NUMBER || itemNumber > MAX_ITEM_NUMBER) {
            throw new IllegalArgumentException(
                    "Item number must be from " + MIN_ITEM_NUMBER + " to " + MAX_ITEM_NUMBER + ".");
        }
        return String.format("%s-%04d", prefix, itemNumber);
    }

    public static Category fromInput(String rawInput) {
        if (rawInput == null) {
            return null;
        }

        String normalized = rawInput.trim();
        for (Category category : values()) {
            if (category.displayName.equalsIgnoreCase(normalized)) {
                return category;
            }
            for (String alias : category.aliases) {
                if (alias.equalsIgnoreCase(normalized)) {
                    return category;
                }
            }
        }
        return null;
    }

    public static Category fromPrefix(String rawPrefix) {
        if (rawPrefix == null) {
            return null;
        }

        String normalized = rawPrefix.trim();
        for (Category category : values()) {
            if (category.prefix.equalsIgnoreCase(normalized)) {
                return category;
            }
        }
        return null;
    }
}
