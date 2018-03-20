package com.example.krot.musicplayer.viewholder;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.event_bus.EventShuffleAllSongs;
import com.example.krot.musicplayer.event_bus.RxBus;
import com.example.krot.musicplayer.model.ShuffleAllSongsItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Krot on 2/26/18.
 */

public class ShuffleAllSongsViewHolder extends ItemBaseViewHolder<ShuffleAllSongsItem> {

    @BindView(R.id.tv_shuffle_all_title)
    TextView tvShuffleAllTitle;
    @BindView(R.id.shuffle_all_container)
    LinearLayout shuffleAllSongsContainer;

    @NonNull
    private Context context;


    public ShuffleAllSongsViewHolder(ViewGroup parent, int resourceId, @NonNull Context context) {
        super(parent, resourceId);
        this.context = context;
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(@Nullable ShuffleAllSongsItem item) {
        super.bindData(item);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "Chantelli_Antiqua.ttf");

        //song title
        tvShuffleAllTitle.setTypeface(typeface, Typeface.BOLD);
    }

    @OnClick(R.id.shuffle_all_container)
    public void doShuffleAllSongs() {
        //bus
        RxBus.getInstance().send(new EventShuffleAllSongs());
    }
}
