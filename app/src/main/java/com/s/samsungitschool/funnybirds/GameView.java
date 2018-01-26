package com.s.samsungitschool.funnybirds;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Админ on 17.01.2018.
 */

public class GameView extends View{
    private int viewWidth;
    private int viewHeight;
    private int points = 52;
    private final int timerInterval = 30;

    private Sprite playerBird, enemyBird1, enemyBird2;

    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    public GameView(Context context) {
        super(context);

        Bitmap b  = BitmapFactory.decodeResource(getResources(), R.drawable.player);

        int w = b.getWidth()/5;
        int h = b.getHeight()/3;

        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, 400, firstFrame, b);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 0 &&  j == 0){
                    continue;
                }
                if (i == 2 && j == 3){
                    continue;
                }
                playerBird.addFrame(new Rect(j*w, i*h, j * w + w, i* w + w));
            }
        }
        // ------ Enemy Sprite -----
        b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        w = b.getWidth()/5;
        h = b.getHeight()/3;

        firstFrame = new Rect(4*w, 0, 5*w, h);
        enemyBird1 = new Sprite(2000, 250, -350, 0, firstFrame, b);
        enemyBird2 = new Sprite(6000, 650, -250, 0, firstFrame, b);
        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0; j--) {
                if (i == 0 && j == 4){
                    continue;
                }
                if (i == 2 && j == 0){
                    continue;
                }
                enemyBird1.addFrame(new Rect(j*w, i*h, j*w + w, i*w + w));
                enemyBird2.addFrame(new Rect(j*w, i*h, j*w + w, i*w + w));
            }
        }

        Timer t = new Timer();
        t.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawARGB(250, 127, 199, 255);
        playerBird.draw(canvas);
        enemyBird1.draw(canvas);
        enemyBird2.draw(canvas);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(55.0f);
        p.setColor(Color.WHITE);
        canvas.drawText(points + "", viewWidth - 100, 70, p);
    }

    private void teleportEnemy1(){
        enemyBird1.setX(viewWidth + Math.random() * 500);
        enemyBird1.setY(Math.random() *  (viewHeight - enemyBird1.getFrameHeight()));
    }
    private void teleportEnemy2(){
        enemyBird2.setX(viewWidth + Math.random() * 500);
        enemyBird2.setY(Math.random() *  (viewHeight - enemyBird1.getFrameHeight()));
    }

    protected void update (){
        playerBird.update(timerInterval);
        enemyBird1.update(timerInterval);
        enemyBird2.update(timerInterval);

        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight){
            playerBird.setY(viewHeight - playerBird.getFrameHeight());
            playerBird.setVY(-playerBird.getVY());
            points--;
        } else if(playerBird.getY() < 0){
            playerBird.setY(0);
            playerBird.setVY(-playerBird.getVY());
            points--;
        }

        if (enemyBird1.getX() < -enemyBird1.getFrameWidth()){
            teleportEnemy1();
            points += 20;
        }
        if (enemyBird2.getX() < -enemyBird2.getFrameWidth()){
            teleportEnemy2();
            points += 20;
        }

        if (enemyBird1.intersect(playerBird)){
            teleportEnemy1();
            points -= 30;
        }
        if (enemyBird2.intersect(playerBird)){
            teleportEnemy2();
            points -= 30;
        }
        invalidate();
    }

    class Timer extends CountDownTimer{
        public Timer(){
            super(Integer.MAX_VALUE, timerInterval);
        }

        @Override
        public void onTick(long l) {
            update();
        }

        @Override
        public void onFinish() {

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();

        if (eventAction == MotionEvent.ACTION_DOWN){
            // ----- Move up -----
            if (event.getY() < playerBird.getBoundingBoxRect().top){
                    playerBird.setVY(-400);
                points--;
            } else if (event.getY() > playerBird.getBoundingBoxRect().bottom){
                playerBird.setVY(400);
                points--;
            }
        }
        return true;
    }
}

