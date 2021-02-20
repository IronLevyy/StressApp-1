package com.zemnuhov.stressapp.ScanResurce;

public class Device {
    String MAC;
    String Name;
    Device(String MAC,String Name){
        this.MAC=MAC;
        this.Name=Name;
    }

    public String getMAC() {
        return MAC;
    }

    public String getName() {
        return Name;
    }
}
