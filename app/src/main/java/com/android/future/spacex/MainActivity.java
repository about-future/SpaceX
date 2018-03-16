package com.android.future.spacex;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.future.spacex.data.Star;

public class MainActivity extends AppCompatActivity {

    private static final String SONG_POSITION = "seek_position";
    private static final String SONG_STATE = "playing";
    //Handles playback of all the sound files
    private MediaPlayer mMediaPlayer;
    private int mSongPosition;
    //Handles audio focus when playing a sound file
    private AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View starFieldView = findViewById(R.id.starfield);
        starFieldView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ImageView logoSpaceX = findViewById(R.id.logoSpaceX);

        // Create and setup the Audio Manager to request audio focus.
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // Create and setup the MediaPlayer
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.flight_proven);
        mMediaPlayer.setScreenOnWhilePlaying(true);

        // Request audio focus for playback and store the request response
        int requestResult = mAudioManager.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // If we have an instance saved and contains our song position,
            // we use it continue playing from that position.
            if (savedInstanceState != null && savedInstanceState.containsKey(SONG_POSITION)) {
                mMediaPlayer.seekTo(savedInstanceState.getInt(SONG_POSITION));
                mMediaPlayer.start();
//                if (savedInstanceState.containsKey(SONG_STATE) && savedInstanceState.getBoolean(SONG_STATE)) {}
            } else {
                // Otherwise, we start playing our song from the beginning
                mMediaPlayer.start();
                // Setup a listener on the MediaPlayer, so that we can stop and relese the MediaPlayer once the sound has finished playing.
                mMediaPlayer.setOnCompletionListener(mCompletionListener);
            }
        }

        starFieldView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        pauseMediaPlayer();
                    } else {
                        startMediaPlayer();
                    }
                }
            }
        });

        starFieldView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                releaseMediaPlayer();
                startActivity(new Intent(MainActivity.this, SpaceXActivity.class));
                Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    /**
     * This listener gets triggered whenever the audio focus changes
     * (i.e., we gain or lose audio focus because of another app or device).
     */
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
                mMediaPlayer.pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mMediaPlayer.start();
                mMediaPlayer.setOnCompletionListener(mCompletionListener);
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                releaseMediaPlayer();
            }
        }
    };

    // This listener gets triggered when the MediaPlayer has completed playing the song
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Now that the song has finished playing, release the media player resources.
            releaseMediaPlayer();
        }
    };

    // Clean up the media player by releasing its resources
    private void releaseMediaPlayer() {
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

            Log.v("Release MP:", "MP released now!");
        }
    }

    // Pause the media player
    private void pauseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
        }
    }

    // Start media player or continue playing the song if media player was paused
    private void startMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
        }
    }

//    // When the activity is stopped, pause the media player, because we won't be playing the song
//    @Override
//    public void onStop() {
//        super.onStop();
//        //pauseMediaPlayer();
//    }
//
//    // When the activity is resumed, start the media player, because we want to continue playing the song
//    @Override
//    protected void onResume() {
//        super.onResume();
//        //startMediaPlayer();
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMediaPlayer != null) {
            outState.putInt(SONG_POSITION, mMediaPlayer.getCurrentPosition());
            //outState.putBoolean(SONG_STATE, mMediaPlayer.isPlaying());
        }
        super.onSaveInstanceState(outState);
    }
}
