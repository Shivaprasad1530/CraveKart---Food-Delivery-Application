package com.craveKart.model;

public class Restaurant {
	private int r_id;
	private String name;
	private String imagePath;
	private float rating;
	private byte epa;
	private String cuisine;
	private boolean isActive;
	private String address;
	private int restaurantManagerid;

	public Restaurant() {

	}

	public Restaurant(int r_id, String name, String imagePath, float rating, byte epa, String cuisine, boolean isActive,
			String address, int restaurantManagerid) {
		super();
		this.r_id = r_id;
		this.name = name;
		this.imagePath = imagePath;
		this.rating = rating;
		this.setEpa(epa);
		this.cuisine = cuisine;
		this.isActive = isActive;
		this.address = address;
		this.restaurantManagerid = restaurantManagerid;
	}

	public int getR_id() {
		return r_id;
	}

	public void setR_id(int r_id) {
		this.r_id = r_id;
	}

	public String getname() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public String getCuisine() {
		return cuisine;
	}

	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getRestaurantManagerid() {
		return restaurantManagerid;
	}

	public void setRestaurantManagerid(int restaurantManagerid) {
		this.restaurantManagerid = restaurantManagerid;
	}

	public byte getEpa() {
		return epa;
	}

	public void setEpa(byte epa) {
		this.epa = epa;
	}

	@Override
	public String toString() {
		return "Restaurant [r_id=" + r_id + ", name=" + name + ", imagePath=" + imagePath + ", rating=" + rating
				+ ", epa=" + epa + ", cuisine=" + cuisine + ", isActive=" + isActive + ", address=" + address
				+ ", restaurantManagerid=" + restaurantManagerid + "]";
	}

	
	
	
}

	