package com.zemnuhov.stressapp.ServerAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatisticRequest {
    @SerializedName("dataTime")
    @Expose
    String dataTime;

    @SerializedName("tonicAvg")
    @Expose
    String tonicAvg;

    @SerializedName("peaksCount")
    @Expose
    String peaksCount;

    @SerializedName("user_id")
    @Expose
    String user_id;

}
