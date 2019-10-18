package com.example.mycameralibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.hardware.Camera.open;

public class CameraActivity extends Activity implements SensorEventListener {
    private static final String FLASH_STATE = "FLASH_STATE";
    private boolean mFlash=false;
    public ImageButton buttonTakePicture;
    private Camera mCamera;
    private CameraPreview mPreview;
    FrameLayout preview;
    private static final String TAG = "CameraPreview";
    OutputStream output;
    File dir;
    ProgressDialog pdupload;
    boolean mPreviewRunning=false;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    String photoName,imagepath,productid,vehicleid,keyringid;
    private SensorManager sensorManager = null;
    int xsensor;
    int ysensor;
    int zsensor;
    File dir1 ;
    File dir2 ;
    File dir3 ;
    File dir4 ;
    File dir5 ;
    FrameLayout cameraseconduse;
    ImageView imgsilhouette;
    String photostate="1";
    ImageButton imgbtnphoto1,imgbtnphoto2,imgbtnphoto3,imgbtnphoto4,imgbtnphoto5;
    TextView txtnumberofphotos;
    private String upload_URL = "https://carkeysexpress.com/api/v1/keyrings/upload";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String CarExpressService = "carexpressservice" ;
    public static final String KeyringId = "keyringid" ;
    int width, height;
    int widthscreen, heightscreen;
    Class CameraFirstUseActivity;
    Class ImagesUploadedActivity;
    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        imgbtnphoto1 = findViewById(R.id.imgbtnphoto1);
        imgbtnphoto2 = findViewById(R.id.imgbtnphoto2);
        imgbtnphoto3 = findViewById(R.id.imgbtnphoto3);
        imgbtnphoto4 = findViewById(R.id.imgbtnphoto4);
        imgbtnphoto5 = findViewById(R.id.imgbtnphoto5);
        txtnumberofphotos = findViewById(R.id.txtnumberofphotos);
        imgsilhouette = findViewById(R.id.imgsilhouette);
        pdupload = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
        pdupload.setMessage("Please wait...");
        pdupload.setCancelable(false);
        cameraseconduse = findViewById(R.id.camera_second_use_preview);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Bundle extras = getIntent().getExtras();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (extras != null) {

            photoName = extras.getString("photo name");
            imagepath = extras.getString("selectedimagepath");

        }
        keyringid = sharedpreferences.getString(KeyringId,null);

