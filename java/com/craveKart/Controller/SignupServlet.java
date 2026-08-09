package com.craveKart.Controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.craveKart.dao.UserDao;
import com.craveKart.daoImplementation.UserDaoImpl;
import com.craveKart.model.User;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("service started");
		String name = req.getParameter("name");
		String email = req.getParameter("email");
		String phone = req.getParameter("phone");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
//		System.out.println(name);
//		System.out.println(username);
		UserDao a = new UserDaoImpl();
		if(a.usernameExists(username)) {
			req.setAttribute("Error", "That Username taken by someone more hungry! Please choose other one");
			RequestDispatcher rd = req.getRequestDispatcher("signup.jsp");
			rd.forward(req, resp);
			return;
		}
		else if(a.emailExists(email)) {
			req.setAttribute("Error", "Email is already registered");
			RequestDispatcher rd = req.getRequestDispatcher("signup.jsp");
			rd.forward(req, resp);
			return;
		}
		
		else {
		User user = new User();
		user.setName(name);
		user.setEmail(email);
		user.setPhone(phone);
		user.setUsername(username);
		user.setPassword(password);
		a.addUser(user);
	//	System.out.println("User: " + user);
		resp.sendRedirect("login.jsp");
		
		
		}
	}

}
