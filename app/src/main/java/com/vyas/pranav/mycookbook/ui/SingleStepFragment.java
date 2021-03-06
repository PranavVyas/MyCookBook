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
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.model.Progress;
import com.blankj.ALog;
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
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vyas.pranav.mycookbook.BuildConfig;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.extrautils.NetworkUtils;
import com.vyas.pranav.mycookbook.modelsutils.MainStepsModel;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vyas.pranav.mycookbook.ui.RecepieDescriptionActivity.KEY_BOOLEAN_TWO_PANE;
import static com.vyas.pranav.mycookbook.recyclerutils.RecepieDescAdapter.KEY_STEP_SINGLE;

public class SingleStepFragment extends Fragment{

    private static final String TAG = "SingleStepFragment";
    private static final String KEY_LAST_PLAY_WHEN_READY = "LastPlayWhenReady";
    private static final String KEY_LAST_POSITION = "LastPosition";
    private static final String KEY_CURRENT_WINDWOW_POSITION = "CurrentWindowIndex";
    private static final String KEY_LAST_STEP = "LastStep";
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
    private BroadcastReceiver myReceiver;
    private fromSingleStepFragement mCallback;
    private static boolean mPlayWhenReady;
    private static long mCurrentPosition;
    private static int mResumeWindow;
    private static int mLastStep;
    private MediaSource mVideoResource;
    @BindView(R.id.progress_buffering_single_step_frag) ProgressBar progressBuffer;

