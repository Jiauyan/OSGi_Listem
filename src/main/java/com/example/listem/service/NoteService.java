package com.example.listem.service;

import java.util.List;

public interface NoteService {
    String addNote(String title, String content);

    boolean updateNote(String id, String title, String content);

    boolean deleteNote(String id);

    List<String> viewAllNotes();

    String searchNoteByTitle(String title);
}
