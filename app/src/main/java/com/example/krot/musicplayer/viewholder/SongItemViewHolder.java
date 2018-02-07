package com.example.krot.musicplayer.viewholder;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.model.SongItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Krot on 2/6/18.
 */

public class SongItemViewHolder extends ItemBaseViewHolder<SongItem> {

    @BindView(R.id.tv_song_title)
    TextView mSongTitle;

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
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "Chantelli_Antiqua.ttf");
        mSongTitle.setTypeface(typeface);
        Log.i("WTF", "name = " + item.getSong().getSongTitle());
        mSongTitle.setText(item.getSong().getSongTitle());
    }
}
