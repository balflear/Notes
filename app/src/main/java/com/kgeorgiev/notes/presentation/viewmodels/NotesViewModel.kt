package com.kgeorgiev.notes.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kgeorgiev.notes.data.entity.Note
import com.kgeorgiev.notes.data.repository.NotesRepository
import com.kgeorgiev.notes.presentation.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kostadin.georgiev on 9/17/2019.
 */
class NotesViewModel @Inject constructor(private var notesRepository: NotesRepository) :
    BaseViewModel() {

    private val notesLiveData: MutableLiveData<List<Note>> by lazy {
        MutableLiveData<List<Note>>()
    }

    fun insertNote(note: Note) {
        startJob {
            notesRepository.insertNote(note)
        }
    }

    fun updateNote(note: Note) {
        startJob {
            notesRepository.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        startJob {
            notesRepository.deleteNote(note)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getNotes(): LiveData<List<Note>> {
        viewModelScope.launch {
            val result = notesRepository.getAllNotes()
            notesLiveData.value = result
        }

        return notesLiveData
    }
}


