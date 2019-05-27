package com.laurensius_dede_suhardiman.ekgapps.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.laurensius_dede_suhardiman.ekgapps.EKGApps;
import com.laurensius_dede_suhardiman.ekgapps.R;
import com.laurensius_dede_suhardiman.ekgapps.appcontroller.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FragmentInputPasien extends Fragment {

    private EditText etNamaPasien, etUmur, etAlamat;
    private Spinner spJenisKelamin;
    private Button btnSubmit;
    private LinearLayout llInputPasien;

    private String name,age,gender,address;


    public FragmentInputPasien() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterInputPasien = inflater.inflate(R.layout.fragment_input_pasien, container, false);
        llInputPasien = (LinearLayout)inflaterInputPasien.findViewById(R.id.ll_input_pasien);
        etNamaPasien = (EditText)inflaterInputPasien.findViewById(R.id.et_nama_pasien);
        etUmur = (EditText)inflaterInputPasien.findViewById(R.id.et_umur);
        etAlamat = (EditText)inflaterInputPasien.findViewById(R.id.et_alamat);
        btnSubmit = (Button)inflaterInputPasien.findViewById(R.id.btn_submit);
        spJenisKelamin = (Spinner)inflaterInputPasien.findViewById(R.id.sp_jenis_kelamin);
        return inflaterInputPasien;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clearForm();

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter(
                getContext(),
                R.layout.support_simple_spinner_dropdown_item,getResources().getStringArray(R.array.jenis_kelamin));
        spJenisKelamin.setAdapter(categoryAdapter);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput() == true){
                    name = etNamaPasien.getText().toString();
                    age = etUmur.getText().toString();
                    gender = spJenisKelamin.getSelectedItem().toString();
                    address = etAlamat.getText().toString();
                    inputPasien();
                }else{
                    Snackbar snackbar = Snackbar.make(llInputPasien,"Pastikan semua bidang telah terisi!",Snackbar.LENGTH_LONG);
                    snackbar.show();
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

    Boolean validateInput(){
        if(etNamaPasien.getText().toString().equals("") || etUmur.getText().toString().equals("") || etAlamat.getText().toString().equals("")){
            return false;
        }else{
            return true;
        }
    }

    void inputPasien(){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String login = getResources().getString(R.string.req_input_pasien);
        String url = EKGApps.endpoint
                .concat(getResources().getString(R.string.endpoint_input_pasien))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.endpoint_slash));
        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage(getResources().getString(R.string.dialog_loading));
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put(getResources().getString(R.string.param_http_name), name);
        params.put(getResources().getString(R.string.param_http_age), age);
        params.put(getResources().getString(R.string.param_http_gender), gender);
        params.put(getResources().getString(R.string.param_http_address), address);
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,url, parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        Log.d(getResources().getString(R.string.debug),response.toString());
                        parseInputResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(llInputPasien, error.toString(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, login);
    }

    void parseInputResponse(JSONObject response){
        try{
            if(response.getString(getResources().getString(R.string.param_severity)).equals(getResources().getString(R.string.severity_success))){
                clearForm();
                Snackbar snackbar = Snackbar.make(llInputPasien, response.getString(getResources().getString(R.string.param_message)), Snackbar.LENGTH_LONG);
                snackbar.show();
            }else
            if(!response.getString(getResources().getString(R.string.param_severity)).equals(getResources().getString(R.string.severity_success))){
                Snackbar snackbar = Snackbar.make(llInputPasien, response.getString(getResources().getString(R.string.param_message)), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }catch (JSONException e){
            Snackbar snackbar = Snackbar.make(llInputPasien, getResources().getString(R.string.error_parse_json), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    void clearForm(){
        etNamaPasien.setText("");
        etUmur.setText("");
        etAlamat.setText("");
        etNamaPasien.requestFocus();
    }

}
