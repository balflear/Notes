package com.kgeorgiev.notes.presentation.ui.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.kgeorgiev.notes.R
import kotlinx.android.synthetic.main.onboarding_pager_layout.view.*


/**
 * Created by kostadin.georgiev on 10/3/2019.
 */
class OnBoardingAdapter(private val list: List<OnBoardingItemEnum>, val context: Context) :
    PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.onboarding_pager_layout, container, false)

        val onBoardingItem = list[position]
        view.onBoardingView.setTitle(context.getString(onBoardingItem.getTitleResId()))
        view.onBoardingView.setDescription(context.getString(onBoardingItem.getDescriptionResId()))
        view.onBoardingView.setImageDrawable(context.getDrawable(onBoardingItem.getDrawableResId())!!)

        container.addView(view)

        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return `object` == view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getCount() = list.size
}