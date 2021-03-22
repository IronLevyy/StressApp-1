package com.zemnuhov.stressapp.DataBase;

import java.util.Calendar;
import java.util.Date;

public class TenMinuteObjectDB {
    Long time;
    Integer peaks;
    Double tonic;
    TenMinuteObjectDB(Long time, Integer peaks ,Double tonic){
        this.time=time;
        this.peaks=peaks;
        this.tonic=tonic;
    }

    public Date getTime() {
        Calendar date=Calendar.getInstance();
        date.setTimeInMillis(time);

        return date.getTime();
    }

    public Integer getPeaks() {
        return peaks;
    }

    public Double getTonic() {
        return tonic;
    }
}
