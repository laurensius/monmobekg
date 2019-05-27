package com.laurensius_dede_suhardiman.ekgapps.model;

public class BPM {
    String id;
    String idPatient;
    String BPM;
    String datetime;

    public BPM(String id,
            String idPatient,
            String BPM,
            String datetime){
        this.id = id;
        this.idPatient = idPatient;
        this.BPM = BPM;
        this.datetime = datetime;
    }

    public String getId() {
        return id;
    }

    public String getIdPatient() {
        return idPatient;
    }

    public String getBPM() {
        return BPM;
    }

    public String getDatetime() {
        return datetime;
    }
}
