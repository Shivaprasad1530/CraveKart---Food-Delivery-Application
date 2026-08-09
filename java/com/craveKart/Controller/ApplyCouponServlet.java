package com.craveKart.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.craveKart.dao.OrderItemDao;
import com.craveKart.daoImplementation.OrderItemImplement;
import com.craveKart.model.OrderItem;
import com.craveKart.model.OrderTable;
import com.craveKart.model.User;

/**
 * ApplyCouponServlet
 *
 * Only one coupon exists right now: REDUCE100 -> flat Rs.100 off,
 * valid only when the cart subtotal is above Rs.500. Validated here
 * server-side (not just in JS) so it can't be spoofed from the browser.
 * Result is stashed in session so ConfirmOrderServlet uses the server's
 * number, never a client-supplied total.
 */
@WebServlet("/ApplyCoupon")
public class ApplyCouponServlet extends HttpServlet {

    private static final String VALID_CODE = "REDUCE100";
    private static final int DISCOUNT_AMOUNT = 100;
    private static final int MIN_SUBTOTAL_FOR_COUPON = 500;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        User user = (User) session.getAttribute("user");
        OrderTable cart = (OrderTable) session.getAttribute("cart");
       

        String code = request.getParameter("code");
        if (code != null) code = code.trim();

        OrderItemDao itemDao = new OrderItemImplement();
        List<OrderItem> items = itemDao.getItemsByOrderId(cart.getO_id());
        int subtotal = 0;
        for (OrderItem oi : items) subtotal += oi.getItem_total();

        boolean applied = false;
        int discount = 0;
        String message;

        if (code == null || code.isEmpty()) {
            message = "Please enter a coupon code";
        } else if (!VALID_CODE.equalsIgnoreCase(code)) {
            message = "Invalid coupon code";
        } else if (subtotal <= MIN_SUBTOTAL_FOR_COUPON) {
            message = "Add items worth more than " + MIN_SUBTOTAL_FOR_COUPON + " to use this coupon";
        } else {
            applied = true;
            discount = DISCOUNT_AMOUNT;
            message = "Coupon applied! " + DISCOUNT_AMOUNT + " off";
        }

        session.setAttribute("couponApplied", applied);
        session.setAttribute("couponDiscount", discount);

        int finalTotal = subtotal - discount;

        out.print("{\"applied\":" + applied
                + ",\"message\":\"" + message + "\""
                + ",\"subtotal\":" + subtotal
                + ",\"discount\":" + discount
                + ",\"finalTotal\":" + finalTotal + "}");
    }
}
