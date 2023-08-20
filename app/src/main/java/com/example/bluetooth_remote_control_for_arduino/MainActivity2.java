package com.example.bluetooth_remote_control_for_arduino;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.bluetooth_remote_control_for_arduino.firstPage.ConnectionMainActivity;

import java.io.IOException;
import java.io.OutputStream;

public class MainActivity2 extends AppCompatActivity {

    int[] data;
    double radius;
    double angle;
    OutputStream outputStream;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        
        data = new int[4];
        outputStream = StreamManager.getInstance().getOutputStream();
        
//        try {
//            outputStream.write(255);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

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

                        radius = Math.min(MAX_DISTANCE, Math.hypot(distanceX, distanceY));
                        System.out.println("radius = " + radius);

                        angle = Math.atan2(distanceY, distanceX);
                        System.out.println("angle = " + angle);

                        double handleX = joystickCenterX + radius * Math.cos(angle);
                        double handleY = joystickCenterY + radius * Math.sin(angle);

                        joystickHandle.setX((float) handleX - (float) joystickHandle.getWidth() / 2.0f);
                        joystickHandle.setY((float) handleY - (float) joystickHandle.getHeight() / 2.0f);

                        setData();
                        try {
                            sendData();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        joystickHandle.setX(joystickCenterX - (float) joystickHandle.getWidth() / 2.0f);
                        joystickHandle.setY(joystickCenterY - (float) joystickHandle.getHeight() / 2.0f);

                        setStopData();
                        try {
                            sendData();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        break;
                }

                return true;
            }
        });
    }

    void setData() {
        if (-Math.PI / 2 <= angle && angle < -Math.PI / 4) {
            data[0] = 1;
            data[1] = 1;
            data[2] = (int) radius;
            data[3] = (int) (radius * (-Math.PI / 4 - angle) / (Math.PI / 4));
        } else if (-Math.PI / 4 <= angle && angle < 0) {
            data[0] = 1;
            data[1] = 0;
            data[2] = (int) radius;
            data[3] = (int) (radius * (Math.PI / 4 + angle) / (Math.PI / 4));
        } else if (Math.PI / 4 <= angle && angle < Math.PI / 2) {
            data[0] = 0;
            data[1] = 0;
            data[2] = (int) radius;
            data[3] = (int) (radius * (-Math.PI / 4 + angle) / (Math.PI / 4));
        } else if (-3 * Math.PI / 4 <= angle && angle < -Math.PI / 2) {
            data[0] = 1;
            data[1] = 1;
            data[2] = (int) (radius * (3 * Math.PI / 4 + angle) / (Math.PI / 4));
            data[3] = (int) radius;
        } else if (-Math.PI <= angle && angle < -3 * Math.PI / 4) {
            data[0] = 0;
            data[1] = 1;
            data[2] = (int) (radius * (-3 * Math.PI / 4 - angle) / (Math.PI / 4));
            data[3] = (int) radius;
        } else if (Math.PI / 2 <= angle && angle < 3 * Math.PI / 4) {
            data[0] = 0;
            data[1] = 0;
            data[2] = (int) (radius * (3 * Math.PI / 4 - angle) / (Math.PI / 4));
            data[3] = (int) radius;
        } else {
            data[0] = 0;
            data[1] = 0;
            data[2] = 0;
            data[3] = 0;
        }

        data[2] += 55;
        data[3] += 55;
    }

    void setStopData() {
        data[0] = 0;
        data[1] = 0;
        data[2] = 0;
        data[3] = 0;
    }

    void sendData() throws IOException {
        for (int i = 0; i < 4; i++) {
            outputStream.write(data[i]);
        }

        outputStream.write(21); // end code
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ConnectionMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}