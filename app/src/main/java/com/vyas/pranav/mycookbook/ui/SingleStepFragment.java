package com.vyas.pranav.mycookbook.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.extrautils.NetworkUtils;
import com.vyas.pranav.mycookbook.modelsutils.MainStepsModel;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.vyas.pranav.mycookbook.R.drawable.no_internet;
import static com.vyas.pranav.mycookbook.RecepieDescriptionActivity.KEY_BOOLEAN_TWO_PANE;
import static com.vyas.pranav.mycookbook.recyclerutils.RecepieDescAdapter.KEY_STEP_SINGLE;

public class SingleStepFragment extends Fragment implements Player.EventListener {

    private static final String TAG = "SingleStepFragment";
    @BindView(R.id.player_single_step)
    PlayerView mPlayerView;
    @BindView(R.id.text_step_no_single_step_frag)
    TextView title;
    @BindView(R.id.text_desc_single_step_frag)
    TextView description;
    @BindView(R.id.image_error_single_step)
    ImageView imageError;
    @BindView(R.id.frame_player_single_step_frag)
    FrameLayout frame;
    private SimpleExoPlayer mPlayer;
    private MainStepsModel currStep;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    @BindView(R.id.scroll_single_step)
    ScrollView scrollView;
    private boolean isVideoAvailable = false;
    private boolean isLandScape;
    private boolean twoPane;
    private TextView tvDefault;
    private BroadcastReceiver myReceiver;

    public SingleStepFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_step, container, false);
        //Getting data from Activity
        ButterKnife.bind(this, view);
        String stepJson = Objects.requireNonNull(getArguments()).getString(KEY_STEP_SINGLE);
        twoPane = getArguments().getBoolean(KEY_BOOLEAN_TWO_PANE);
        Log.d(TAG, "onCreateView: stepJson : " + stepJson);
        Gson gson = new Gson();
        currStep = gson.fromJson(stepJson, MainStepsModel.class);
        //Checking Screen Orientation
        isLandScape = Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        //Checking for video Availability
        isVideoAvailable = !(currStep.getVideoURL().length() == 0);
        title.setText("Step " + (currStep.getId() + 1) + " : \n" + currStep.getShortDescription());
        description.setText(currStep.getDescription());
        mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.appwidget_preview));
        //checking for video
        if (!isVideoAvailable) {
            mPlayerView.setVisibility(View.GONE);
            imageError.setVisibility(View.VISIBLE);
        } else {
            imageError.setVisibility(View.GONE);
            mPlayerView.setVisibility(View.VISIBLE);
            mMediaSession = createandReturnMediaSession();
            readyPlayer();
            mMediaSession.setActive(true);
        }
        if (twoPane) {
            tvDefault = view.findViewById(R.id.text_default_frame_tab);
        } else {
            //checking for orientation
            if (isLandScape) {
                //checking for video
                if (isVideoAvailable) {
                    mPlayerView.setVisibility(View.VISIBLE);
                    imageError.setVisibility(View.GONE);
                    scrollView.setVisibility(View.INVISIBLE);
                } else {
                    mPlayerView.setVisibility(View.GONE);
                    imageError.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                }
            } else {
                //imageError.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
            }
        }
        checkConnectivity();
        return view;
    }

    public void checkConnectivity() {
        if (mPlayer != null) {
            if (!NetworkUtils.hasInternetConnection(getContext())) {
                Toast.makeText(getContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
                mPlayerView.setVisibility(View.GONE);
                Picasso.get().load(R.drawable.no_internet).into(imageError);
                imageError.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(), "Connection Sucessfull", Toast.LENGTH_SHORT).show();
                mPlayerView.setVisibility(View.VISIBLE);
                Picasso.get().load(R.drawable.ic_no_video_laptop).into(imageError);
                imageError.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkConnectivity();
                //Toast.makeText(context, "Network Changed", Toast.LENGTH_SHORT).show();
            }
        };
        getContext().registerReceiver(myReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(myReceiver);
    }

    public void readyPlayer() {
        if (mPlayer == null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            // Create the player
            mPlayer =
                    ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

            // Bind the player to the view.
            mPlayerView.setPlayer(mPlayer);
            mPlayer.addListener(this);
            String videoUrl = currStep.getVideoURL();
            Log.d(TAG, "readyPlayer: Getting Video Url Ready as : " + videoUrl);
            Uri videoUri = Uri.parse(videoUrl);
            DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(Objects.requireNonNull(getActivity()),
                    Util.getUserAgent(getActivity(), "MyCookBook"),
                    defaultBandwidthMeter);
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(videoUri);
            mPlayer.prepare(mediaSource);
            mPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        releasePlayer();
    }

    public MediaSessionCompat createandReturnMediaSession() {
        mMediaSession = new MediaSessionCompat(Objects.requireNonNull(getActivity()), TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new mMediaSessionCallbacks());
        return mMediaSession;
    }

    void releasePlayer() {
        if (mPlayer != null) {
            //playbackPosition = mPlayer.getCurrentPosition();
            //currentWindow = mPlayer.getCurrentWindowIndex();
            //playWhenReady = mPlayer.getPlayWhenReady();
            mPlayer.release();
            mPlayer = null;
            mMediaSession.setActive(false);
            Log.d(TAG, "releasePlayer: Player Released Sucesfully");
        }
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
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mPlayer.getCurrentPosition(), 1f);
            Log.d(TAG, "onPlayerStateChanged: Playing Video Now");
        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mPlayer.getCurrentPosition(), 1f);
            Log.d(TAG, "onPlayerStateChanged: Video Paused just now");
        } else if (playbackState == PlaybackStateCompat.STATE_CONNECTING) {
            Toast.makeText(getActivity(), "Connecting Please Wait...", Toast.LENGTH_SHORT).show();
        } else if (playbackState == PlaybackStateCompat.STATE_BUFFERING) {
            Toast.makeText(getActivity(), "Buffering Now...", Toast.LENGTH_SHORT).show();
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Toast.makeText(getActivity(), "Error Occured :" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private class mMediaSessionCallbacks extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mPlayer.seekTo(0);
        }

    }

    public class networkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkConnectivity();
            //Toast.makeText(context, "Network Changed", Toast.LENGTH_SHORT).show();
        }
    }
}