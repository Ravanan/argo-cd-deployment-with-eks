package com.example.cartms.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a shopping cart owned by a single user.
 * A cart is identified by {@code cartId} and aggregates a mutable list
 * of {@link CartItem} entries. Not thread-safe — intended for use behind
 * a request-scoped controller or a synchronized service layer.
 */
public class Cart {
    private String cartId;
    private String userId;
    private List<CartItem> items = new ArrayList<>();

    public Cart(String cartId, String userId) {
        this.cartId = cartId;
        this.userId = userId;
    }

    public String getCartId() {
        return cartId;
    }

    public String getUserId() {
        return userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    /** Appends an item to the cart. Duplicates are not merged. */
    public void addItem(CartItem item) {
        items.add(item);
    }

    /** Removes every item whose productId matches the argument. */
    public void removeItem(String productId) {
        items.removeIf(i -> i.getProductId().equals(productId));
    }

    /** Returns the sum of {@code price * quantity} across all items. */
    public double calculateTotal() {
        return items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }
}
