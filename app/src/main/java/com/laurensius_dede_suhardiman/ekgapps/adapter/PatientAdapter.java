package com.laurensius_dede_suhardiman.ekgapps.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.laurensius_dede_suhardiman.ekgapps.R;
import com.laurensius_dede_suhardiman.ekgapps.model.Patient;

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.HolderPatient> {

    List<Patient> patient;
    Context ctx;

    public PatientAdapter(List<Patient>patient, Context ctx){
        this.patient =patient;
        this.ctx = ctx;
    }

    @Override
    public HolderPatient onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_patient,viewGroup,false);
        HolderPatient holderPatient = new HolderPatient(v);
        return holderPatient;
    }

    @Override
    public void onBindViewHolder(HolderPatient holderPatient,int i){
        holderPatient.tvNamaPasien.setText(patient.get(i).getName());
        holderPatient.tvUmur.setText(patient.get(i).getAge());
        holderPatient.tvJenisKelamain.setText(patient.get(i).getGender());
        holderPatient.tvAlamat.setText(patient.get(i).getAddress());
    }


    @Override
    public int getItemCount(){
        return patient.size();
    }

    public Patient getItem(int position){
        return patient.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderPatient extends  RecyclerView.ViewHolder{
        CardView cvPatient;
        TextView tvNamaPasien, tvUmur, tvJenisKelamain, tvAlamat;

        HolderPatient(View itemView){
            super(itemView);
            cvPatient = (CardView) itemView.findViewById(R.id.cv_patient);
            tvNamaPasien = (TextView)itemView.findViewById(R.id.tv_nama_pasien);
            tvUmur = (TextView)itemView.findViewById(R.id.tv_umur);
            tvJenisKelamain = (TextView)itemView.findViewById(R.id.tv_jenis_kelamin);
            tvAlamat = (TextView)itemView.findViewById(R.id.tv_alamat);
        }

    }

}