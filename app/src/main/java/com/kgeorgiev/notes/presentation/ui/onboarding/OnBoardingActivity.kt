package com.kgeorgiev.notes.presentation.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.kgeorgiev.notes.R
import com.kgeorgiev.notes.presentation.ui.activities.HomeActivity
import kotlinx.android.synthetic.main.activity_on_boarding.*

class OnBoardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        initViewPager()
        initFinishBtnListener()
    }

    private fun initViewPager() {
        val onBoardingItems = OnBoardingItemEnum.values().toMutableList()

        val onBoardingAdapter = OnBoardingAdapter(onBoardingItems, this)
        vpOnboarding.adapter = onBoardingAdapter
        circleIndicator.setViewPager(vpOnboarding)

        vpOnboarding.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                // no impl needed here
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // no impl needed here
            }

            override fun onPageSelected(position: Int) {
                Log.e("TAG", "onPageSelected() position:$position")

                if (position == onBoardingItems.size - 1) {
                    showFinishButton()
                } else {
                    hideFinishButton()
                }
            }
        })
    }

    private fun showFinishButton() {
        btnOnBoardingFinish.visibility = View.VISIBLE
        btnOnBoardingFinish.animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
    }

    private fun hideFinishButton() {
        btnOnBoardingFinish.animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
        btnOnBoardingFinish.visibility = View.INVISIBLE
    }

    private fun initFinishBtnListener() {
        btnOnBoardingFinish.setOnClickListener {
            openHomeScreen()
        }
    }

    private fun openHomeScreen() {
        finish()

        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
