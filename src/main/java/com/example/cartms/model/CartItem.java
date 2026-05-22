package com.example.cartms.model;

/**
 * Immutable value object representing a single line item in a {@link Cart}.
 * Carries a snapshot of the product's name and unit price at the time it
 * was added — later catalog price changes do not retroactively update carts.
 */
public class CartItem {
    private String productId;
    private String name;
    private int quantity;
    private double price;

    public CartItem(String productId, String name, int quantity, double price) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    /** Unit price (not line total). Use {@code price * quantity} for line total. */
    public double getPrice() {
        return price;
    }
}
