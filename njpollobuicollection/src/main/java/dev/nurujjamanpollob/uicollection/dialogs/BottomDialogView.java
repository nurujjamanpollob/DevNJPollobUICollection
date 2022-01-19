package dev.nurujjamanpollob.uicollection.dialogs;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import dev.nurujjamanpollob.uicollection.R;

public class BottomDialogView extends AppCompatDialog {

    private final Context context;
    private Boolean cancelDialogOnClickOuside = false;
    private CoordinatorLayout rootView;
    private boolean isShowing = false;
    private OnDialogUiLoadListener uiLoadListener;
    private int layoutId = 0;



    public BottomDialogView(@NonNull Context context) {
        super(context);

        this.context = context;
    }

    {
        // Set window feature before drawing content on screen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

    }

    /**
     * Method to set Dialog Layout View programmatically.
     * Layout direction is from bottom on the screen,
     * @param dialogLayoutID layout ID of dialog view that needs to be drawn on screen.
     */

    public void setDialogResourceByLayout(@LayoutRes int dialogLayoutID) {

        this.layoutId = dialogLayoutID;

            drawDialogUI(dialogLayoutID, false);


    }

    public void cancelDialogOnClickOutside(Boolean isCancelOnClickOutside) {

        this.cancelDialogOnClickOuside = isCancelOnClickOutside;
    }


    private void cancelDialogOnClickOutside() {

        rootView.setOnClickListener(v -> {

            if (cancelDialogOnClickOuside) {
                onBackPressed();
            }
        });


    }



    public Boolean isDialogShowing(){

        return isShowing;
    }





    @NonNull
    public <T extends View> T getViewByIdentity(@IdRes int id, T viewExpected){

        return rootView.findViewById(id) != null ? rootView.findViewById(id) : viewExpected;

    }

    public interface OnDialogUiLoadListener{

        default void onUiLoaded(){}
        default void rootUI(CoordinatorLayout view){}

    }


    public void setUILoadListener(OnDialogUiLoadListener uiLoadListener){

        this.uiLoadListener = uiLoadListener;

        drawDialogUI(layoutId, isShowing());


    }



    private void drawDialogUI(int id, boolean skipExisting) {


        if (!skipExisting) {
            // Set content view
            setContentView(R.layout.custom_dialog_layout);
            Window window = getWindow();
            window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // So, inflate the view and attach to CoordinatorLayout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            CoordinatorLayout cl = findViewById(R.id.bottom_view_dialog_root_coordinatorelayout);
            rootView = findViewById(R.id.custom_dialog_layout_root);
            inflater.inflate(id, cl, true);
            // Set Dialog click outside listener
            cancelDialogOnClickOutside();
            isShowing = true;


            show();

        }


        if (isDialogShowing()) {

            if (uiLoadListener != null) {

                if (rootView != null) {

                    uiLoadListener.onUiLoaded();
                    uiLoadListener.rootUI(rootView);
                }

            }
        }


    }


}
