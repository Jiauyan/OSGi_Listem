package com.example.listem.service;

import com.example.listem.model.Note;

import java.util.List;

public interface NoteService {
    String addNote(String title, String content);

    boolean updateNote(String id, String title, String content);

    boolean deleteNote(String id);

    List<Note> viewAllNotes();

    Note searchNoteByTitle(String title);
}