        if(sharedpreferences.getString(CarExpressService,null)!=null && sharedpreferences.getString(CarExpressService,null).equals("CarkeysExpressHouseKey")) {

            ImageView imgviewdotappprocess1 = findViewById(R.id.imgviewdotappprocess1);
            ImageView imgviewdotappprocess2 = findViewById(R.id.imgviewdotappprocess2);
            ImageView imgviewdotappprocess3 = findViewById(R.id.imgviewdotappprocess3);
            ImageView imgviewdotappprocess4 = findViewById(R.id.imgviewdotappprocess4);
            ImageView imgviewdotappprocess5 = findViewById(R.id.imgviewdotappprocess5);
            imgviewdotappprocess1.setImageDrawable(getResources().getDrawable(R.drawable.dot_gradient_50));
            imgviewdotappprocess2.setImageDrawable(getResources().getDrawable(R.drawable.dot_light_gray_50));
            imgviewdotappprocess3.setImageDrawable(getResources().getDrawable(R.drawable.dot_light_gray_50));
            imgviewdotappprocess4.setVisibility(View.GONE);
            imgviewdotappprocess5.setVisibility(View.GONE);

        }
        for (int i = 1 ; i<6 ; i++) {

            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" + "photo" + i + ".png");
            if (dir.exists()) {
                //Do something
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                switch (i){
                    case 1 :

                        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" + "photo" + i + ".png");
                        final Bitmap myBitmap1 = BitmapFactory.decodeFile(dir.getAbsolutePath());

                        if(myBitmap1 != null) {
                            imgbtnphoto1.setImageBitmap(Bitmap.createScaledBitmap(myBitmap1, 70, 70, false));
                        }
                        photostate = "2";
                        txtnumberofphotos.setText("2/5");
                        break;
                    case 2 :

                        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" + "photo" + i + ".png");
                        final Bitmap myBitmap2 = BitmapFactory.decodeFile(dir.getAbsolutePath());
                        if(myBitmap2 != null) {
                            imgbtnphoto2.setImageBitmap(Bitmap.createScaledBitmap(myBitmap2, 70, 70, false));
                        }txtnumberofphotos.setText("3/5");
                        photostate = "3";
                        break;
                    case 3 :

                        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" + "photo" + i + ".png");
                        final Bitmap myBitmap3 = BitmapFactory.decodeFile(dir.getAbsolutePath());
                        if(myBitmap3 != null) {
                            imgbtnphoto3.setImageBitmap(Bitmap.createScaledBitmap(myBitmap3, 70, 70, false));
                        }
                        txtnumberofphotos.setText("4/5");
                        photostate = "4";
                        break;
                    case 4 :

                        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" + "photo" + i + ".png");
                        final Bitmap myBitmap4 = BitmapFactory.decodeFile(dir.getAbsolutePath());
                        if(myBitmap4 != null) {
                            imgbtnphoto4.setImageBitmap(Bitmap.createScaledBitmap(myBitmap4, 70, 70, false));
                        }
                        txtnumberofphotos.setText("5/5");
                        photostate = "5";
                        break;
                    case 5 :

                        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" + "photo" + i + ".png");
                        final Bitmap myBitmap5 = BitmapFactory.decodeFile(dir.getAbsolutePath());
                        if(myBitmap5 != null) {
                            imgbtnphoto5.setImageBitmap(Bitmap.createScaledBitmap(myBitmap5, 70, 70, false));
                        }
                        Handler handler = new Handler();
                        handler.postDelayed(
                                new Runnable() {
                                    public void run() {

                                        Log.i(TAG, "onCreate: Camera activity: " + productid + "  "+ vehicleid+ " keyring :  " +keyringid);
                                        dir1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" + "photo" + 1 + ".png");
                                        dir2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" + "photo" + 2 + ".png");
                                        dir3 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" + "photo" + 3 + ".png");
                                        dir4 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" + "photo" + 4 + ".png");
                                        dir5 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" + "photo" + 5 + ".png");


                                        BroadcastReceiver resultReceiver1 = createBroadcastReceiver1();
                                        LocalBroadcastManager.getInstance(CameraActivity.this).registerReceiver(resultReceiver1, new IntentFilter("com.example.keysbymail.shippinginfo.uploadfirsttwophoto"));
                                        BroadcastReceiver resultReceiver2 = createBroadcastReceiver2();
                                        LocalBroadcastManager.getInstance(CameraActivity.this).registerReceiver(resultReceiver2, new IntentFilter("com.example.keysbymail.shippinginfo.uploadsecondtwophoto"));
                                        BroadcastReceiver resultReceiver3 = createBroadcastReceiver3();
                                        LocalBroadcastManager.getInstance(CameraActivity.this).registerReceiver(resultReceiver3, new IntentFilter("com.example.keysbymail.shippinginfo.uploadthirdtwophoto"));
                                        BroadcastReceiver resultReceiver4 = createBroadcastReceiver4();
                                        LocalBroadcastManager.getInstance(CameraActivity.this).registerReceiver(resultReceiver4, new IntentFilter("com.example.keysbymail.shippinginfo.uploadfourthtwophoto"));
                                        BroadcastReceiver resultReceiver5 = createBroadcastReceiver5();
                                        LocalBroadcastManager.getInstance(CameraActivity.this).registerReceiver(resultReceiver5, new IntentFilter("com.example.keysbymail.shippinginfo.uploadfifthtwophoto"));

                                        Log.i(TAG, "onCreate: get String: " + " " + "productid: " + productid + " " + " " + "keyringid: " + keyringid + " " + "vehicleid: " + vehicleid);
                                        if (dir1 != null && dir2 != null && dir3 != null && dir4 != null && dir5 != null && keyringid!=null) {

                                            pdupload.show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(
                                                    new Runnable() {
                                                        public void run() {
                                                            uploadFisrttwoImage(dir1);
                                                        }
                                                    }, 5000);




                                        } else {
                                            AlertDialog alertDialog = new AlertDialog.Builder(CameraActivity.this).create();
                                            alertDialog.setTitle("Upload Images");
                                            alertDialog.setMessage("Images not found, do you want to repeat taking pictures?");
                                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();

                                                            Intent intent = null;
                                                            try {
                                                                intent = new Intent(CameraActivity.this,Class.forName("com.example.carkeysexpress.CameraFirstUseActivity"));
                                                            } catch (ClassNotFoundException e) {
                                                                e.printStackTrace();
                                                            }
                                                            startActivity(intent);
                                                        }
                                                    });
                                            alertDialog.show();
                                        }


                                    }
                                }, 3000);
                        break;

                }
            } else {
                //Nothing
            }
        }


        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener( this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        mCamera = getCameraInstance();
        Camera.Parameters params = mCamera.getParameters();

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors) { Log.i(TAG,"sensor " + "name " + sensor.getName() + " type " + sensor.getStringType()); }



        List<String> focusModes = params.getSupportedFocusModes();
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Log.i(TAG,"Focus modes: " + focusModes);
        List<Camera.Size> previews = params.getSupportedPreviewSizes();

        for (int i=0 ; i<sizes.size(); i++){
            Log.i(TAG,"sizes: " + sizes.get(i).width +"x" + sizes.get(i).height + "\n");
        }
        for (int i=0 ; i<previews.size(); i++){
            Log.i(TAG,"Preview sizes: " + previews.get(i).width +"x" + previews.get(i).height + "\n" );
            if(i ==0){
                width = previews.get(i).width;
                height = previews.get(i).height;
            }

        }
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthscreen = size.x;
        heightscreen = size.y;


        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this,mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_second_use_preview);
        preview.addView(mPreview);
        // create a file where picture is saved
        File filepath= Environment.getExternalStorageDirectory();
        dir=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture");
        dir.mkdirs();
        boolean rc1 = dir.setExecutable(true, false);
        boolean rc2 = dir.setReadable(true, false);
        boolean rc3 = dir.setWritable(true, false);
        buttonTakePicture = (ImageButton) findViewById(R.id.button_capture);
        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mCamera.takePicture(null,mPictureCallback,mPictureCallback);

            }
        });


    }

    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xsensor = (int) Math.pow(sensorEvent.values[0], 2);
            ysensor = (int) Math.pow(sensorEvent.values[1], 2);
            zsensor = (int) Math.pow(sensorEvent.values[2], 2);
            if(ysensor==0 && xsensor==0){
                imgsilhouette.setBackgroundResource(R.drawable.car_express_edge_cut_silhouette);
                buttonTakePicture.setVisibility(View.VISIBLE);

            }
            if(zsensor==0 && xsensor<102 && ysensor==0){
                Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(),
                        R.drawable.cube_left);
                imgsilhouette.setBackgroundResource(R.drawable.cube_left);
                buttonTakePicture.setVisibility(View.INVISIBLE);
            }
            if (ysensor==0 && xsensor!=0 && zsensor!=0 && xsensor>zsensor ){
                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),
                        R.drawable.cube_left);
                imgsilhouette.setBackgroundResource(R.drawable.cube_left);
                buttonTakePicture.setVisibility(View.INVISIBLE);


            }
            if (ysensor==0 && xsensor!=0 && zsensor!=0 && xsensor<zsensor){
                Bitmap bitmapp = BitmapFactory.decodeResource(getResources(),
                        R.drawable.cube_right);
                imgsilhouette.setBackgroundResource(R.drawable.cube_right);
                buttonTakePicture.setVisibility(View.INVISIBLE);

            }
            if (ysensor==0 && xsensor==102 && zsensor==0){
                Bitmap bitmappp = BitmapFactory.decodeResource(getResources(),
                        R.drawable.cube_right);
                imgsilhouette.setBackgroundResource(R.drawable.cube_right);
                buttonTakePicture.setVisibility(View.INVISIBLE);

            }
            if (xsensor==0 && ysensor!=0 && zsensor>=0   ){
                Bitmap bitmapppp = BitmapFactory.decodeResource(getResources(),
                        R.drawable.cube_up);
                imgsilhouette.setBackgroundResource(R.drawable.cube_up);
                buttonTakePicture.setVisibility(View.INVISIBLE);


            }


            Log.i(TAG, "Accelerometre  Sensor: " + "x: " + xsensor + "   " + "y: " + ysensor + "   " + "z: " + zsensor);

        }

    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }





    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
    //function which capture the picture from the camera preview and save it in the properly path
    Camera.PictureCallback mPictureCallback=new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String file_name= "photo"+ photostate + ".png";
            File file=new File(dir,file_name);
            boolean rc1 = file.setExecutable(true, false);
            boolean rc2 = file.setReadable(true, false);
            boolean rc3 = file.setWritable(true, false);

            if(data!=null)
            {
                //camera.stopPreview();



                mPreviewRunning=false;
                mCamera = getCameraInstance();
                //camera.release();
                try
                {
                    BitmapFactory.Options opts=new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0,opts);
                    /********************Camera preview is in portrait mode but image captured is rotated********/
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    {
                        // Notice that width and height are reversed
                        //Bitmap scaled = Bitmap.createScaledBitmap(bitmap,700, 400, true);
                        Bitmap scaled = Bitmap.createScaledBitmap(bitmap,600, 300, true);
                        // Setting post rotate to 90
                        Matrix mtx = new Matrix();
                        mtx.postRotate(90);
                        // Rotating Bitmap
                        bitmap = Bitmap.createBitmap(scaled,0,0,600-160,300, mtx, true);
                        // bitmap = Bitmap.createBitmap(scaled,0,0,630,300, mtx, true);

                    }

                    try
                    {
                        output=new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                        output.flush();
                        output.close();

                        Intent intent =  new Intent(CameraActivity.this , CropActivity.class);
                        intent.putExtra("image uri" , Uri.fromFile(file).toString());
                        Log.i(TAG, "onPictureTaken: URI: " + Uri.fromFile(file).toString());

                        if(sharedpreferences.getString(CarExpressService,null)!=null && sharedpreferences.getString(CarExpressService,null).equals("CarkeysExpressHouseKey")) {
                            intent.putExtra("photo name", "photo" + photostate);
                            intent.putExtra("selectedimagepath", imagepath);
                            Toasty.success(getApplicationContext(), "picture saved", Toast.LENGTH_LONG).show();
                            startActivity(intent);
                        }

                        else{
                            intent.putExtra("photo name", "photo"+photostate);
                            intent.putExtra("selectedimagepath", imagepath);
                            Toasty.success(getApplicationContext(),"picture saved",Toast.LENGTH_LONG).show();
                            startActivity(intent);

                        }


                    }
                    catch(Exception e) {
                        Toast.makeText(getApplicationContext(),TAG + ": " + e.toString(),Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(),TAG + ": " + e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }

    };

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
    }


    /** A safe way to get an instance of the Camera object. */
    public Camera getCameraInstance(){
        Camera c = null;
        try {
            c = open(); // attempt to get a Camera instance
        }
        catch (Exception e){
        }
        return c; // returns null if camera is unavailable
    }
    /** A basic Camera preview class */

    //paint a rectangle in the camera preview


    private BroadcastReceiver createBroadcastReceiver1() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "onReceive: upload first two photo: " +  intent.getStringExtra("upload first two photo"));
                if(intent.getStringExtra("upload first two photo").contains("{\"id\"")) {


                    Toasty.success(getApplicationContext(), "first photo", Toasty.LENGTH_SHORT).show();

                    // delete 1st image
                    dir1.delete();
                    uploadSecondtwoImage(dir2);


                }
                else if(intent.getStringExtra("upload first two photo").contains("com.android.volley.ServerError")){
                    pdupload.dismiss();
                    Toasty.error(getApplicationContext(), "Please try again...",Toasty.LENGTH_SHORT).show();

                }
                else if(intent.getStringExtra("upload first two photo").contains("com.android.volley.NoConnectionError")){
                    pdupload.dismiss();
                    Toasty.warning(getApplicationContext(), "Please check your internet connection...",Toasty.LENGTH_SHORT).show();

                }
