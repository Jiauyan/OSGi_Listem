package com.example.listem.implementation;

import com.example.listem.service.NoteService;

import com.example.listem.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoteServiceImpl implements NoteService {

    @Override
    public String addNote(String title, String content) {
        String sql = "INSERT INTO notes (title, content) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return String.valueOf(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateNote(String id, String title, String content) {
        String sql = "UPDATE notes SET title = ?, content = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setString(3, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteNote(String id) {
        String sql = "DELETE FROM notes WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> viewAllNotes() {
        List<String> notes = new ArrayList<>();
        String sql = "SELECT id, title, content FROM notes";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                notes.add("ID: " + rs.getLong("id") + ", Title: " + rs.getString("title") + ", Content: " + rs.getString("content"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    @Override
    public String searchNoteByTitle(String title) {
        String sql = "SELECT id, title, content FROM notes WHERE title = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return "ID: " + rs.getLong("id") + ", Title: " + rs.getString("title") + ", Content: " + rs.getString("content");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}