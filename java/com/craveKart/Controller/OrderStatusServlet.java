package com.craveKart.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.craveKart.dao.MenuDao;
import com.craveKart.dao.OrderItemDao;
import com.craveKart.dao.OrderTableDao;
import com.craveKart.dao.RestaurantDao;
import com.craveKart.dao.UserDao;
import com.craveKart.daoImplementation.MenuImplementation;
import com.craveKart.daoImplementation.OrderItemImplement;
import com.craveKart.daoImplementation.OrderTableImplement;
import com.craveKart.daoImplementation.RestaurantImplementaion;
import com.craveKart.daoImplementation.UserDaoImpl;
import com.craveKart.model.Menu;
import com.craveKart.model.OrderItem;
import com.craveKart.model.OrderTable;
import com.craveKart.model.Restaurant;
import com.craveKart.model.User;

/**
 * OrderStatusServlet
 *
 * Customer lands here right after ConfirmOrderServlet succeeds and stays here until status='delivered'.
 *
 * No "action" param -> loads the full tracking page (order + items + restaurant).
 * action=getStatus  -> lightweight JSON poll (status + total), called every
 *                      few seconds by the page's own JS so it updates live
 *                      without a full reload.
 * action=acceptDelivery -> the customer's own confirmation step; only allowed
 *                      while status='dispatched', flips it to 'delivered'.
 *
 * Every branch re-checks that the order actually belongs to the logged-in
 * user (order.u_id == user.id) before showing or changing anything -- same
 * ownership-check principle as the restaurant manager dashboard.
 */
@WebServlet("/OrderStatus")
public class OrderStatusServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");
        UserDao userDao = new UserDaoImpl();
        User user = userDao.getUserDetails(username);

        String action = req.getParameter("action");
        boolean isJsonAction = "getStatus".equals(action) || "acceptDelivery".equals(action);

        if (user == null) {
            if (isJsonAction) {
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().print("{\"error\":\"Please login again.\"}");
                return;
            }
            req.setAttribute("Error", "Please login to view your order.");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }

        int o_id = parseIntOrDefault(req.getParameter("o_id"), -1);
        OrderTableDao cartDao = new OrderTableImplement();
        OrderTable order = cartDao.getOrderTable(o_id);

        if (order == null || order.getU_id() != user.getId()) {
            if (isJsonAction) {
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().print("{\"error\":\"Order not found.\"}");
                return;
            }
            req.setAttribute("Error", "That order could not be found.");
            req.getRequestDispatcher("restaurant.jsp").forward(req, resp);
            return;
        }

        if ("getStatus".equals(action)) {
            resp.setContentType("application/json");
            resp.getWriter().print(
                "{\"status\":\"" + order.getStatus() + "\",\"totalAmt\":" + order.getTotal_amt() + "}"
            );
            return;
        }

        if ("acceptDelivery".equals(action)) {
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();

            if (!"reached_location".equalsIgnoreCase(order.getStatus())) {
                out.print("{\"error\":\"Your delivery partner hasn't arrived yet.\"}");
                return;
            }
            order.setStatus("delivered");
            cartDao.updateOrderTable(order);
            out.print("{\"success\":true,\"status\":\"delivered\"}");
            return;
        }

        // Normal page load -- gather everything the JSP needs to display
        OrderItemDao itemDao = new OrderItemImplement();
        MenuDao menuDao = new MenuImplementation();
        RestaurantDao restaurantDao = new RestaurantImplementaion();

        List<OrderItem> items = itemDao.getItemsByOrderId(order.getO_id());
        List<Menu> menus = new ArrayList<>();
        List<OrderItem> matchedItems = new ArrayList<>();
        for (OrderItem oi : items) {
            Menu m = menuDao.getItem(oi.getMe_id());
            if (m != null) {
                menus.add(m);
                matchedItems.add(oi);
            }
        }

        Restaurant restaurant = restaurantDao.getRestaurant(order.getRe_id());

        req.setAttribute("order", order);
        req.setAttribute("orderMenus", menus);
        req.setAttribute("orderItems", matchedItems);
        req.setAttribute("restaurant", restaurant);

        RequestDispatcher rd = req.getRequestDispatcher("/orderStatus.jsp");
        rd.forward(req, resp);
    }

    private int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }
}