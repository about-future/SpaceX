package com.about.future.spacex.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.about.future.spacex.R;
import com.about.future.spacex.databinding.ActivityStarfieldBinding;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.utils.DetectSwipeGestureListener;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

public class StarfieldActivity extends AppCompatActivity {
    private static final String SONG_POSITION = "seek_position";
    private static final String SOUND_ON = "sound_on";

    private ExoPlayer mExoPlayer;
    private long mAudioPosition;
    private int mVolumeState;

    private Handler handler;
    private Runnable runnable;

    // Store the current Toast
    private Toast mToast;

    // This is the gesture detector compat instance
    private GestureDetectorCompat gestureDetectorCompat = null;

    private ActivityStarfieldBinding binding;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding = ActivityStarfieldBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the layer type of starfield, so that the shadows of the stars are visible
        binding.starfield.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // Prepare params for SpaceX logo
        // Logo width is 74% of screen width and logo height is 12.5% of width logo
        int width = (int) (ScreenUtils.getScreenWidhtInPixels(this) * 0.74);
        int height = (int) (width * 0.125);

        // Prepare the margins of logo
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        // Left margin is 18.75% of screen width, the rest are 0
        layoutParams.setMargins((int) (ScreenUtils.getScreenWidhtInPixels(this) * 0.1875), 0, 0, 0);
        // Set params for SpaceX logo
        binding.logoSpaceX.setLayoutParams(layoutParams);

        //handler = new Handler();
        initializePlayer(this);

        // Volume on listener
        binding.volumeOn.setOnClickListener(view -> {
            if (mExoPlayer != null) {
                mExoPlayer.setVolume(0);
                binding.volumeOn.setVisibility(View.GONE);
                binding.volumeOff.setVisibility(View.VISIBLE);
                showToast(getString(R.string.sound_off));
            }
        });

        // Volume off listener
        binding.volumeOff.setOnClickListener(view -> {
            if (mExoPlayer != null) {
                mExoPlayer.setVolume(1);
                binding.volumeOn.setVisibility(View.VISIBLE);
                binding.volumeOff.setVisibility(View.GONE);
                showToast(getString(R.string.sound_on));
            }
        });

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
            //TrackSelector trackSelector = new DefaultTrackSelector();
            //mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            mExoPlayer = new ExoPlayer.Builder(context).build();
            binding.playerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            //mExoPlayer.addListener(this);
        }

        //Uri mediaUri = RawResourceDataSource.buildRawResourceUri(R.raw.flight_proven);
        Uri mediaUri = Uri.parse("asset:///flight_proven_z_master.mp3");

        // Prepare the MediaSource.
        //String userAgent = Util.getUserAgent(context, "SpaceX");
        //DataSource.Factory factory = new DefaultDataSourceFactory(context, userAgent);
        //MediaSource mediaSource = new ExtractorMediaSource.Factory(factory).createMediaSource(mediaUri);

        MediaItem mediaItem = MediaItem.fromUri(mediaUri);
        mExoPlayer.setMediaItem(mediaItem);
        mExoPlayer.prepare();


        // Resume playing position
        if (mAudioPosition != 0) {
            mExoPlayer.seekTo(mAudioPosition);
        }

        // Start playing
        mExoPlayer.setPlayWhenReady(true);

        handler = new Handler();
        credits();
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    // When the activity is resumed, initialize ExoPlayer, because we want to continue playing the song
    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer(this);
        resetVolume();
    }

    // When the activity is paused, release ExoPlayer too, so the song will not be playing
    @Override
    public void onPause() {
        super.onPause();

        if (mExoPlayer != null) {
            mAudioPosition = mExoPlayer.getCurrentPosition();
            mVolumeState = binding.volumeOn.getVisibility();
            releasePlayer();
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mExoPlayer != null) {
            releasePlayer();
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(SONG_POSITION, mAudioPosition);
        outState.putInt(SOUND_ON, binding.volumeOn.getVisibility());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAudioPosition = savedInstanceState.getLong(SONG_POSITION, 0);
        mVolumeState = savedInstanceState.getInt(SOUND_ON);
        resetVolume();
    }

    private void resetVolume() {
        // Restore volume status (on or off)
        if (mVolumeState == View.GONE) {
            mExoPlayer.setVolume(0);
            binding.volumeOn.setVisibility(View.GONE);
            binding.volumeOff.setVisibility(View.VISIBLE);
        } else {
            mExoPlayer.setVolume(1);
            binding.volumeOn.setVisibility(View.VISIBLE);
            binding.volumeOff.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void credits() {
        if (mExoPlayer != null) {
            long position = mExoPlayer.getCurrentPosition();

            if ((position > 35000 && position <= 45000)
                    || (position > 115000 && position <= 145000)
                    || (position > 205000 && position <= 235000)) {
                binding.songCredits.setVisibility(View.VISIBLE);
                binding.songCredits.setImageResource(R.drawable.credits_swipe);
                binding.songCredits.setContentDescription(getString(R.string.skip));
            } else if ((position > 0 && position <= 5000)
                    || (position > 25000 && position <= 35000)
                    || (position > 45000 && position <= 65000)
                    || (position > 95000 && position <= 115000)
                    || (position > 145000 && position <= 165000)
                    || (position > 195000 && position <= 205000)
                    || (position > 235000 && position <= 255000)
                    || (position > 285000)) {
                binding.songCredits.setVisibility(View.INVISIBLE);
            } else if ((position > 5000 && position <= 25000)
                    || (position > 65000 && position <= 95000)
                    || (position > 165000 && position < 195000)
                    || (position > 255000 && position < 285000)) {
                binding.songCredits.setVisibility(View.VISIBLE);
                binding.songCredits.setImageResource(R.drawable.credits_artist);
                binding.songCredits.setContentDescription(getString(R.string.song_credits));
            }

            runnable = () -> {
                try {
                    credits();
                } catch (NullPointerException e) {
                    Log.v("Credits Exception", "" + e);
                }
            };
            handler.postDelayed(runnable, 5000);
        }
    }



    /*@Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        // If the background song has ended, start SpaceX activity
        if (playbackState == Player.STATE_ENDED) {
            mExoPlayer.seekTo(0);
            startActivity(new Intent(StarfieldActivity.this, SpaceXActivity.class));
        }
    }*/

    /*@Override
    public void onSeekProcessed() {
        Log.v("POSITION:", "" + mExoPlayer.getCurrentPosition());
    }*/
}
