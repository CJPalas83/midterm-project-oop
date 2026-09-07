public abstract class Item {
    public static final int MIN_QUANTITY = 0;
    public static final int MAX_QUANTITY = 10000;
    public static final double MIN_PRICE = 0.01;
    public static final double MAX_PRICE = 1000000.00;

    private final String id;
    private final String name;
    private int quantity;
    private double price;

    protected Item(String id, String name, int quantity, double price) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID is required.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required.");
        }

        this.id = id;
        this.name = name;
        updateQuantity(quantity);
        updatePrice(price);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity < MIN_QUANTITY || newQuantity > MAX_QUANTITY) {
            throw new IllegalArgumentException("Quantity is outside the allowed range.");
        }
        quantity = newQuantity;
    }

    public void updatePrice(double newPrice) {
        if (Double.isNaN(newPrice) || Double.isInfinite(newPrice)
                || newPrice < MIN_PRICE || newPrice > MAX_PRICE) {
            throw new IllegalArgumentException("Price is outside the allowed range.");
        }
        price = newPrice;
    }

    public abstract Category getCategory();
}
