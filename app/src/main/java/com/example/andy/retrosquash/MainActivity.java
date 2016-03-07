package com.example.andy.retrosquash;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SquashCourtView mCourtView;
    private Point mSize;
    private boolean mShouldStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        mCourtView = new SquashCourtView();
        this.setContentView(mCourtView);

        mSize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(mSize);

        this.showReadyDialog();
    }

    protected void showReadyDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Retro Squash")
                .setMessage("Are you ready?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startGame();
                    }
                })
                .setCancelable(false)
                .show();
    }

    protected void startGame() {
        mShouldStart = true;
        mCourtView.moveBall();
    }

    private class SquashCourtView extends View {
        private static final int BAR_WIDTH = 200;
        private static final int BAR_HEIGHT = 25;
        private static final int BAR_BOTTOM_OFFSET = 250;
        private static final float BALL_RADIUS = 20;
        private static final int BALL_BOTTOM_OFFET = 280;

        private Canvas mCanvas;
        private Integer mBarXOffset;
        private Float mBallX;
        private Float mBallY;

        public SquashCourtView() {
            super(MainActivity.this);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            mCanvas = canvas;
            this.drawBar();
            this.drawBall();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            PointF point = new PointF(event.getX(), event.getY());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "point: " + point.x);
                    mBarXOffset = (int) point.x;
                    this.invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:

                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }
            return true;
        }

        protected void drawBar() {
            int left = 0;
            if (mBarXOffset == null) {
                left = (mSize.x / 2 - (BAR_WIDTH / 2));
            } else {
                if (mBarXOffset > (mSize.x - BAR_WIDTH)) {
                    left = mSize.x - BAR_WIDTH;
                } else {
                    left = mBarXOffset;
                }
            }
            int top = mSize.y - BAR_BOTTOM_OFFSET;
            int right = left + BAR_WIDTH;
            int bottom = top + BAR_HEIGHT;
            Rect bar = new Rect();
            bar.set(left, top, right, bottom);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            mCanvas.drawRect(bar, paint);
        }

        protected void drawBall() {
            if (mBallX == null) {
                mBallX = (mSize.x / 2) - (BALL_RADIUS / 2);
            }
            if (mBallY == null) {
                mBallY = (float) mSize.y - BALL_BOTTOM_OFFET;
            }
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            mCanvas.drawCircle(mBallX, mBallY, BALL_RADIUS, paint);
        }

        public void moveBall() {
            mBallX += 10f;
            mBallY -= 10f;
            this.invalidate();
        }
    }
}