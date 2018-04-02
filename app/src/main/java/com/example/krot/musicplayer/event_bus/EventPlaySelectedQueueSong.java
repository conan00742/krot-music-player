package com.example.krot.musicplayer.event_bus;


/**
 * Created by Krot on 3/1/18.
 */

public class EventPlaySelectedQueueSong {
    private int position;


    public EventPlaySelectedQueueSong(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

}
