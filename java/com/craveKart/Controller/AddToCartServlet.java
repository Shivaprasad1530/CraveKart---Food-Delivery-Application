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

import com.craveKart.dao.MenuDao;
import com.craveKart.dao.OrderItemDao;
import com.craveKart.dao.OrderTableDao;
import com.craveKart.daoImplementation.MenuImplementation;
import com.craveKart.daoImplementation.OrderItemImplement;
import com.craveKart.daoImplementation.OrderTableImplement;
import com.craveKart.model.Menu;
import com.craveKart.model.OrderItem;
import com.craveKart.model.OrderTable;
import com.craveKart.model.User;

/**
 * AddToCartServlet
 *
 * AJAX endpoint hit from menu.jsp / cart.jsp.
 * GET params:
 *   me_id  - the menu item id
 *   action - "add" | "increment" | "decrement"
 *
 * Returns JSON: {"itemQty":N, "cartCount":N, "cartTotal":N}
 * or          : {"error":"message"}
 *
 * NOTE: assumes MenuDao has a getMenu(int me_id) method (used elsewhere
 * to fetch a single menu item, alongside restaurantMenu(r_id)). If your
 * MenuDao doesn't have this yet, add it — it's needed to trust the price
 * server-side instead of trusting whatever the client sends.
 */
@WebServlet("/AddToCart")
public class AddToCartServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        User user = (User) session.getAttribute("user");
        OrderTable cart = (OrderTable) session.getAttribute("cart");

        if (user == null || cart == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\":\"No active cart. Please open a restaurant's menu first.\"}");
            return;
        }

        int me_id;
        String action;
        try {
            me_id = Integer.parseInt(request.getParameter("me_id"));
            action = request.getParameter("action"); // add or subtract item.
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid request\"}");
            return;
        }

        OrderItemDao itemDao = new OrderItemImplement();
        OrderTableDao cartDao = new OrderTableImplement();
        MenuDao menuDao = new MenuImplementation();

        Menu menuItem = menuDao.getItem(me_id);
        if (menuItem == null || !menuItem.isAvailable()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Item unavailable\"}");
            return;
        }
        int price = menuItem.getPrice(); 

        OrderItem existing = itemDao.getOrderItemByOrderAndMenu(cart.getO_id(), me_id);
        int newQty;

        if (action == null) action = "";

        switch (action) {
            case "add":
            case "increment":
                if (existing == null) {
                    OrderItem oi = new OrderItem();
                    oi.setO_id(cart.getO_id());
                    oi.setMe_id(me_id);
                    oi.setQuantity(1);
                    oi.setItem_total(price);
                    itemDao.addOrderItem(oi);
                    newQty = 1;
                } else {
                    existing.setQuantity(existing.getQuantity() + 1);
                    existing.setItem_total(existing.getQuantity() * price);
                    itemDao.updateOrderItem(existing);
                    newQty = existing.getQuantity();
                }
                break;

            case "decrement":
                if (existing == null) {
                    newQty = 0;
                } else {
                    int qty = existing.getQuantity() - 1;
                    if (qty <= 0) {
                        itemDao.deleteOrderItem(existing.getOi_id());
                        newQty = 0;
                    } else {
                        existing.setQuantity(qty);
                        existing.setItem_total(qty * price);
                        itemDao.updateOrderItem(existing);
                        newQty = qty;
                    }
                }
                break;

            default:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Unknown action\"}");
                return;
        }

        // Recompute cart-wide totals from DB (source of truth) and sync session + ordertable
        List<OrderItem> items = itemDao.getItemsByOrderId(cart.getO_id());
        int cartCount = 0, cartTotal = 0;
        for (OrderItem oi : items) {
            cartCount += oi.getQuantity();
            cartTotal += oi.getItem_total();
        }
        cart.setTotal_amt(cartTotal);
        cartDao.updateOrderTable(cart);
        session.setAttribute("cart", cart);
        session.setAttribute("cartCount", cartCount);
        session.setAttribute("cartTotal", cartTotal);

        out.print("{\"itemQty\":" + newQty + ",\"cartCount\":" + cartCount + ",\"cartTotal\":" + cartTotal + "}");
    }
}