package com.craveKart.dao;
import java.util.List;
import java.util.Set;

import com.craveKart.model.Menu;
public interface MenuDao {
	
	public void addItem(Menu menu);

	public void deleteItem(int m_id);
	
	public void updateItem(Menu menu);
	
	public Menu getItem(int m_id);
	
	public Set<Integer> getMenuDish(String name);
	
	public Set<Integer> getRestauarant(String name);
	
	public List<Menu> getMenu();
	
	public List<Menu> restaurantMenu(int r_id);
}
