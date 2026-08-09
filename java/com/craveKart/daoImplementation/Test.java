package com.craveKart.daoImplementation;
import com.craveKart.dao.*;
import com.craveKart.model.*;
import com.craveKart.daoImplementation.*;
public class Test {

	public static void main(String[] args) {
		UserDao u = new UserDaoImpl();
		User user = new User();
		user.setName("Sourabh");
		user.setEmail("SoU@gmail");
		user.setAddress("BTM");
		user.setUsername("sourabh1");
		

	}

}
