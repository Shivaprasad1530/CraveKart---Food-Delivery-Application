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
 * CartServlet
 *
 * Loads the current cart and its line items, joined with the Menu details needed to render them (name/price/image), then forwards to cart.jsp.
 *  Two parallel lists (cartMenus / cartOrderItems) are used
 */
@WebServlet("/Cart") 
public class CartServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        OrderTable cart = (OrderTable) session.getAttribute("cart"); //gets cart
        List<Menu> cartMenus = new ArrayList<>();
        List<OrderItem> cartOrderItems = new ArrayList<>();
        int cartTotal = 0;
        //When user adds any item
        if (cart != null) {
            OrderItemDao itemDao = new OrderItemImplement();
            MenuDao menuDao = new MenuImplementation();
            //Gets list of items user adds to cart
            List<OrderItem> items = itemDao.getItemsByOrderId(cart.getO_id());
            for (OrderItem oi : items) {
            	//gets item details which user has added
                Menu m = menuDao.getItem(oi.getMe_id());
                if (m.isAvailable()) {//checks menu item is available
                    cartMenus.add(m);//adds menu details to cartMenus list 
                    cartOrderItems.add(oi); // adds quantity of items 
                    cartTotal += oi.getItem_total();//calculates total upon adding the items 
                }
            }
        }

        request.setAttribute("cartMenus", cartMenus);
        request.setAttribute("cartOrderItems", cartOrderItems);
        request.setAttribute("cartTotal", cartTotal);

        RequestDispatcher rd = request.getRequestDispatcher("cart.jsp");
        rd.forward(request, response);
    }
}