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
 * RestaurantDashboardServlet
 *
 * GET/no "action" param -> loads the dashboard:
 *   - the restaurant this manager owns (via restaurant.restaurantManagerId)
 *   - orders at status='confirmed' -> "Incoming Orders" panel (Mark Prepared)
 *   - orders at status='prepared'/'rider_confirmed' -> "Preparing / Awaiting
 *     Rider" panel, showing the rider's name+phone once one has claimed it
 *     (Mark Dispatched, only once a rider has actually accepted)
 *   - the restaurant's full menu list, for availability + price editing
 *
 * "action" param present -> handleAction() runs instead: toggleStatus,
 * markPrepared, markDispatched, toggleAvailability, updatePrice, addMenuItem.
 * Same pattern as AddToCartServlet -- one endpoint, one action parameter,
 * JSON response.
 */
@WebServlet("/restaurantDashboard")
public class RestaurantDashboard extends HttpServlet {

    // Small view-model so the JSP can walk "order + its items + customer name"
    // as one unit per row, instead of juggling three parallel lists.
   
    public static class PendingOrderView {
        private OrderTable order;
        private List<Menu> menus = new ArrayList<>();
        private List<OrderItem> items = new ArrayList<>();
        private String customerName;

        public OrderTable getOrder() { return order; }
        public void setOrder(OrderTable order) { this.order = order; }

        public List<Menu> getMenus() { return menus; }
        public void setMenus(List<Menu> menus) { this.menus = menus; }

        public List<OrderItem> getItems() { return items; }
        public void setItems(List<OrderItem> items) { this.items = items; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
    }

    // View-model for the "Preparing / Awaiting Rider" panel -- covers both
    // status='prepared' (no rider yet) and status='rider_confirmed' (rider
    // assigned, agentName/agentPhone populated, ready to be dispatched).
    public static class PreparingOrderView {
        private OrderTable order;
        private List<Menu> menus = new ArrayList<>();
        private List<OrderItem> items = new ArrayList<>();
        private String agentName;
        private String agentPhone;

        public OrderTable getOrder() { return order; }
        public void setOrder(OrderTable order) { this.order = order; }

        public List<Menu> getMenus() { return menus; }
        public void setMenus(List<Menu> menus) { this.menus = menus; }

        public List<OrderItem> getItems() { return items; }
        public void setItems(List<OrderItem> items) { this.items = items; }

        public String getAgentName() { return agentName; }
        public void setAgentName(String agentName) { this.agentName = agentName; }

        public String getAgentPhone() { return agentPhone; }
        public void setAgentPhone(String agentPhone) { this.agentPhone = agentPhone; }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	//If manager performs any action like closing restaurant and making a menu item unavailable it will be stored as action and those are are handles in seperate method
        String action = req.getParameter("action");
        if (action != null) {
            handleAction(req, resp, action);
            return;
        }

        HttpSession session = req.getSession();//Creates a session
        //Gets user [restaurant manager] details
        String username = (String) session.getAttribute("username");
        UserDao userDao = new UserDaoImpl();
        User user = userDao.getUserDetails(username);

        //Gets restaurant details
        RestaurantDao restaurantDao = new RestaurantImplementaion();
        Restaurant restaurant = restaurantDao.getRestaurantByManagerId(user.getId());

        if (restaurant == null) {
            req.setAttribute("Error", "No restaurant is linked to this manager account yet.");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }

        OrderTableDao cartDao = new OrderTableImplement();
        OrderItemDao itemDao = new OrderItemImplement();
        MenuDao menuDao = new MenuImplementation();
        
        //Gets a list of all confirmed carts[order table] ordered to this restaurant
        List<OrderTable> confirmedOrders = cartDao.getOrdersByRestaurantAndStatus(restaurant.getR_id(), "confirmed");

        List<PendingOrderView> pendingOrders = new ArrayList<>();
        for (OrderTable order : confirmedOrders) {
            PendingOrderView view = new PendingOrderView();
            view.setOrder(order);

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
            view.setMenus(menus);
            view.setItems(matchedItems);

            User customer = userDao.getUser(order.getU_id());
            view.setCustomerName((customer != null) ? customer.getName() : "Unknown customer");

            pendingOrders.add(view);
        }

        List<Menu> menuList = menuDao.restaurantMenu(restaurant.getR_id());

        // "Preparing / Awaiting Rider" panel -- covers both stages the
        // restaurant still needs to watch: not yet claimed by a rider
        // ('prepared'), and claimed but not yet handed over ('rider_confirmed')
        List<OrderTable> preparedOrders = cartDao.getOrdersByRestaurantAndStatus(restaurant.getR_id(), "prepared");
        List<OrderTable> riderConfirmedOrders = cartDao.getOrdersByRestaurantAndStatus(restaurant.getR_id(), "rider_confirmed");

        List<PreparingOrderView> preparingOrders = new ArrayList<>();
        for (OrderTable order : preparedOrders) {
            preparingOrders.add(buildPreparingOrderView(order, itemDao, menuDao, userDao));
        }
        for (OrderTable order : riderConfirmedOrders) {
            preparingOrders.add(buildPreparingOrderView(order, itemDao, menuDao, userDao));
        }

        req.setAttribute("restaurant", restaurant);
        req.setAttribute("pendingOrders", pendingOrders);
        req.setAttribute("preparingOrders", preparingOrders);
        req.setAttribute("menuList", menuList);

        RequestDispatcher rd = req.getRequestDispatcher("/restaurantDashboard.jsp");
        rd.forward(req, resp);
    }

