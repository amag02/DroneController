package com.example.amag0.dronecontroller2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Alexander Magyari on 11/21/2019.
 * Class handler for dialogue box functionality.
 */

public class DialogBox extends DialogFragment {
    private String messageContent = "No Message";

    public void setMessageContent(String message){ messageContent = message; }


    // Fatal error message box. Alerts the user that their program is about to end,
    // with a sad message prior to exiting.
    public Dialog errorDialog(Context context){
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setMessage(messageContent)
                .setTitle("Fatal error!")
                .setNegativeButton("Close Application", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
        return builder.create();
    }
}
