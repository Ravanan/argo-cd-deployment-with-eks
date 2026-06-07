package com.example.cartms.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {
    private static final String CART_ID_COOKIE = "cartId";
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 30; // 30 days

    public static void setCartIdCookie(HttpServletResponse response, String cartId) {
        Cookie cookie = new Cookie(CART_ID_COOKIE, cartId);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static String getCartIdFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (CART_ID_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
