package com.craveKart.model;

public class Menu {
	private int m_id;
	private int re_id;
	private String name;
	private String desc;
	private int price;
	private boolean isAvailable;
	private String imagePath;
	
	public Menu() {}
	
	public Menu(int m_id, int re_id, String name, String desc, int price, boolean isAvailable, String imagePath) {
		super();
		this.m_id = m_id;
		this.re_id = re_id;
		this.name = name;
		this.desc = desc;
		this.price = price;
		this.isAvailable = isAvailable;
		this.imagePath = imagePath;
	}

	public int getM_id() {
		return m_id;
	}

	public void setM_id(int m_id) {
		this.m_id = m_id;
	}

	public int getRe_id() {
		return re_id;
	}

	public void setRe_id(int re_id) {
		this.re_id = re_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Override
	public String toString() {
		return "Menu [m_id=" + m_id + ", re_id=" + re_id + ", name=" + name + ", desc=" + desc + ", price=" + price
				+ ", isAvailable=" + isAvailable + ", imagePath=" + imagePath + "]";
	}
	
	
	
	
}
