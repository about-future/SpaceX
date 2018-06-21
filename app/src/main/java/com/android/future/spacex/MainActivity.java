package com.android.future.spacex;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.future.spacex.utils.ScreenUtils;

public class MainActivity extends AppCompatActivity {

    private static final String SONG_POSITION = "seek_position";
    private static final String SOUND_ON = "sound_on";

    // Handles playback of all the sound files
    private MediaPlayer mMediaPlayer;
    // Handles audio focus when playing a sound file
    private AudioManager mAudioManager;
    // Store the current Toast
    private Toast mToast;

    private ImageView volumeOn;
    private ImageView volumeOff;

    private TextView mArtist;
    private TextView mSong;

    // TODO: SpaceX live feed on 2 Feb 2018 https://www.youtube.com/watch?v=BPQHG-LevZM

//    private static final int CREDIT_ARTIST_INSERTION = 139800;
//    private static final int CREDIT_SONG_INSERTION = 142200;
//    private static final int CREDITS_EXTRACTION = 180000;

    // This listener gets triggered when the MediaPlayer has completed playing the song
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Now that the song has finished playing, release the media player resources and start the SpaceX activity
            startSpaceXActivity();
        }
    };

    // This listener gets triggered whenever the audio focus changes (i.e., we gain or lose audio
    // focus because of another app or device).
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing an important song.

                // Pause playback
                pauseMediaPlayer();
                Log.v("OnAudioFocusChangeListe", "PAUSED");
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                startMediaPlayer();
                Log.v("OnAudioFocusChangeListe", "START");
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                releaseMediaPlayer();
                Log.v("OnAudioFocusChangeListe", "RELEASE");
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the layer type of starfield, so that the shadows of the stars are visible
        View starFieldView = findViewById(R.id.starfield);
        starFieldView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // Prepare params for SpaceX logo
        ImageView logoSpaceX = findViewById(R.id.logoSpaceX);
        // Logo width is 74% of screen width and logo height is 12.5% of width logo.
        int width = (int) (ScreenUtils.getScreenWidhtInPixels(this) * 0.74);
        int height = (int) (width * 0.125);

        // Prepare the margins of logo
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        // Left margin is 18.75% of screen width, the rest are 0
        layoutParams.setMargins((int) (ScreenUtils.getScreenWidhtInPixels(this) * 0.1875),0,0,0);
        // Set params for SpaceX logo
        logoSpaceX.setLayoutParams(layoutParams);

        // Volume on/off "buttons"
        volumeOn = findViewById(R.id.volumeIcon);
        volumeOff = findViewById(R.id.muteIcon);

        // Volume on listener
        volumeOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.setVolume(0, 0);
                        volumeOn.setVisibility(View.GONE);
                        volumeOff.setVisibility(View.VISIBLE);
                        showToast(getString(R.string.sound_off));
                    }
                }
            }
        });

        // Volume off listener
        volumeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.setVolume(1, 1);
                        volumeOn.setVisibility(View.VISIBLE);
                        volumeOff.setVisibility(View.GONE);
                        showToast(getString(R.string.sound_on));
                    }
                }
            }
        });

        // Set music credits
        mArtist = findViewById(R.id.artist);
        mSong = findViewById(R.id.song);

        // Create and setup the Audio Manager to request audio focus.
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Create and setup the MediaPlayer
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.flight_proven);
        mMediaPlayer.setOnCompletionListener(mCompletionListener);

        // Request audio focus for playback and store the request response
        int requestResult = mAudioManager.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // If we have an instance saved and contains our song position, we use it to continue
            // playing from that position.
            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey(SONG_POSITION)) {
                    mMediaPlayer.seekTo(savedInstanceState.getInt(SONG_POSITION));
                }
                // Start or continue playing
                startMediaPlayer();

                // Reset the sound on or sound off status
                if (savedInstanceState.containsKey(SOUND_ON) && savedInstanceState.getInt(SOUND_ON) == View.GONE) {
                    mMediaPlayer.setVolume(0, 0);
                    volumeOn.setVisibility(View.GONE);
                    volumeOff.setVisibility(View.VISIBLE);
                } else {
                    mMediaPlayer.setVolume(1, 1);
                    volumeOn.setVisibility(View.VISIBLE);
                    volumeOff.setVisibility(View.GONE);
                }
            }
        }

        starFieldView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startSpaceXActivity();
                return true;
            }
        });
    }

    // Clean up the media player by releasing its resources
    public void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            // Regardless of whether or not we were granted audio focus, abandon it. This also
            // unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    // Pause the media player
    public void pauseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    // Start media player or continue playing the song if media player was paused
    public void startMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    // When the activity is resumed, start the media player, because we want to continue playing the song
    @Override
    protected void onResume() {
        super.onResume();
        startMediaPlayer();
    }

    // When the activity is paused, pause the media player too, so the song will not be playing
    @Override
    protected void onPause() {
        super.onPause();
        pauseMediaPlayer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMediaPlayer != null) {
            outState.putInt(SONG_POSITION, mMediaPlayer.getCurrentPosition());
            outState.putInt(SOUND_ON, volumeOn.getVisibility());
        }
        super.onSaveInstanceState(outState);
    }

    private void startSpaceXActivity() {
        releaseMediaPlayer();
        startActivity(new Intent(MainActivity.this, SpaceXActivity.class));
        showToast(getString(R.string.welcome));
        finish();
    }

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
