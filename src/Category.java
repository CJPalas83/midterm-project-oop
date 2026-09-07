public enum Category {
    CLOTHING("Clothing", "CLO"),
    ELECTRONICS("Electronics", "ELE"),
    ENTERTAINMENT("Entertainment", "ENT");

    public static final int MIN_ITEM_NUMBER = 1;
    public static final int MAX_ITEM_NUMBER = 9999;

    private final String displayName;
    private final String prefix;

    Category(String displayName, String prefix) {
        this.displayName = displayName;
        this.prefix = prefix;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPrefix() {
        return prefix;
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
