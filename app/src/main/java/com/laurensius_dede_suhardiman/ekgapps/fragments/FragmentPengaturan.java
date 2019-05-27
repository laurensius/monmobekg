package com.laurensius_dede_suhardiman.ekgapps.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.laurensius_dede_suhardiman.ekgapps.EKGApps;
import com.laurensius_dede_suhardiman.ekgapps.R;
import com.laurensius_dede_suhardiman.ekgapps.utilities.AppSessionManager;

public class FragmentPengaturan extends Fragment {

    private EditText etServerEndpoint;
    private Button btnSave;
    private LinearLayout llPengaturan;

    public FragmentPengaturan() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterPengaturan = inflater.inflate(R.layout.fragment_pengaturan, container, false);
        etServerEndpoint = (EditText)inflaterPengaturan.findViewById(R.id.et_server_endpoint);
        btnSave = (Button)inflaterPengaturan.findViewById(R.id.btn_save);
        llPengaturan = (LinearLayout)inflaterPengaturan.findViewById(R.id.ll_pengaturan);
        return inflaterPengaturan;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etServerEndpoint.setText(EKGApps.endpoint);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etServerEndpoint.getText().toString().equals("") ){
                    Snackbar snackbar = Snackbar.make(llPengaturan,getResources().getString(R.string.preferences_setting_null),Snackbar.LENGTH_LONG);
                    snackbar.show();
                }else{
                    AppSessionManager appSessionManager = new AppSessionManager(getActivity());
                    appSessionManager.setSharedPreferences(etServerEndpoint.getText().toString());
                    String recentEndpoint = EKGApps.endpoint;
                    EKGApps ekgApps = new EKGApps();
                    ekgApps.renewSession(getContext());
                    String newEndpoint = EKGApps.endpoint;
                    if(recentEndpoint.equals(newEndpoint)){
                        Snackbar snackbar = Snackbar.make(llPengaturan,getResources().getString(R.string.preferences_setting_success),Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }else{
                        Snackbar snackbar = Snackbar.make(llPengaturan,getResources().getString(R.string.preferences_setting_success),Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
