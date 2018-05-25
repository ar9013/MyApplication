package poca.club.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String TAG = "CameraActivity";

    Camera.Size previewFrameSize = null;
     boolean isCameraPreview = false;

    SurfaceView cameraView, transparentView;
    SurfaceHolder holder, holderTransparent;
    Camera camera;



    int deviceHeight, deviceWidth;
    int[] tempFrameData = null;
    int[] rgbFrameData = null;

    public static int getScreenWidth() {

        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {

        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);
        cameraView = (SurfaceView) findViewById(R.id.CameraView);

        holder = cameraView.getHolder();
        holder.addCallback((SurfaceHolder.Callback) this);
        cameraView.setSecure(true);

        transparentView = (SurfaceView) findViewById(R.id.TransparentView);
        holderTransparent = transparentView.getHolder();
        holderTransparent.addCallback((SurfaceHolder.Callback) this);
        holderTransparent.setFormat(PixelFormat.TRANSLUCENT);
        transparentView.setZOrderMediaOverlay(true);

        deviceWidth = getScreenWidth();

        deviceHeight = getScreenHeight();
        tempFrameData = new int[deviceHeight * deviceWidth];
        rgbFrameData = new int[deviceHeight * deviceWidth];

        Log.d(TAG, "deviceHeight :" + deviceHeight);
        Log.d(TAG, "deviceWidth :" + deviceWidth);

        Log.d(TAG,"path : "+this.getFilesDir());

    }

    public void refreshCamera() {

        if (holder.getSurface() == null) {

            return;
        }

        try {
            camera.stopPreview();
            isCameraPreview = false;

        } catch (Exception e) {

        }

        try {
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(this);
            camera.startPreview();
            isCameraPreview = true;

            // 更新 camera 的時候畫上去
            Draw(20,200,100,250,120,30,15,40);

        } catch (Exception e) {

        }
    }

    private void Draw(float BLx, float BLy, float BRx, float BRy, float TLx, float TLy , float TRx, float TRy) {
        Canvas canvas = holderTransparent.lockCanvas(null);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        paint.setColor(Color.GREEN);
        canvas.drawLine(BLx,BLy,BRx,BRy,paint);

        paint.setColor(Color.YELLOW);
        canvas.drawLine(BRx,BRy,TRx,TRy,paint);

        paint.setColor(Color.BLUE);
        canvas.drawLine(TRx,TRy,TLx,TLy,paint);

        paint.setColor(Color.CYAN);
        canvas.drawLine(TLx,TLy,BLx,BLy,paint);

        holderTransparent.unlockCanvasAndPost(canvas);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onPreviewFrame(byte[] yuvNV21FrameData, Camera camera) {


    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera = Camera.open(); //open a camera

            Camera.Size size = camera.getParameters().getPreviewSize();


            int width = size.width;
            int height = size.height;

            Log.d(TAG,"width :"+width);
            Log.d(TAG,"height :"+height);


        } catch (Exception e) {

            Log.i("Exception", e.toString());

            return;
        }

        Camera.Parameters param;

        param = camera.getParameters();

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation() == Surface.ROTATION_0) {
            camera.setDisplayOrientation(90);
        }
        camera.setParameters(param);

        try {
            camera.setPreviewDisplay(holder);

            camera.startPreview();
            isCameraPreview = true;

        } catch (Exception e) {
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        // 釋放 相機
        holder.removeCallback(this);
        holderTransparent.removeCallback(this);
        holder.removeCallback(this);
        camera = null;

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        refreshCamera(); //call method for refress camera
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
