package com.example.krot.musicplayer.model;

/**
 * Created by Krot on 2/26/18.
 */

public class ShuffleAllSongsItem implements Item {

    public ShuffleAllSongsItem() {

    }

    @Override
    public boolean equals(Object obj) {
        return true;
    }

    @Override
    public boolean sameContent(Item currentItem) {
        return true;
    }
}
