package com.example.krot.musicplayer.viewholder;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.event_bus.EventPlaySong;
import com.example.krot.musicplayer.event_bus.RxBus;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.repository.SongItemRepository;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Krot on 2/6/18.
 */

public class SongItemViewHolder extends ItemBaseViewHolder<SongItem> {

    @BindView(R.id.song_container)
    ConstraintLayout songContainer;

    @BindView(R.id.tv_song_title)
    TextView mSongTitle;

    @BindView(R.id.tv_duration)
    TextView mDuration;

    @BindView(R.id.tv_artist)
    TextView mArtistName;

    @BindView(R.id.ic_option)
    ImageView iconOption;

    @BindView(R.id.ic_playing)
    public ImageView icPlaying;

    @NonNull
    private Context mContext;

    @NonNull
    private RxBus bus;

    public SongItemViewHolder(ViewGroup parent, int resourceId, @NonNull Context context, RxBus bus) {
        super(parent, resourceId);
        mContext = context;
        this.bus = bus;
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(@Nullable SongItem item) {
        super.bindData(item);
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "Chantelli_Antiqua.ttf");

        //song title
        mSongTitle.setTypeface(typeface, Typeface.BOLD);
        mSongTitle.setText(item.getSong().getSongTitle());

        //duration
        mDuration.setTypeface(typeface, Typeface.BOLD);
        mDuration.setText(SongItemRepository.convertDuration(item.getSong().getDuration()));

        //artist name
        mArtistName.setTypeface(typeface);
        mArtistName.setText(item.getSong().getArtistName());


    }

    @OnClick(R.id.song_container)
    public void doPlaySong() {
        bus.send(new EventPlaySong(getAdapterPosition(), SongItemViewHolder.this));
    }

}
