package com.craveKart.dao;
import java.util.List;

import com.craveKart.model.User;
public interface UserDao {
	
	public void addUser(User user);
	
	public void updateUser(User user);
	
	public User getUser(int id);
	
	public void deleteUser(int id);
	
	public boolean usernameExists(String username);
	
	public boolean emailExists(String email);
	
	public boolean login(String username, String password);
	
	public User getUserDetails(String username);
	
	List<User> getAllUsers();
}
