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
import com.craveKart.dao.OrderTableDao;
import com.craveKart.dao.UserDao;
import com.craveKart.daoImplementation.OrderItemImplement;
import com.craveKart.daoImplementation.OrderTableImplement;
import com.craveKart.daoImplementation.UserDaoImpl;
import com.craveKart.model.OrderItem;
import com.craveKart.model.OrderTable;
import com.craveKart.model.User;

/**
 * ConfirmOrderServlet
 *
 * Finalizes the order:
 *  - saves the (possibly edited) address onto the user's profile
 *  - re-validates the coupon/subtotal server-side (never trusts a client total)
 *  - sets ordertable.status = 'confirmed' and payment_mode
 *  - clears the session cart so the next restaurant visit starts fresh
 *
 * No payment gateway integration yet, so COD or any UPI app choice both
 * confirm immediately per current requirements.
 */
@WebServlet("/ConfirmOrder")
public class ConfirmOrderServlet extends HttpServlet {

    private static final int MIN_SUBTOTAL_FOR_COUPON = 500;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	System.out.println("ConfirmOrder hit. address param = [" + request.getParameter("address") + "]");
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        User user = (User) session.getAttribute("user");
        OrderTable cart = (OrderTable) session.getAttribute("cart");

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\":\"Please login again.\"}");
            return;
        }
        if (cart == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"No active cart.\"}");
            return;
        }

        String address = request.getParameter("address");
        String paymentMode = request.getParameter("paymentMode"); // "COD" or "UPI"
        String upiApp = request.getParameter("upiApp");            // "PhonePe" | "GPay" | "Paytm" (display only)

        if (address == null || user.getAddress() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Delivery address is required.\"}");
            return;
        }
        if (paymentMode == null || !(paymentMode.equals("COD") || paymentMode.equals("UPI"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Please select a payment method.\"}");
            return;
        }
        if (paymentMode.equals("UPI") && (upiApp == null || upiApp.trim().isEmpty())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Please select a UPI app.\"}");
            return;
        }

        // Save address onto the user's profile if it changed
        String trimmedAddress = address.trim();
        String existingAddress = user.getAddress();
        
        if (existingAddress == null || !trimmedAddress.equals(existingAddress)) {
            user.setAddress(trimmedAddress);
            UserDao userDao = new UserDaoImpl();
            userDao.updateUser(user);
            session.setAttribute("user", user);
        }

        // Recompute subtotal from DB -- never trust a client-sent total
        OrderItemDao itemDao = new OrderItemImplement();
        List<OrderItem> items = itemDao.getItemsByOrderId(cart.getO_id());
        if (items.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Your cart is empty.\"}");
            return;
        }
        int subtotal = 0;
        for (OrderItem oi : items) subtotal += oi.getItem_total();

        Boolean couponApplied = (Boolean) session.getAttribute("couponApplied");
        Integer discount = (Integer) session.getAttribute("couponDiscount");
        int finalTotal = subtotal;
        if (Boolean.TRUE.equals(couponApplied) && discount != null && subtotal > MIN_SUBTOTAL_FOR_COUPON) {
            finalTotal = subtotal - discount;
        }

        cart.setTotal_amt(finalTotal);
        cart.setStatus("confirmed");
        cart.setPayment_mode(paymentMode);

        OrderTableDao cartDao = new OrderTableImplement();
        cartDao.updateOrderTable(cart);

        int confirmedOrderId = cart.getO_id();
        System.out.println(cart);
        System.out.println(session.getAttribute("cartQtyMap"));
        System.out.println(session.getAttribute("cartCount"));

        // Clear the session cart so the next restaurant visit starts fresh
        session.removeAttribute("cart");
        session.removeAttribute("cartCount");
        session.removeAttribute("cartTotal");
        session.removeAttribute("cartQtyMap");
        session.removeAttribute("couponApplied");
        session.removeAttribute("couponDiscount");

        out.print("{\"success\":true,\"orderId\":" + confirmedOrderId + ",\"total\":" + finalTotal + "}");
    }
}
