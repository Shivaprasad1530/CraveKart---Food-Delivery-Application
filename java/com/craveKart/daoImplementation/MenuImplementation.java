package com.craveKart.daoImplementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.craveKart.dao.MenuDao;
import com.craveKart.model.Menu;

public class MenuImplementation implements MenuDao {
	static Connection connect;
	static Statement stmt;
	static PreparedStatement pstmt;
	static ResultSet res;

	static final String INSERT_QUERY = "INSERT INTO menu (name, description, price, isAvailable, imagePath, re_id) values (?,?,?,?,?,?)";
	static final String DELETE_QUERY = "DELETE FROM menu WHERE m_id = ?";
	static final String UPDATE_QUERY = "UPDATE menu SET name=?, description=?, price = ?, isAvailable=?, imagePath = ?, re_id = ? WHERE m_id = ?";
	static final String GET_QUERY = "SELECT * FROM menu WHERE m_id = ?";
	static final String MENU_NAME_QUERY = "SELECT * FROM menu WHERE name LIKE ?";
	static final String SEARCH_DISH_QUERY = "SELECT * FROM menu WHERE name LIKE ? OR name LIKE ? OR name LIKE ?";
	static final String RESTAURANT_QUERY = "SELECT * FROM menu WHERE re_id = ?";
	static final String GETALL_QUERY = "SELECT * FROM menu";

	public MenuImplementation() {
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
	public void addItem(Menu menu) {
		try {
			pstmt = connect.prepareStatement(INSERT_QUERY);
			pstmt.setString(1, menu.getName());
			pstmt.setString(2, menu.getDesc());
			pstmt.setInt(3, menu.getPrice());
			pstmt.setBoolean(4, menu.isAvailable());
			pstmt.setString(5, menu.getImagePath());
			pstmt.setInt(6, menu.getRe_id());
			System.out.println(pstmt.executeUpdate() + " row updated");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteItem(int m_id) {
		try {
			pstmt = connect.prepareStatement(DELETE_QUERY);
			pstmt.setInt(1, m_id);
			System.out.println(pstmt.executeUpdate());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateItem(Menu menu) {
		try {
			pstmt = connect.prepareStatement(UPDATE_QUERY);
			pstmt.setString(1, menu.getName());
			pstmt.setString(2, menu.getDesc());
			pstmt.setInt(3, menu.getPrice());
			pstmt.setBoolean(4, menu.isAvailable());
			pstmt.setString(5, menu.getImagePath());
			pstmt.setInt(6, menu.getRe_id());
			pstmt.setInt(7, menu.getM_id());
			System.out.println(pstmt.executeUpdate() + " row updated");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Menu getItem(int m_id) {
		Menu m = null;
		try {
			pstmt = connect.prepareStatement(GET_QUERY);
			pstmt.setInt(1, m_id);
			res = pstmt.executeQuery();
			if (res.next()) {
				m = new Menu();
				m.setM_id(res.getInt("m_id"));
				m.setName(res.getString("name"));
				m.setDesc(res.getString("description"));
				m.setPrice(res.getInt("price"));
				m.setImagePath(res.getString("imagepath"));
				m.setAvailable(res.getBoolean("isAvailable"));
				m.setRe_id(res.getInt("re_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return m;
	}

	@Override
	public List<Menu> getMenu() {
		// TODO Auto-generated method stub
		List<Menu> arr = new ArrayList<>();
		Menu m =null;
		try {
		stmt = connect.createStatement();
		res = stmt.executeQuery(GETALL_QUERY);
		while(res.next()) {
			m = new Menu();
			m.setM_id(res.getInt("m_id"));
			m.setName(res.getString("name"));
			m.setDesc(res.getString("description"));
			m.setPrice(res.getInt("price"));
			m.setImagePath(res.getString("imagepath"));
			m.setAvailable(res.getBoolean("isAvailable"));
			m.setRe_id(res.getInt("re_id"));
			arr.add(m);
		}
		
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return arr;
	}

	@Override
	public List<Menu> restaurantMenu(int r_id) {
		List<Menu> arr = new ArrayList<>();
		Menu m =null;
		try {
		pstmt = connect.prepareStatement(RESTAURANT_QUERY);
		pstmt.setInt(1, r_id);
		res = pstmt.executeQuery();
		while(res.next()) {
			m = new Menu();
			m.setM_id(res.getInt("m_id"));
			m.setName(res.getString("name"));
			m.setDesc(res.getString("description"));
			m.setPrice(res.getInt("price"));
			m.setImagePath(res.getString("imagepath"));
			m.setAvailable(res.getBoolean("isAvailable"));
			m.setRe_id(res.getInt("re_id"));
			arr.add(m);
		}
		
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return arr;
	}

	@Override
	public Set<Integer> getRestauarant(String name) {
		Set<Integer> r = new LinkedHashSet<>();
		try {
		pstmt = connect.prepareStatement(MENU_NAME_QUERY);
		pstmt.setString(1, "%"+name);
		res = pstmt.executeQuery();
		System.out.println(name);
		while(res.next()) {
			Menu m = new Menu();
			r.add(res.getInt("re_id"));
		}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return r;
	}

	@Override
	public Set<Integer> getMenuDish(String name) {
		Set<Integer> r = new LinkedHashSet<>();
		try {
		pstmt = connect.prepareStatement(SEARCH_DISH_QUERY);
		pstmt.setString(1, "%"+name);
		pstmt.setString(2, name+"%");
		pstmt.setString(3, "%"+name+"%");
		res = pstmt.executeQuery();
		//System.out.println(name);
		while(res.next()) {
			Menu m = new Menu();
			r.add(res.getInt("re_id"));
		}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return r;
	}

}
