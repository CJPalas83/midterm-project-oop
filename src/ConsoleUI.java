import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ConsoleUI {
    private static final int NAME_WIDTH = 24;
    private static final String PESO_SYMBOL = "\u20B1";

    private final Scanner scanner;
    private final PrintStream output;
    private final NumberFormat numberFormat;

    public ConsoleUI() {
        scanner = new Scanner(System.in);
        try {
            output = new PrintStream(System.out, true, "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            throw new IllegalStateException("UTF-8 output is not supported.", exception);
        }
        numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
    }

    public String readLine(String prompt) {
        output.print(prompt);
        return scanner.nextLine();
    }

    public void showMenu(String title, String... options) {
        output.println();
        output.println(title);
        for (String option : options) {
            output.println(option);
        }
    }

    public void showMessage(String message) {
        output.println(message);
    }

    public void showItemDetails(Item item) {
        output.println();
        output.println("ID: " + item.getId());
        output.println("Name: " + item.getName());
        output.println("Quantity: " + item.getQuantity());
        output.println("Price: " + formatPrice(item.getPrice()));
        output.println("Category: " + item.getCategory().getDisplayName());
    }

    public void showItemsTable(List<Item> items, boolean includeCategory) {
        String divider = repeat('-', includeCategory ? 75 : 60);

        output.println();
        output.println(divider);
        if (includeCategory) {
            output.printf("%-9s %-24s %8s %14s %-13s%n",
                    "ID", "Name", "Quantity", "Price", "Category");
        } else {
            output.printf("%-9s %-24s %8s %14s%n",
                    "ID", "Name", "Quantity", "Price");
        }
        output.println(divider);

        for (Item item : items) {
            String displayName = truncate(item.getName(), NAME_WIDTH);
            if (includeCategory) {
                output.printf("%-9s %-24s %8d %14s %-13s%n",
                        item.getId(), displayName, item.getQuantity(), formatPrice(item.getPrice()),
                        item.getCategory().getDisplayName());
            } else {
                output.printf("%-9s %-24s %8d %14s%n",
                        item.getId(), displayName, item.getQuantity(), formatPrice(item.getPrice()));
            }
        }
        output.println(divider);
    }

    public void pause() {
        readLine("Press Enter to return to the Main Menu...");
    }

    public String formatPrice(double price) {
        return PESO_SYMBOL + numberFormat.format(price);
    }

    private String truncate(String value, int maximumLength) {
        if (value.length() <= maximumLength) {
            return value;
        }
        return value.substring(0, maximumLength - 3) + "...";
    }

    private String repeat(char character, int count) {
        StringBuilder result = new StringBuilder(count);
        for (int index = 0; index < count; index++) {
            result.append(character);
        }
        return result.toString();
    }
}
