package com.kgeorgiev.notes.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kgeorgiev.notes.domain.entity.Note
import com.kgeorgiev.notes.domain.repository.NotesRepositroy
import com.kgeorgiev.notes.presentation.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kostadin.georgiev on 9/17/2019.
 */
class NotesViewModel @Inject constructor(private var notesRepositoryImpl: NotesRepositroy) :
    BaseViewModel() {

    private val notesLiveData: MutableLiveData<List<Note>> by lazy {
        MutableLiveData<List<Note>>()
    }

    fun insertNote(note: Note) {
        startJob {
            notesRepositoryImpl.insertNote(note)
        }
    }

    fun updateNote(note: Note) {
        startJob {
            notesRepositoryImpl.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        startJob {
            notesRepositoryImpl.deleteNote(note)
        }
    }

    fun getNote(noteId: Int): LiveData<List<Note>> {
        viewModelScope.launch {
            val note = notesRepositoryImpl.getNote(noteId)
            val list = ArrayList<Note>()
            list.add(note)
            notesLiveData.value = list
        }

        return notesLiveData
    }

    @Suppress("UNCHECKED_CAST")
    fun getNotes(): LiveData<List<Note>> {
        viewModelScope.launch {
            val result = notesRepositoryImpl.getAllNotes()
            notesLiveData.value = result
        }

        return notesLiveData
    }
}


