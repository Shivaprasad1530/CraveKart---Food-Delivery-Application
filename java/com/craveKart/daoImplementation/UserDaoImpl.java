        package com.craveKart.daoImplementation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.craveKart.dao.UserDao;
import com.craveKart.model.User;

public class UserDaoImpl implements UserDao {

	static Connection connect;
	static Statement stmt;
	static PreparedStatement pstmt;
	static ResultSet res;
	static final String INSERT_QUERY = "INSERT INTO user (name,email,phoneNo,userName,password) VALUES (?,?,?,?,?)";
	static final String UPDATE_QUERY = "UPDATE user SET name = ?,email=?,phoneNo=?,address=?,userName=?,password=?,role=? WHERE userId = ?";
	static final String GET_QUERY = "SELECT * FROM user WHERE userId = ?";	
	static final String DELETE_QUERY = "DELETE FROM user WHERE userId =?";
	static final String GET_ALL_QUERY = "SELECT * FROM user";
	static final String USERNAME_QUERY = "SELECT * FROM user WHERE username = ?";
	static final String EMAIL_QUERY = "SELECT * FROM user WHERE email = ?";
//	static final String GET_ID_QUERY = "SELECT userId FROM user WHERE username = ?";
	static final String LOGIN_QUERY = "SELECT * FROM user WHERE username = ? AND password = ?";
	

	public UserDaoImpl() {
		String url = "jdbc:mysql://localhost:3306/tapfoods";
		String user = "root";
		String pass = "password";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connect = DriverManager.getConnection(url, user, pass);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addUser(User user) {
		try {
			pstmt = connect.prepareStatement(INSERT_QUERY);
			pstmt.setString(1, user.getName());
			pstmt.setString(2, user.getEmail());
			pstmt.setString(3, user.getPhone());
			pstmt.setString(4, user.getUsername());
			pstmt.setString(5, user.getPassword());			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateUser(User user) {
		try {
			pstmt = connect.prepareStatement(UPDATE_QUERY);
			pstmt.setString(1, user.getName());
			pstmt.setString(2, user.getEmail());
			pstmt.setString(3, user.getPhone());
			pstmt.setString(4, user.getAddress());
			pstmt.setString(5, user.getUsername());
			pstmt.setString(6, user.getPassword());
			pstmt.setString(7, user.getRole());
			pstmt.setInt(8, user.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public User getUser(int id) {
		User user = null;
		try {
			pstmt = connect.prepareStatement(GET_QUERY);
			pstmt.setInt(1, id);
			res = pstmt.executeQuery();
			if (res.next()) {
				String name = res.getString("name");
				String email = res.getString("email");
				String phone = res.getString("phoneNo");
				String address = res.getString("address");
				String username = res.getString("username");
				String pass = res.getString("password");
				String role = res.getString("role");
				Date created = res.getDate("createDate");
				Date lastLogin = res.getDate("lastLogin");
				user = new User(id, name, email, phone, address, username, pass, role, created, lastLogin);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public void deleteUser(int id) {
		try {
		pstmt = connect.prepareStatement(DELETE_QUERY);
		pstmt.setInt(1, id);
		pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<User> getAllUsers() {
		ArrayList<User> arr = new ArrayList<>();
		User user = null;
		try {
			stmt = connect.createStatement();
			
			res = stmt.executeQuery(GET_ALL_QUERY);
			while (res.next()) {
				int id = res.getInt("id");
				String name = res.getString("name");
				String email = res.getString("email");
				String phone = res.getString("phoneNo");
				String address = res.getString("address");
				String username = res.getString("username");
				String pass = res.getString("password");
				String role = res.getString("role");
				Date created = res.getDate("createDate");
				Date lastLogin = res.getDate("lastLogin");
				user = new User(id, name, email, phone, address, username, pass, role, created, lastLogin);
				arr.add(user);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return arr;
	}

	@Override
	public boolean usernameExists(String username) {
		try {
			pstmt = connect.prepareStatement(USERNAME_QUERY);
			pstmt.setString(1, username);
			res = pstmt.executeQuery();
			return res.next(); 
		}catch(SQLException s) {
			s.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean emailExists(String email) {
		try {
			pstmt = connect.prepareStatement(EMAIL_QUERY);
			pstmt.setString(1, email);
			res = pstmt.executeQuery();
			return res.next(); 
		}catch(SQLException s) {
			s.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean login(String username, String password) {
		try {
			pstmt = connect.prepareStatement(LOGIN_QUERY);
			pstmt.setString(1, username);
			pstmt.setString(2, password);		
			res = pstmt.executeQuery();
			return res.next(); 
		}catch(SQLException s) {
			s.printStackTrace();
		}
		return false;
	}

	@Override
	public User getUserDetails(String username) {
		User user = null;
		try {
			pstmt = connect.prepareStatement(USERNAME_QUERY);
			pstmt.setString(1, username);
			res = pstmt.executeQuery();
			if (res.next()) {
				String name = res.getString("name");
				String email = res.getString("email");
				String phone = res.getString("phoneNo");
				String address = res.getString("address");
				int id = res.getInt("userId");
				String pass = res.getString("password");
				String role = res.getString("role");
				Date created = res.getDate("createDate");
				Date lastLogin = res.getDate("lastLogin");
				user = new User(id, name, email, phone, address, username, pass, role, created, lastLogin);
			}
			}catch(SQLException e) {
				e.printStackTrace();
			}
		return user;
	
	}
}
