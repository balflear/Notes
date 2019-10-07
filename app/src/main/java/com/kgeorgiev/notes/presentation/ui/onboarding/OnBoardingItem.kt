package com.kgeorgiev.notes.presentation.ui.onboarding

import com.kgeorgiev.notes.R

/**
 * Created by kostadin.georgiev on 10/3/2019.
 */
enum class OnBoardingItem constructor(
    private val mTitleResId: Int,
    private val mDescriptionResId: Int,
    private val mDrawableResId: Int
) {
    ONBOARDING_HELLO(
        R.string.onboarding_hello_title,
        R.string.onboarding_hello_description,
        R.drawable.onboarding_hello
    ),
    ONBOARDING_SECURITY(
        R.string.onboarding_security_title,
        R.string.onboarding_security_description,
        R.drawable.onboarding_lock
    );

    fun getDescriptionResId() = mDescriptionResId

    fun getDrawableResId() = mDrawableResId

    fun getTitleResId() = mTitleResId
}
