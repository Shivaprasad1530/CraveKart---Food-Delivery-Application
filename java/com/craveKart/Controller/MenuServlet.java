package com.craveKart.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.craveKart.daoImplementation.MenuImplementation;
import com.craveKart.daoImplementation.OrderItemImplement;
import com.craveKart.daoImplementation.OrderTableImplement;
import com.craveKart.daoImplementation.RestaurantImplementaion;
import com.craveKart.model.Menu;
import com.craveKart.model.OrderItem;
import com.craveKart.model.OrderTable;
import com.craveKart.model.Restaurant;
import com.craveKart.model.User;

/**
 * MenuServlet
 *
 * Triggered when a user clicks on a restaurant menu link in restaurant.jsp.
 * Expects a request parameter "r_id" (the restaurant's primary key) and userId
 * users primary key
 *
 * Cart behavior: - session "cart" holds the OrderTable (o_id + re_id) for
 * whichever restaurant the user currently has open.
 * 
 * If the user opens a diff restaurant than the one in session, then the
 * current cart is deleted and a new one is created for new restaurant
 * 
 */
@WebServlet("/Menu")
public class MenuServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		//Gets the restaurant id of the current restaurant.
		String r_idParam = request.getParameter("r_id");
		int r_id = Integer.parseInt(r_idParam.trim());
		User user = (User) session.getAttribute("user");
		//If user has not login it will redirect to login page
		if (user == null) {
			request.setAttribute("Error", "We know you are very hungry but please login to view menu");
			RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
			rd.forward(request, response);
			return;
		}

		int userId = user.getId();
		OrderTableDao cartDao = new OrderTableImplement();
		OrderItemDao itemDao = new OrderItemImplement();

		OrderTable cart = (OrderTable) session.getAttribute("cart");

		// Switched restaurants (or first visit had a stale cart) -> wipe it
		if (cart != null && cart.getRe_id() != r_id) {
			itemDao.deleteAllByOrderId(cart.getO_id());
			cartDao.deleteOrderTable(cart.getO_id());
			cart = null;
			session.removeAttribute("cart");
		}

		// creates a new cart for restaurant 
		if (cart == null) {
			OrderTable ot = new OrderTable();
			ot.setU_id(userId);
			ot.setRe_id(r_id);
			ot.setTotal_amt(0);
			ot.setStatus("initialised"); 
			int newOid = cartDao.addOrdertable(ot); //creates a new cart and returns o_id [primary key] of ordertable.
			ot.setO_id(newOid);
			cart = ot;
			session.setAttribute("cart", cart);
		}

		// Build qty-map so menu.jsp can pre-render steppers for items
		// already in this cart (e.g. user navigated back from cart page)
		List<OrderItem> existingItems = itemDao.getItemsByOrderId(cart.getO_id());
		Map<Integer, Integer> cartQtyMap = new HashMap<>();
		int cartCount = 0, cartTotal = 0;
		for (OrderItem oi : existingItems) {
			cartQtyMap.put(oi.getMe_id(), oi.getQuantity());
			cartCount += oi.getQuantity();
			cartTotal += oi.getItem_total();
		}
		session.setAttribute("cartCount", cartCount);
		session.setAttribute("cartTotal", cartTotal);

		//gets restaurant id and gets the list of items in the specified restaurant
		
		RestaurantDao rt = new RestaurantImplementaion();
		Restaurant r = rt.getRestaurant(r_id);
		boolean r_active = r.getIsActive();
		MenuDao menuDAO = new MenuImplementation();
		List<Menu> menuList = menuDAO.restaurantMenu(r_id);

		session.setAttribute("menuList", menuList);
		session.setAttribute("r_active", r_active);
		session.setAttribute("cartQtyMap", cartQtyMap);

		RequestDispatcher rd = request.getRequestDispatcher("Menu.jsp");
		rd.forward(request, response);
	}
}