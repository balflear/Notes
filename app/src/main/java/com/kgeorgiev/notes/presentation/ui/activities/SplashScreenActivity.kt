package com.kgeorgiev.notes.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.kgeorgiev.notes.App
import com.kgeorgiev.notes.R
import com.kgeorgiev.notes.domain.SharedPrefsWrapper
import com.kgeorgiev.notes.presentation.ui.onboarding.OnBoardingActivity
import kotlinx.android.synthetic.main.activity_splash_screen_avtivity.*
import javax.inject.Inject

class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPrefsWrapper: SharedPrefsWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen_avtivity)

        val animDuration = resources.getInteger(R.integer.splash_anim_duration).toLong()
        (applicationContext as App).appComponent.inject(this)

        tvSplashText.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation)

        Handler().postDelayed({
            if (lottieView.isAnimating) {
                lottieView.cancelAnimation()
            }
            finish()

            handleNextScreen()
        }, animDuration)
    }

    private fun handleNextScreen() {
        val isAppFirstStarted =
            sharedPrefsWrapper.isFirstStartedApp(SharedPrefsWrapper.FIRST_STARTED_APP_KEY)

        if (isAppFirstStarted) {
            sharedPrefsWrapper.saveFirstStartedApp(SharedPrefsWrapper.FIRST_STARTED_APP_KEY, false)
            goToOnboardingScreen()
        } else {
            goToHomeScreen()
        }
    }

    private fun goToHomeScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    private fun goToOnboardingScreen() {
        startActivity(Intent(this, OnBoardingActivity::class.java))
    }
}
