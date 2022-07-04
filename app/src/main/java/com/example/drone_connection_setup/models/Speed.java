package com.example.drone_connection_setup.models;

public class Speed {

    private float hspeed;
    private float vspeed;

    public Speed(float hspeed, float vspeed) {
        this.hspeed = hspeed;
        this.vspeed = vspeed;
    }

    public float getHspeed() {
        return hspeed;
    }

    public float getVspeed() {
        return vspeed;
    }
}
