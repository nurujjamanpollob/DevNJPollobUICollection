package dev.nurujjamanpollob.uicollection.dialogs;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
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
 * Why? because, the View you have passed, it is inflated by the context of BottomDialogView, not by Context of
 * Your activity / fragment.
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

public class AdvancedDialogView extends AppCompatDialog {

    private final Context context;
    private Boolean cancelDialogOnClickOuside = false;
    private CoordinatorLayout rootView;
    private boolean isShowing = false;
    private OnDialogUiLoadListener uiLoadListener;
    private int layoutId = 0;
    private DialogOptions dialogOptions;



    public AdvancedDialogView(@NonNull Context context) {
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

    public void setDialogResourceByLayoutId(@LayoutRes int dialogLayoutID, DialogOptions dialogGravityOptions) {

        this.layoutId = dialogLayoutID;
        this.dialogOptions = dialogGravityOptions;

            drawDialogUI(dialogLayoutID, false, dialogOptions);


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
        default void onAnimationFinished(Animation animation, int animationId){}

    }


    public void setUILoadListener(OnDialogUiLoadListener uiLoadListener){

        this.uiLoadListener = uiLoadListener;

        drawDialogUI(layoutId, isShowing(), dialogOptions);


    }



    private void drawDialogUI(int id, boolean skipExisting, DialogOptions dialogOptions) {


        if (!skipExisting) {
            // Set content view
            setContentView(R.layout.custom_dialog_layout);
            Window window = getWindow();
            window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rootView = findViewById(R.id.custom_dialog_layout_root);
            // So, inflate the view and attach to CoordinatorLayout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            CoordinatorLayout cl = getViewByIdentity(R.id.bottom_view_dialog_root_coordinatorelayout, new CoordinatorLayout(context));


            //If Dialog Ui gravity mode is defined
            if(dialogOptions != null) {

                setDialogUiDirectionMode(cl, dialogOptions);


            }else {


                // Use default Ui gravity = bottom
                setDialogUiDirectionMode(cl, DialogOptions.DIALOG_GRAVITY_BOTTOM);

            }

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


    private void setDialogUiDirectionMode(CoordinatorLayout cl, DialogOptions dialogOptions){

        CoordinatorLayout.LayoutParams coOrdinatorParam = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);

        // Set Dialog Gravity
        coOrdinatorParam.gravity = dialogOptions == DialogOptions.DIALOG_GRAVITY_BOTTOM ? Gravity.BOTTOM :
                dialogOptions == DialogOptions.DIALOG_GRAVITY_TOP ? Gravity.TOP : Gravity.CENTER;

        cl.setLayoutParams(coOrdinatorParam);

    }


    public void setUiAnimation(@AnimRes int animationResId){

        if(rootView != null){

            Animation dialogAnimation = AnimationUtils.loadAnimation(context, animationResId);

            // Animate The views
            rootView.setAnimation(dialogAnimation);

            dialogAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    if(uiLoadListener != null){
                        uiLoadListener.onAnimationFinished(animation, animationResId);
                    }

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

    }





}
