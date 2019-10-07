package com.kgeorgiev.notes.presentation.ui.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.res.getResourceIdOrThrow
import com.kgeorgiev.notes.R
import kotlinx.android.synthetic.main.on_boarding_view.view.*

/**
 * This class represents customView used for Onboarding
 * it has 3 components: ImageView -> Title Textview -> Description Textview
 */
class OnBoardingView
@JvmOverloads
constructor(
    context: Context?, attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) :
    LinearLayout(context, attrs, defStyle, defStyleRes) {

    private var currentView: View =
        LayoutInflater.from(context).inflate(R.layout.on_boarding_view, this, true)

    init {
        attrs?.let {
            val typedArray = context?.obtainStyledAttributes(
                it,
                R.styleable.OnBoardingView, 0, 0
            )
            val title = typedArray?.getString(R.styleable.OnBoardingView_titleText)
            val description = typedArray?.getString(R.styleable.OnBoardingView_descriptionText)
            val imageDrawable =
                context?.getDrawable(typedArray?.getResourceIdOrThrow(R.styleable.OnBoardingView_drawable)!!)

            tvOnBoardingTitle.text = title
            tvOnBoardingDescription.text = description
            ivOnBoarding.setImageDrawable(imageDrawable)

            typedArray?.recycle()
        }
    }

    fun setTitle(title: String) {
        currentView.tvOnBoardingTitle.text = title
    }

    fun setDescription(description: String) {
        currentView.tvOnBoardingDescription.text = description
    }

    fun setImageDrawable(drawable: Drawable) {
        currentView.ivOnBoarding.setImageDrawable(drawable)
    }
}
