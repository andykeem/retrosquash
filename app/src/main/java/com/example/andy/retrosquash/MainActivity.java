package com.example.andy.retrosquash;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private CourtView mCourtView;
    private Point mScreenSize;
    private int mScreenWidth;
    private int mScreenHeight;

    private Point mRacket;
    private int mRacketWidth;
    private int mRacketHeith;

    private Point mBall;
    private int mBallWidth;
    private boolean mBallMoveDown;
    private boolean mBallMoveLeft;
    private boolean mBallMoveRight;
    private boolean mBallMoveUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        mCourtView = new CourtView(this);
        this.setContentView(mCourtView);

        mScreenSize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(mScreenSize);
        mScreenWidth = mScreenSize.x;
        mScreenHeight = mScreenSize.y;
//        Log.d(TAG, "screen size: " + mScreenSize);

        mRacket = new Point();
        mRacket.x = mScreenWidth / 2;
        mRacket.y = mScreenHeight - 200;
        mRacketWidth = mScreenWidth / 6;
        mRacketHeith = 10;

        mBallWidth = mScreenWidth / 35;
        mBall = new Point();
        mBall.x = mScreenWidth / 2;
        mBall.y = mBallWidth;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCourtView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCourtView.pause();
    }

    private class CourtView extends SurfaceView implements Runnable {

        private SurfaceHolder mHolder;
        private Context mContext;
        private Thread mThrd;
        private boolean mPlaying;
        private Canvas mCanvas;
        private Paint mPaint;
        private Random mRand;

        public CourtView(Context context) {
            super(context);
            mContext = context;
            mHolder = this.getHolder();
            mPaint = new Paint();

            mBallMoveDown = true;
            // get the random x angle
            mRand = new Random();
            int rand = mRand.nextInt(2);
            Log.d(TAG, "random: " + rand);
            switch (rand) {
                case 0: // move to left
                    mBallMoveLeft = true;
                    mBallMoveRight = false;
                    break;
                case 1: // move to right
                    mBallMoveLeft = false;
                    mBallMoveRight = true;
                    break;
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mRacket.x = (int) event.getX();
                    break;
            }
            return true;
        }

        @Override
        public void run() {
            while (mPlaying) {
                updateCourt();
                drawCourt();
                updateFPS();
            }
        }

        protected void updateCourt() {
//            Log.d(TAG, "updateCourt() called..");

            // check side (x or y) collisions..
            // if the ball touches the right side of the screen..
            if ((mBall.x + mBallWidth) >= mScreenWidth) {
                mBallMoveLeft = true;
                mBallMoveRight = false;
            }

            // if the ball touches the left side of the screen..
            if (mBall.x <= 0) {
                mBallMoveLeft = false;
                mBallMoveRight = true;
            }

            // move ball to top when it hits the bottom of the screen
            if (mBall.y > mScreenHeight) {
                mBall.y = mBallWidth;
            }

            // handle when ball hits the racket move ball up..
            if ( ((mBall.y + mBallWidth) > (mRacket.y - (mRacketHeith / 2))) &&
                    ((mBall.y + mBallWidth) < (mRacket.y + (mRacketHeith / 2))) &&
                    ((mRacket.x - (mRacketWidth / 2)) < mBall.x) &&
                    (mBall.x < (mRacket.x + (mRacketWidth / 2))) ) {
                mBallMoveDown = false;
                mBallMoveUp = true;
            }

            // if ball hits the top of the screen move ball down..
            if (mBall.y <= 0) {
                mBallMoveDown = true;
                mBallMoveUp = false;
            }

            if (mBallMoveLeft) {
                mBall.x -= 10;
            }
            if (mBallMoveRight) {
                mBall.x += 10;
            }
            if (mBallMoveUp) {
                mBall.y -= 10;
            }
            if (mBallMoveDown) {
                mBall.y += 10;
            }
        }

        protected void drawCourt() {
//            Log.d(TAG, "drawCourt() called..");

            if (mHolder.getSurface().isValid()) {
                mCanvas = mHolder.lockCanvas();
                mCanvas.drawColor(Color.BLACK);
                mPaint.setColor(Color.argb(255, 255, 255, 255));

                // draw ball..
                mCanvas.drawRect(mBall.x, mBall.y, (mBall.x + mBallWidth), (mBall.y + mBallWidth), mPaint);

                // draw racket..
                mCanvas.drawRect((mRacket.x - (mRacketWidth / 2)), (mRacket.y - (mRacketHeith / 2)),
                        (mRacket.x + (mRacketWidth / 2)), (mRacket.y + (mRacketHeith / 2)), mPaint);

                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }

        protected void updateFPS() {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                Log.e(TAG, ie.getMessage(), ie);
            }
        }

        public void resume() {
            mPlaying = true;
            mThrd = new Thread(this);
            mThrd.start();
        }

        public void pause() {
            mPlaying = false;
            try {
                mThrd.join();
            } catch (InterruptedException ie) {
                Log.e(TAG, ie.getMessage(), ie);
            }
        }
    }
}
