package com.bluewater.zxingscanlib.bean;

import androidx.annotation.ColorRes;

import com.bluewater.zxingscanlib.R;

import java.io.Serializable;

/**
 * 配置类
 */
public class ZxingConfig implements Serializable
{
    /*是否播放声音*/
    private boolean isPlayBeep = true;

    /*是否震动*/
    private boolean isShake = true;

    /*是否显示下方的其他功能布局*/
    private boolean isShowbottomLayout = true;

    /*是否显示闪光灯按钮*/
    private boolean isShowFlashLight = true;

    /*是否显示相册按钮*/
    private boolean isShowAlbum = true;

    /*是否解析条形码*/
    private boolean isDecodeBarCode = true;

    /*是否全屏扫描*/
    private boolean isFullScreenScan = true;

    /*四个角的颜色*/
    @ColorRes
    private int reactColor = R.color.react;

    /*扫描框颜色*/
    @ColorRes
    private int frameLineColor = -1;

    /*扫描线颜色*/
    @ColorRes
    private int scanLineColor = R.color.scanLineColor;

    @Override
    public String toString()
    {
        return "ZxingConfig{" +
                "isPlayBeep=" + isPlayBeep +
                ", isShake=" + isShake +
                ", isShowbottomLayout=" + isShowbottomLayout +
                ", isShowFlashLight=" + isShowFlashLight +
                ", isShowAlbum=" + isShowAlbum +
                ", isDecodeBarCode=" + isDecodeBarCode +
                ", isFullScreenScan=" + isFullScreenScan +
                ", reactColor=" + reactColor +
                ", frameLineColor=" + frameLineColor +
                ", scanLineColor=" + scanLineColor +
                '}';
    }

    public boolean isPlayBeep()
    {
        return isPlayBeep;
    }

    public ZxingConfig setPlayBeep(boolean playBeep)
    {
        isPlayBeep = playBeep;
        return this;
    }

    public boolean isShake()
    {
        return isShake;
    }

    public ZxingConfig setShake(boolean shake)
    {
        isShake = shake;
        return this;
    }

    public boolean isShowbottomLayout()
    {
        return isShowbottomLayout;
    }

    public ZxingConfig setShowbottomLayout(boolean showbottomLayout)
    {
        isShowbottomLayout = showbottomLayout;
        return this;
    }

    public boolean isShowFlashLight()
    {
        return isShowFlashLight;
    }

    public ZxingConfig setShowFlashLight(boolean showFlashLight)
    {
        isShowFlashLight = showFlashLight;
        return this;
    }

    public boolean isShowAlbum()
    {
        return isShowAlbum;
    }

    public ZxingConfig setShowAlbum(boolean showAlbum)
    {
        isShowAlbum = showAlbum;
        return this;
    }

    public boolean isDecodeBarCode()
    {
        return isDecodeBarCode;
    }

    public ZxingConfig setDecodeBarCode(boolean decodeBarCode)
    {
        isDecodeBarCode = decodeBarCode;
        return this;
    }

    public boolean isFullScreenScan()
    {
        return isFullScreenScan;
    }

    public ZxingConfig setFullScreenScan(boolean fullScreenScan)
    {
        isFullScreenScan = fullScreenScan;
        return this;
    }

    public int getReactColor()
    {
        return reactColor;
    }

    public ZxingConfig setReactColor(int reactColor)
    {
        this.reactColor = reactColor;
        return this;
    }

    public int getFrameLineColor()
    {
        return frameLineColor;
    }

    public ZxingConfig setFrameLineColor(int frameLineColor)
    {
        this.frameLineColor = frameLineColor;
        return this;
    }

    public int getScanLineColor()
    {
        return scanLineColor;
    }

    public ZxingConfig setScanLineColor(int scanLineColor)
    {
        this.scanLineColor = scanLineColor;
        return this;
    }
}
