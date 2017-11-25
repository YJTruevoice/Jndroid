package com.yakir.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yakir.constant.SDKConstant;
import com.yakir.core.AdParameters;
import com.yakir.util.Utils;
import com.yalir.R;
import com.yakir.util.LogUtils;

/**
 * @author yakir
 * @function 视频播放、暂停等事件的触发
 */

public class CustomVideoView extends RelativeLayout implements View.OnClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, TextureView.SurfaceTextureListener {

    private static final String TAG = "MediaVideoView";
    /**
     * 模拟播放器生命周期状态
     */
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INVAL = 1000;
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSING = 2;
    private static final int LOAD_TOTAL_COUNT = 3;

    /**
     * UI
     */
    private ViewGroup mParentContainer;
    private RelativeLayout mPlayerView;
    private TextureView mVideoView;
    private Button mMiniPlayBtn;
    private ImageView mFullBtn;
    private ImageView mLoadingBar;
    private ImageView mFrameView;
    private AudioManager audioManager;// 音量控制器
    private Surface videoSurface; // 显示帧数据

    /**
     * data
     */
    private String mUrl;// 要加载的视频地址
    private boolean isMute;// 是否静音
    private String mFrameURI;
    private int mScreenWidth, mDestationHeight;// 屏幕宽度、默认视频高度

    /**
     * state状态保存
     */
    private boolean canPlay = true;
    private boolean mIsRealPause;
    private boolean mIsComplete;
    private int mCurrentCount;
    private int playerState = STATE_IDLE;

