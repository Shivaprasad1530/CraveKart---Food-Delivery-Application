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

import com.craveKart.dao.OrderTableDao;
import com.craveKart.dao.RestaurantDao;
import com.craveKart.dao.UserDao;
import com.craveKart.daoImplementation.OrderTableImplement;
import com.craveKart.daoImplementation.RestaurantImplementaion;
import com.craveKart.daoImplementation.UserDaoImpl;
import com.craveKart.model.OrderTable;
import com.craveKart.model.Restaurant;
import com.craveKart.model.User;

/**
 * DeliveryDashboardServlet
 *
 * GET/no "action" param -> loads the dashboard. An agent can only ever have
 * ONE active delivery at a time:
 *   - if they already have one (status IN rider_confirmed/dispatched/
 *     reached_location) -> show ONLY that, in "activeDelivery". No available
 *     orders are shown at all until it's completed.
 *   - otherwise -> show every order at status='prepared' with no agent
 *     claimed yet, as "availableOrders" to accept.
 *
 * Deliberately does NOT expose order contents (menu items) to the agent --
 * only restaurant/customer identity, address, phone, and the order ID. The
 * restaurant is responsible for verifying/sealing what's inside; the agent
 * only handles logistics.
 *
 * "action" param present -> handleAction(): acceptOrder, markArrived.
 */
@WebServlet("/deliveryDashboard")
public class DeliveryDashboard extends HttpServlet {

    // View-model for one row -- either an available order to accept, or the
    // agent's current active delivery. No item/menu data by design.
    public static class DeliveryOrderView {
        private OrderTable order;
        private String restaurantName;
        private String restaurantAddress;
        private String restaurantPhone;
        private String customerName;
        private String customerAddress;
        private String customerPhone;

        public OrderTable getOrder() { return order; }
        public void setOrder(OrderTable order) { this.order = order; }

        public String getRestaurantName() { return restaurantName; }
        public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

        public String getRestaurantAddress() { return restaurantAddress; }
        public void setRestaurantAddress(String restaurantAddress) { this.restaurantAddress = restaurantAddress; }

        public String getRestaurantPhone() { return restaurantPhone; }
        public void setRestaurantPhone(String restaurantPhone) { this.restaurantPhone = restaurantPhone; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getCustomerAddress() { return customerAddress; }
        public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }

        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action != null) {
            handleAction(req, resp, action);
            return;
        }

        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");
        UserDao userDao = new UserDaoImpl();
        User user = userDao.getUserDetails(username);

        if (user == null || !"Delivery Agent".equalsIgnoreCase(user.getRole())) {
            req.setAttribute("Error", "Please login as a delivery agent to view this page");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }

        OrderTableDao cartDao = new OrderTableImplement();
        RestaurantDao restaurantDao = new RestaurantImplementaion();

        OrderTable activeOrder = cartDao.getActiveOrderForAgent(user.getId());

        if (activeOrder != null) {
            DeliveryOrderView activeDelivery = buildView(activeOrder, restaurantDao, userDao);
            req.setAttribute("activeDelivery", activeDelivery);
            req.setAttribute("availableOrders", new ArrayList<DeliveryOrderView>());
        } else {
            List<OrderTable> available = cartDao.getAvailableOrdersForPickup();
            List<DeliveryOrderView> availableViews = new ArrayList<>();
            for (OrderTable order : available) {
                availableViews.add(buildView(order, restaurantDao, userDao));
            }
            req.setAttribute("activeDelivery", null);
            req.setAttribute("availableOrders", availableViews);
        }

        RequestDispatcher rd = req.getRequestDispatcher("/deliveryDashboard.jsp");
        rd.forward(req, resp);
    }

    private DeliveryOrderView buildView(OrderTable order, RestaurantDao restaurantDao, UserDao userDao) {
        DeliveryOrderView view = new DeliveryOrderView();
        view.setOrder(order);

        Restaurant restaurant = restaurantDao.getRestaurant(order.getRe_id());
        if (restaurant != null) {
            view.setRestaurantName(restaurant.getname());
            view.setRestaurantAddress(restaurant.getAddress());
            User manager = userDao.getUser(restaurant.getRestaurantManagerid());
            if (manager != null) {
                view.setRestaurantPhone(manager.getPhone());
            }
        }

        User customer = userDao.getUser(order.getU_id());
        if (customer != null) {
            view.setCustomerName(customer.getName());
            view.setCustomerAddress(customer.getAddress());
            view.setCustomerPhone(customer.getPhone());
        }

        return view;
    }

    /**
     * acceptOrder / markArrived, routed by "action" -- same one-endpoint
     * pattern as every other servlet in this project.
     */
    private void handleAction(HttpServletRequest req, HttpServletResponse resp, String action)
            throws IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();

        String username = (String) session.getAttribute("username");
        UserDao userDao = new UserDaoImpl();
        User user = userDao.getUserDetails(username);

        if (user == null || !"Delivery Agent".equalsIgnoreCase(user.getRole())) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\":\"Not authorized.\"}");
            return;
        }

        OrderTableDao cartDao = new OrderTableImplement();

        switch (action) {

            case "acceptOrder": {
                // Server-side guard against multiple active deliveries --
                // don't just rely on the UI hiding the list.
                OrderTable existingActive = cartDao.getActiveOrderForAgent(user.getId());
                if (existingActive != null) {
                    out.print("{\"error\":\"You already have an active delivery to finish first.\"}");
                    return;
                }

                int o_id = parseIntOrDefault(req.getParameter("o_id"), -1);
                boolean claimed = cartDao.claimOrderForAgent(o_id, user.getId());

                if (!claimed) {
                    out.print("{\"error\":\"This order was just accepted by someone else.\"}");
                    return;
                }
                out.print("{\"success\":true,\"o_id\":" + o_id + "}");
                break;
            }

            case "markArrived": {
                int o_id = parseIntOrDefault(req.getParameter("o_id"), -1);
                OrderTable order = cartDao.getOrderTable(o_id);

                if (order == null || order.getDeliveryAgentId() == null
                        || order.getDeliveryAgentId() != user.getId()) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"error\":\"This isn't your delivery.\"}");
                    return;
                }
                if (!"dispatched".equalsIgnoreCase(order.getStatus())) {
                    out.print("{\"error\":\"This order hasn't been dispatched by the restaurant yet.\"}");
                    return;
                }
                order.setStatus("reached_location");
                cartDao.updateOrderTable(order);
                out.print("{\"success\":true,\"o_id\":" + o_id + "}");
                break;
            }

            default:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Unknown action.\"}");
        }
    }

    private int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }
}