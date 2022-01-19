package dev.nurujjamanpollob.uicollection.dialogs;


import android.content.Context;
import android.graphics.Color;
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


/**
 * Advanced Dialog with full support for adding custom view, and to deal with custom views.
 * This class allow you to replace Dialog View without re-initiate them, and covers The issue with
 * that come with getting NullPointerException for View, that is Being erased from Android System,
 * And you try to listen for its event. which is logical, and impossible to fix without create a new instance
 * of this class and separately Query Views that returns Not-Null.
 *
 * I fixed this issue, you are allowed to have Views, which may be Null if it's root is being erased,
 * and will give you correct callback, if you rollback to this view again.
 *
 * Anyway Inflating custom View can be easy, but managing?
 * It may be easy but tricky also.
 *
 * Also, If you have a class, that needs to show different kind of Dialogs with Views,
 * manage View callback from one place, tired of initiate instance of Dialog again and again, then, it's for you.
 *
 * So, Anyway I have a deal for you. Just inflate a view with setDialogResourceByLayoutId(id),
 * Which will draw content based on default or your provided layout_gravity option.
 *
 * So, your layout file will get inflated.
 * So, you might went to listen on View events like setOnClickListener, setOnLongClickListener.
 * Simply, calling findViewById(id) from your activity / fragment instance will give you NullPointerException.
 * Here is a fix:
 * Just set a listener like:
 * BottomViewDialog.setUILoadListener(new BottomDialogView.OnDialogUiLoadListener() {
 *                 @Override
 *                 public void onUiLoaded() {
 *
 *                 // Try to query your view inside this call
 *
 *                 }
 *        });
 *
 *
 * Okay, this listener ensures that all View is 100% drawn by Android System.
 * So, you will be safely able to query your target View,
 * again findViewById(id) from your activity / fragment instance will give you NullPointerException.
 *
 * So, this come with a solution
 * just call getViewByIdentity(viewId, ExpectedViewType) from interface BottomDialogView.OnDialogUiLoadListener{ onUiLoaded(){
 *
 *
 * }} method.
 *
 *
 * Okay, here is code example:
 *
 * BottomViewDialog.setUILoadListener(new BottomDialogView.OnDialogUiLoadListener() {
 *  *                 @Override
 *  *                 public void onUiLoaded() {
 *  *
 *  *                 // get signUp Button instance from inflated layout
 *                      Button signUp = BottomViewDialog.getViewByIdentity(R.id.sign_up_button, new Button(Context));
 *  *
 *  *                 }
 *  *        });
 *
 *  Easy. No exception.
 *
 */

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

    public void setDialogResourceByLayoutId(@LayoutRes int dialogLayoutID) {

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


    /**
     * This method ensures a View object that is being need to get.
     * @param id int to get a View by it's unique identifier.
     * @param viewExpected the kind of View, you expect to get, the passed Object will be returned,
     *                     If the requested View get by unique identifier is not found.
     * @param <T> The kind of View instance, you are looking for.
     * @return either a resolved Not-Null View, or the View object from <T> viewExpected.
     * @throws ClassCastException if the View instance by unique identity number is null,
     * and your expectation <T> viewExpected, or initiation of <T extends View> is wrong.
     */
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
