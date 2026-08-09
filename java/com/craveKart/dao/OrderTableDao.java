package com.craveKart.dao;

import java.util.List;
import com.craveKart.model.OrderTable;

public interface OrderTableDao {

    // CHANGED: now returns the generated o_id (was void). Needed so we can
    // link orderitem rows to this cart right after creating it.
    int addOrdertable(OrderTable ot);

    void deleteOrderTable(int o_id);
    void updateOrderTable(OrderTable ot);
    OrderTable getOrderTable(int o_id);
    List<OrderTable> getAllOrderTable();

    // NEW: every order for a restaurant sitting at a given status — powers
    // the restaurant manager dashboard ("show me all confirmed orders")
    List<OrderTable> getOrdersByRestaurantAndStatus(int re_id, String status);

    // NEW: orders ready for pickup that no delivery agent has claimed yet —
    // powers the delivery agent's "available orders" list
    List<OrderTable> getAvailableOrdersForPickup();

    // NEW: this agent's current in-progress delivery, if any (rider_confirmed/
    // dispatched/reached_location) — an agent can only have one at a time
    OrderTable getActiveOrderForAgent(int agentId);

    // NEW: atomically claims an order for an agent — only succeeds if it's
    // still 'prepared' and unclaimed, so two agents can't both grab it in a race
    boolean claimOrderForAgent(int o_id, int agentId);
}