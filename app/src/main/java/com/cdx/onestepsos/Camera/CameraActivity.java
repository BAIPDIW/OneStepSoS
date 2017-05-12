package com.cdx.onestepsos.Camera;


import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.cdx.onestepsos.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private Camera mCamera;
    private SurfaceView mPreview;
    private SurfaceHolder mHolder;
    private TextView tv_camera_show_time;
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头
    private int cameraCount;
    private int photoCount = 0;
    private Bundle picPathBundle = new Bundle();
    private Handler handler;
    private Handler handler2;
    private Thread thread;
    private boolean flag = true;
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File tempFile = getOutputMediaFile();
            try {
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(data);
                fos.close();
                if(cameraPosition == 1){//后置拍照
                    picPathBundle.putString("picPathBack",tempFile.getAbsolutePath());
                }else if(cameraPosition == 0){//前置拍照
                    picPathBundle.putString("picPathFront",tempFile.getAbsolutePath());
                }
                photoCount = photoCount +1;
                if(photoCount == cameraCount) {
                    Intent intent = new Intent();
                    intent.putExtra("picPath",picPathBundle);
                    setResult(1,intent);
                    finish();
                    flag = false;
                }else{
                    CameraChange();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            capture();
                        }
                    },3*1000);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        tv_camera_show_time = (TextView) findViewById(R.id.tv_camera_show_time);
        mPreview = (SurfaceView) findViewById(R.id.preview);
        mHolder = mPreview.getHolder();
        cameraCount = Camera.getNumberOfCameras();
        mHolder.addCallback(this);
        handler = new Handler();
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(null);
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|WindowManager.LayoutParams.FLAG_FULLSCREEN);
        handler2 = new Handler() {
            public void handleMessage(Message msg) {
                tv_camera_show_time.setText((String)msg.obj);
            }
        };
        thread = new Thread(runnable);
        thread.start();

    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String time;
            while(flag){
                time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                handler2.sendMessage(handler.obtainMessage(100,time));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    @Override
    public void onBackPressed() {
        return ;//屏蔽掉返回键
    }

    private void CameraChange(){
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for(int i = 0 ; i < cameraCount ; i ++) {
            Camera.getCameraInfo(i,cameraInfo);
            if(cameraPosition == 1){//现在是后置,变为前置
                if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                    mCamera = Camera.open(i);
                    try {
                        mCamera.setPreviewDisplay(mHolder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setStartPreview(mCamera, mHolder);
                    cameraPosition = 0;
                    break;
                }
            }else{//现在是前置， 变更为后置
                if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        mCamera.setPreviewDisplay(mHolder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setStartPreview(mCamera, mHolder);
                    cameraPosition = 1;
                    break;
                }
           }
        }

    }
    public void capture() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    mCamera.takePicture(null, null, mPictureCallback);
                }else{
                    mCamera.takePicture(null, null, mPictureCallback);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera();
            if (mHolder != null) {
                setStartPreview(mCamera, mHolder);
            }
        }
        if(photoCount == 0) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    capture();
                }
            }, 3 * 1000);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();

    }
    /**
     * 获取Camera对象
     *
     * @return
     */
    private Camera getCamera() {
        Camera camera;
        try {
            camera = Camera.open();

        } catch (Exception e) {
            camera = null;
        }
        return camera;
    }

    /**
     * 开始预览相机内容
     */
    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        setStartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        setStartPreview(mCamera, mHolder);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();

    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.i("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }
}
