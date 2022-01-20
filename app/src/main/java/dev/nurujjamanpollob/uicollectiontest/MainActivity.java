package dev.nurujjamanpollob.uicollectiontest;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dev.nurujjamanpollob.uicollection.dialogs.AdvancedDialogView;
import dev.nurujjamanpollob.uicollection.dialogs.DialogOptions;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {



            AdvancedDialogView view1 = new AdvancedDialogView(MainActivity.this);
            view1.setDialogResourceByLayoutId(R.layout.dialog_view, null);
            view1.setUiAnimation(R.anim.dialog_sample_animation);
            view1.cancelDialogOnClickOutside(true);
            view1.setUILoadListener(new AdvancedDialogView.OnDialogUiLoadListener() {
                @Override
                public void onUiLoaded() {


                    Button signup = view1.getViewByIdentity(R.id.sign_up, new Button(getApplicationContext()));

                    // Set sign up click listener
                    signup.setOnClickListener(v -> {

                        view1.setDialogResourceByLayoutId(R.layout.dialog_view_sign_up, DialogOptions.DIALOG_GRAVITY_BOTTOM);

                        view1.setUiAnimation(R.anim.dialog_rotate);

                    });

                }
            });




        });
    }


}