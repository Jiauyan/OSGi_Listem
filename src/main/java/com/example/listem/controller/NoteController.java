package com.example.listem.controller;

import com.example.listem.model.Note;
import com.example.listem.service.NoteService;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class NoteController extends HttpServlet {
    private final NoteService noteService;

    // Constructor to initialize noteService
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String title = req.getParameter("title");
        String path = req.getPathInfo();

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if ("/all".equals(path)) {
            List<Note> notes = noteService.viewAllNotes();
            out.write(notes.toString());
        } else if (title != null) {
            Note note = noteService.searchNoteByTitle(title);
            if (note != null) {
                out.write(note.toString());
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\": \"Note not found\"}");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Invalid request\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String body = sb.toString();
        // Parse JSON (use a library like Jackson or Gson)
        JSONObject jsonObject = new JSONObject(body); // Assuming org.json library
        String title = jsonObject.optString("title");
        String content = jsonObject.optString("content");

        if (!title.isEmpty() && !content.isEmpty()) {
            String id = noteService.addNote(title, content);
            out.write("{\"id\": \"" + id + "\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Title and content are required\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String title = req.getParameter("title");
        String content = req.getParameter("content");

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if (id != null && title != null && content != null) {
            boolean updated = noteService.updateNote(id, title, content);
            if (updated) {
                out.write("{\"message\": \"Note updated successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\": \"Note not found\"}");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"ID, title, and content are required\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if (id != null) {
            boolean deleted = noteService.deleteNote(id);
            if (deleted) {
                out.write("{\"message\": \"Note deleted successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\": \"Note not found\"}");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"ID is required\"}");
        }
    }
}