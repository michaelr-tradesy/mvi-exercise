package com.example.mviexercise.ui.login

import android.os.SystemClock
import android.view.View

class ThrottlingClickListener(
    private var throttleTime: Int = 1000,
    private val onThrottleClick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(view: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < throttleTime) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onThrottleClick(view)
    }
}

/**
 * Set a [ThrottlingClickListener]
 */
fun View.setThrottlingClickListener(onClick: (View) -> Unit) {
    setOnClickListener(ThrottlingClickListener { onClick(it) })
}

/**
 * Set a [ThrottlingClickListener]
 */
fun View.setThrottlingClickListener(throttlingClickListener: ThrottlingClickListener?) {
    setOnClickListener(throttlingClickListener)
}