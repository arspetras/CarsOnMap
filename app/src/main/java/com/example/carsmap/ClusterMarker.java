package com.example.carsmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private int iconPictureURL;

    public ClusterMarker(LatLng position, String title, String snippet, int iconPictureURL) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPictureURL = iconPictureURL;
    }

    public ClusterMarker() {

    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public void setIconPictureURL(int iconPictureURL) {
        this.iconPictureURL = iconPictureURL;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public int getIconPictureURL() {
        return iconPictureURL;
    }
}
