package com.android.future.spacex;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.future.spacex.utils.DetectSwipeGestureListener;
import com.android.future.spacex.utils.ScreenUtils;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StarfieldActivity extends AppCompatActivity implements Player.EventListener {

    private static final String SONG_POSITION = "seek_position";
    private static final String SOUND_ON = "sound_on";

    @BindView(R.id.playerView)
    PlayerView mPlayerView;

    private ImageView volumeOn;
    private ImageView volumeOff;
    private TextView mArtist;

    private SimpleExoPlayer mExoPlayer;
    private long mAudioPosition;

    // Store the current Toast
    private Toast mToast;

    // This is the gesture detector compat instance.
    private GestureDetectorCompat gestureDetectorCompat = null;

    // TODO: SpaceX live feed on 2 Feb 2018 https://www.youtube.com/watch?v=BPQHG-LevZM

//    private static final int CREDIT_ARTIST_INSERTION = 139800;
//    private static final int CREDIT_SONG_INSERTION = 142200;
//    private static final int CREDITS_EXTRACTION = 180000;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starfield);

        // Bind the views
        ButterKnife.bind(this);

        // Set the layer type of starfield, so that the shadows of the stars are visible
        final View starFieldView = findViewById(R.id.starfield);
        starFieldView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // Prepare params for SpaceX logo
        ImageView logoSpaceX = findViewById(R.id.logoSpaceX);
        // Logo width is 74% of screen width and logo height is 12.5% of width logo.
        int width = (int) (ScreenUtils.getScreenWidhtInPixels(this) * 0.74);
        int height = (int) (width * 0.125);

        // Prepare the margins of logo
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        // Left margin is 18.75% of screen width, the rest are 0
        layoutParams.setMargins((int) (ScreenUtils.getScreenWidhtInPixels(this) * 0.1875), 0, 0, 0);
        // Set params for SpaceX logo
        logoSpaceX.setLayoutParams(layoutParams);

        initializePlayer(this);

        // Volume on/off "buttons"
        volumeOn = findViewById(R.id.volumeIcon);
        volumeOff = findViewById(R.id.muteIcon);

        // Volume on listener
        volumeOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mExoPlayer != null) {
                    mExoPlayer.setVolume(0);
                    volumeOn.setVisibility(View.GONE);
                    volumeOff.setVisibility(View.VISIBLE);
                    showToast(getString(R.string.sound_off));
                }
            }
        });

        // Volume off listener
        volumeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mExoPlayer != null) {
                    mExoPlayer.setVolume(1);
                    volumeOn.setVisibility(View.VISIBLE);
                    volumeOff.setVisibility(View.GONE);
                    showToast(getString(R.string.sound_on));
                }
            }
        });

        // Set music credits
        mArtist = findViewById(R.id.artist);

        // Create a common gesture listener object
        DetectSwipeGestureListener gestureListener = new DetectSwipeGestureListener();
        // Set activity in the listener
        gestureListener.setActivity(this);
        // Create the gesture detector with the gesture listener
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Pass activity on touch event to the gesture detector
        gestureDetectorCompat.onTouchEvent(event);
        // Return true to tell android OS that event has been consumed, do not pass it to other event listeners
        return true;
    }

    private void initializePlayer(Context context) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);
        }

        Uri mediaUri = RawResourceDataSource.buildRawResourceUri(R.raw.flight_proven);

        // Prepare the MediaSource.
        String userAgent = Util.getUserAgent(context, "SpaceX");
        DataSource.Factory factory = new DefaultDataSourceFactory(context, userAgent);
        MediaSource mediaSource = new ExtractorMediaSource.Factory(factory).createMediaSource(mediaUri);
        mExoPlayer.prepare(mediaSource);

        // Resume playing position
        if (mAudioPosition != 0) {
            mExoPlayer.seekTo(mAudioPosition);
        }

        // Start playing
        mExoPlayer.setPlayWhenReady(true);
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // When the activity is resumed, initialize ExoPlayer, because we want to continue playing the song
    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer(this);
    }

    // When the activity is paused, release ExoPlayer too, so the song will not be playing
    @Override
    public void onPause() {
        super.onPause();

        if (mExoPlayer != null) {
            mAudioPosition = mExoPlayer.getCurrentPosition();
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(SONG_POSITION, mAudioPosition);
        outState.putInt(SOUND_ON, volumeOn.getVisibility());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAudioPosition = savedInstanceState.getLong(SONG_POSITION, 0);

        // Reset the sound on or sound off status
        if (savedInstanceState.containsKey(SOUND_ON) && savedInstanceState.getInt(SOUND_ON) == View.GONE) {
            mExoPlayer.setVolume(0);
            volumeOn.setVisibility(View.GONE);
            volumeOff.setVisibility(View.VISIBLE);
        } else {
            mExoPlayer.setVolume(1);
            volumeOn.setVisibility(View.VISIBLE);
            volumeOff.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        // If the background song has ended, start SpaceX activity
        if (playbackState == Player.STATE_ENDED) {
            startActivity(new Intent(StarfieldActivity.this, SpaceXActivity.class));
            mExoPlayer.seekTo(0);
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
}
