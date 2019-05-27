package com.laurensius_dede_suhardiman.ekgapps.fragments;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.laurensius_dede_suhardiman.ekgapps.EKGApps;
import com.laurensius_dede_suhardiman.ekgapps.R;
import com.laurensius_dede_suhardiman.ekgapps.adapter.PatientAdapter;
import com.laurensius_dede_suhardiman.ekgapps.appcontroller.AppController;
import com.laurensius_dede_suhardiman.ekgapps.listener.CustomUIListener;
import com.laurensius_dede_suhardiman.ekgapps.model.Patient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FragmentCetakData extends Fragment {

    private RecyclerView rvPatient;
    private RecyclerView.LayoutManager mLayoutManager;
    private PatientAdapter patientAdapter= null;
    private LinearLayout llNoData,llCetakData;
    List<Patient> listPatient = new ArrayList<>();

    public FragmentCetakData() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterCetakData = inflater.inflate(R.layout.fragment_cetak_data, container, false);
        llCetakData = (LinearLayout)inflaterCetakData.findViewById(R.id.ll_cetak_data);
        llNoData = (LinearLayout)inflaterCetakData.findViewById(R.id.ll_no_data);
        rvPatient = (RecyclerView)inflaterCetakData.findViewById(R.id.rv_patient);
        return inflaterCetakData;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llNoData.setVisibility(View.VISIBLE);
        rvPatient.setVisibility(View.GONE);
        rvPatient.addOnItemTouchListener(new CustomUIListener(getActivity(),new CustomUIListener.OnItemClickListener(){
            @Override
            public void onItemClick(View childVew, int childAdapterPosition) {
                String idPatient = listPatient.get(childAdapterPosition).getId();
                downloadFile(idPatient);
            }
        }));
        llNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCetakData();
            }
        });
        requestCetakData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    void requestCetakData(){
        String tag_req_cetak_data = getResources().getString(R.string.req_cetak_data);
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String url =  EKGApps.endpoint
                .concat(getResources().getString(R.string.endpoint_cetak_data))
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
                        parseCetakData(response);
                        pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(getResources().getString(R.string.debug), error.toString());
                        Snackbar snackbar = Snackbar.make(llCetakData, error.toString(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        pDialog.dismiss();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_cetak_data);
    }

    void parseCetakData(JSONObject response){
        try{
            if(response.getString(getResources().getString(R.string.param_severity)).equals(getResources().getString(R.string.severity_success))){
                JSONObject objContent = response.getJSONObject(getResources().getString(R.string.param_content));
                JSONArray arrayPatient = objContent.getJSONArray(getResources().getString(R.string.param_patient));
                if(arrayPatient.length() > 0){
                    llNoData.setVisibility(View.GONE);
                    rvPatient.setVisibility(View.VISIBLE);
                    if(listPatient.size() > 0){
                        listPatient.clear();
                    }
                    for(int x=0;x<arrayPatient.length();x++){
                        listPatient.add(new Patient(
                                objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(x).getString(getResources().getString(R.string.param_id)),
                                objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(x).getString(getResources().getString(R.string.param_name)),
                                objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(x).getString(getResources().getString(R.string.param_age)),
                                objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(x).getString(getResources().getString(R.string.param_gender)),
                                objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(x).getString(getResources().getString(R.string.param_address)),
                                objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(x).getString(getResources().getString(R.string.param_status)),
                                objContent.getJSONArray(getResources().getString(R.string.param_patient)).getJSONObject(x).getString(getResources().getString(R.string.param_datetime))
                        ));
                    }
                    rvPatient.setAdapter(null);
                    rvPatient.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    rvPatient.setLayoutManager(mLayoutManager);
                    patientAdapter = new PatientAdapter(listPatient,getActivity());
                    patientAdapter.notifyDataSetChanged();
                    rvPatient.setAdapter(patientAdapter);
                }else{
                    llNoData.setVisibility(View.VISIBLE);
                    rvPatient.setVisibility(View.GONE);
                }
            }else
            if(!response.getString(getResources().getString(R.string.param_severity)).equals(getResources().getString(R.string.severity_success))){
                Snackbar snackbar = Snackbar.make(llCetakData, response.getString(getResources().getString(R.string.param_message)), Snackbar.LENGTH_LONG);
                snackbar.show();
                llNoData.setVisibility(View.VISIBLE);
                rvPatient.setVisibility(View.GONE);
            }
        }catch (JSONException e){
            Snackbar snackbar = Snackbar.make(llCetakData, getResources().getString(R.string.error_parse_json), Snackbar.LENGTH_LONG);
            snackbar.show();
            llNoData.setVisibility(View.VISIBLE);
            rvPatient.setVisibility(View.GONE);
        }
    }

    void downloadFile(String idPatient){
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.dialog_loading));
        pDialog.show();
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String url =  EKGApps.endpoint
                .concat(getResources().getString(R.string.endpoint_download))
                .concat(String.valueOf(idPatient))
                .concat(getResources().getString(R.string.endpoint_slash))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.endpoint_slash));
        try{
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    getResources().getString(R.string.ekg_report) + idPatient + getResources().getString(R.string.ext_pdf));
            DownloadManager dm = (DownloadManager) getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Snackbar snackbar = Snackbar
                    .make(llCetakData, getResources().getString(R.string.notif_download_success) , Snackbar.LENGTH_LONG);
            snackbar.show();
            pDialog.dismiss();
        }catch (Exception e){
            pDialog.dismiss();
            Snackbar snackbar = Snackbar
                    .make(llCetakData, getResources().getString(R.string.notif_download_failed) , Snackbar.LENGTH_LONG);
            snackbar.show();

        }

    }
}
