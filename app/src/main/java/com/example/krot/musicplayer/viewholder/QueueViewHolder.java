package com.example.krot.musicplayer.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.event_bus.EventPlaySelectedQueueSong;
import com.example.krot.musicplayer.event_bus.RxBus;
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

    @NonNull
    private Context mContext;


    public QueueViewHolder(ViewGroup parent, int resourceId, Context context) {
        super(parent, resourceId);
        mContext = context;
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


        if (item.isSelected()) {
            mSongTitle.setTextColor(mContext.getResources().getColor(R.color.orange));
            mDuration.setTextColor(mContext.getResources().getColor(R.color.orange));
            mArtistName.setTextColor(mContext.getResources().getColor(R.color.orange));
            icPlaying.setVisibility(View.VISIBLE);
//            if (SongPlaybackManager.getSongPlaybackManagerInstance().isPlaying()) {
//                icPlaying.setImageDrawable(ContextCompat.getDrawable(icPlaying.getContext(), R.drawable.ic_mini_queue_playlist_pause));
//            } else {
//                icPlaying.setImageDrawable(ContextCompat.getDrawable(icPlaying.getContext(), R.drawable.ic_mini_queue_playlist_play));
//            }
        } else {
            mSongTitle.setTextColor(mContext.getResources().getColor(R.color.default_song_text_color));
            mDuration.setTextColor(mContext.getResources().getColor(R.color.default_song_text_color));
            mArtistName.setTextColor(mContext.getResources().getColor(R.color.grey));
            icPlaying.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.queue_song_container)
    public void playSelectedQueueSong() {
        RxBus.getInstance().send(new EventPlaySelectedQueueSong(getAdapterPosition()));
    }



}
