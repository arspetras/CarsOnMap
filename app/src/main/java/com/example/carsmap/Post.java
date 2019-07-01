package com.example.carsmap;

import java.util.HashMap;
import java.util.Map;

public class Post {


    private String id;

    private String plateNumber;

    private Map<String,String>location = new HashMap<>();

    private Map<String,String>model = new HashMap<>();

    private String batteryPercentage;

    private String batteryEstimatedDistance;

    private String isCharging;



    public String getId() {
        return id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public Map<String, String> getLocation() {
        return location;
    }

    public Map<String, String> getModel() {
        return model;
    }

    public String getBatteryPercentage() {
        return batteryPercentage;
    }

    public String getBatteryEstimatedDistance() {
        return batteryEstimatedDistance;
    }

    public String isCharging() {
        return isCharging;
    }
}
