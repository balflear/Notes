package com.kgeorgiev.notes.domain

import java.text.DateFormat
import java.util.*

/**
 * Created by kostadin.georgiev on 9/19/2019.
 */
class DateFormatter {

    companion object {
        fun formatDate(date: Date): String {
            return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date)
        }
    }
}