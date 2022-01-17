package dev.nurujjamanpollob.uicollection.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import dev.nurujjamanpollob.uicollection.R;

public class BottomDialogView extends AppCompatDialog {

    private final Context context;

    public BottomDialogView(@NonNull Context context) {
        super(context);

        this.context = context;
    }

    {
        // Set window feature before drawing content on screen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

    }


    public void setDialogResourceByLayout(int dialogLayoutID){

        // Set content view
        setContentView(R.layout.custom_dialog_layout);

        Window window = getWindow();
        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // So, inflate the view and attach to CoordinatorLayout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CoordinatorLayout cl = findViewById(R.id.bottom_view_dialog_root_coordinatorelayout);
        inflater.inflate(dialogLayoutID, cl, true);

    }



}