//com.android.volley.NoConnectionError
            }
        };
    }
    private BroadcastReceiver createBroadcastReceiver2() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "onReceive: upload second two photo: " +  intent.getStringExtra("upload second two photo"));
                if(intent.getStringExtra("upload second two photo").contains("{\"id\"")) {

                    Toasty.success(getApplicationContext(), "second photo", Toasty.LENGTH_SHORT).show();



                    // delete second image
                    dir2.delete();
                    uploadthirdtwoImage(dir3);



                }
                else if(intent.getStringExtra("upload second two photo").contains("com.android.volley.ServerError")){
                    pdupload.dismiss();
                    Toasty.error(getApplicationContext(), "Please try again...",Toasty.LENGTH_SHORT).show();
                }
                else if(intent.getStringExtra("upload second two photo").contains("com.android.volley.NoConnectionError")){
                    pdupload.dismiss();
                    Toasty.warning(getApplicationContext(), "Please check your internet connection...",Toasty.LENGTH_SHORT).show();

                }
            }
        };
    }
    private BroadcastReceiver createBroadcastReceiver3() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "onReceive: upload third two photo: " +  intent.getStringExtra("upload third two photo"));
                if(intent.getStringExtra("upload third two photo").contains("{\"id\":")) {

                    Toasty.success(getApplicationContext(), "third photo", Toasty.LENGTH_SHORT).show();

                    dir3.delete();
                    uploadfourthtwoImage(dir4);


                }
                else if(intent.getStringExtra("upload third two photo").contains("com.android.volley.ServerError")){
                    pdupload.dismiss();
                    Toasty.error(getApplicationContext(), "Please try again...",Toasty.LENGTH_SHORT).show();

                }
                else if(intent.getStringExtra("upload third two photo").contains("com.android.volley.NoConnectionError")){
                    pdupload.dismiss();
                    Toasty.warning(getApplicationContext(), "Please check your internet connection...",Toasty.LENGTH_SHORT).show();

                }
