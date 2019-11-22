package com.example.amag0.dronecontroller2;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.TextView;

import java.io.IOError;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements JoyStickView.JoystickListener{

    float throttle = 0, yaw = 0, pitch = 0, roll = 0;
    String droneID = "null";
    TextView varView = null;
    BLE b1;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            //android.Manifest.permission.BLUETOOTH,
            //android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };


    //Creating first checks for bluetooth permissions and
    //requests if they are not already provided.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            if (!hasPermissions(this, PERMISSIONS)) {
                Log.d("Debug", "Requesting Permissions");
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            } else {
                Log.d("Debug", "Already Have Permissions.");
                init();
            }
        }
        catch(Exception e){
            DialogBox errorBox = new DialogBox();
            errorBox.setMessageContent("Fatal Error: " + e + ". Closing Application.");
            errorBox.errorDialog(this);
        }
    }

    private void onRequestPermissionsResult(){
        try {
            init();
        }
        catch(Exception e){
            DialogBox errorBox = new DialogBox();
            errorBox.setMessageContent("Fatal Error: " + e + ". Closing Application.");
            errorBox.errorDialog(this);
        }
    }

    //Check if bluetooth is available on this device
    private void init() throws IOException{
        b1 = new BLE();
        if (!b1.blueToothExists()) throw new IOException("Bluetooth device does not exist on this device.");
        if (!b1.blueToothIsEnabled()) throw new IOException("Bluetooth is not enabled on this device.");
        if (!b1.blueToothLEExists()) throw new IOException("BLE does not exist on this device");
        varView = (TextView)findViewById(R.id.varView);
        varViewUpdate();
    }

    //Waits for the joystick movement, and updates the variable text box
    //to reflect the changes.
    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int source){
        switch(source)

        {

            case R.id.rightStick:

                Log.d("Right Joystick", "X: " + xPercent + " Y: " + yPercent);
                roll = xPercent;
                pitch = yPercent;
                break;

            case R.id.leftStick:

                Log.d("Left Joystick", "X: " + xPercent + " Y: " + yPercent);
                throttle = yPercent;
                yaw = xPercent;
                break;

        }
        varViewUpdate();
    }

    public void varViewUpdate(){
        String output = String.format(
                "Throttle: %d%% " +
                "\nYaw: %d%%" +
                "\nPitch: %d%%" +
                "\nRoll: %d%%" +
                "\nID: %s",
                (int)(throttle * 100), (int)(yaw * 100), (int)(pitch * 100), (int)(roll * 100), b1.getName()
                );

        varView.setText(output);
    }

    //Check permission list and requests them from user if not
    //already provided
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
