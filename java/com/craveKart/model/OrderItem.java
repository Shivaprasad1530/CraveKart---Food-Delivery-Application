package com.craveKart.model;

public class OrderItem {
	private int o_id;
	private int oi_id;
	private int quantity;
	private int item_total;
	private int me_id;
	
	public OrderItem() {
		
	}

	public OrderItem(int o_id, int oi_id, int quantity, int item_total, int me_id) {
		super();
		this.o_id = o_id;
		this.oi_id = oi_id;
		this.quantity = quantity;
		this.item_total = item_total;
		this.me_id = me_id;
	}

	public int getO_id() {
		return o_id;
	}

	public void setO_id(int o_id) {
		this.o_id = o_id;
	}

	public int getOi_id() {
		return oi_id;
	}

	public void setOi_id(int ot_id) {
		this.oi_id = ot_id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getItem_total() {
		return item_total;
	}

	public void setItem_total(int item_total) {
		this.item_total = item_total;
	}

	public int getMe_id() {
		return me_id;
	}

	public void setMe_id(int me_id) {
		this.me_id = me_id;
	}

	@Override
	public String toString() {
		return "OrderItem [o_id=" + o_id + ", ot_id=" + oi_id + ", quantity=" + quantity + ", item_total=" + item_total
				+ ", me_id=" + me_id + "]";
	}
	
	

}
