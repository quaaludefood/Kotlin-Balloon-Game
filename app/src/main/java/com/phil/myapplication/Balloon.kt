package com.phil.myapplication

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.phil.myapplication.utils.PixelHelper

class Balloon : AppCompatImageView, AnimatorListener, AnimatorUpdateListener {
    private var mAnimator: ValueAnimator? = null
    private var mListener: BalloonListener? = null
    private var mPopped = false

    //context is mainActivity touch event
    constructor(context: Context?) : super(context) {}

    constructor(context: Context, color: Int, rawHeight: Int) : super(context) {
        mListener = context as BalloonListener
        setImageResource(R.drawable.balloon)
        this.setColorFilter(color)
        val rawWidth = rawHeight / 2
        val dpHeight: Int = PixelHelper.pixelsToDp(rawHeight, context)
        val dpWidth: Int = PixelHelper.pixelsToDp(rawWidth, context)
        val params = ViewGroup.LayoutParams(dpWidth, dpHeight)
        layoutParams = params

    }

    fun releaseBalloon(screenHeight: Int, duration: Int) {
        mAnimator = ValueAnimator()
        mAnimator!!.duration = duration.toLong()
        mAnimator!!.setFloatValues(screenHeight.toFloat(), 0f)
        //define direction of animation
        mAnimator!!.interpolator = LinearInterpolator()
        mAnimator!!.setTarget(this)
        mAnimator!!.addListener(this)
        mAnimator!!.addUpdateListener(this)
        mAnimator!!.start()
    }

    //addListener event handling methods
    override fun onAnimationStart(animation: Animator) {}

    //This is called when a balloon reaches the ceiling
    override fun onAnimationEnd(animation: Animator) {
        if (!mPopped) {
            mListener!!.popBalloon(this, false)
        }
    }

    override fun onAnimationCancel(animation: Animator) {}
    override fun onAnimationRepeat(animation: Animator) {}
    //handle the event each time that animated value changes
    override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
        y = valueAnimator.animatedValue as Float
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mPopped && event.action == MotionEvent.ACTION_DOWN) { //The balloon popped because the user touched it - could've been hitting ceiling
            mListener!!.popBalloon(this, true)
            mPopped = true
            mAnimator!!.cancel()
        }
        return super.onTouchEvent(event)
    }

    fun setPopped(popped: Boolean) {
        mPopped = popped
        if (popped) {
            mAnimator!!.cancel()
        }
    }

    //Create a callback method that'll tell the mainActivity when a balloon is clicked
    interface BalloonListener {
        fun popBalloon(balloon: Balloon?, userTouch: Boolean)
    }
}
