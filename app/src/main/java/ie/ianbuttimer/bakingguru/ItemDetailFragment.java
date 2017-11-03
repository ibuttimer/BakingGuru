/*
 * Copyright (c) 2017 Ian Buttimer.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ie.ianbuttimer.bakingguru;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ie.ianbuttimer.bakingguru.bake.IngredientsStep;
import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.bake.Step;
import ie.ianbuttimer.bakingguru.data.adapter.IngredientAdapter;
import ie.ianbuttimer.bakingguru.network.NetworkStatusReceiver;
import ie.ianbuttimer.bakingguru.network.NetworkUtils;
import ie.ianbuttimer.bakingguru.utils.Dialog;
import ie.ianbuttimer.bakingguru.utils.ScreenMode;
import ie.ianbuttimer.bakingguru.utils.UriUtils;
import timber.log.Timber;

import static android.support.v4.media.session.PlaybackStateCompat.STATE_NONE;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static ie.ianbuttimer.bakingguru.ItemListActivity.ARG_ITEM;
import static ie.ianbuttimer.bakingguru.bake.IngredientsStep.INGREDIENTS_STEP_ID;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment implements Player.EventListener {

    /** The argument representing the current step. */
    public static final String ARG_STEP = "step";
    /** The argument representing the current step. */
    public static final String ARG_POSITION = "position";
    /** Setup network status listener argument for instance state bundle */
    private static final String NETWORK_STATUS_ARG = "network_status";

    private static final float NORMAL_SPEED = 1f;

    /**
     * The content this fragment is presenting.
     */
    private Recipe mRecipe;
    private Step mStep;
    private int mStepIndex;
    private long mPosition;
    private Uri mMediaUri;

    private SimpleExoPlayer mExoPlayer;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    @BindView(R.id.item_detail) ConstraintLayout mItemDetail;
    // normal step views
    @Nullable @BindView(R.id.tv_step_title_stepA) TextView mTitle;
    @Nullable @BindView(R.id.pv_player_stepA) SimpleExoPlayerView mPlayerView;
    @Nullable @BindView(R.id.tv_description_stepA) TextView mDescriptionTextView;
    @Nullable @BindView(R.id.b_previous_stepA) Button mPrevButton;
    @Nullable @BindView(R.id.b_next_stepA) Button mNextButton;
    @Nullable @BindView(R.id.cl_description_item_detail) ConstraintLayout mDescriptionLayout;   // description layout in normal landscape mode
    @Nullable @BindView(R.id.cl_step_details_stepA) ConstraintLayout mStepDetailsLayout;        // step details layout in 2 panel mode
    @Nullable @BindView(R.id.ll_navigation_item_details) LinearLayout mNavigationLayout;        // navigation layout in normal mode
    // ingredients step views
    @Nullable @BindView(R.id.rv_ingredient_list_item_detail) RecyclerView mRecyclerView;        // ingredients layout in 2 panel landscape mode

    private Unbinder mUnbinder;

    private IItemDetail mHost;

    @Nullable private NetworkStatusReceiver.NetworkStatusListener mNetworkStatus;

    private ScreenMode mScreenMode;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mHost = (IItemDetail) context;
        } catch (ClassCastException e) {
            Timber.e("Host activity must implement IItemDetail", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getArguments();
        }

        mRecipe = Parcels.unwrap(bundle.getParcelable(ARG_ITEM));
        mStepIndex = bundle.getInt(ARG_STEP);
        mPosition = bundle.getLong(ARG_POSITION);

        mStep = mRecipe.getStep(mStepIndex);

        boolean setupListener = false;
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(NETWORK_STATUS_ARG)) {
                setupListener = savedInstanceState.getBoolean(NETWORK_STATUS_ARG);
            }
        }
        if (setupListener) {
            registerNetworkStatusListener();
        }

        // Initialize the Media Session.
        initializeMediaSession();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

        mScreenMode = ScreenMode.getScreenMode(
                (mDescriptionLayout != null),   // description layout only available in normal landscape
                (mNavigationLayout == null));   // navigation layout not in 2 panel mode

        if (mPrevButton != null) {
            mPrevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeStep(-1);
                }
            });
        }
        if (mNextButton != null) {
            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeStep(1);
                }
            });
        }

        // Initialize the player.
        initializePlayer();

        getActivity().setTitle(mRecipe.getName());

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set the data in the host activity (needed for orientation change)
        if (mHost != null) {
            mHost.setData(mRecipe, mStepIndex);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mPosition = 0;
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            if (mExoPlayer.isCurrentWindowSeekable()) {
                mPosition = mExoPlayer.getCurrentPosition();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
        mUnbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(ARG_ITEM, Parcels.wrap(mRecipe));
        outState.putInt(ARG_STEP, mStepIndex);
        outState.putLong(ARG_POSITION, mPosition);
        outState.putBoolean(NETWORK_STATUS_ARG, (mNetworkStatus != null));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterNetworkStatusListener();
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), ItemDetailFragment.class.getSimpleName());

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // StepSessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new ItemDetailFragment.StepSessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    /**
     * Initialize ExoPlayer.
     */
    private void initializePlayer() {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            // renderers factory with no drm or extensions
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(
                    getContext(), null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

            mPlayerView.setPlayer(mExoPlayer);

            mPlayerView.setDefaultArtwork(
                    BitmapFactory.decodeResource(getResources(), R.drawable.film));

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            setStep(mRecipe, mStepIndex);
        }
    }

    /**
     * Set the displayed step
     * @param recipe    Recipe to use
     * @param stepIdx   Index of step to display
     */
    private void setStep(Recipe recipe, int stepIdx) {

        mStep = recipe.getStep(stepIdx);
        if (mStep != null) {
            mStepIndex = stepIdx;

            stop(); // stop any currently playing

            mTitle.setText(mStep.getShortDescription());

            mMediaUri = null;
            String url = mStep.getVideoURL();
            if (!TextUtils.isEmpty(url)) {
                mMediaUri = UriUtils.urlToUri(url);
            }

            int visibility;
            if (isIngredientStep(mStep)) {
                // process special case ingredients step
                IngredientsStep ingredientsStep = (IngredientsStep) mStep;
                setIngredients(ingredientsStep);
                visibility = View.VISIBLE;

                setPlayerVisibility(View.GONE);
            } else {
                // process normal step
                play();
                setDescription(mStep, (mMediaUri != null));
                visibility = View.GONE;
            }
            if (mRecyclerView != null) {
                mRecyclerView.setVisibility(visibility);
            }

            if (mHost != null) {
                mHost.onStepChange(mStepIndex);
            }
        }
    }

    /**
     * Check if step is an ingredients step
     * @param step  Step to check
     * @return  <code>true</code> if ingredients steo, <code>false</code> otherwise
     */
    private boolean isIngredientStep(Step step) {
        return (step.getId() == INGREDIENTS_STEP_ID);
    }

    /**
     * Play media
     */
    private void play() {
        int playerVisibility;

        if (mMediaUri == null) {
            // nothing to play
            playerVisibility = View.GONE;
        } else {
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "recipeStep");
            MediaSource mediaSource = new ExtractorMediaSource(mMediaUri,
                    new DefaultDataSourceFactory(getContext(), userAgent),
                    new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);

            playerVisibility = View.VISIBLE;
        }
        setPlayerVisibility(playerVisibility);

        mExoPlayer.seekTo(mPosition);
    }

    /**
     * Set the player visibility
     * @param visibility    visibility to set
     */
    private void setPlayerVisibility(int visibility) {
        boolean playWhenReady;
        if (visibility == View.VISIBLE) {
            mPlayerView.showController();
            playWhenReady = true;
        } else {
            mPlayerView.hideController();
            playWhenReady = false;
        }
        mPlayerView.setVisibility(visibility);
        mExoPlayer.setPlayWhenReady(playWhenReady);
    }

    /**
     * Stop any currently playing media
     */
    private void stop() {
        mExoPlayer.setPlayWhenReady(false);
        mExoPlayer.stop();
    }

    /**
     * Set the displayed description
     * @param step      Step info to display
     * @param haveMedia Have media to play
     */
    private void setDescription(Step step, boolean haveMedia) {

        mDescriptionTextView.setText(step.getDescription());

        int count = mRecipe.getStepCount();
        ctrlNavButton(mPrevButton, ((mStepIndex > 0) && (mStepIndex < count)));
        ctrlNavButton(mNextButton, ((mStepIndex >= 0) && (mStepIndex < (count - 1))));

        ConstraintLayout itemDetailLayout = null;  // layout containing step details
        @IdRes int topViewId = 0;               // id of view to constrain description to

        if (mScreenMode.isNormalMode()) {
            if (mScreenMode.isLandscapeMode()) {
                if (mDescriptionLayout != null) {
                    // in normal landscape mode the video takes up the whole screen and the description is hidden
                    int visibility;
                    if (haveMedia) {
                        // have video so hide description in landscape
                        visibility = View.INVISIBLE;
                    } else {
                        // no video so show description in landscape
                        visibility = View.VISIBLE;
                    }
                    mDescriptionLayout.setVisibility(visibility);
                }
            } else {    // mScreenMode == ScreenMode.NORMAL_PORTRAIT
                if (haveMedia) {
                    // media player in top half of screen
                    topViewId = R.id.gl_horizontalHalf_stepA;    // guideline in portrait
                } else {
                    // no media so player is hidden
                    topViewId = R.id.cl_step_details_stepA;    // parent view in portrait
                }
                itemDetailLayout = mStepDetailsLayout;
            }
        } else {
            // two panel mode
            if (haveMedia) {
                // media player in top half of screen
                topViewId = R.id.gl_horizontalHalf_stepA;    // guideline in 2 panel
            } else {
                // no media so player is hidden
                topViewId = R.id.cl_step_details_stepA;     // parent view in 2 panel
            }

            itemDetailLayout = mStepDetailsLayout;
        }
        // adjust constraints to position description
        if (itemDetailLayout != null) {
            ConstraintSet constraint = new ConstraintSet();
            constraint.clone(itemDetailLayout);
            constraint.connect(R.id.tv_description_stepA, ConstraintSet.TOP, topViewId, ConstraintSet.TOP);
            constraint.applyTo(itemDetailLayout);
        }
    }

    /**
     * Set the displayed ingredients
     * @param ingredientsStep   ingredient step
     */
    private void setIngredients(IngredientsStep ingredientsStep) {
        if (mRecyclerView != null) {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());

            mRecyclerView.setLayoutManager(mLayoutManager);

            IngredientAdapter adapter = new IngredientAdapter(ingredientsStep.getIngredients(), null);

            /* Setting the adapter attaches it to the RecyclerView in our layout. */
            mRecyclerView.setAdapter(adapter);
        }
    }

    /**
     * Increase/decrease the current step
     * @param change    Step increase/decrease
     */
    private void changeStep(int change) {
        int newIdx = mStepIndex + change;
        int count = mRecipe.getStepCount();
        if ((newIdx >= 0) && (newIdx < count)) {
            mPosition = 0;
            setStep(mRecipe, newIdx);
        }
    }

    /**
     * Set button status
     * @param button    Button
     * @param enabled   Enabled state
     */
    private void ctrlNavButton(@Nullable Button button, boolean enabled) {
        if (button != null){
            button.setEnabled(enabled);
            button.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }


    /////// Player.EventListener implementation ///////


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        //no op
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        //no op
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        //no op
    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync.
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        int state = STATE_NONE;
        if((playbackState == Player.STATE_READY) && playWhenReady){
            state = STATE_PLAYING;
        } else if((playbackState == Player.STATE_READY)){
            state = STATE_PAUSED;
        }
        if (state != STATE_NONE) {
            mStateBuilder.setState(state, mExoPlayer.getCurrentPosition(), NORMAL_SPEED);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        //no op
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        mPosition = mExoPlayer.getCurrentPosition();

        if (!NetworkUtils.isInternetAvailable(getActivity())) {
            // internet not currently available, so register to resume playing
            registerNetworkStatusListener();

            Dialog.showNoNetworkDialog(getContext());
        }

        Timber.e("Player error", error);
    }

    @Override
    public void onPositionDiscontinuity() {
        //no op
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        //no op
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class StepSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    /**
     * Interface for communication between this fragment and its host activity
     */
    public interface IItemDetail {

        /**
         * Called when the current step changes
         * @param step  Current step
         */
        void onStepChange(int step);

        /**
         * Set the data in the parent activity<br>
         * (Required for orientation changes where the activity is recreated and the fragment is automatically added)
         * @param recipe        Current recipe
         * @param step          Current step
         */
        void setData(Recipe recipe, int step);
    }

    /**
     * Class to request recipes on network connection event
     */
    private class ReconnectedNetworkStatusListener implements NetworkStatusReceiver.NetworkStatusListener {
        @Override
        public void onNetworkStatusChanged(boolean isConnected) {
            if (isConnected) {
                play();
            }
        }
    }

    /**
     * Register a request recipes on network connection event listener
     */
    private void registerNetworkStatusListener() {
        if (mNetworkStatus == null) {
            mNetworkStatus = new ReconnectedNetworkStatusListener();
            boolean success = NetworkStatusReceiver.registerListener(mNetworkStatus);
            Timber.d("Listener " + (success ? "" : "not ") + "registered");
        }
    }

    /**
     * Unregister a request recipes on network connection event listener
     */
    private void unregisterNetworkStatusListener() {
        if (mNetworkStatus != null) {
            boolean success = NetworkStatusReceiver.unregisterListener(mNetworkStatus);
            Timber.d("Listener " + (success ? "" : "not ") + "unregistered");
        }
    }


}
