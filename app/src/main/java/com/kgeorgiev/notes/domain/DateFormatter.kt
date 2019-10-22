package com.kgeorgiev.notes.domain

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by kostadin.georgiev on 9/19/2019.
 */
class DateFormatter {

    companion object {
        private var dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        fun formatDate(date: Date): String {
            return dateFormatter.format(date)
        }

        fun formatDate(dateInMs: Long): String {
            return dateFormatter.format(Date(dateInMs))
        }
    }
}