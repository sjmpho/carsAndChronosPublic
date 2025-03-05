package com.example.carsandchronos.Mechanic_Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.carsandchronos.Fragments.MainFragment;
import com.example.carsandchronos.Fragments.SettingsFragment;
import com.example.carsandchronos.Models.Mechanic;
import com.example.carsandchronos.R;
import com.example.carsandchronos.Utility.Utility;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class mech_main_new extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    TextView name ;
    int MechID;
    Mechanic mechanic;
    MainFragment mainFragment;
    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mech_main_new);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.btm);
        name = findViewById(R.id.tv_name);

        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                finishAffinity();
            }
        });
        mechanic = Utility.getMechanic();
        MechID = Utility.getMechID();
        name.setText(mechanic.getName()+" "+mechanic.getSurname());
        Initailise(MechID);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Bookings)
                {
                    name.setText(mechanic.getName()+" "+mechanic.getSurname());
                    SwitchFrags(mainFragment);

                } else if (item.getItemId() == R.id.account)
                {
                    name.setText("Settings");
                    SwitchFrags(settingsFragment);

                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.Bookings);
    }
    private void SwitchFrags(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_new_mech,fragment)
                .commit();

    }
    private void Initailise(int ID){
        mainFragment = MainFragment.newInstance(mechanic);
        settingsFragment = SettingsFragment.newInstance(mechanic);

    }
}