    private PreparingOrderView buildPreparingOrderView(OrderTable order, OrderItemDao itemDao, MenuDao menuDao, UserDao userDao) {
        PreparingOrderView view = new PreparingOrderView();
        view.setOrder(order);

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
        view.setMenus(menus);
        view.setItems(matchedItems);

        if (order.getDeliveryAgentId() != null) {
            User agent = userDao.getUser(order.getDeliveryAgentId());
            if (agent != null) {
                view.setAgentName(agent.getName());
                view.setAgentPhone(agent.getPhone());
            }
        }

        return view;
    }

    /**
     * All write actions for the dashboard, routed through one method by an
     * "action" parameter -- same pattern as AddToCartServlet's add/increment/
     * decrement. Every branch re-fetches the manager's OWN restaurant from
     * session/DB and checks that whatever they're touching (an order, a menu
     * item) actually belongs to it -- never trusts an r_id/re_id sent by the
     * client, same principle as never trusting a client-sent price.
     */
    private void handleAction(HttpServletRequest req, HttpServletResponse resp, String action)
            throws IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();

        String username = (String) session.getAttribute("username");
        UserDao userDao = new UserDaoImpl();
        User user = userDao.getUserDetails(username);

        if (user == null || !"Restaurant manager".equalsIgnoreCase(user.getRole())) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\":\"Not authorized.\"}");
            return;
        }

        RestaurantDao restaurantDao = new RestaurantImplementaion();
        Restaurant restaurant = restaurantDao.getRestaurantByManagerId(user.getId());
        if (restaurant == null) {
            out.print("{\"error\":\"No restaurant is linked to this account.\"}");
            return;
        }

        switch (action) {

            case "toggleStatus": {
                restaurant.setIsActive(!restaurant.getIsActive());
                restaurantDao.updateRestaurant(restaurant);
                out.print("{\"isActive\":" + restaurant.getIsActive() + "}");
                break;
            }

            case "markPrepared": {
                int o_id = parseIntOrDefault(req.getParameter("o_id"), -1);
                OrderTableDao cartDao = new OrderTableImplement();
                OrderTable order = cartDao.getOrderTable(o_id);

                if (order == null || order.getRe_id() != restaurant.getR_id()) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"error\":\"That order doesn't belong to your restaurant.\"}");
                    return;
                }
                if (!"confirmed".equalsIgnoreCase(order.getStatus())) {
                    out.print("{\"error\":\"Order isn't in a state that can be marked prepared.\"}");
                    return;
                }
                order.setStatus("prepared");
                cartDao.updateOrderTable(order);
                out.print("{\"success\":true,\"o_id\":" + o_id + "}");
                break;
            }

            case "toggleAvailability": {
                int m_id = parseIntOrDefault(req.getParameter("m_id"), -1);
                MenuDao menuDao = new MenuImplementation();
                Menu menu = menuDao.getItem(m_id);

                if (menu == null || menu.getRe_id() != restaurant.getR_id()) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"error\":\"That item doesn't belong to your restaurant.\"}");
                    return;
                }
                menu.setAvailable(!menu.isAvailable());
                menuDao.updateItem(menu);
                out.print("{\"available\":" + menu.isAvailable() + "}");
                break;
            }

            case "updatePrice": {
                int m_id = parseIntOrDefault(req.getParameter("m_id"), -1);
                int newPrice = parseIntOrDefault(req.getParameter("price"), -1);

                if (newPrice <= 0) {
                    out.print("{\"error\":\"Price must be greater than zero.\"}");
                    return;
                }
                MenuDao menuDao = new MenuImplementation();
                Menu menu = menuDao.getItem(m_id);

                if (menu == null || menu.getRe_id() != restaurant.getR_id()) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"error\":\"That item doesn't belong to your restaurant.\"}");
                    return;
                }
                menu.setPrice(newPrice);
                menuDao.updateItem(menu);
                out.print("{\"success\":true,\"m_id\":" + m_id + ",\"price\":" + newPrice + "}");
                break;
            }

            case "addMenuItem": {
                String name = trimOrNull(req.getParameter("name"));
                String desc = trimOrNull(req.getParameter("desc"));
                String imagePath = trimOrNull(req.getParameter("imagePath"));
                int price = parseIntOrDefault(req.getParameter("price"), -1);

                if (name == null || name.isEmpty() || price <= 0) {
                    out.print("{\"error\":\"A dish name and a valid price are required.\"}");
                    return;
                }
                MenuDao menuDao = new MenuImplementation();
                Menu newItem = new Menu();
                newItem.setName(name);
                newItem.setDesc(desc == null ? "" : desc);
                newItem.setPrice(price);
                newItem.setImagePath(imagePath == null ? "" : imagePath);
                newItem.setAvailable(true);
                newItem.setRe_id(restaurant.getR_id()); // never from the client
                menuDao.addItem(newItem);
                out.print("{\"success\":true}");
                break;
            }

            case "markDispatched": {
                int o_id = parseIntOrDefault(req.getParameter("o_id"), -1);
                OrderTableDao cartDao = new OrderTableImplement();
                OrderTable order = cartDao.getOrderTable(o_id);

                if (order == null || order.getRe_id() != restaurant.getR_id()) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"error\":\"That order doesn't belong to your restaurant.\"}");
                    return;
                }
                if (!"rider_confirmed".equalsIgnoreCase(order.getStatus())) {
                    out.print("{\"error\":\"A rider hasn't accepted this order yet.\"}");
                    return;
                }
                order.setStatus("dispatched");
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

    private String trimOrNull(String s) {
        return (s == null) ? null : s.trim();
    }
}