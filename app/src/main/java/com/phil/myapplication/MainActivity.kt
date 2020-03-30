package com.phil.myapplication

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.phil.myapplication.utils.Constants.Companion.BALLOONS_PER_LEVEL
import com.phil.myapplication.utils.Constants.Companion.MAX_ANIMATION_DELAY
import com.phil.myapplication.utils.Constants.Companion.MAX_ANIMATION_DURATION
import com.phil.myapplication.utils.Constants.Companion.MIN_ANIMATION_DELAY
import com.phil.myapplication.utils.Constants.Companion.MIN_ANIMATION_DURATION
import com.phil.myapplication.utils.Constants.Companion.NUMBER_OF_PINS
import com.phil.myapplication.utils.HighScoreHelper
import com.phil.myapplication.utils.SimpleAlertDialog

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.random.Random


class MainActivity : AppCompatActivity(), Balloon.BalloonListener {

    val TAG: String = "AppDebug"

    private lateinit var mContentView: ViewGroup
    private lateinit var mScoreDisplay: TextView
    private lateinit var mLevelDisplay:TextView
    private lateinit var mGoButton: Button

    private val mBalloonColors = IntArray(3)
    private var mNextColor = 0
    private  var mScreenWidth:Int = 0
    private  var mScreenHeight:Int = 0
    private var mLevel = 0
    private  var mScore:Int = 0
    private  var mPinsUsed:Int = 0
    private val mPinImages: MutableList<ImageView> = ArrayList()
    private val mBalloons:  MutableList<Balloon> = ArrayList<Balloon>()

    private  var mPlaying = false
    private var mGameStopped = true
    private var mBalloonsPopped = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBalloonColors[0] = Color.argb(255, 255, 0, 0)
        mBalloonColors[1] = Color.argb(255, 0, 255, 0)
        mBalloonColors[2] = Color.argb(255, 0, 0, 255)

        window.setBackgroundDrawableResource(R.drawable.modern_background)

      //  mContentView = findViewById(R.id.activity_main) as? ViewGroup
        mContentView =  findViewById(R.id.activity_main)

