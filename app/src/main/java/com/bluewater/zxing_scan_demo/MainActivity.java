package com.bluewater.zxing_scan_demo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluewater.zxingscanlib.android.CaptureActivity;
import com.bluewater.zxingscanlib.bean.ZxingConfig;
import com.bluewater.zxingscanlib.common.Constant;
import com.bluewater.zxingscanlib.encode.CodeCreator;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ExplainReasonCallbackWithBeforeParam;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private Context mContext;
    private Activity mActivity;

    private TextView result;                //扫描结果
    private Button scanBtn;                 //扫描

    private EditText contentEt;             //编辑内容

    private Button encodeBtnWithLogo;       //生成logo二维码
    private ImageView contentIvWithLogo;    //logo二维码图片

    private Button encodeBtn;               //生成二维码
    private ImageView contentIv;            //二维码图片

    private int REQUEST_CODE_SCAN = 111;

    private String contentEtString;

   @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mActivity = this;

        initView();
        initEvent();
    }

    private void initView()
    {
        result = findViewById(R.id.result);
        scanBtn = findViewById(R.id.scanBtn);
        contentEt = findViewById(R.id.contentEt);
        encodeBtnWithLogo = findViewById(R.id.encodeBtnWithLogo);
        contentIvWithLogo = findViewById(R.id.contentIvWithLogo);
        encodeBtn = findViewById(R.id.encodeBtn);
        contentIv = findViewById(R.id.contentIv);
    }

    private void initEvent()
    {
        //扫码
        scanBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PermissionX.init((FragmentActivity) mActivity)
                        .permissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .onExplainRequestReason(new ExplainReasonCallbackWithBeforeParam()
                        {
                            @Override
                            public void onExplainReason(ExplainScope scope, List<String> deniedList, boolean beforeRequest)
                            {
                                scope.showRequestReasonDialog(deniedList, "即将申请的权限是程序必须依赖的权限", "我已明白");
                            }
                        })
                        .onForwardToSettings(new ForwardToSettingsCallback()
                        {
                            @Override
                            public void onForwardToSettings(ForwardScope scope, List<String> deniedList)
                            {
                                scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "我已明白");
                            }
                        })
                        .request(new RequestCallback()
                        {
                            @Override
                            public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList)
                            {
                                if (allGranted)
                                {
                                    Intent intent = new Intent(mContext, CaptureActivity.class);

                                    /*ZxingConfig是配置类
                                     *可以设置是否显示底部布局，闪光灯，相册，
                                     * 是否播放提示音  震动
                                     * 设置扫描框颜色等
                                     * */
                                    ZxingConfig config = new ZxingConfig();
//                                    config.setPlayBeep(false);//是否播放扫描声音 默认为true
//                                    config.setShake(false);//是否震动  默认为true
//                                    config.setDecodeBarCode(false);//是否扫描条形码 默认为true
//                                    config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为绿色
//                                    config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
//                                    config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认为绿色
                                    config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                                    intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);

                                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "您拒绝了如下权限：" + deniedList, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        //生成logo二维码
        encodeBtnWithLogo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                contentEtString = contentEt.getText().toString().trim();
                if (TextUtils.isEmpty(contentEtString))
                {
                    Toast.makeText(mContext, "请输入要生成二维码图片的字符串", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);   //logo图标
                Bitmap bitmap = CodeCreator.createQRCode(contentEtString, 400, 400, logo);

                if (bitmap != null)
                {
                    contentIvWithLogo.setImageBitmap(bitmap);
                }
            }
        });

        //生成二维码
        encodeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                contentEtString = contentEt.getText().toString().trim();
                if (TextUtils.isEmpty(contentEtString))
                {
                    Toast.makeText(mContext, "请输入要生成二维码图片的字符串", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap bitmap = CodeCreator.createQRCode(contentEtString, 400, 400, null);
                if (bitmap != null)
                {
                    contentIv.setImageBitmap(bitmap);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK)
        {
            if (data != null)
            {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                result.setText("扫描结果为：" + content);
            }
        }
    }
}