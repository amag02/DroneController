package com.example.amag0.dronecontroller2;

/**
 * Created by amag0 on 11/11/2019.
 */

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
/**
 * Created by amag0 on 11/11/2019.
 */

public class JoyStickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    float centerX;
    float centerY;
    float baseRadius;
    float hatRadius;
    float maxDistance;
    int lockedAxis = 0; //0 for unlocked, 1 for x, 2 for y
    float lockDistance;
    private JoystickListener joystickCallback;

    public JoyStickView(Context context){
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;
    }

    public JoyStickView(Context context, AttributeSet attributes){
        super(context, attributes);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;
    }

    void setupDimensions(){
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / 3;
        hatRadius = Math.min(getWidth(), getHeight()) / 20;
        maxDistance = 1.3f * baseRadius;
        lockDistance = .1f * maxDistance;
    }

    private void drawJoystick(float newX, float newY){
        if(getHolder().getSurface().isValid()) {
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            //Draw background
            colors.setARGB(255, 80, 80, 80);
            myCanvas.drawRect(0, 0,  getWidth(), getHeight(), colors);

            //Draw stick box
            colors.setARGB(255, 50, 50, 50);
            RectF backRect = new RectF((float)(centerX - (1.1 * baseRadius)), (float)(centerY - (1.1 * baseRadius)), (float)(centerX + (1.1 * baseRadius)), (float)(centerY + (1.1 * baseRadius)));
            myCanvas.drawRoundRect(backRect, 15, 15, colors);

            //Draw Stick Guide
            colors.setARGB(255, 180 , 180, 180);
            Shader shader = new RadialGradient(
                    (float)centerX,
                    (float)centerY,
                    360f,
                    new int[] {Color.rgb(160, 160, 160), Color.rgb(120, 120, 120), Color.rgb(110,110,110)},
                    new float[] {0, .6f, 1},
                    Shader.TileMode.CLAMP);
            colors.setShader(shader);
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors);
            colors.setShader(null);

            //Stick line guides
            colors.setARGB(255, 50, 50, 50);
            colors.setStrokeWidth(25);
            myCanvas.drawLine(centerX - (2 * baseRadius / 3), centerY, centerX + (2 * baseRadius / 3), centerY, colors);
            myCanvas.drawLine(centerX, centerY - (2 * baseRadius / 3), centerX, centerY + (2 * baseRadius / 3), colors);
            myCanvas.drawCircle(centerX, centerY, lockDistance, colors);

            //Draw shaft
            colors.setARGB(255, 110, 110, 110);
            Path linepath = new Path();
            linepath.reset();
            linepath.moveTo(centerX + (hatRadius / 3) , centerY + ((newY - centerY) / 2));
            linepath.lineTo(centerX - (hatRadius / 3), centerY + ((newY - centerY) / 2));
            linepath.lineTo(newX - hatRadius, newY);
            linepath.lineTo(newX + hatRadius, newY);
            linepath.lineTo(centerX + (hatRadius / 3), centerY + ((newY - centerY) / 2));
            myCanvas.drawPath(linepath, colors);
            linepath.reset();
            linepath.moveTo(centerX + ((newX - centerX) / 2), centerY + (hatRadius / 3));
            linepath.lineTo(centerX + ((newX - centerX) / 2), centerY - (hatRadius / 3));
            linepath.lineTo(newX, newY - hatRadius);
            linepath.lineTo(newX, newY + hatRadius);
            linepath.lineTo(centerX + ((newX - centerX) / 2), centerY + (hatRadius / 3));
            myCanvas.drawPath(linepath, colors);

            //Draw top of stick
            colors.setARGB(255, 120, 120, 120);
            myCanvas.drawCircle(newX, newY, hatRadius, colors);
            getHolder().unlockCanvasAndPost(myCanvas);
        }
    }

    //Waits for the joystick to move
    public interface JoystickListener
    {
        void onJoystickMoved(float xPercent, float yPercent, int source);
    }


    //When the user touches inside the joystick view,
    //move the top of the joystick to the users finger
    public boolean onTouch(View v, MotionEvent e){
        if(v.equals(this)){
            if(e.getAction() != e.ACTION_UP){
                float distance = (float)Math.sqrt((double)((centerY - e.getY()) * (centerY - e.getY())) + ((e.getX() - centerX) * (e.getX() - centerX)));

                if (lockedAxis == 2) {
                    if (e.getY() < centerY - maxDistance) {
                        drawJoystick(centerX, centerY - maxDistance);
                        joystickCallback.onJoystickMoved(0, 1, getId());
                    } else if (e.getY() > maxDistance + centerY) {
                        drawJoystick(centerX, centerY + maxDistance);
                        joystickCallback.onJoystickMoved(0, -1, getId());
                    } else {
                        drawJoystick(centerX, e.getY());
                        joystickCallback.onJoystickMoved(0, -1 * ((e.getY() - centerY) / maxDistance), getId());
                    }
                    if ((Math.abs(centerX - e.getX()) < lockDistance)&& (Math.abs(centerY - e.getY()) < lockDistance)){
                        lockedAxis = 0;
                    }
                }
                if (lockedAxis == 1) {
                    if (e.getX() > centerX + maxDistance) {
                        drawJoystick(centerX + maxDistance, centerY);
                        joystickCallback.onJoystickMoved(1, 0, getId());
                    } else if (e.getX() < centerX - maxDistance) {
                        drawJoystick(centerX - maxDistance, centerY);
                        joystickCallback.onJoystickMoved(-1, 0, getId());
                    } else {
                        drawJoystick(e.getX(), centerY);
                        joystickCallback.onJoystickMoved((e.getX() - centerX) / maxDistance, 0, getId());
                    }
                    if ((Math.abs(centerX - e.getX()) < lockDistance)&& (Math.abs(centerY - e.getY()) < lockDistance)){
                        lockedAxis = 0;
                    }
                }
                if (lockedAxis == 0) {
                    if (distance > lockDistance){
                        if (Math.abs(e.getX() - centerX) > Math.abs(e.getY() - centerY)) {
                            lockedAxis = 1;
                            Log.d("DEBUG", "Locked X Axis!");
                        }
                        else {
                            lockedAxis = 2;
                            Log.d("DEBUG", "Locked Y Axis!");
                        }
                    } else {
                        drawJoystick(e.getX(), e.getY());
                        joystickCallback.onJoystickMoved((e.getX() - centerX) / baseRadius, (e.getY() - centerY) / baseRadius, getId());
                    }
                }
            }
            else{
                drawJoystick(centerX, centerY);
                joystickCallback.onJoystickMoved(0, 0, getId());
                lockedAxis = 0;
            }
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        setupDimensions();
        drawJoystick(centerX, centerY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){

    }
}
