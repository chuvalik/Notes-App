package com.example.feature_note.domain.use_case

import android.util.Log
import com.example.feature_note.domain.repository.NoteRepository

class SynchronizeNotesUseCase(
    private val repository: NoteRepository
) {

    suspend operator fun invoke() {
        repository.synchronizeDeletedNotes()
        repository.synchronizeNotes()
    }
}