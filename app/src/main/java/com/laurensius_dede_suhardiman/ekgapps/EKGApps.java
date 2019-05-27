package com.laurensius_dede_suhardiman.ekgapps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.laurensius_dede_suhardiman.ekgapps.fragments.FragmentCetakData;
import com.laurensius_dede_suhardiman.ekgapps.fragments.FragmentInputPasien;
import com.laurensius_dede_suhardiman.ekgapps.fragments.FragmentMonitoring;
import com.laurensius_dede_suhardiman.ekgapps.fragments.FragmentPengaturan;
import com.laurensius_dede_suhardiman.ekgapps.utilities.AppSessionManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

public class EKGApps extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Dialog dialBox;
    private AppSessionManager appSessionManager;
    public static String endpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ekgapps);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        renewSession(EKGApps.this);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment selectedFragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_input_pasien:
                        selectedFragment = new FragmentInputPasien();
                        break;
                    case R.id.navigation_monitoring:
                        selectedFragment = new FragmentMonitoring();
                        break;
                    case R.id.navigation_cetak_data:
                        selectedFragment = new FragmentCetakData();
                        break;
                    case R.id.navigation_pengaturan:
                        selectedFragment = new FragmentPengaturan();
                        break;
                }
                if(selectedFragment != null){
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fl_master, selectedFragment);
                    transaction.commit();
                }

                return true;
            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_master, new FragmentInputPasien());
        transaction.commit();
        dialBox = createDialogBox();
    }

    public void renewSession(Context ctx){
        appSessionManager = new AppSessionManager(ctx);
        EKGApps.endpoint = appSessionManager.endpoint;
    }

    @Override
    public void onBackPressed() {
        dialBox.show();
    }

    private Dialog createDialogBox(){
        dialBox = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.confirm_exit_title))
                .setMessage(getResources().getString(R.string.confirm_exit_body))
                .setPositiveButton(getResources().getString(R.string.confirm_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.confirm_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialBox.dismiss();
                    }
                })
                .create();
        return dialBox;
    }

}