//com.android.volley.NoConnectionError
            }
        };
    }
    private BroadcastReceiver createBroadcastReceiver4() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "onReceive: upload fourth two photo: " +  intent.getStringExtra("upload fourth two photo"));
                if(intent.getStringExtra("upload fourth two photo").contains("{\"id\":")) {

                    Toasty.success(getApplicationContext(), "fourth photo", Toasty.LENGTH_SHORT).show();

                    dir4.delete();
                    uploadfifthtwoImage(dir5);




                }
                else if(intent.getStringExtra("upload fourth two photo").contains("com.android.volley.ServerError")){
                    pdupload.dismiss();
                    Toasty.error(getApplicationContext(), "Please try again...",Toasty.LENGTH_SHORT).show();

                }
                else if(intent.getStringExtra("upload fourth two photo").contains("com.android.volley.NoConnectionError")){
                    pdupload.dismiss();
                    Toasty.warning(getApplicationContext(), "Please check your internet connection...",Toasty.LENGTH_SHORT).show();

                }
//com.android.volley.NoConnectionError
            }
        };
    }
    private BroadcastReceiver createBroadcastReceiver5() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "onReceive: upload fifth two photo: " +  intent.getStringExtra("upload fifth two photo"));
                if(intent.getStringExtra("upload fifth two photo").contains("{\"id\":")) {
                    // delete fifth image
                    dir5.delete();

                    pdupload.dismiss();
                    Toasty.success(getApplicationContext(), "fifth photo", Toasty.LENGTH_SHORT).show();

                    Intent intentImagesUploaded = null;
                    try {
                        intentImagesUploaded = new Intent(CameraActivity.this, Class.forName("com.example.carkeysexpress.ImagesUploadedActivity"));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    startActivity(intentImagesUploaded);
                    finish();

                }
                else if(intent.getStringExtra("upload fifth two photo").contains("com.android.volley.ServerError")){
                    pdupload.dismiss();
                    Toasty.error(getApplicationContext(), "Please try again...",Toasty.LENGTH_SHORT).show();

                }
                else if(intent.getStringExtra("upload fifth two photo").contains("com.android.volley.NoConnectionError")){
                    pdupload.dismiss();
                    Toasty.warning(getApplicationContext(), "Please check your internet connection...",Toasty.LENGTH_SHORT).show();

                }
            }
        };
    }
    public void uploadFisrttwoImage(File diir){

        SimpleMultiPartRequest multiPartRequestWithParams = new SimpleMultiPartRequest(Request.Method.POST, upload_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response ", "first two images: " + response);
                        // TODO: Do something on success
                        Intent intent = new Intent("com.example.keysbymail.shippinginfo.uploadfirsttwophoto");
                        intent.putExtra("upload first two photo", response.toString());
                        LocalBroadcastManager.getInstance(CameraActivity.this).sendBroadcast(intent);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle your error here
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("com.example.keysbymail.shippinginfo.uploadfirsttwophoto");
                intent.putExtra("upload first two photo",error.toString());
                LocalBroadcastManager.getInstance(CameraActivity.this).sendBroadcast(intent);
            }
        });

        // Add the file here
        multiPartRequestWithParams.addFile("photos[]", String.valueOf(diir));

        // Add the params here
        multiPartRequestWithParams.addStringParam("keyring_id",keyringid);

        RequestQueue queue = Volley.newRequestQueue(CameraActivity.this);
        ///  queue.getCache().clear();
        queue.add(multiPartRequestWithParams);

    }
    public void uploadSecondtwoImage(File diir ){


        SimpleMultiPartRequest multiPartRequestWithParams = new SimpleMultiPartRequest(Request.Method.POST, upload_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response","second two images: " + response);
                        // TODO: Do something on success
                        Intent intent = new Intent("com.example.keysbymail.shippinginfo.uploadsecondtwophoto");
                        intent.putExtra("upload second two photo", response.toString());
                        LocalBroadcastManager.getInstance(CameraActivity.this).sendBroadcast(intent);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle your error here
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("com.example.keysbymail.shippinginfo.uploadsecondtwophoto");
                intent.putExtra("upload second two photo",error.toString());
                LocalBroadcastManager.getInstance(CameraActivity.this).sendBroadcast(intent);
            }
        });

        // Add the file here
        multiPartRequestWithParams.addFile("photos[]", String.valueOf(diir));

        // Add the params here
        multiPartRequestWithParams.addStringParam("keyring_id",keyringid);

        RequestQueue queue = Volley.newRequestQueue(CameraActivity.this);
        //queue.getCache().clear();
        queue.add(multiPartRequestWithParams);
    }
    public void uploadthirdtwoImage(File diir ){




        SimpleMultiPartRequest multiPartRequestWithParams = new SimpleMultiPartRequest(Request.Method.POST, upload_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response","third two images: " + response);
                        // TODO: Do something on success
                        Intent intent = new Intent("com.example.keysbymail.shippinginfo.uploadthirdtwophoto");
                        intent.putExtra("upload third two photo", response.toString());
                        LocalBroadcastManager.getInstance(CameraActivity.this).sendBroadcast(intent);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle your error here
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("com.example.keysbymail.shippinginfo.uploadthirdtwophoto");
                intent.putExtra("upload third two photo",error.toString());
                LocalBroadcastManager.getInstance(CameraActivity.this).sendBroadcast(intent);
            }
        });

        // Add the file here
        multiPartRequestWithParams.addFile("photos[]", String.valueOf(diir));

        // Add the params here
        multiPartRequestWithParams.addStringParam("keyring_id",keyringid);

        RequestQueue queue = Volley.newRequestQueue(CameraActivity.this);
        // queue.getCache().clear();
        queue.add(multiPartRequestWithParams);


    }
    public void uploadfourthtwoImage(File diir ){


        SimpleMultiPartRequest multiPartRequestWithParams = new SimpleMultiPartRequest(Request.Method.POST, upload_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", "fourth two images: " +response);
                        // TODO: Do something on success
                        Intent intent = new Intent("com.example.keysbymail.shippinginfo.uploadfourthtwophoto");
                        intent.putExtra("upload fourth two photo", response.toString());
                        LocalBroadcastManager.getInstance(CameraActivity.this).sendBroadcast(intent);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle your error here
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("com.example.keysbymail.shippinginfo.uploadfourthtwophoto");
                intent.putExtra("upload fourth two photo",error.toString());
                LocalBroadcastManager.getInstance(CameraActivity.this).sendBroadcast(intent);
            }
        });

        // Add the file here
        multiPartRequestWithParams.addFile("photos[]", String.valueOf(diir));

        // Add the params here
        multiPartRequestWithParams.addStringParam("keyring_id",keyringid);

        RequestQueue queue = Volley.newRequestQueue(CameraActivity.this);

        queue.add(multiPartRequestWithParams);

    }


    public void uploadfifthtwoImage(File diir ){



        SimpleMultiPartRequest multiPartRequestWithParams = new SimpleMultiPartRequest(Request.Method.POST, upload_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", "fifth two images: " +response);
                        // TODO: Do something on success
                        Intent intent = new Intent("com.example.keysbymail.shippinginfo.uploadfifthtwophoto");
                        intent.putExtra("upload fifth two photo", response.toString());
                        LocalBroadcastManager.getInstance(CameraActivity.this).sendBroadcast(intent);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle your error here
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("com.example.keysbymail.shippinginfo.uploadfifthtwophoto");
                intent.putExtra("upload fifth two photo",error.toString());
                LocalBroadcastManager.getInstance(CameraActivity.this).sendBroadcast(intent);
            }
        });

        // Add the file here
        multiPartRequestWithParams.addFile("photos[]", String.valueOf(diir));


        // Add the params here
        multiPartRequestWithParams.addStringParam("keyring_id",keyringid);
        RequestQueue queue = Volley.newRequestQueue(CameraActivity.this);
        queue.add(multiPartRequestWithParams);

    }
    @Override
    protected void onResume()
    {
        super.onResume();
        // Register this class as a listener for the accelerometer sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        // ...and the orientation sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop()
    {
        // Unregister the listener
        sensorManager.unregisterListener(this);
        super.onStop();
    }
    public void onDestroy() {

        super.onDestroy();
        finish();

    }
    public void onBackPressed() {
        try {
        if(photostate=="1"){
            Intent intent = null;

                intent = new Intent(CameraActivity.this, Class.forName("com.example.carkeysexpress.CameraFirstUseActivity"));

            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" + photoName + ".png");
            dir.delete();
            startActivity(intent);
            finish();

        }
        else {
            Intent intent = new Intent(CameraActivity.this, Class.forName("com.example.carkeysexpress.CameraFirstUseActivity"));


            intent.putExtra("photo name", "photo" +String.valueOf(Integer.parseInt(photostate)-1));
            intent.putExtra("selectedimagepath", imagepath);
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" +"photo" +String.valueOf(Integer.parseInt(photostate)-1) + ".png");
            dir.delete();
            startActivity(intent);
            finish();

        }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



    }





}






