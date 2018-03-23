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
    public TextView mSongTitle;

    @BindView(R.id.tv_duration)
    public TextView mDuration;

    @BindView(R.id.tv_artist)
    public TextView mArtistName;

    @BindView(R.id.ic_option)
    ImageView iconOption;

    @BindView(R.id.ic_playing)
    public ImageView icPlaying;

    @NonNull
    private Context mContext;


    public SongItemViewHolder(ViewGroup parent, int resourceId, @NonNull Context context) {
        super(parent, resourceId);
        mContext = context;
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(@Nullable SongItem item) {
        super.bindData(item);

        if (item.getSong() != null) {

            //song title selected
            mSongTitle.setText(item.getSong().getSongTitle());

            //duration selected
            mDuration.setText(SongItemRepository.convertDuration(item.getSong().getDuration()));

            //artist name selected
            mArtistName.setText(item.getSong().getArtistName());

            if (item.isSelected()) {
                mSongTitle.setTextColor(mContext.getResources().getColor(R.color.orange));
                mDuration.setTextColor(mContext.getResources().getColor(R.color.orange));
                mArtistName.setTextColor(mContext.getResources().getColor(R.color.orange));
            } else {
                mSongTitle.setTextColor(mContext.getResources().getColor(R.color.default_song_text_color));
                mDuration.setTextColor(mContext.getResources().getColor(R.color.default_song_text_color));
                mArtistName.setTextColor(mContext.getResources().getColor(R.color.grey));
            }


        }

    }

    @OnClick(R.id.song_container)
    public void doPlaySong() {
        Log.i("WTF", "select song: item = " + item);
        if (item != null) {
            item.setSelected(true);
        }

        RxBus.getInstance().send(new EventPlaySong(getAdapterPosition(), SongItemViewHolder.this));

    }


}
