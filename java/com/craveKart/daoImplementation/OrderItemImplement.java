package com.craveKart.daoImplementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.craveKart.dao.OrderItemDao;
import com.craveKart.model.OrderItem;

public class OrderItemImplement implements OrderItemDao {

    static Connection connect;
    static Statement stmt;
    static PreparedStatement pstmt;
    static ResultSet res;

    static final String INSERT_QUERY = "INSERT INTO orderitem (o_id, quantity, item_total, me_id) values (?,?,?,?)";
    static final String DELETE_QUERY = "DELETE FROM orderitem WHERE oi_id = ?";
    static final String UPDATE_QUERY ="UPDATE orderitem SET o_id=?, quantity=?, item_total = ?, me_id=? WHERE oi_id = ?";
    static final String GET_QUERY = "SELECT * FROM orderitem WHERE oi_id = ?";
    static final String GETALL_QUERY = "SELECT * FROM orderitem";

    // NEW queries
    static final String ITEMS_BY_ORDER_QUERY = "SELECT * FROM orderitem WHERE o_id = ?";
    static final String ITEM_BY_ORDER_AND_MENU_QUERY = "SELECT * FROM orderitem WHERE o_id = ? AND me_id = ?";
    static final String DELETE_ALL_BY_ORDER_QUERY = "DELETE FROM orderitem WHERE o_id = ?";

    public OrderItemImplement() {
        String url = "jdbc:mysql://localhost:3306/tapfoods";
        String user = "root";
        String pass = "password";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addOrderItem(OrderItem oi) {
        try {
            pstmt = connect.prepareStatement(INSERT_QUERY);
            pstmt.setInt(1, oi.getO_id());
            pstmt.setInt(2, oi.getQuantity());
            pstmt.setInt(3, oi.getItem_total());
            pstmt.setInt(4, oi.getMe_id());
            System.out.println(pstmt.executeUpdate() + " row inserted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOrderItem(int oi_id) {
        try {
            pstmt = connect.prepareStatement(DELETE_QUERY);
            pstmt.setInt(1, oi_id);
            System.out.println(pstmt.executeUpdate() + " row deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrderItem(OrderItem oi) {
        try {
            pstmt = connect.prepareStatement(UPDATE_QUERY);
            pstmt.setInt(1, oi.getO_id());
            pstmt.setInt(2, oi.getQuantity());
            pstmt.setInt(3, oi.getItem_total());
            pstmt.setInt(4, oi.getMe_id());
            // BUG FIX: this used to be setInt(4, oi.getOi_id()) — overwriting
            // me_id and leaving the 5th placeholder (the WHERE oi_id) unset,
            // which throws at runtime. The WHERE clause is param index 5.
            pstmt.setInt(5, oi.getOi_id());
            System.out.println(pstmt.executeUpdate() + " row updated");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public OrderItem getOrderItem(int oi_id) {
        OrderItem m = null;
        try {
            pstmt = connect.prepareStatement(GET_QUERY);
            pstmt.setInt(1, oi_id);
            res = pstmt.executeQuery();
            if (res.next()) {
                m = new OrderItem();
                m.setOi_id(res.getInt("oi_id"));
                m.setO_id(res.getInt("o_id"));
                m.setItem_total(res.getInt("item_total"));
                m.setMe_id(res.getInt("me_id"));
                m.setQuantity(res.getInt("quantity"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    @Override
    public List<OrderItem> getAllOrderItem() {
        List<OrderItem> arr = new ArrayList<>();
        OrderItem m = null;
        try {
            stmt = connect.createStatement();
            res = stmt.executeQuery(GETALL_QUERY);
            while (res.next()) {
                m = new OrderItem();
                m.setOi_id(res.getInt("oi_id"));
                m.setO_id(res.getInt("o_id"));
                m.setItem_total(res.getInt("item_total"));
                m.setMe_id(res.getInt("me_id"));
                m.setQuantity(res.getInt("quantity"));
                arr.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arr;
    }

    @Override
    public List<OrderItem> getItemsByOrderId(int o_id) {
        List<OrderItem> arr = new ArrayList<>();
        try {
            pstmt = connect.prepareStatement(ITEMS_BY_ORDER_QUERY);
            pstmt.setInt(1, o_id);
            res = pstmt.executeQuery();
            while (res.next()) {
                OrderItem m = new OrderItem();
                m.setOi_id(res.getInt("oi_id"));
                m.setO_id(res.getInt("o_id"));
                m.setItem_total(res.getInt("item_total"));
                m.setMe_id(res.getInt("me_id"));
                m.setQuantity(res.getInt("quantity"));
                arr.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arr;
    }

    @Override
    public OrderItem getOrderItemByOrderAndMenu(int o_id, int me_id) {
        OrderItem m = null;
        try {
            pstmt = connect.prepareStatement(ITEM_BY_ORDER_AND_MENU_QUERY);
            pstmt.setInt(1, o_id);
            pstmt.setInt(2, me_id);
            res = pstmt.executeQuery();
            if (res.next()) {
                m = new OrderItem();
                m.setOi_id(res.getInt("oi_id"));
                m.setO_id(res.getInt("o_id"));
                m.setItem_total(res.getInt("item_total"));
                m.setMe_id(res.getInt("me_id"));
                m.setQuantity(res.getInt("quantity"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    @Override
    public void deleteAllByOrderId(int o_id) {
        try {
            pstmt = connect.prepareStatement(DELETE_ALL_BY_ORDER_QUERY);
            pstmt.setInt(1, o_id);
            System.out.println(pstmt.executeUpdate() + " rows deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}