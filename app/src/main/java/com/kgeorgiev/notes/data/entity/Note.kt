package com.kgeorgiev.notes.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by kostadin.georgiev on 9/17/2019.
 */
@Entity(tableName = "notes_table")
@Parcelize
data class Note(//This warning is known issue bug for the IDE https://stackoverflow.com/questions/56018761/class-x-is-not-abstract-and-does-not-implement-fun-writetoparcel-defined-in-an
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "creation_date")
    var dateOfCreation: Date,

    @ColumnInfo(name = "reminder_date")
    var dateOfReminder: Long, // In ms

    @ColumnInfo(name = "is_locked")
    var isLocked: Boolean

) : Parcelable {
    constructor(
        title: String = "",
        description: String = "",
        dateOfCreation: Date,
        dateOfReminder: Long = 0,
        isLocked: Boolean = false
    ) : this(0, title, description, dateOfCreation, dateOfReminder, isLocked)
}
