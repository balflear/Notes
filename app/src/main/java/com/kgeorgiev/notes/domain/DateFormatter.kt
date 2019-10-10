package com.kgeorgiev.notes.domain

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by kostadin.georgiev on 9/19/2019.
 */
class DateFormatter {

    companion object {
        var df = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        fun formatDate(date: Date): String {
            return df.format(date)
        }

        fun formatDate(dateInMs: Long): String {
            return df.format(Date(dateInMs))
        }
    }
}