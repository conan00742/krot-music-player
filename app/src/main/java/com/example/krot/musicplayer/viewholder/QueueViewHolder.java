package com.example.krot.musicplayer.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.event_bus.EventPlaySelectedQueueSong;
import com.example.krot.musicplayer.event_bus.RxBus;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.repository.SongItemRepository;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Krot on 3/1/18.
 */

public class QueueViewHolder extends ItemBaseViewHolder<SongItem> {

    @BindView(R.id.queue_song_container)
    ConstraintLayout songContainer;

    @BindView(R.id.tv_queue_song_title)
    TextView mSongTitle;

    @BindView(R.id.tv_queue_duration)
    TextView mDuration;

    @BindView(R.id.tv_queue_artist)
    TextView mArtistName;

    @BindView(R.id.ic_queue_option)
    ImageView iconOption;

    @BindView(R.id.ic_queue_playing)
    public ImageView icPlaying;


    public QueueViewHolder(ViewGroup parent, int resourceId) {
        super(parent, resourceId);
        ButterKnife.bind(this, itemView);
    }


    @Override
    public void bindData(@Nullable SongItem item) {
        super.bindData(item);
        //song title
        mSongTitle.setText(item.getSong().getSongTitle());

        //duration
        mDuration.setText(SongItemRepository.convertDuration(item.getSong().getDuration()));

        //artist name
        mArtistName.setText(item.getSong().getArtistName());
    }

    @OnClick(R.id.queue_song_container)
    public void playSelectedQueueSong() {
        RxBus.getInstance().send(new EventPlaySelectedQueueSong(getAdapterPosition(), QueueViewHolder.this));
    }


}
