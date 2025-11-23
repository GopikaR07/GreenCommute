package com.greencommute.dao;

import com.greencommute.model.Commute;
import com.greencommute.util.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CommuteDAO {

    public static boolean addCommute(Commute commute) throws SQLException {
        String sql = "INSERT INTO commutes (user_id, date, from_location, to_location, mode, duration, distance, co2_saved, points_earned) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, commute.getUserId());
            ps.setDate(2, Date.valueOf(commute.getDate()));
            ps.setString(3, commute.getFromLocation());
            ps.setString(4, commute.getToLocation());
            ps.setString(5, commute.getMode());
            ps.setInt(6, commute.getDuration());
            ps.setDouble(7, commute.getDistance());
            ps.setDouble(8, commute.getCo2Saved());
            ps.setInt(9, commute.getPointsEarned());
            return ps.executeUpdate() == 1;
        }
    }

    public static List<Commute> getCommutesByUser(int userId) throws SQLException {
        List<Commute> list = new ArrayList<>();
        String sql = "SELECT * FROM commutes WHERE user_id = ? ORDER BY date DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Commute commute = new Commute(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getDate("date").toLocalDate(),
                            rs.getString("from_location"),
                            rs.getString("to_location"),
                            rs.getString("mode"),
                            rs.getInt("duration"),
                            rs.getDouble("distance"),
                            rs.getDouble("co2_saved"),
                            rs.getInt("points_earned")
                    );
                    list.add(commute);
                }
            }
        }
        return list;
    }

    public static double getTotalCO2Saved(int userId) throws SQLException {
        String sql = "SELECT SUM(co2_saved) as total FROM commutes WHERE user_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }
}