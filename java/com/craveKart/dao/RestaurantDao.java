package com.craveKart.dao;

import java.util.List;
import java.util.Set;

import com.craveKart.model.Restaurant;

public interface RestaurantDao {
	
	public void addRestaurant(Restaurant restaurant);

	public void deleteRestaurant(int r_id);

	public void updateRestaurant(Restaurant restaurant);

	public Restaurant getRestaurant(int r_id);
	
	public Restaurant getRestaurantByManagerId(int restaurantManagerid);
	
	public List<Restaurant> getAllRestaurant();
	
	public List<Restaurant> getARestaurants(Set<Integer> res);

}
