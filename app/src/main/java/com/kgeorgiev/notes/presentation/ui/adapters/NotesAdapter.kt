package com.kgeorgiev.notes.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kgeorgiev.notes.R
import com.kgeorgiev.notes.data.entity.Note
import com.kgeorgiev.notes.domain.DateFormatter
import kotlinx.android.synthetic.main.note_list_item.view.*

/**
 * Created by kostadin.georgiev on 9/17/2019.
 */
class NotesAdapter(
    private var items: ArrayList<Note>,
    val context: Context,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        val currentNote: Note = items[position]

        holder.cvNote.animation =
            AnimationUtils.loadAnimation(context, R.anim.fade_left_to_right_animation)

        holder.ivLockNote.visibility = if (currentNote.isLocked) View.VISIBLE else View.GONE
        holder.ivReminderNote.visibility = if (currentNote.dateOfReminder > 0) View.VISIBLE else View.GONE
        holder.tvTitle.text = currentNote.title
        holder.tvDescription.text =
            if (currentNote.isLocked) context.getString(R.string.msg_note_is_locked) else currentNote.description
        holder.tvDate.text = DateFormatter.formatDate(currentNote.dateOfCreation)

        holder.cvNote.setOnClickListener {
            onClickListener.onNoteClicked(items[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.note_list_item,
                parent,
                false
            )
        )
    }

    fun updateValues(items: ArrayList<Note>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val cvNote: CardView = view.cvNote
        val tvTitle: TextView = view.tvItemTitle
        val tvDescription: TextView = view.tvItemDescription
        val tvDate: TextView = view.tvItemDate
        val ivLockNote: ImageView = view.ivLockNote
        val ivReminderNote: ImageView = view.ivReminderNote
    }

    interface OnClickListener {
        fun onNoteClicked(note: Note)
    }
}