    private MediaPlayer mediaPlayer;
    private ADVideoPlayerListener listener;// 事件监听回调
    private ScreenEventReceiver mScreenReceiver; // 监听屏幕是否锁屏
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_MSG:
                    if (isPlaying()) {
                        listener.onBufferUpdate(getCurrentPosition());
                        sendEmptyMessageDelayed(TIME_MSG, TIME_INVAL);
                    }
                    break;
            }
        }
    };

    private ADFrameImageLoadListener mFrameLoadListener;

    public CustomVideoView(Context context, ViewGroup parentContainer) {
        super(context);
        mParentContainer = parentContainer;
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        initData();
        initView();
        registerBroadcastReceiver();
    }

    private void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mDestationHeight = (int) (mScreenWidth * SDKConstant.VIDEO_HEIGHT_PERCENT);
    }

    private void initView() {
        mPlayerView = (RelativeLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.xadsdk_video_player, this);
        mVideoView = (TextureView) mPlayerView.findViewById(R.id.xadsdk_player_video_textureView);
        mVideoView.setOnClickListener(this);
        mVideoView.setKeepScreenOn(true);
        mVideoView.setSurfaceTextureListener(this);
        // 小模式状态
        initSmallLayoutMode();
    }

    /**
     * 小模式状态
     */
    private void initSmallLayoutMode() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mScreenWidth, mDestationHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayerView.setLayoutParams(params);

        mMiniPlayBtn = (Button) mPlayerView.findViewById(R.id.xadsdk_small_play_btn);
        mFullBtn = (ImageView) mPlayerView.findViewById(R.id.xadsdk_to_full_view);
        mLoadingBar = (ImageView) mPlayerView.findViewById(R.id.loading_bar);
        mFrameView = (ImageView) mPlayerView.findViewById(R.id.framing_view);
        mMiniPlayBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);
    }

    /**
     * 在view显示发生改变时调用
     *
     * @param changedView
     * @param visibility
     */
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        LogUtils.d(TAG, "onVisibilityChanged" + visibility);
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && playerState == STATE_PAUSING) {
            if (isRealPause() || isComplete()) {
                pause();
            } else {
                decideCanPlay();
            }
        } else {
            pause();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        LogUtils.i(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == this.mMiniPlayBtn) {
            if (this.playerState == STATE_PAUSING) {
                if (Utils.getVisiblePercent(mParentContainer)
                        > SDKConstant.VIDEO_SCREEN_PERCENT) {
                    resume();
                    this.listener.onClickPlay();
                }
            } else {
                load();
            }
        } else if (v == this.mFullBtn) {
            this.listener.onClickFullScreenBtn();
        } else if (v == mVideoView) {
            this.listener.onClickVideo();
        }
    }

    /**
     * 缓冲更新
     *
     * @param mp
     * @param percent
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    /**
     * 播放完成
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (listener != null) {
            listener.onADVideoPlayComplete();
        }
        setIsComplete(true);
        setIsPauseClicked(true);
        playBack();//回到播放初始状态
    }

    /**
     * 播放产生异常
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        this.playerState = STATE_ERROR;
        mediaPlayer = mp;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
        if (mCurrentCount >= LOAD_TOTAL_COUNT) {
            if (listener != null) {
                listener.onADVideoLoadFailed();
            }
            showPauseOrPlayView(false);
        }
        stop();
        return true;
    }

    /**
     * 播放器处于就绪状态
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        LogUtils.d(TAG, "onPrepared");
        showPlayView();
        mediaPlayer = mp;
        if (mediaPlayer != null) {
            mediaPlayer.setOnBufferingUpdateListener(this);
            mCurrentCount = 0;
            if (listener != null) {
                listener.onADVideoLoadSuccess();
            }
            //满足自动播放条件，则直接播放
            if (Utils.canAutoPlay(getContext(),
                    AdParameters.getCurrentSetting()) &&
                    Utils.getVisiblePercent(mParentContainer) > SDKConstant.VIDEO_SCREEN_PERCENT) {
                setCurrentPlayState(STATE_PAUSING);
                resume();
            } else {
                setCurrentPlayState(STATE_PLAYING);
                pause();
            }
//            decideCanPlay();
        }
    }

    /**
     * 播放器已处于就绪状态
     *
     * @param surface
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        LogUtils.d(TAG, "onSurfaceTextureAvailable");
        videoSurface = new Surface(surface);
        checkMediaPlayer();
        mediaPlayer.setSurface(videoSurface);
        load();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        LogUtils.d(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    /**
     * 加载视频Url
     */
    public void load() {
        LogUtils.d(TAG, "do load");
        if (this.playerState != STATE_IDLE) {
            return;
        }
        LogUtils.d(TAG, "do play url = " + this.mUrl);
        try {
            showLoadingView();
            setCurrentPlayState(STATE_IDLE);
            checkMediaPlayer();// 检查并创建MediaPlayer
            mute(true);
            mediaPlayer.setDataSource(mUrl);
            mediaPlayer.prepareAsync(); // 异步加载视频
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage());
            stop();
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (this.playerState != STATE_PLAYING) {
            return;
        }
        LogUtils.d(TAG, "do pause");
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
            if (!canPlay) {
                this.mediaPlayer.seekTo(0);
            }
        }
        showPauseOrPlayView(false);
        mHandler.removeCallbacksAndMessages(null);
    }

    //全屏不显示暂停状态,后续可以整合，不必单独出一个方法
    public void pauseForFullScreen() {
        if (playerState != STATE_PLAYING) {
            return;
        }
        LogUtils.d(TAG, "do full pause");
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
            if (!this.canPlay) {
                mediaPlayer.seekTo(0);
            }
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 回复视频播放
     */
    public void resume() {
        if (playerState != STATE_PAUSING) {
            return;
        }
        LogUtils.d(TAG, "do resume");
        if (!isPlaying()) {
            entryResumeState();// 置为播放中的状态值
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.start();
            mHandler.sendEmptyMessage(TIME_MSG);
            showPauseOrPlayView(true);
        } else {
            showPauseOrPlayView(false);
        }
    }

    /**
     * 播放完成回到初始状态
     */
    public void playBack() {
        LogUtils.d(TAG, " do playBack");
        setCurrentPlayState(STATE_PAUSING);
        mHandler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
        }
        showPauseOrPlayView(false);
    }

    /**
     * 停止
     * 清空MediaPlayer并重新load
     */
    public void stop() {
        LogUtils.d(TAG, "do stop");
        if (this.mediaPlayer != null) {
            this.mediaPlayer.reset();
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        setCurrentPlayState(STATE_IDLE);
        if (mCurrentCount < LOAD_TOTAL_COUNT) {
            mCurrentCount += 1;
            load();
        } else {
            showPauseOrPlayView(false);
        }
    }

    private void showPauseOrPlayView(boolean b) {
        showPauseView(b);
        showPlayView();
    }

    /**
     * 销毁播放器view
     */
    public void destroy() {
        LogUtils.d(TAG, " do destroy");
        if (this.mediaPlayer != null) {
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        setCurrentPlayState(STATE_IDLE);
        mCurrentCount = 0;
        setIsComplete(false);
        setIsPauseClicked(false);
        unRegisterBroadcastReceiver();
        mHandler.removeCallbacksAndMessages(null); //release all message and runnable
        showPauseView(false); //除了播放和loading外其余任何状态都显示pause
    }

    /**
     * 跳转到指定点继续播放视频
     *
     * @param position
     */
    public void seekAndResume(int position) {
        if (mediaPlayer != null) {
            showPauseView(true);
            entryResumeState();
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    LogUtils.d(TAG, "do seek and resume");
                    mediaPlayer.start();
                    mHandler.sendEmptyMessage(TIME_MSG);
                }
            });
        }
    }

    /**
     * 跳转到指定点暂停视频
     *
     * @param position
     */
    public void seekAndPause(int position) {
        if (this.playerState != STATE_PLAYING) {
            return;
        }
        showPauseView(false);
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    LogUtils.d(TAG, "do seek and pause");
                    mediaPlayer.pause();
                    mHandler.removeCallbacksAndMessages(null);
                }
            });
        }
    }

    public void setListener(ADVideoPlayerListener listener) {
        this.listener = listener;
    }

    public void setFrameLoadListener(ADFrameImageLoadListener frameLoadListener) {
        this.mFrameLoadListener = frameLoadListener;
    }

    /**
     * 检查MediaPlayer对象，若无则创建
     */
    private synchronized void checkMediaPlayer() {
        if (null == mediaPlayer) {
            mediaPlayer = createMediaPlayer();
        }
    }

    private MediaPlayer createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if (videoSurface != null && videoSurface.isValid()) {
            mediaPlayer.setSurface(videoSurface);
        } else {
            stop();
        }
        return mediaPlayer;
    }

    private void registerBroadcastReceiver() {
        if (mScreenReceiver == null) {
            mScreenReceiver = new ScreenEventReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getContext().registerReceiver(mScreenReceiver, filter);
        }
    }

    private void unRegisterBroadcastReceiver() {
        if (null != mScreenReceiver) {
            getContext().unregisterReceiver(mScreenReceiver);
        }
    }

    private void decideCanPlay() {
        //来回切换页面时，只有 >50,且满足自动播放条件才自动播放
        if (Utils.getVisiblePercent(mParentContainer) > SDKConstant.VIDEO_SCREEN_PERCENT) {
            resume();
        } else {
            pause();
        }
    }

    public void isShowFullBtn(boolean isShow) {
        mFullBtn.setImageResource(isShow ? R.drawable.xadsdk_ad_mini : R.drawable.xadsdk_ad_mini_null);
        mFullBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * true is no voice
     *
     * @param mute
     */
    public void mute(boolean mute) {
        LogUtils.d(TAG, "mute");
        isMute = mute;
        if (mediaPlayer != null && this.audioManager != null) {
            float volume = isMute ? 0.0f : 1.0f;
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public boolean isFrameHidden() {
        return mFrameView.getVisibility() == View.VISIBLE ? false : true;
    }

    public boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    public boolean isRealPause() {
        return mIsRealPause;
    }

    public boolean isComplete() {
        return mIsComplete;
    }

    public boolean isPauseBtnClicked() {
        return mIsRealPause;
    }

    public void setIsPauseClicked(boolean click) {
        this.mIsRealPause = click;
    }

    public void setIsComplete(boolean complete) {
        this.mIsComplete = complete;
    }

    public void setDataSource(String url) {
        this.mUrl = url;
    }

    public void setFrameURI(String url) {
        mFrameURI = url;
    }

    private void showPauseView(boolean show) {
        mFullBtn.setVisibility(show ? VISIBLE : GONE);
        mMiniPlayBtn.setVisibility(show ? GONE : VISIBLE);
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(GONE);
        if (!show) {
            mFrameView.setVisibility(VISIBLE);
            loadFramImage();
        } else {
            mFrameView.setVisibility(GONE);
        }
    }

    private void showPlayView() {
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
    }

    private void loadFramImage() {
        if (mFrameLoadListener != null) {
            mFrameLoadListener.onStartFrameLoad(mFrameURI, new ImageLoaderListener() {
                @Override
                public void onLoadingComplete(Bitmap loadedImage) {
                    if (loadedImage != null) {
                        mFrameView.setScaleType(ImageView.ScaleType.FIT_XY);
                        mFrameView.setImageBitmap(loadedImage);
                    } else {
                        mFrameView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        mFrameView.setImageResource(R.drawable.xadsdk_img_error);
                    }
                }
            });
        }
    }

    private void showLoadingView() {
        mFullBtn.setVisibility(GONE);
        mLoadingBar.setVisibility(VISIBLE);
        AnimationDrawable anim = (AnimationDrawable) mLoadingBar.getDrawable();
        anim.start();
        mMiniPlayBtn.setVisibility(GONE);
        mFrameView.setVisibility(GONE);
        loadFramImage();
    }

    /**
     * 重新进入播放状态
     */
    private void entryResumeState() {
        canPlay = true;
        setCurrentPlayState(STATE_PLAYING);
        setIsPauseClicked(false);
        setIsComplete(false);
    }

    public void setCurrentPlayState(int state) {
        this.playerState = state;
    }

    public int getDuration() {
        if (null != mediaPlayer) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentPosition() {
        if (null != mediaPlayer) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return true;
    }

    private class ScreenEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 主动锁屏时：pause; 解锁时：resume
            switch (intent.getAction()) {
                case Intent.ACTION_USER_PRESENT:
                    if (playerState == STATE_PAUSING) {
                        if (mIsRealPause) {
                            pause();// 播放结束，依然停止
                        } else {
                            decideCanPlay();
                        }
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    if (playerState == STATE_PLAYING) {
                        pause();
                    }
                    break;
            }
        }
    }

    public interface ADVideoPlayerListener {
        void onADVideoLoadSuccess();

        void onADVideoPlayComplete();

        void onADVideoLoadFailed();

        void onBufferUpdate(int time);

        void onClickFullScreenBtn();

        void onClickVideo();

        void onClickBackBtn();

        void onClickPlay();
    }

    public interface ADFrameImageLoadListener {
        void onStartFrameLoad(String url, ImageLoaderListener listener);
    }

    public interface ImageLoaderListener {
        /**
         * 如果图片下载不成功，传null
         *
         * @param loadedImage
         */
        void onLoadingComplete(Bitmap loadedImage);
    }

}
