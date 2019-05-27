package com.laurensius_dede_suhardiman.ekgapps.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.snackbar.Snackbar;
import com.laurensius_dede_suhardiman.ekgapps.EKGApps;
import com.laurensius_dede_suhardiman.ekgapps.R;
import com.laurensius_dede_suhardiman.ekgapps.appcontroller.AppController;
import com.laurensius_dede_suhardiman.ekgapps.model.BPM;
import com.laurensius_dede_suhardiman.ekgapps.model.Patient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentMonitoring extends Fragment {


    private TextView tvNamaPasien, tvUmur, tvJenisKelamin, tvAlamat, tvNotifStart, tvNotifNoBPM;
    private Button btnEKG;
    private LinearLayout llMonitoring, llAvailable, llNoData;
    private Patient currentPatient;

    Timer timer = new Timer();
    private LineChart lcEKG;
    List<BPM> listBPM = new ArrayList<>();

    public FragmentMonitoring() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View inflaterMonitoring = inflater.inflate(R.layout.fragment_monitoring, container, false);
        llMonitoring = (LinearLayout)inflaterMonitoring.findViewById(R.id.ll_monitoring);
        llAvailable = (LinearLayout)inflaterMonitoring.findViewById(R.id.ll_available);
        llNoData = (LinearLayout)inflaterMonitoring.findViewById(R.id.ll_no_data);
        tvNamaPasien = (TextView) inflaterMonitoring.findViewById(R.id.tv_nama_pasien);
        tvUmur = (TextView) inflaterMonitoring.findViewById(R.id.tv_umur);
        tvJenisKelamin = (TextView) inflaterMonitoring.findViewById(R.id.tv_jenis_kelamin);
        tvAlamat = (TextView) inflaterMonitoring.findViewById(R.id.tv_alamat);
        btnEKG = (Button)inflaterMonitoring.findViewById(R.id.btn_ekg);
        tvNotifStart = (TextView)inflaterMonitoring.findViewById(R.id.tv_notif_start);
        tvNotifNoBPM = (TextView)inflaterMonitoring.findViewById(R.id.tv_notif_no_bpm);
        lcEKG = (LineChart)inflaterMonitoring.findViewById(R.id.lc_ekg);
        return inflaterMonitoring;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llNoData.setVisibility(View.VISIBLE);
        llAvailable.setVisibility(View.GONE);
        requestPasienProses();

        llNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPasienProses();
            }
        });

        btnEKG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPatient.getStatus().equals(getResources().getString(R.string.status_open))){
                    requestUpdateStatus(currentPatient.getId(),getResources().getString(R.string.status_proses));
                    startEKG();
                }else
                if(currentPatient.getStatus().equals(getResources().getString(R.string.status_proses))){
                    requestUpdateStatus(currentPatient.getId(),getResources().getString(R.string.status_close));
                    stopEKG();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    void requestPasienProses(){
        String tag_req_monitoring = getResources().getString(R.string.req_pasien_proses);
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String url =  EKGApps.endpoint
                .concat(getResources().getString(R.string.endpoint_pasien_proses))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.endpoint_slash));
        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage(getResources().getString(R.string.dialog_loading));
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(getResources().getString(R.string.debug), response.toString());
                        parsePasienProses(response);
                        pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(getResources().getString(R.string.debug), error.toString());
                        Snackbar snackbar = Snackbar.make(llMonitoring, error.toString(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        pDialog.dismiss();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_monitoring);
    }

    void parsePasienProses(JSONObject response){
        try{
            if(response.getString(getResources().getString(R.string.param_severity)).equals(getResources().getString(R.string.severity_success))){
                JSONObject objContent = response.getJSONObject(getResources().getString(R.string.param_content));
                currentPatient = new Patient(
                    objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(0).getString(getResources().getString(R.string.param_id)),
                        objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(0).getString(getResources().getString(R.string.param_name)),
                        objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(0).getString(getResources().getString(R.string.param_age)),
                        objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(0).getString(getResources().getString(R.string.param_gender)),
                        objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(0).getString(getResources().getString(R.string.param_address)),
                        objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(0).getString(getResources().getString(R.string.param_status)),
                        objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(0).getString(getResources().getString(R.string.param_datetime))
                );
                llAvailable.setVisibility(View.VISIBLE);
                llNoData.setVisibility(View.GONE);
                tvNamaPasien.setText(currentPatient.getName());
                tvUmur.setText(currentPatient.getAge());
                tvJenisKelamin.setText(currentPatient.getGender());
                tvAlamat.setText(currentPatient.getAddress());
                if(currentPatient.getStatus().equals(getResources().getString(R.string.status_open))){
                    Log.d("STATUS","EQUALS OPEN");
                    btnEKG.setText(getResources().getString(R.string.start_ekg));
                    btnEKG.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    tvNotifStart.setVisibility(View.VISIBLE);
                    tvNotifNoBPM.setVisibility(View.GONE);
                }else
                if(currentPatient.getStatus().equals(getResources().getString(R.string.status_proses))){
                    btnEKG.setText(getResources().getString(R.string.stop_ekg));
                    btnEKG.setBackgroundColor(getResources().getColor(R.color.colorAccentInverse));
                    tvNotifStart.setVisibility(View.GONE);
                    startEKG();
                }
            }else{
                llAvailable.setVisibility(View.GONE);
                llNoData.setVisibility(View.VISIBLE);
                Snackbar snackbar = Snackbar.make(llMonitoring,response.getString(getResources().getString(R.string.param_message)),Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }catch (JSONException e){
            Snackbar snackbar = Snackbar.make(llMonitoring,getResources().getString(R.string.error_parse_json),Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }


    void requestUpdateStatus(String idPatient, String status){
        String tag_req_update_status = getResources().getString(R.string.req_pasien_proses);
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String url =  EKGApps.endpoint
                .concat(getResources().getString(R.string.endpoint_update_status))
                .concat(idPatient)
                .concat(getResources().getString(R.string.endpoint_slash))
                .concat(status)
                .concat(getResources().getString(R.string.endpoint_slash))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.endpoint_slash));
        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage(getResources().getString(R.string.dialog_loading));
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(getResources().getString(R.string.debug), response.toString());
                        parseUpdateStatus(response);
                        pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(getResources().getString(R.string.debug), error.toString());
                        Snackbar snackbar = Snackbar.make(llMonitoring, error.toString(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        pDialog.dismiss();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_update_status);
    }

    void parseUpdateStatus(JSONObject response){
        try {
            if(response.getString(getResources().getString(R.string.param_severity)).equals(getResources().getString(R.string.severity_success))) {
                requestPasienProses();
            }else{
                Snackbar snackbar = Snackbar.make(llMonitoring,response.getString(getResources().getString(R.string.param_message)),Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }catch (JSONException e){
            Snackbar snackbar = Snackbar.make(llMonitoring,getResources().getString(R.string.error_parse_json),Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }


    void requestMonitoring(String idPatient){
        String tag_req_monitoring = getResources().getString(R.string.req_monitoring);
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String url =  EKGApps.endpoint
                .concat(getResources().getString(R.string.endpoint_bpm_monitoring))
                .concat(idPatient)
                .concat(getResources().getString(R.string.endpoint_slash))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.endpoint_slash));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(getResources().getString(R.string.debug), response.toString());
                        parseMonitoring(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(getResources().getString(R.string.debug), error.toString());
                        Snackbar snackbar = Snackbar.make(llMonitoring, error.toString(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_monitoring);
    }

    void parseMonitoring(JSONObject response){
        try {
            if(response.getString(getResources().getString(R.string.param_severity)).equals(getResources().getString(R.string.severity_success))) {
                JSONObject objContent = response.getJSONObject(getResources().getString(R.string.param_content));
                JSONArray arrayBPM = objContent.getJSONArray(getResources().getString(R.string.param_bpm));
                if(arrayBPM.length() > 0){
                    lcEKG.setVisibility(View.VISIBLE);
                    tvNotifNoBPM.setVisibility(View.GONE);
                    if(listBPM.size() > 0){
                        listBPM.clear();
                    }
                    for(int x=0;x<arrayBPM.length();x++){
                        listBPM.add(new BPM(
                                arrayBPM.getJSONObject(x).getString(getResources().getString(R.string.param_id)),
                                arrayBPM.getJSONObject(x).getString(getResources().getString(R.string.param_id_patient)),
                                arrayBPM.getJSONObject(x).getString(getResources().getString(R.string.param_bpm_val)),
                                arrayBPM.getJSONObject(x).getString(getResources().getString(R.string.param_datetime))
                        ));
                    }
                    buatGrafik();
                }else{
                    lcEKG.setVisibility(View.GONE);
                    tvNotifNoBPM.setVisibility(View.VISIBLE);
                }
            }else{
                Snackbar snackbar = Snackbar.make(llMonitoring,response.getString(getResources().getString(R.string.param_message)),Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }catch (JSONException e){
            Snackbar snackbar = Snackbar.make(llMonitoring,getResources().getString(R.string.error_parse_json),Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    void buatGrafik(){
        List<Entry> entries_ketinggian = new ArrayList<Entry>();
        int index = 1;
        for(int x=0;x<listBPM.size();x++){
            entries_ketinggian.add(new Entry(index, Float.parseFloat(listBPM.get(x).getBPM())));
            index++;
        }
        lcEKG.clear();
        LineDataSet datasetEKG = new LineDataSet(entries_ketinggian, "BPM");
        datasetEKG.setColor(Color.parseColor("#EE6722"));
        datasetEKG.setValueTextColor(Color.parseColor("#999999"));
        LineData lineDataKetinggian = new LineData(datasetEKG);
        lcEKG.setData(lineDataKetinggian);
        lcEKG.invalidate();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        timer.cancel();
    }

    void startEKG(){
        final Handler handler = new Handler();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            requestMonitoring(currentPatient.getId());
                        } catch (Exception e) {
                            Log.d(getResources().getString(R.string.debug), e.getMessage());
                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.fl_master),e.getMessage(), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000);
    }

    void stopEKG(){
        timer.cancel();
    }

}
