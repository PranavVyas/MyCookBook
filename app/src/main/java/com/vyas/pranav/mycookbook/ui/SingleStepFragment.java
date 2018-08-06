package com.vyas.pranav.mycookbook.ui;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
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
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.modelsutils.MainStepsModel;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.vyas.pranav.mycookbook.recyclerutils.RecepieDescAdapter.KEY_STEP_SINGLE;

public class SingleStepFragment extends Fragment implements ExoPlayer.EventListener{

    private static final String TAG = "SingleStepFragment";
    @BindView(R.id.player_single_step) PlayerView mPlayerView;
    @BindView(R.id.text_step_no_single_step_frag) TextView title;
    @BindView(R.id.text_desc_single_step_frag) TextView description;
    @BindView(R.id.image_error_single_step) ImageView imageError;
    SimpleExoPlayer mPlayer;
    MainStepsModel currStep;
    MediaSessionCompat mMediaSession;
    PlaybackStateCompat.Builder mStateBuilder;
    @BindView(R.id.scroll_single_step) ScrollView scrollView;
    boolean isVideoAvailable = false;
    boolean isLandScape;

    public SingleStepFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_step,container,false);
        ButterKnife.bind(this,view);
        isLandScape = getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        String stepJson = getArguments().getString(KEY_STEP_SINGLE);
        Gson gson = new Gson();
        currStep = gson.fromJson(stepJson, MainStepsModel.class);
        isVideoAvailable = !(currStep.getVideoURL().length() == 0);
        title.setText("Step "+currStep.getId()+" : \n"+currStep.getShortDescription());
        description.setText(currStep.getDescription());
        //checking for video
        if (!isVideoAvailable){
            mPlayerView.setVisibility(View.GONE);
            imageError.setVisibility(View.VISIBLE);
            //Toast.makeText(getActivity(), "No Video Here", Toast.LENGTH_SHORT).show();
        }else{
            imageError.setVisibility(View.GONE);
            readyPlayer();
            mMediaSession = createandReturnMediaSession();
            mMediaSession.setActive(true);
        }
        //checking for orientation
        if(isLandScape){
            //checking for video
            if(isVideoAvailable) {
                scrollView.setVisibility(View.INVISIBLE);
            }
        }else{
            imageError.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public void readyPlayer(){
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create the player
        mPlayer =
                ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

        // Bind the player to the view.
        mPlayerView.setPlayer(mPlayer);
        String videoUrl = currStep.getVideoURL();
        Uri videoUri = Uri.parse(videoUrl);
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(),"MyCookBook"),
                defaultBandwidthMeter);
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(true);
    }

    public MediaSessionCompat createandReturnMediaSession(){
        if(mMediaSession == null){
            mMediaSession = new MediaSessionCompat(getActivity(),TAG);
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
        return mMediaSession;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        releasePlayer();
    }

    void releasePlayer(){
        if(isVideoAvailable) {
            mPlayer.release();
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
        if (isLoading){
            //Toast.makeText(getActivity(), "Loading", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == Player.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == Player.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mPlayer.getCurrentPosition(), 1f);
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
}
