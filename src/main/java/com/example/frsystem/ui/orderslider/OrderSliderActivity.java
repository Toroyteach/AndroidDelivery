package com.example.frsystem.ui.orderslider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.frsystem.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.material.tabs.TabLayout.GRAVITY_FILL;

public class OrderSliderActivity extends AppCompatActivity {

    public static final String Database_Path = "users_available_jobs_image";
    private StorageReference storageReference;
    private AppBarConfiguration mAppBarConfiguration;
    private Toolbar cutomtoolbar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_slider);

        storageReference = FirebaseStorage.getInstance().getReference();


        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Sign Up"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign in"));
        tabLayout.setTabGravity(GRAVITY_FILL);

        final ViewPager viewpage = findViewById(R.id.orderpager);
        PageAdapter pageAdaptor = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewpage.setAdapter(pageAdaptor);
        viewpage.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpage.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}
