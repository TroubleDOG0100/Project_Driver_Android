package com.bingostudio.project_driver;

public class Vector2D {

    public float x, y;

    public Vector2D(float x, float y){
        this.x = x;
        this.y = y;
    }
    public Vector2D(){this(0.f, 0.f);}

    @Override
    public String toString(){
        return "x: " + x + " y:" + y;
    }

    public void setTo(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
