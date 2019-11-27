package com.kgeorgiev.notes.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kgeorgiev.notes.domain.entity.Note
import com.kgeorgiev.notes.domain.usecase.*
import com.kgeorgiev.notes.presentation.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kostadin.georgiev on 9/17/2019.
 */
class NotesViewModel @Inject constructor(
    private val insertNoteUseCase: InsertNoteUseCase,
    private val getNotesUseCase: GetNotesUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase
) :
    BaseViewModel() {
    private val notesLiveData: MutableLiveData<List<Note>> by lazy {
        MutableLiveData<List<Note>>()
    }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            insertNoteUseCase.invoke(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            updateNoteUseCase.invoke(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            deleteNoteUseCase.invoke(note)
        }
    }

    fun getNote(noteId: Int): LiveData<List<Note>> {
        viewModelScope.launch {
            //val note = notesRepositoryImpl.getNote(noteId)
            val note = getNoteUseCase.invoke(noteId)
            val list = ArrayList<Note>()
            list.add(note)
            notesLiveData.value = list
        }

        return notesLiveData
    }

    @Suppress("UNCHECKED_CAST")
    fun getNotes(): LiveData<List<Note>> {
        viewModelScope.launch {
            val result = getNotesUseCase.invoke()
            notesLiveData.value = result
        }

        return notesLiveData
    }
}