        mContentView.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mContentView.viewTreeObserver.removeOnGlobalLayoutListener(this);
                      mScreenWidth = mContentView.getWidth()
                     mScreenHeight = mContentView.getHeight()
                }
            });

        setToFullScreen()



        mContentView.setOnClickListener { view: View? -> setToFullScreen() }

        mScoreDisplay = findViewById<View>(R.id.score_display) as TextView
        mLevelDisplay = findViewById<View>(R.id.level_display) as TextView

        mPinImages.add(findViewById<View>(R.id.pushpin1) as ImageView)
        mPinImages.add(findViewById<View>(R.id.pushpin2) as ImageView)
        mPinImages.add(findViewById<View>(R.id.pushpin3) as ImageView)
        mPinImages.add(findViewById<View>(R.id.pushpin4) as ImageView)
        mPinImages.add(findViewById<View>(R.id.pushpin5) as ImageView)
        mGoButton = findViewById<View>(R.id.go_button) as Button

        updateDisplay()

    }

    override fun onResume() {
        super.onResume()
        setToFullScreen()
    }

     fun setToFullScreen()
     {
         activity_main.systemUiVisibility =
             View.SYSTEM_UI_FLAG_LOW_PROFILE or
                     View.SYSTEM_UI_FLAG_FULLSCREEN or
                     View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                     View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                     View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                     View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                     Window.FEATURE_NO_TITLE
     }

    fun updateDisplay(){
        mScoreDisplay.text = mScore.toString()
        mLevelDisplay.text = mLevel.toString()
    }

      inner class BalloonLauncher : AsyncTask<Int, Int, Void>() {

            override fun doInBackground(vararg params: Int?): Void? {

                if(params.size != 1){
                    AssertionError("Expected 1 param for current level")
                }

                var level = params[0]
                val maxDelay: Int = Math.max(MIN_ANIMATION_DELAY, MAX_ANIMATION_DELAY - (level!! - 1) * 500 )
                val minDelay = maxDelay / 2

                var balloonsLaunched = 0

                while(mPlaying && balloonsLaunched <BALLOONS_PER_LEVEL){

                    //              Get a random horizontal position for the next balloon
                    val random = java.util.Random(Date().time)
                    Log.d(TAG, "random: ${random} mScreenWidth: ${mScreenWidth}")
                    val xPosition = random.nextInt(mScreenWidth - 200)
                    publishProgress(xPosition)
                    balloonsLaunched++

                    val delay = Random.nextInt(minDelay) + minDelay
                    Log.d(TAG, "xPosition: ${xPosition} delay: ${delay}")

                    try{
                        Thread.sleep(delay.toLong())
                    }catch( e : InterruptedException){
                        e.printStackTrace()
                    }
                }
                return null
            }

            override fun onProgressUpdate(vararg values: Int?) {
                super.onProgressUpdate(*values)
                val xPosition = values[0]
                launchBalloon(xPosition!!)
            }
    }

    fun launchBalloon(x : Int ){
        // mListener = context as BalloonListener
        val balloon = Balloon(this, mBalloonColors[mNextColor], 150)
        mBalloons.add(balloon)

        if(mNextColor + 1 == mBalloonColors.size){
            mNextColor = 0
        }else{
            mNextColor++
        }
        balloon.x = x.toFloat()
        balloon.y = (mScreenHeight + balloon.height).toFloat()
        mContentView.addView(balloon)

        //let 'er fly!
        val duration = Math.max( MIN_ANIMATION_DURATION,MAX_ANIMATION_DURATION - mLevel * 1000)
        balloon.releaseBalloon(mScreenHeight, duration)
    }

    override fun popBalloon(balloon: Balloon?, userTouch: Boolean): Unit {
        mBalloonsPopped++
        mContentView.removeView(balloon)
        mBalloons.remove(balloon)
        if (userTouch) {
            mScore++
        } else {
            mPinsUsed++
            if (mPinsUsed <= mPinImages.size) {
                mPinImages[mPinsUsed - 1]
                    .setImageResource(R.drawable.pin_off)
            }
            if (mPinsUsed == NUMBER_OF_PINS) {
                gameOver(true)
                return
            } else {
                Toast.makeText(this, "missed me! :P", Toast.LENGTH_SHORT).show()
            }
        }
        updateDisplay()
        if (mBalloonsPopped == BALLOONS_PER_LEVEL) {
            finishLevel()
        }
    }

    //fun = public void
    fun goButtonClickHandler(view: View?) {
        if (mPlaying) {
            gameOver(false)
        } else if (mGameStopped) {
            startGame()
        } else {
            startLevel()
        }
    }

    private fun gameOver(allPinsUsed: Boolean) {
        Toast.makeText(this, "GAME OVER, CHAN.", Toast.LENGTH_SHORT).show()

        for (balloon in mBalloons) {
            mContentView.removeView(balloon)
            balloon.setPopped(true)
        }
        mBalloons.clear()
        mPlaying = false
        mGameStopped = true
        mGoButton.text = "Start game"

        if (allPinsUsed) {
            if (HighScoreHelper.isTopScore(this, mScore)) {
                HighScoreHelper.setTopScore(this, mScore)
                val dialog: SimpleAlertDialog = SimpleAlertDialog.newInstance(
                    "New high score",
                    String.format("New high score: %d", mScore)
                )
                dialog.show(supportFragmentManager, null)
            }
        } else {
        }
    }

    private fun startLevel() {
        mLevel ++
        updateDisplay()
       val launcher = BalloonLauncher()
        launcher.execute(mLevel)
        mPlaying = true
        mBalloonsPopped = 0;
        mGoButton.setText("stop game")
    }

    private fun startGame() {
        setToFullScreen()
        mScore = 0
        mLevel = 0
        mPinsUsed = 0
        for (pin in mPinImages) {
            pin.setImageResource(R.drawable.pin)
        }
        mGameStopped = false
        startLevel()
    }

    private fun finishLevel() {
        Toast.makeText(this, String.format("Finished level %d", mLevel), Toast.LENGTH_SHORT)
            .show()
        mPlaying = false
        mGoButton.text = String.format("start level %d", mLevel + 1)
    }
}
