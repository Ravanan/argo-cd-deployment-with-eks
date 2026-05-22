package com.example.cartms.controller;

import com.example.cartms.model.Cart;
import com.example.cartms.model.CartItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * REST controller exposing cart operations under {@code /cart}.
 * Backed by the in-memory {@code Map<String, Cart>} bean defined in
 * {@link com.example.cartms.config.CartDataConfig} — all state is
 * process-local and lost on restart.
 *
 * Concurrency: the injected map is a plain {@link java.util.HashMap},
 * so concurrent writes from multiple requests are unsafe. Acceptable
 * for the demo; swap for {@link java.util.concurrent.ConcurrentHashMap}
 * or a real repository before production.
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    // Injected from CartDataConfig#carts() — shared, mutable, process-local state.
    @Autowired
    private Map<String, Cart> carts;

    /**
     * Adds an item to the cart, creating the cart on first use.
     * The placeholder userId {@code "user123"} is used when auto-creating —
     * replace with the authenticated principal once auth is wired up.
     */
    @PostMapping("/{cartId}/addItem")
    public Cart addItem(@PathVariable String cartId,
                        @RequestBody CartItem item) {
        Cart cart = carts.computeIfAbsent(cartId, id -> new Cart(id, "user123"));
        cart.addItem(item);
        return cart;
    }

    /**
     * Removes every line item with the given productId from the cart.
     * No-op (returns {@code null}) if the cart does not exist.
     */
    @DeleteMapping("/{cartId}/removeItem/{productId}")
    public Cart removeItem(@PathVariable String cartId,
                           @PathVariable String productId) {
        Cart cart = carts.get(cartId);
        if (cart != null) {
            cart.removeItem(productId);
        }
        return cart;
    }

    /**
     * Returns the cart for the given id, or 404 if no such cart exists.
     * Carts are created lazily by {@link #addItem} — clients should POST
     * an item to bring a cart into existence rather than GETting first.
     */
    @GetMapping("/{cartId}")
    public Cart getCart(@PathVariable String cartId) {
        Cart cart = carts.get(cartId);
        if (cart == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found: " + cartId);
        }
        return cart;
    }

    /** Returns the cart total, or {@code 0.0} when the cart is missing. */
    @GetMapping("/{cartId}/total")
    public double getTotal(@PathVariable String cartId) {
        Cart cart = carts.get(cartId);
        return cart != null ? cart.calculateTotal() : 0.0;
    }
}
