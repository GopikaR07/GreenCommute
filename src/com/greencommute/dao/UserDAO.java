package com.greencommute.dao;

import com.greencommute.model.User;
import com.greencommute.util.DBConnection;
import java.sql.*;

public class UserDAO {

    public static boolean register(String fullname, String email, String password) throws SQLException {
        String sql = "INSERT INTO users (fullname, email, password, total_points, reward_tier) VALUES (?,?,?,0,'Bronze')";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, fullname);
            ps.setString(2, email);
            ps.setString(3, password);
            return ps.executeUpdate() == 1;
        }
    }

    public static User authenticate(String email, String password) throws SQLException {
        String sql = "SELECT id, fullname, email, current_streak, highest_streak, total_points, reward_tier FROM users WHERE email = ? AND password = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFullname(rs.getString("fullname"));
                    user.setEmail(rs.getString("email"));
                    user.setCurrentStreak(rs.getInt("current_streak"));
                    user.setHighestStreak(rs.getInt("highest_streak"));
                    user.setTotalPoints(rs.getInt("total_points"));
                    user.setRewardTier(rs.getString("reward_tier"));
                    return user;
                }
                return null;
            }
        }
    }

    public static void updateUserPoints(int userId, int points) throws SQLException {
        String sql = "UPDATE users SET total_points = total_points + ? WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, points);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public static void updateStreak(int userId, int currentStreak, int highestStreak) throws SQLException {
        String sql = "UPDATE users SET current_streak = ?, highest_streak = ? WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, currentStreak);
            ps.setInt(2, highestStreak);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }
}