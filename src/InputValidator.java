import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 50;
    public static final String CANCEL_COMMAND = "0";

    private static final Pattern PREFIXED_ID_PATTERN = Pattern.compile("^([A-Z]{3})-(\\d{1,4})$");
    private static final Pattern SCOPED_NUMBER_PATTERN = Pattern.compile("^\\d{1,4}$");
    private static final Pattern LETTER_PATTERN = Pattern.compile(".*\\p{L}.*");
    private static final Pattern PRODUCT_NAME_PATTERN =
            Pattern.compile("^[\\p{L}\\p{N} .,'/&+\"():-]+$");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern PRICE_PATTERN = Pattern.compile("^\\d+(?:\\.\\d{1,2})?$");

    public int parseMenuChoice(String raw, int minimum, int maximum) {
        String normalized = raw == null ? "" : raw.trim();
        try {
            int choice = Integer.parseInt(normalized);
            if (choice < minimum || choice > maximum) {
                throw new NumberFormatException();
            }
            return choice;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Please enter a whole number from " + minimum + " to " + maximum + ".");
        }
    }

    public String normalizeFullItemId(String raw) {
        String normalized = raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
        Matcher matcher = PREFIXED_ID_PATTERN.matcher(normalized);
        Category category = matcher.matches() ? Category.fromPrefix(matcher.group(1)) : null;
        if (category == null) {
            throw fullIdException();
        }

        int itemNumber = Integer.parseInt(matcher.group(2));
        if (itemNumber < Category.MIN_ITEM_NUMBER || itemNumber > Category.MAX_ITEM_NUMBER) {
            throw fullIdException();
        }
        return category.formatItemId(itemNumber);
    }

    public String normalizeScopedItemId(String raw, Category category) {
        if (category == null) {
            throw new IllegalArgumentException("A category is required for a scoped item ID.");
        }

        String normalized = raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
        int itemNumber;

        if (SCOPED_NUMBER_PATTERN.matcher(normalized).matches()) {
            itemNumber = Integer.parseInt(normalized);
        } else {
            Matcher matcher = PREFIXED_ID_PATTERN.matcher(normalized);
            if (!matcher.matches()) {
                throw scopedIdException(category);
            }
            if (!category.getPrefix().equals(matcher.group(1))) {
                throw new IllegalArgumentException(
                        "The ID prefix must match the selected " + category.getDisplayName() + " category.");
            }
            itemNumber = Integer.parseInt(matcher.group(2));
        }

        if (itemNumber < Category.MIN_ITEM_NUMBER || itemNumber > Category.MAX_ITEM_NUMBER) {
            throw scopedIdException(category);
        }
        return category.formatItemId(itemNumber);
    }

    public String normalizeName(String raw) {
        String normalized = raw == null ? "" : raw.trim().replaceAll("\\s+", " ");
        if (normalized.length() < MIN_NAME_LENGTH || normalized.length() > MAX_NAME_LENGTH
                || !LETTER_PATTERN.matcher(normalized).matches()
                || !PRODUCT_NAME_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Item name must be 2 to 50 characters, contain at least one letter, "
                            + "and use only common product-name punctuation.");
        }
        return normalized;
    }

    public int parseQuantity(String raw) {
        String normalized = raw == null ? "" : raw.trim();
        if (!QUANTITY_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Enter a whole-number quantity from 0 to 10,000.");
        }
        try {
            int quantity = Integer.parseInt(normalized);
            if (quantity < Item.MIN_QUANTITY || quantity > Item.MAX_QUANTITY) {
                throw new NumberFormatException();
            }
            return quantity;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a whole-number quantity from 0 to 10,000.");
        }
    }

    public double parsePrice(String raw) {
        String normalized = raw == null ? "" : raw.trim();

        // Syntax and precision are deliberately checked before parsing so the
        // user's original number of decimal places is not lost.
        if (!PRICE_PATTERN.matcher(normalized).matches()) {
            throw priceException();
        }

        try {
            double price = Double.parseDouble(normalized);
            if (Double.isNaN(price) || Double.isInfinite(price)
                    || price < Item.MIN_PRICE || price > Item.MAX_PRICE) {
                throw new NumberFormatException();
            }
            return price;
        } catch (NumberFormatException exception) {
            throw priceException();
        }
    }

    private IllegalArgumentException scopedIdException(Category category) {
        return new IllegalArgumentException(category.getDisplayName() + " Item IDs must use "
                + category.getPrefix() + "-1 through " + category.getPrefix() + "-9999.");
    }

    private IllegalArgumentException fullIdException() {
        return new IllegalArgumentException(
                "Item ID must use CLO, ELE, or ENT followed by a number from 1 to 9999 "
                        + "(example: ELE-27).");
    }

    private IllegalArgumentException priceException() {
        return new IllegalArgumentException(
                "Enter a price from 0.01 to 1,000,000.00 with at most 2 decimal places.");
    }
}
