package com.craveKart.Controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.craveKart.dao.UserDao;
import com.craveKart.daoImplementation.UserDaoImpl;
import com.mysql.cj.Session;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		UserDao user = new UserDaoImpl();
		if(user.login(username, password)) {
			Cookie c1 = new Cookie("username", username);
			Cookie c2 = new Cookie("password", password);
			resp.addCookie(c1);
			resp.addCookie(c2);
			HttpSession session = req.getSession();
			session.setAttribute("username", username);
			resp.sendRedirect("RestaurantServlet");
		}else {
			req.setAttribute("Error", "Invalid username or password");
			RequestDispatcher rd = req.getRequestDispatcher("login.jsp");
			rd.forward(req, resp);
			return;
		}
	}

}
