/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.data.datasource.local;

/**
 *
 * @author abdel
 */
import com.mycompany.model.app.Player;
import java.sql.*;

public class PlayerDAO {

    public PlayerDAO() {
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS session_player (" +
                     "id INT PRIMARY KEY, " +
                     "username VARCHAR(255), " +
                     "password VARCHAR(255), " +
                     "avatar VARCHAR(255), " +
                     "score BIGINT);";
        try (Connection conn = LocalDBConnection.getConnection(); 
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void save(Player player) {
        clear(); // Keep only one session active locally
        String sql = "INSERT INTO session_player VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = LocalDBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, player.getId());
            pstmt.setString(2, player.getUserName());
            pstmt.setString(3, player.getPassword());
            pstmt.setString(4, player.getAvatar());
            pstmt.setLong(5, player.getScore());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Player get() {
        String sql = "SELECT * FROM session_player LIMIT 1";
        try (Connection conn = LocalDBConnection.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Player p = new Player();
                p.setId(rs.getInt("id"));
                p.setUserName(rs.getString("username"));
                p.setPassword(rs.getString("password"));
                p.setAvatar(rs.getString("avatar"));
                p.setScore(rs.getLong("score"));
                return p;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void clear() {
        try (Connection conn = LocalDBConnection.getConnection(); 
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM session_player");
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
