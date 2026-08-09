package com.craveKart.daoImplementation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.craveKart.dao.RestaurantDao;
import com.craveKart.model.Restaurant;

public class RestaurantImplementaion implements RestaurantDao {

	static Connection connect;
	static Statement stmt;
	static PreparedStatement pstmt;
	static ResultSet res;

	static final String INSERT_QUERY = "INSERT INTO restaurant (name,imagePath,ratings,cuisine,address,isActive,epa) values (?,?,?,?,?,?,?)";
	static final String DELETE_QUERY = "DELETE FROM restaurant WHERE r_id = ?";
	static final String UPDATE_QUERY = "UPDATE restaurant SET name=?, imagePath=?, ratings=?, epa=?, cuisine=?, isActive=?, address=?, restaurantManagerId=? WHERE r_id=?";
	static final String GET_QUERY = "SELECT * FROM restaurant WHERE r_id = ?";
	static final String GET_BY_MANAGER_QUERY = "SELECT * FROM restaurant WHERE restaurantManagerid = ?";	
	static final String GETALL_QUERY = "SELECT * FROM restaurant";

	public RestaurantImplementaion() {
		// System.out.println("Constructor called");
		String url = "jdbc:mysql://localhost:3306/tapfoods";
		String user = "root";
		String pass = "password";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connect = DriverManager.getConnection(url, user, pass);
			// System.out.println("Connection: "+ connect);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addRestaurant(Restaurant restaurant) {
		try {
			pstmt = connect.prepareStatement(INSERT_QUERY);
			pstmt.setString(1, restaurant.getname());
			pstmt.setString(2, restaurant.getImagePath());
			pstmt.setFloat(3, restaurant.getRating());
			pstmt.setString(4, restaurant.getCuisine());
			pstmt.setString(5, restaurant.getAddress());
			pstmt.setBoolean(6, restaurant.getIsActive());
			pstmt.setInt(7, restaurant.getEpa());
			// pstmt.setInt(8, restaurant.getRestaurantManagerid());
			System.out.println(pstmt.executeUpdate() + " row updated");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteRestaurant(int r_id) {
		try {
			pstmt = connect.prepareStatement(DELETE_QUERY);
			pstmt.setInt(1, r_id);
			System.out.println(pstmt.executeUpdate() + " row updated");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateRestaurant(Restaurant restaurant) {
		try {
			PreparedStatement pstmt = connect.prepareStatement(UPDATE_QUERY);

			pstmt.setString(1, restaurant.getname());
			pstmt.setString(2, restaurant.getImagePath());
			pstmt.setDouble(3, restaurant.getRating());
			pstmt.setInt(4, restaurant.getEpa());
			pstmt.setString(5, restaurant.getCuisine());
			pstmt.setBoolean(6, restaurant.getIsActive());
			pstmt.setString(7, restaurant.getAddress());
			pstmt.setInt(8, restaurant.getRestaurantManagerid());
			pstmt.setInt(9, restaurant.getR_id());

			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Restaurant getRestaurant(int r_id) {
		Restaurant restaurant = null;
		try {
			PreparedStatement pstmt = connect.prepareStatement(GET_QUERY);

			pstmt.setInt(1, r_id);

			res = pstmt.executeQuery();

			if (res.next()) {

				restaurant = new Restaurant();

				restaurant.setR_id(res.getInt("r_id"));
				restaurant.setName(res.getString("name"));
				restaurant.setImagePath(res.getString("imagePath"));
				restaurant.setRating(res.getFloat("ratings"));
				restaurant.setEpa(res.getByte("epa"));
				restaurant.setCuisine(res.getString("cuisine"));
				restaurant.setIsActive(res.getBoolean("isActive"));
				restaurant.setAddress(res.getString("address"));
				restaurant.setRestaurantManagerid(res.getInt("restaurantManagerId"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return restaurant;
	}

	@Override
	public List<Restaurant> getAllRestaurant() {
		List<Restaurant> restaurantList = new ArrayList<>();
		try {

			stmt = connect.createStatement();

			res = stmt.executeQuery(GETALL_QUERY);

			while (res.next()) {

				Restaurant restaurant = new Restaurant();

				restaurant.setR_id(res.getInt("r_id"));
				restaurant.setName(res.getString("name"));
				restaurant.setImagePath(res.getString("imagePath"));
				restaurant.setRating(res.getFloat("ratings"));
				restaurant.setEpa(res.getByte("epa"));
				restaurant.setCuisine(res.getString("cuisine"));
				restaurant.setIsActive(res.getBoolean("isActive"));
				restaurant.setAddress(res.getString("address"));
				restaurant.setRestaurantManagerid(res.getInt("restaurantManagerid"));

				restaurantList.add(restaurant);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return restaurantList;
	}

	@Override
	public List<Restaurant> getARestaurants(Set<Integer> restaurants) {
		List<Restaurant> restaurantList = new ArrayList<>();
		StringBuilder query = new StringBuilder("SELECT * FROM restaurant WHERE ");
		//Selects all the restaurants from the set
		for (int i = 0; i < restaurants.size(); i++) {
			query.append("r_id = ?");
			if (i < restaurants.size() - 1) {
				query.append(" OR ");
			}
		}
		String rest = query.toString();
		//System.out.println(rest);
		try {
			pstmt = connect.prepareStatement(rest);
			int j=1;
			//Adds the restaurantId's from the set into the string
			for(int i : restaurants) {
				pstmt.setInt(j,i);
				j++;
			}
			res = pstmt.executeQuery();

			while (res.next()) {

				Restaurant restaurant = new Restaurant();

				restaurant.setR_id(res.getInt("r_id"));
				restaurant.setName(res.getString("name"));
				restaurant.setImagePath(res.getString("imagePath"));
				restaurant.setRating(res.getFloat("ratings"));
				restaurant.setEpa(res.getByte("epa"));
				restaurant.setCuisine(res.getString("cuisine"));
				restaurant.setIsActive(res.getBoolean("isActive"));
				restaurant.setAddress(res.getString("address"));
				restaurant.setRestaurantManagerid(res.getInt("restaurantManagerid"));
				restaurantList.add(restaurant);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return restaurantList;
	}

	@Override
	public Restaurant getRestaurantByManagerId(int restaurantManagerid) {
		Restaurant restaurant = null;
		try {
			PreparedStatement pstmt = connect.prepareStatement(GET_BY_MANAGER_QUERY);

			pstmt.setInt(1, restaurantManagerid);

			res = pstmt.executeQuery();

			if (res.next()) {

				restaurant = new Restaurant();

				restaurant.setR_id(res.getInt("r_id"));
				restaurant.setName(res.getString("name"));
				restaurant.setImagePath(res.getString("imagePath"));
				restaurant.setRating(res.getFloat("ratings"));
				restaurant.setEpa(res.getByte("epa"));
				restaurant.setCuisine(res.getString("cuisine"));
				restaurant.setIsActive(res.getBoolean("isActive"));
				restaurant.setAddress(res.getString("address"));
				restaurant.setRestaurantManagerid(res.getInt("restaurantManagerId"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return restaurant;
	}

}
