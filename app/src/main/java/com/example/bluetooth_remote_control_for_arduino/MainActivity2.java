package com.example.bluetooth_remote_control_for_arduino;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.bluetooth_remote_control_for_arduino.firstPage.ConnectionMainActivity;

public class MainActivity2 extends AppCompatActivity {
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ImageView joystickBase = findViewById(R.id.joystick_base);
        ImageView joystickHandle = findViewById(R.id.joystick_handle);

        joystickHandle.setOnTouchListener(new View.OnTouchListener() {
            private double startX, startY;
            private float joystickCenterX, joystickCenterY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int MAX_DISTANCE = 200;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();

                        joystickCenterX = view.getX() + (float) view.getWidth() / 2.0f;
                        joystickCenterY = view.getY() + (float) view.getHeight() / 2.0f;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        double distanceX = event.getX() - startX;
                        double distanceY = event.getY() - startY;

                        double radius = Math.min(MAX_DISTANCE, Math.hypot(distanceX, distanceY));
                        System.out.println("radius = " + radius);

                        double angle = Math.atan2(distanceY, distanceX);
                        System.out.println("angle = " + angle);

                        double handleX = joystickCenterX + radius * Math.cos(angle);
                        double handleY = joystickCenterY + radius * Math.sin(angle);

                        joystickHandle.setX((float) handleX - (float) joystickHandle.getWidth() / 2.0f);
                        joystickHandle.setY((float) handleY - (float) joystickHandle.getHeight() / 2.0f);
                        break;

                    case MotionEvent.ACTION_UP:
                        joystickHandle.setX(joystickCenterX - (float) joystickHandle.getWidth() / 2.0f);
                        joystickHandle.setY(joystickCenterY - (float) joystickHandle.getHeight() / 2.0f);

                        break;
                }

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ConnectionMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}