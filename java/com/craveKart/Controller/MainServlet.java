package com.craveKart.Controller;

/**
 * Main servlet it creates a list of all the restaurants in the db using model packages. 
 * The list of selected food items is also created here itself
 * Adds the list to the session then restaurant.jsp will take it from there
 * Restaurant.jsp is our actual homepage
 * 
 * */
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.craveKart.dao.MenuDao;
import com.craveKart.dao.RestaurantDao;
import com.craveKart.dao.UserDao;
import com.craveKart.daoImplementation.MenuImplementation;
import com.craveKart.daoImplementation.RestaurantImplementaion;
import com.craveKart.daoImplementation.UserDaoImpl;
import com.craveKart.model.Restaurant;
import com.craveKart.model.User;

@WebServlet("/RestaurantServlet")
public class MainServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RestaurantDao r = new RestaurantImplementaion();
		List restaurants = r.getAllRestaurant();// Gets the list of all restaurants with details
		HttpSession session = req.getSession();//Session creation
		session.setAttribute("restaurants", restaurants);// restaurant list added into session
		String username = (String) session.getAttribute("username");// Gets username from the session if user logins.														// after user logins.
		UserDao user = new UserDaoImpl();
		User u = user.getUserDetails(username);// Gets complete user details using the username
		
		//If the user role is restaurant manager he is taken to his restaurant dashboard
		if(u != null && u.getRole().equals("Restaurant manager")) {
			RequestDispatcher rd = req.getRequestDispatcher("/restaurantDashboard");// It connects to restaurant dashboard
			rd.include(req, resp);
			return;
		}
		//If the user role is delivery agent he is taken to his delivery dashboard
		else if (u != null && u.getRole().equalsIgnoreCase("Delivery Agent")) {
		    RequestDispatcher rd = req.getRequestDispatcher("/deliveryDashboard");
		    rd.forward(req, resp);
		    return;
		}
		//If the role is customer it shows the homepage where all restaurants are present. 
		else {
		session.setAttribute("user", u);// user object added to session
		
		//When user clicks on any particular dish on the homepage----
		MenuDao menu = new MenuImplementation();
		//Check if user has clicked on any cuisine  item if yes then creates a list of restaurants having that food item and adds it to session
		if (req.getParameter("cuisine") != null) {
			
			String cuisine = req.getParameter("cuisine");//Gets the food item user clicked.
			
			Set<Integer> res = menu.getRestauarant(cuisine);// Gets a set of restaurant_id's serving the particualr food item		
			List<Restaurant> rest = r.getARestaurants(res); //Gets a list of all restaurants with details whose id is present in set.
			//System.out.println(rest);
			session.setAttribute("menuRestaurants", rest); // Adds that restaurant list into session
			resp.sendRedirect("restaurant.jsp"); // Displays only them in the homepage
			return;
		}
	
		//When user enters any particular dish in search tag on the homepage----
		if(req.getParameter("dish") != null) {
			String dish = req.getParameter("dish");
		
			Set<Integer> res = menu.getMenuDish(dish);
			
			List<Restaurant> rest = r.getARestaurants(res);
		
			session.setAttribute("menuRestaurants", rest);
			resp.sendRedirect("restaurant.jsp");
			return;
		}

		RequestDispatcher rd = req.getRequestDispatcher("restaurant.jsp");// It connects to homepage
		rd.forward(req, resp);
		System.out.println("passed main servlet");
		}
	}

}
