package com.richdomapps.ticker;

import com.richdomapps.ticker.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    // For Animation
    private TextView textViewToAnimate;
    private int widthOfTextViewToAnimate;

    private Animation animationMove;

    private String phoneModel;
    private long offset = 0;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offset = getIntent().getExtras().getLong("offset");
        Log.d("offset", String.valueOf(offset));
        mp = new MediaPlayer();
        mp= MediaPlayer.create(this, R.raw.getdownonit);



        //Possible Phones
        //"Nexus 5"
        //"Nexus 7"
        //"Nexus 4"
        phoneModel = Build.MODEL;
        Log.d("MODEL PHONE", phoneModel);
        switch (phoneModel) {
            case "Nexus 5":
                setContentView(R.layout.activity_fullscreen_nexus_5);
                animationMove = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.move_text_view_nexus_5);
                Log.d("Nexus 5","Nexus 5");
                break;
            case "Nexus 7":
                setContentView(R.layout.activity_fullscreen_nexus_7);
                animationMove = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.move_text_view_nexus_7);
                Log.d("Nexus 7","Nexus 7");
                break;
            case "Nexus 4":
                setContentView(R.layout.activity_fullscreen_nexus_4);
                animationMove = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.move_text_view_nexus_4);
                Log.d("Nexus 4","Nexus 4");
                break;

            default:
                setContentView(R.layout.activity_fullscreen_nexus_5);
                animationMove = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.move_text_view_nexus_5);
                Log.d("default","default");
                break;
        }

        setupActionBar();

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        //For animation

        // load the animation

        textViewToAnimate = (TextView) findViewById(R.id.fullscreen_content);

    }




    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    protected void onResume() {
        super.onResume();

        delayedStartAnimation(getStartAnimationWaitTime());
        delayedStartMusic(getStartMusicWaitTime());

    }

    private int getStartAnimationWaitTime(){
        long currentTime = System.currentTimeMillis() + offset;
        Log.d("currentTime",String.valueOf(currentTime));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int minute = calendar.get(Calendar.MINUTE) + 1;
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);

        long startTime = calendar.getTimeInMillis();
        long waitTimeLong = startTime - currentTime;
        int  waitTimeInt = (int) waitTimeLong;

        switch (phoneModel) {
            case "Nexus 5":
                //do nothing, Nexus 5 is first device, leave wait time alone
                break;
            case "Nexus 7":
                waitTimeInt += 947.23; //takes Nexus 5 2216.5401 to break plane
                break;
            case "Nexus 4":
                waitTimeInt = waitTimeInt + (int)2216.5401 + (int)2427.5994; //takes Nexus 7 2427.5994 to break plane
                break;
            case "SAMSUNG-SGH-I527":
                waitTimeInt = waitTimeInt + (int)2216.5401 + (int)2427.5994 + (int) 2216.2629; //takes Nexus 4 2216.2629 to break plane
                // for MEGA, it takes 2367.8373 to cross plane.
                // all added up: 2216.5401 + 2427.5994 + 2216.2629 + 2367.8373 = 9228.2397
                break;
            default:

                break;
        }

        Log.d("waitTimeInt",String.valueOf(waitTimeInt));


        return waitTimeInt;

    }

    private int getStartMusicWaitTime(){
        long currentTime = System.currentTimeMillis() + offset;
        Log.d("currentTime",String.valueOf(currentTime));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int minute = calendar.get(Calendar.MINUTE) + 1;
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);

        //long currTime = System.currentTimeMillis()+offset;
        //long waitTime = playTime-currTime;

        long startTime = calendar.getTimeInMillis();
        long waitTimeLong = startTime - currentTime;
        int  waitTimeInt = (int) waitTimeLong;

        return waitTimeInt;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        widthOfTextViewToAnimate = textViewToAnimate.getWidth();
        Log.d("width", String.valueOf(widthOfTextViewToAnimate));


    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    Handler startAnimationHandler = new Handler();
    Runnable startAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            mp.start();
            textViewToAnimate.startAnimation(animationMove);
        }

    };

    Handler resetAnimationHandler = new Handler();
    Runnable resetAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            textViewToAnimate.clearAnimation();

        }

    };

    Handler startMusicHandler = new Handler();
    Runnable startMusicRunnable = new Runnable() {
        @Override
        public void run() {
            mp.start();
        }

    };



    private void delayedStartAnimation(int delayMillis) {
        startAnimationHandler.removeCallbacks(startAnimationRunnable);

        startAnimationHandler.postDelayed(startAnimationRunnable, delayMillis);
        resetAnimationHandler.postDelayed(resetAnimationRunnable, delayMillis + 4000);
        startAnimationHandler.postDelayed(startAnimationRunnable, delayMillis + (int)9228.2397);

        int restartAnimationWaitTime = delayMillis + 4000;
        int startAnimationWaitTime = delayMillis + (int)9228.2397;

        for(int i=0; i<20; i++){
            restartAnimationWaitTime += (int)9228.2397;
            startAnimationWaitTime += (int)9228.2397;
            resetAnimationHandler.postDelayed(resetAnimationRunnable,restartAnimationWaitTime);
            startAnimationHandler.postDelayed(startAnimationRunnable,startAnimationWaitTime);
        }
    }

    private void delayedStartMusic(int delayMillis){
        startMusicHandler.removeCallbacks(startMusicRunnable);
        startMusicHandler.postDelayed(startMusicRunnable, delayMillis);
    }
}
