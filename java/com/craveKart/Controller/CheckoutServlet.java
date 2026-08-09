package com.craveKart.Controller;

import java.io.IOException;
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
import com.craveKart.daoImplementation.MenuImplementation;
import com.craveKart.daoImplementation.OrderItemImplement;
import com.craveKart.model.Menu;
import com.craveKart.model.OrderItem;
import com.craveKart.model.OrderTable;
import com.craveKart.model.User;

/**
 * CheckoutServlet
 *
 * Loads the order summary for the current cart (same join pattern as
 * CartServlet) plus the user's saved address, and forwards to
 * checkout.jsp. Clears any leftover coupon state from a previous order.
 */
@WebServlet("/Checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        

        OrderTable cart = (OrderTable) session.getAttribute("cart");
        List<Menu> cartMenus = new ArrayList<>();
        List<OrderItem> cartOrderItems = new ArrayList<>();
        int subtotal = 0;

        if (cart != null) {
            OrderItemDao itemDao = new OrderItemImplement();
            MenuDao menuDao = new MenuImplementation();
            List<OrderItem> items = itemDao.getItemsByOrderId(cart.getO_id());
            for (OrderItem oi : items) {
                Menu m = menuDao.getItem(oi.getMe_id());
                if (m != null) {
                    cartMenus.add(m);
                    cartOrderItems.add(oi);
                    subtotal += oi.getItem_total();
                }
            }
        }

        // Fresh checkout visit -> don't carry over a coupon from a past order
        session.removeAttribute("couponApplied");
        session.removeAttribute("couponDiscount");

        request.setAttribute("cartMenus", cartMenus);
        request.setAttribute("cartOrderItems", cartOrderItems);
        request.setAttribute("subtotal", subtotal);

        RequestDispatcher rd = request.getRequestDispatcher("checkout.jsp");
        rd.forward(request, response);
    }
}
