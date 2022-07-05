package com.example.feature_note.presentation.note_add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.StateStringPropertyDelegate
import com.example.feature_note.domain.model.Note
import com.example.feature_note.domain.use_case.InsertNoteUseCase
import com.example.feature_note.domain.use_case.UpdateNoteUseCase
import com.example.feature_note.domain.use_case.ValidateTitleUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*


class NoteAddViewModel(
    private val insertNoteUseCase: InsertNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val validateTitleUseCase: ValidateTitleUseCase,
    state: SavedStateHandle
) : ViewModel() {

    private val note = state.get<Note>("note")

    var noteTitle by StateStringPropertyDelegate(state, "title", note?.title, "")
    var noteContent by StateStringPropertyDelegate(state, "content", note?.content, "")

    private var noteColor by StateStringPropertyDelegate(
        state,
        "color",
        note?.color,
        Note.colors.random()
    )
    private var noteId by StateStringPropertyDelegate(
        state,
        "id",
        note?.id,
        UUID.randomUUID().toString()
    )

    private val _noteAddEditChannel = Channel<UiAddEditEvent>()
    val noteAddEditEvent get() = _noteAddEditChannel.receiveAsFlow()

    fun onEvent(event: NoteAddEditEvent) {
        when (event) {
            is NoteAddEditEvent.TitleChanged -> noteTitle = event.title
            is NoteAddEditEvent.ContentChanged -> noteContent = event.content
            is NoteAddEditEvent.BackButtonClicked -> navigateBack()
            is NoteAddEditEvent.NoteSubmitted -> submitNote()
        }
    }

    private fun submitNote() {

        if (!isValidationSuccessful()) return

        if (note != null) {
            val updatedNote = note.copy(
                title = noteTitle,
                content = noteContent,
            )
            updateNote(updatedNote)

        } else {
            val newNote = Note(
                id = noteId,
                title = noteTitle,
                content = noteContent,
                color = noteColor
            )
            insertNote(newNote)
        }
    }

    private fun isValidationSuccessful(): Boolean {
        val titleResult = validateTitleUseCase(noteTitle)
        if (!titleResult.successful) {
            showSnackbar(titleResult.errorMessage ?: "")
            return false
        }

        return true
    }

    private fun navigateBack() = viewModelScope.launch {
        _noteAddEditChannel.send(UiAddEditEvent.NavigateBack)
    }

    private fun updateNote(note: Note) = viewModelScope.launch {
        updateNoteUseCase(note)
        _noteAddEditChannel.send(UiAddEditEvent.NavigateToListScreen)
    }

    private fun insertNote(note: Note) = viewModelScope.launch {
        insertNoteUseCase(note)
        _noteAddEditChannel.send(UiAddEditEvent.NavigateToListScreen)
    }

    private fun showSnackbar(text: String) = viewModelScope.launch {
        _noteAddEditChannel.send(UiAddEditEvent.ShowSnackbar(text))
    }

    sealed class UiAddEditEvent {
        data class ShowSnackbar(val message: String) : UiAddEditEvent()
        object NavigateToListScreen : UiAddEditEvent()
        object NavigateBack : UiAddEditEvent()
    }
}