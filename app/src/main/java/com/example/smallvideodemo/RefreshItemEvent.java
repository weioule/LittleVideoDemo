package com.example.smallvideodemo;

/**
 * @author weioule
 * @date 2019/8/4.
 */
public class RefreshItemEvent {
    private int position, type;

    public RefreshItemEvent(int position, int type) {
        this.position = position;
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public int getType() {
        return type;
    }
}
