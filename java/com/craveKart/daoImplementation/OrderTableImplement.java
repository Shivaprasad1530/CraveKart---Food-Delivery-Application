package com.craveKart.daoImplementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.craveKart.dao.OrderTableDao;
import com.craveKart.model.OrderTable;

public class OrderTableImplement implements OrderTableDao {

    static Connection connect;
    static Statement stmt;
    static PreparedStatement pstmt;
    static ResultSet res;

    static final String INSERT_QUERY =
        "INSERT INTO ordertable (u_id, total_amt, status, payment_mode, re_id) values (?,?,?,?,?)";
    static final String DELETE_QUERY = "DELETE FROM ordertable WHERE o_id = ?";
    static final String UPDATE_QUERY =
        "UPDATE ordertable SET u_id=?, total_amt=?, status=?, payment_mode=?, re_id=?, delivery_agent_id=? WHERE o_id=?";
    static final String GET_QUERY = "SELECT * FROM ordertable WHERE o_id = ?";
    static final String GETALL_QUERY = "SELECT * FROM ordertable";
    static final String GET_BY_RESTAURANT_AND_STATUS_QUERY =
        "SELECT * FROM ordertable WHERE re_id = ? AND status = ? ORDER BY o_id ASC";


    static final String GET_AVAILABLE_FOR_PICKUP_QUERY =
        "SELECT * FROM ordertable WHERE status = 'prepared' AND delivery_agent_id IS NULL ORDER BY o_id ASC";


    static final String GET_ACTIVE_FOR_AGENT_QUERY =
        "SELECT * FROM ordertable WHERE delivery_agent_id = ? " +
        "AND status IN ('rider_confirmed','dispatched','reached_location') LIMIT 1";


    static final String CLAIM_ORDER_QUERY =
        "UPDATE ordertable SET delivery_agent_id = ?, status = 'rider_confirmed' " +
        "WHERE o_id = ? AND status = 'prepared' AND delivery_agent_id IS NULL";

    public OrderTableImplement() {
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

    private Integer readDeliveryAgentId(ResultSet rs) throws SQLException {
        int val = rs.getInt("delivery_agent_id");
        return rs.wasNull() ? null : val;
    }

    @Override
    public int addOrdertable(OrderTable ot) {
        int generatedId = -1;
        try {
            pstmt = connect.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, ot.getU_id());
            pstmt.setInt(2, ot.getTotal_amt());
            pstmt.setString(3, ot.getStatus());
            pstmt.setString(4, ot.getPayment_mode());
            pstmt.setInt(5, ot.getRe_id());
            int rows = pstmt.executeUpdate();
            System.out.println(rows + " row inserted");
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedId = keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    @Override
    public void deleteOrderTable(int o_id) {
        try {
            pstmt = connect.prepareStatement(DELETE_QUERY);
            pstmt.setInt(1, o_id);
            System.out.println(pstmt.executeUpdate() + " row deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrderTable(OrderTable ot) {
        try {
            pstmt = connect.prepareStatement(UPDATE_QUERY);
            pstmt.setInt(1, ot.getU_id());
            pstmt.setInt(2, ot.getTotal_amt());
            pstmt.setString(3, ot.getStatus());
            pstmt.setString(4, ot.getPayment_mode());
            pstmt.setInt(5, ot.getRe_id());
            if (ot.getDeliveryAgentId() == null) {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(6, ot.getDeliveryAgentId());
            }
            pstmt.setInt(7, ot.getO_id());
            System.out.println(pstmt.executeUpdate() + " row updated");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public OrderTable getOrderTable(int o_id) {
        OrderTable m = null;
        try {
            pstmt = connect.prepareStatement(GET_QUERY);
            pstmt.setInt(1, o_id);
            res = pstmt.executeQuery();
            if (res.next()) {
                m = new OrderTable();
                m.setO_id(res.getInt("o_id"));
                m.setStatus(res.getString("status"));
                m.setPayment_mode(res.getString("payment_mode"));
                m.setTotal_amt(res.getInt("total_amt"));
                m.setU_id(res.getInt("u_id"));
                m.setRe_id(res.getInt("re_id"));
                m.setDeliveryAgentId(readDeliveryAgentId(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    @Override
    public List<OrderTable> getOrdersByRestaurantAndStatus(int re_id, String status) {
        List<OrderTable> arr = new ArrayList<>();
        try {
            pstmt = connect.prepareStatement(GET_BY_RESTAURANT_AND_STATUS_QUERY);
            pstmt.setInt(1, re_id);
            pstmt.setString(2, status);
            res = pstmt.executeQuery();
            while (res.next()) {
                OrderTable m = new OrderTable();
                m.setO_id(res.getInt("o_id"));
                m.setStatus(res.getString("status"));
                m.setPayment_mode(res.getString("payment_mode"));
                m.setTotal_amt(res.getInt("total_amt"));
                m.setU_id(res.getInt("u_id"));
                m.setRe_id(res.getInt("re_id"));
                m.setDeliveryAgentId(readDeliveryAgentId(res));
                arr.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arr;
    }

    @Override
    public List<OrderTable> getAllOrderTable() {
        List<OrderTable> arr = new ArrayList<>();
        OrderTable m = null;
        try {
            stmt = connect.createStatement();
            res = stmt.executeQuery(GETALL_QUERY);
            while (res.next()) {
                m = new OrderTable();
                m.setO_id(res.getInt("o_id"));
                m.setStatus(res.getString("status"));
                m.setPayment_mode(res.getString("payment_mode"));
                m.setTotal_amt(res.getInt("total_amt"));
                m.setU_id(res.getInt("u_id"));
                m.setRe_id(res.getInt("re_id"));
                m.setDeliveryAgentId(readDeliveryAgentId(res));
                arr.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arr;
    }

    @Override
    public List<OrderTable> getAvailableOrdersForPickup() {
        List<OrderTable> arr = new ArrayList<>();
        try {
            stmt = connect.createStatement();
            res = stmt.executeQuery(GET_AVAILABLE_FOR_PICKUP_QUERY);
            while (res.next()) {
                OrderTable m = new OrderTable();
                m.setO_id(res.getInt("o_id"));
                m.setStatus(res.getString("status"));
                m.setPayment_mode(res.getString("payment_mode"));
                m.setTotal_amt(res.getInt("total_amt"));
                m.setU_id(res.getInt("u_id"));
                m.setRe_id(res.getInt("re_id"));
                m.setDeliveryAgentId(readDeliveryAgentId(res));
                arr.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arr;
    }

    @Override
    public OrderTable getActiveOrderForAgent(int agentId) {
        OrderTable m = null;
        try {
            pstmt = connect.prepareStatement(GET_ACTIVE_FOR_AGENT_QUERY);
            pstmt.setInt(1, agentId);
            res = pstmt.executeQuery();
            if (res.next()) {
                m = new OrderTable();
                m.setO_id(res.getInt("o_id"));
                m.setStatus(res.getString("status"));
                m.setPayment_mode(res.getString("payment_mode"));
                m.setTotal_amt(res.getInt("total_amt"));
                m.setU_id(res.getInt("u_id"));
                m.setRe_id(res.getInt("re_id"));
                m.setDeliveryAgentId(readDeliveryAgentId(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    @Override
    public boolean claimOrderForAgent(int o_id, int agentId) {
        try {
            pstmt = connect.prepareStatement(CLAIM_ORDER_QUERY);
            pstmt.setInt(1, agentId);
            pstmt.setInt(2, o_id);
            int rows = pstmt.executeUpdate();
            System.out.println(rows + " row(s) claimed");
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}