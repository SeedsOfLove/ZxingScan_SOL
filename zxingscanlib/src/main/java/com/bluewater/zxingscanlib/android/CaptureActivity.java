package com.bluewater.zxingscanlib.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluewater.zxingscanlib.R;
import com.bluewater.zxingscanlib.bean.ZxingConfig;
import com.bluewater.zxingscanlib.camera.CameraManager;
import com.bluewater.zxingscanlib.common.Constant;
import com.bluewater.zxingscanlib.decode.DecodeImgCallback;
import com.bluewater.zxingscanlib.decode.DecodeImgThread;
import com.bluewater.zxingscanlib.decode.ImageUtil;
import com.bluewater.zxingscanlib.view.ViewfinderView;
import com.google.zxing.Result;

import java.io.IOException;

public class CaptureActivity extends AppCompatActivity implements SurfaceHolder.Callback
{
    private static final String TAG = CaptureActivity.class.getSimpleName();
    public ZxingConfig config;

    private SurfaceView previewView;            //相机画面
    private AppCompatImageView ivBack;          //返回
    private ViewfinderView viewfinderView;      //扫码区域
    private LinearLayout llBottom;              //底部布局
    private LinearLayout llFlashLight;          //闪光灯
    private AppCompatImageView ivFlashLight;    //闪光灯图标
    private TextView tvFlashLight;              //闪光灯文字
    private LinearLayout llAlbum;               //相册

    private boolean hasSurface;

    private InactivityTimer inactivityTimer;

    private BeepManager beepManager;

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;

    private SurfaceHolder surfaceHolder;

    public ViewfinderView getViewfinderView()
    {
        return viewfinderView;
    }

    public Handler getHandler()
    {
        return handler;
    }

    public CameraManager getCameraManager()
    {
        return cameraManager;
    }

    public void drawViewfinder()
    {
        viewfinderView.drawViewfinder();
    }

    static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);     //保持屏幕常亮
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            window.setStatusBarColor(Color.BLACK);
        }

        setContentView(R.layout.activity_capture);

        /*先获取配置信息*/
        try
        {
            config = (ZxingConfig) getIntent().getExtras().get(Constant.INTENT_ZXING_CONFIG);
        } catch (Exception e)
        {
            Log.i("config", e.toString());
        }

        if (config == null)
        {
            config = new ZxingConfig();
        }

        initView();
        initEvent();

        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);

        beepManager = new BeepManager(this);
        beepManager.setPlayBeep(config.isPlayBeep());
        beepManager.setVibrate(config.isShake());
    }

    /**
     * 初始化视图
     */
    private void initView()
    {
        previewView = findViewById(R.id.sv_zslib);
        ivBack = findViewById(R.id.iv_zslib_back);
        viewfinderView = findViewById(R.id.vv_zslib);
        llBottom = findViewById(R.id.ll_zslib_bottom);
        llFlashLight = findViewById(R.id.ll_zslib_flash_light);
        ivFlashLight = findViewById(R.id.iv_zslib_flash_light);
        tvFlashLight = findViewById(R.id.tv_zslib_flash_light);
        llAlbum = findViewById(R.id.ll_zslib_album);

        viewfinderView.setZxingConfig(config);

        switchVisibility(llBottom, config.isShowbottomLayout());        //初始底部栏显示状态
        switchVisibility(llFlashLight, config.isShowFlashLight());      //初始闪光灯显示状态
        switchVisibility(llAlbum, config.isShowAlbum());                //初始相册显示状态

        /*有闪光灯就显示手电筒按钮  否则不显示*/
        if (isSupportCameraLedFlash(getPackageManager()))
        {
            llFlashLight.setVisibility(View.VISIBLE);
        }
        else
        {
            llFlashLight.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化事件
     */
    private void initEvent()
    {
        //返回
        ivBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        //切换闪光灯
        llFlashLight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                cameraManager.switchFlashLight(handler);
            }
        });

        //打开相册
        llAlbum.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Constant.REQUEST_IMAGE);
            }
        });
    }

    /**
     * 切换控件显示
     * @param view
     * @param b
     */
    private void switchVisibility(View view, boolean b)
    {
        if (b)
        {
            view.setVisibility(View.VISIBLE);
        }
        else
        {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 是否有闪光灯
     * @param pm
     * @return
     */
    private static boolean isSupportCameraLedFlash(PackageManager pm)
    {
        if (pm != null)
        {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null)
            {
                for (FeatureInfo f : features)
                {
                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH.equals(f.name))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 切换闪光灯图片
     */
    public void switchFlashImg(int flashState)
    {
        if (flashState == Constant.FLASH_OPEN)
        {
            ivFlashLight.setImageResource(R.drawable.zssol_open);
            tvFlashLight.setText(R.string.close_flash);
        }
        else
        {
            ivFlashLight.setImageResource(R.drawable.zssol_close);
            tvFlashLight.setText(R.string.open_flash);
        }
    }

    /**
     * 返回的扫描结果
     */
    public void handleDecode(Result rawResult)
    {
        inactivityTimer.onActivity();

        beepManager.playBeepSoundAndVibrate();

        Intent intent = getIntent();
        intent.putExtra(Constant.CODED_CONTENT, rawResult.getText());
        setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        if (!hasSurface)
        {
            hasSurface = true;
            initCamera(surfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        hasSurface = false;
    }

    /**
     * 初始化相机
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder)
    {
        if (surfaceHolder == null)
        {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen())
        {
            return;
        }
        try
        {
            cameraManager.openDriver(surfaceHolder);        // 打开Camera硬件设备

            if (handler == null)
            {
                handler = new CaptureActivityHandler(this, cameraManager);   // 创建一个handler来打开预览，并抛出一个运行时异常
            }
        } catch (IOException ioe)
        {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e)
        {
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * 显示错误提醒对话框
     */
    private void displayFrameworkBugMessageAndExit()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("扫一扫");
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        cameraManager = new CameraManager(getApplication(), config);

        viewfinderView.setCameraManager(cameraManager);
        handler = null;

        surfaceHolder = previewView.getHolder();
        if (hasSurface)
        {
            initCamera(surfaceHolder);
        }
        else
        {
            surfaceHolder.addCallback(this);    // 重置callback，等待surfaceCreated()来初始化camera
        }

        beepManager.updatePrefs();
        inactivityTimer.onResume();
    }

    @Override
    protected void onPause()
    {
        Log.i(TAG, "onPause");

        if (handler != null)
        {
            handler.quitSynchronously();
            handler = null;
        }

        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();

        if (!hasSurface)
        {
            surfaceHolder.removeCallback(this);
        }

        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        inactivityTimer.shutdown();
        viewfinderView.stopAnimator();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_IMAGE && resultCode == RESULT_OK)
        {
            String path = ImageUtil.getImageAbsolutePath(this, data.getData());

            new DecodeImgThread(path, new DecodeImgCallback()
            {
                @Override
                public void onImageDecodeSuccess(Result result)
                {
                    handleDecode(result);
                }

                @Override
                public void onImageDecodeFailed()
                {
                    Toast.makeText(CaptureActivity.this, R.string.scan_failed_tip, Toast.LENGTH_SHORT).show();
                }
            }).run();
        }
    }
}