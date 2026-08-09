package com.craveKart.model;

import java.util.Date;

public class OrderTable {
	private int u_id;
	private int o_id;
	private Date order_date;
	private int total_amt;
	private String status;
	private String payment_mode;
	private int re_id;
	private Integer deliveryAgentId;

	
	
	public OrderTable() {}

	public OrderTable(int u_id, int o_id, Date order_date, int total_amt, String status, String payment_mode,
			int re_id) {
		super();
		this.u_id = u_id;
		this.o_id = o_id;
		this.order_date = order_date;
		this.total_amt = total_amt;
		this.status = status;
		this.payment_mode = payment_mode;
		this.re_id = re_id;
	}

	public int getU_id() {
		return u_id;
	}

	public void setU_id(int u_id) {
		this.u_id = u_id;
	}

	public int getO_id() {
		return o_id;
	}

	public void setO_id(int o_id) {
		this.o_id = o_id;
	}

	public Date getOrder_date() {
		return order_date;
	}

	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}

	public int getTotal_amt() {
		return total_amt;
	}

	public void setTotal_amt(int total_amt) {
		this.total_amt = total_amt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPayment_mode() {
		return payment_mode;
	}

	public void setPayment_mode(String payment_mode) {
		this.payment_mode = payment_mode;
	}

	public int getRe_id() {
		return re_id;
	}

	public void setRe_id(int re_id) {
		this.re_id = re_id;
	}
	
	public Integer getDeliveryAgentId() {
	    return deliveryAgentId;
	}

	public void setDeliveryAgentId(Integer deliveryAgentId) {
	    this.deliveryAgentId = deliveryAgentId;
	}

	@Override
	public String toString() {
		return "OrderTable [u_id=" + u_id + ", o_id=" + o_id + ", order_date=" + order_date + ", total_amt=" + total_amt
				+ ", status=" + status + ", payment_mode=" + payment_mode + ", re_id=" + re_id + "]";
	};
	
	

}
