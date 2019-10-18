package com.example.mycameralibrary;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Boolean mFlashState;
    private boolean mAutofocusState = true;
    private boolean mShouldScaleToFill = true;
    private static final String TAG = "CameraPreview";
    private CameraWrapper mCameraWrapper;
    private Context mContext;
    private SurfaceHolder mHolder;
    int width, height;
    private Camera mCamera;
    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;
    boolean mPreviewRunning=false;
    private float mAspectTolerance = 0.1f;


    public CameraPreview(Context context, Camera camera) {
        super(context);
        mContext = context;
        mCamera = camera;

        // supported preview sizes
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        for(int i = 0 ; i< mSupportedPreviewSizes.size() ;i++){
            if(i ==0){
                width = mSupportedPreviewSizes.get(i).width;
                height = mSupportedPreviewSizes.get(i).height;
            }
        }
        for(Camera.Size str: mSupportedPreviewSizes)
            Log.e(TAG, str.width + "/" + str.height);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public void surfaceCreated(SurfaceHolder holder) {
        // empty. surfaceChanged will take care of stuff
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        mCamera.stopPreview();

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.e(TAG, "surfaceChanged => w=" + w + ", h=" + h);
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or reformatting changes here
        // start preview with new settings

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            parameters.setExposureCompensation(0);
            parameters.setPictureFormat(ImageFormat.JPEG);
            parameters.setJpegQuality(100);
            Camera.Size picSize =   getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);

            //parameters.setPictureSize(picSize.width, picSize.height);
            parameters.setPictureSize(width,height);
            //parameters.setPreviewSize(width, height);
            parameters.set("orientation", "portrait");
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(mHolder);
            List<String> focusModes = parameters.getSupportedFocusModes();
            Log.i(TAG,"preview size used: "+ width + "/" + height);
            if (focusModes.contains("continuous-picture")) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            } else if (focusModes.contains("auto")){
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }

               mCamera.setParameters(parameters);

        } catch (Exception e) {
            // older phone - doesn't support these calls
        }
        mCamera.startPreview();
        mPreviewRunning=true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getBestPreviewSize( width, height);
        }

        if (mPreviewSize!=null) {
            float ratio;
            if(mPreviewSize.height >= mPreviewSize.width)
                ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
            else
                ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;

            // One of these methods should be used, second method squishes preview slightly
            setMeasuredDimension(width, (int) (width * ratio));
        }
    }
    private Camera.Size getBestPreviewSize(int width, int height) {
        List<Camera.Size> sizes = mCamera.getParameters()
                .getSupportedPreviewSizes();
        if (sizes == null)
            return null;
        Camera.Size optimalSize = null;
        int tmpSize;

        int minWidthDiff = 1000;
        for (Camera.Size size : sizes) {
            if (size.width > size.height)
                tmpSize = size.height;
            else
                tmpSize = size.width;
            if (Math.abs(tmpSize - width) < minWidthDiff) {
                minWidthDiff = Math.abs(tmpSize - width);
                optimalSize = size;
            }
        }
        return optimalSize;
    }
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }
    public void setFlash(boolean flag) {
        mFlashState = flag;
        if(mCamera != null && CameraUtils.isFlashSupported(mCamera)) {

            Camera.Parameters parameters =mCamera.getParameters();
            if(flag) {
                if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                    return;
                }
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                    return;
                }
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            mCamera.setParameters(parameters);
        }
    }
    public boolean getFlash() {
        if(mCamera != null && CameraUtils.isFlashSupported(mCamera)) {
            Camera.Parameters parameters = mCamera.getParameters();
            if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    public void toggleFlash() {
        if(mCamera != null && CameraUtils.isFlashSupported(mCamera)) {
            Camera.Parameters parameters = mCamera.getParameters();
            if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }
            mCamera.setParameters(parameters);
        }
    }


    public void setShouldScaleToFill(boolean shouldScaleToFill) {
        mShouldScaleToFill = shouldScaleToFill;
    }
    public void setAspectTolerance(float aspectTolerance) {
        mAspectTolerance = aspectTolerance;
    }
    public byte[] getRotatedData(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        int width = size.width;
        int height = size.height;
        int rotationCount = getRotationCount();
        if(rotationCount == 1 || rotationCount == 3) {
            for (int i = 0; i < rotationCount; i++) {
                byte[] rotatedData = new byte[data.length];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++)
                        rotatedData[x * height + height - y - 1] = data[x + y * width];
                }
                data = rotatedData;
            }
        }
        return data;
    }
    public int getRotationCount() {
        int displayOrientation = this.getDisplayOrientation();
        return displayOrientation / 90;
    }
    public int getDisplayOrientation() {
        if (mCamera == null) {
            //If we don't have a camera set there is no orientation so return dummy value
            return 0;
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        if(mCameraWrapper.mCameraId == -1) {
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        } else {
            Camera.getCameraInfo(mCameraWrapper.mCameraId, info);
        }
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }
}