    public SingleStepFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (fromSingleStepFragement) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_step, container, false);
        initALog();
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getLong(KEY_LAST_POSITION);
            mPlayWhenReady = savedInstanceState.getBoolean(KEY_LAST_PLAY_WHEN_READY);
            mResumeWindow = savedInstanceState.getInt(KEY_CURRENT_WINDWOW_POSITION);
            mLastStep = savedInstanceState.getInt(KEY_LAST_STEP);
            ALog.d( "Getting values and saving them as ","mPlayWhenReady : " +mPlayWhenReady, "mCurrentPosition : " + mCurrentPosition);
        }
        ButterKnife.bind(this, view); //Binding Views
        //Getting data From activity
        String stepJson = Objects.requireNonNull(getArguments()).getString(KEY_STEP_SINGLE);
        twoPane = getArguments().getBoolean(KEY_BOOLEAN_TWO_PANE);
        Gson gson = new Gson();
        currStep = gson.fromJson(stepJson, MainStepsModel.class);
        //Checking Screen Orientation
        isLandScape = Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        //Checking for video Availability
        isVideoAvailable = !(currStep.getVideoURL().length() == 0);
        createAndStartMediaSession();
        checkConnectivity();
        populateUI();
        return view;
    }

    public void checkConnectivity() {
        if (mPlayer != null && isVideoAvailable) {
            if (!NetworkUtils.hasInternetConnection(Objects.requireNonNull(getContext()))) {
                Toast.makeText(getContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
                mPlayerView.setVisibility(GONE);
                Picasso.get().load(R.drawable.no_internet).into(imageError);
                imageError.setVisibility(VISIBLE);
            } else {
                //Toast.makeText(getContext(), "Connection Sucessfull", Toast.LENGTH_SHORT).show();
                mPlayerView.setVisibility(VISIBLE);
                Picasso.get().load(R.drawable.ic_no_video_laptop).into(imageError);
                imageError.setVisibility(GONE);
            }
        }
    }

    public void createAndStartMediaSession() {
        if (mMediaSession == null) {
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
        }
        mMediaSession.setActive(true);
    }

    void releasePlayer() {
        if (mPlayer != null) {
            stopBuffering();
            ALog.d("Released Player and stoped Buffering");
            mPlayer.release();
            mPlayer = null;
        }
        if (mMediaSession != null) {
            mMediaSession.setActive(false);
        }
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
            //Network is Changed
            checkConnectivity();
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        mPlayerView.setUseController(!isInPictureInPictureMode);
    }

    public interface fromSingleStepFragement {
        void stepReceived(boolean VideoAvailability, boolean isLandscapeOrientation, boolean twoPaneAvailability);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mPlayer != null) {
            //ALog.d( "onSaveInstanceState: Saved Value in SavedInstanceSate Bundle : mPlaywhen R");
            outState.putLong(KEY_LAST_POSITION, mCurrentPosition);
            outState.putBoolean(KEY_LAST_PLAY_WHEN_READY, mPlayWhenReady);
            outState.putInt(KEY_CURRENT_WINDWOW_POSITION, mResumeWindow);
            outState.putInt(KEY_LAST_STEP,mLastStep);
            ALog.d( "Putting values to onSavedInstanceState as ","mPlayWhenReady : " +mPlayWhenReady, "mCurrentPosition : " + mCurrentPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getContext()).unregisterReceiver(myReceiver);//Unregister Listener for networkChange
        if(mPlayer != null) {
            mCurrentPosition = mPlayer.getCurrentPosition();
            mPlayWhenReady = mPlayer.getPlayWhenReady();
            mResumeWindow = mPlayer.getCurrentWindowIndex();
            mLastStep = currStep.getId();
            ALog.d( "Saving Data in variables as ","mPlayWhenReady : " +mPlayWhenReady, "mCurrentPosition : " + mCurrentPosition);
        }
        if (Util.SDK_INT <= 23) {
            // releasing player
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        super.onStop();
        if (Util.SDK_INT > 23) {
            // releasing player
            releasePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setBroadcastReceiver();
        if (mPlayer == null && isVideoAvailable) {
            String userAgent = Util.getUserAgent(getContext(), getContext().getApplicationContext().getApplicationInfo().packageName);
            DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getContext(), null, httpDataSourceFactory);
            Uri videoUri = Uri.parse(currStep.getVideoURL());
            ALog.d( "Getting Video Url as "+videoUri.toString());
            mVideoResource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);
            initExoPlayer();
            if (!isVideoAvailable){
                stopBuffering();
            }
        }
    }

    private void getPlayerInstance(){
        if(mPlayer == null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            // Create the player
            mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);
            // Bind the player to the view.
            mPlayerView.setPlayer(mPlayer);
            ALog.d("Created mPlayer in mPlayerView successfully");
        }
    }

    private void initExoPlayer() {
        getPlayerInstance();
        mPlayer.prepare(mVideoResource);
        if (mCurrentPosition == 0 || mLastStep != currStep.getId()) {
            mPlayer.setPlayWhenReady(true);
            startBuffering();
            //ALog.d( "onCreateView: Creating new instance as ","mPlayWhenReady : " +mPlayWhenReady, "mCurrentPosition : " + mCurrentPosition);
            mPlayerView.getPlayer().seekTo(0);
        } else{
            startBuffering();
            mPlayer.setPlayWhenReady(mPlayWhenReady);
            ALog.d( "Getting values and Giving them to Start Activity as ","mPlayWhenReady : " +mPlayWhenReady, "mCurrentPosition : " + mCurrentPosition);
            mPlayerView.getPlayer().seekTo(mResumeWindow,mCurrentPosition);
        }
        mPlayer.addListener(new Player.EventListener() {
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
                    stopBuffering();
                    //ALog.d( "onPlayerStateChanged: Playing Video Now");
                } else if ((playbackState == Player.STATE_READY)) {
                    mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                            mPlayer.getCurrentPosition(), 1f);
                    //ALog.d( "onPlayerStateChanged: Video Paused just now");
                } else if (playbackState == Player.STATE_BUFFERING) {
                    startBuffering();
                }
                if (mMediaSession != null) {
                    mMediaSession.setPlaybackState(mStateBuilder.build());
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
        });
    }

    public void setBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkConnectivity();
                //Toast.makeText(context, "Network Changed", Toast.LENGTH_SHORT).show();
            }
        };
        Objects.requireNonNull(getContext()).registerReceiver(myReceiver, filter);
    }

    private void initViewConfig(){
        if (!isVideoAvailable) {
            mPlayerView.setVisibility(GONE);
            imageError.setVisibility(VISIBLE);
            scrollView.setVisibility(VISIBLE);
            //stopBuffering();
        } else {
            imageError.setVisibility(GONE);
            mPlayerView.setVisibility(VISIBLE);
            if(isLandScape) scrollView.setVisibility(GONE);
            else scrollView.setVisibility(VISIBLE);
            //loadMediaToPlayer(currStep.getVideoURL());
        }
    }

    private void populateUI(){
        title.setText("Step " + (currStep.getId() + 1) + " : \n" + currStep.getShortDescription());
        description.setText(currStep.getDescription());
        mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.appwidget_preview));
        initViewConfig();
    }

    private void startBuffering(){
        if(isVideoAvailable && progressBuffer.getVisibility() == GONE){
            progressBuffer.setVisibility(VISIBLE);
            ALog.d("Starting Buffering Now");
            Toast.makeText(getActivity(), "PLEASE WAIT Video is buffering Now...", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopBuffering(){
        //Toast.makeText(getActivity(), "Buffering Now...", Toast.LENGTH_SHORT).show();
        progressBuffer.setVisibility(GONE);
        ALog.d("Buffering Finished ...Ready Now");
    }

    public void initALog() {
        ALog.Config config = ALog.init(getContext())
                .setLogSwitch(BuildConfig.DEBUG)
                .setConsoleSwitch(BuildConfig.DEBUG)
                .setGlobalTag(null)
                .setLogHeadSwitch(true)
                .setLog2FileSwitch(false)
                .setDir("")
                .setFilePrefix("")
                .setBorderSwitch(true)
                .setSingleTagSwitch(true)
                .setConsoleFilter(ALog.V)
                .setFileFilter(ALog.V)
                .setStackDeep(1)
                .setStackOffset(0)
                .setSaveDays(3)
                .addFormatter(new ALog.IFormatter<ArrayList>() {
                    @Override
                    public String format(ArrayList list) {
                        return "ALog Formatter ArrayList { " + list.toString() + " }";
                    }
                });
    }
}