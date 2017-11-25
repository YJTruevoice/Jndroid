package com.yakir.core;

import android.view.ViewGroup;

import com.yakir.widget.CustomVideoView.ADVideoPlayerListener;

/**
 * @author yakir
 * @functin
 */

public class VideoAdSlot implements ADVideoPlayerListener {


    @Override
    public void onADVideoLoadSuccess() {

    }

    @Override
    public void onADVideoPlayComplete() {

    }

    @Override
    public void onADVideoLoadFailed() {

    }

    @Override
    public void onBufferUpdate(int time) {

    }

    @Override
    public void onClickFullScreenBtn() {

    }

    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {

    }

    //传递消息到appcontext层
    public interface AdSDKSlotListener {

        ViewGroup getAdParent();

        void onAdVideoLoadSuccess();

        void onAdVideoLoadFailed();

        void onAdVideoLoadComplete();

        void onClickVideo(String url);
    }
}
