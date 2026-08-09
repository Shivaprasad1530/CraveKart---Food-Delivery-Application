package com.craveKart.dao;

import java.util.List;
import com.craveKart.model.OrderItem;

public interface OrderItemDao {

    void addOrderItem(OrderItem oi);
    void deleteOrderItem(int oi_id);
    void updateOrderItem(OrderItem oi);
    OrderItem getOrderItem(int oi_id);
    List<OrderItem> getAllOrderItem();

    // NEW: all line items belonging to one cart (o_id) — powers cart.jsp
    // and the qty-map used to pre-render steppers on menu.jsp
    List<OrderItem> getItemsByOrderId(int o_id);

    // NEW: does this cart already contain this menu item? — needed so
    // "Add" increments an existing row instead of creating duplicates
    OrderItem getOrderItemByOrderAndMenu(int o_id, int me_id);

    // NEW: wipe every line item when a cart is abandoned (restaurant switch)
    void deleteAllByOrderId(int o_id);
}