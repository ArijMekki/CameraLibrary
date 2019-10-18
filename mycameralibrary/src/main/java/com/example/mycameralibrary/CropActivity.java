package com.example.mycameralibrary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


import java.io.File;


public class CropActivity extends AppCompatActivity {
    String TAG = CropActivity.class.getSimpleName();
    String imageUriString;
    String photoName ,photoState;
    String imagepath;
    String productid,vehicleid,keyringid;
    Double price;
    //String parentActivity;
    private static final String EXTRA_URI = "https://pp.vk.me/c637119/v637119751/248d1/6dd4IPXWwzI.jpg";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String CarExpressService = "carexpressservice" ;
    File dir;
    /* public static Intent callingIntent(Context context, Uri imageUri) {
        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra(EXTRA_URI, imageUri);
        return intent;
    }
*/
    //2private CropIwaView cropView;
    private ImageView cropView;


    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_crop);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Bundle extras = getIntent().getExtras();
        if (extras!=null){

            imageUriString = extras.getString("image uri");
            photoName = extras.getString("photo name");
            imagepath = extras.getString("selectedimagepath");
            /*productid = extras.getString("productid");
            vehicleid = extras.getString("vehicleid");
            keyringid = extras.getString("keyringid");
            price = extras.getDouble("price");
            */
            //parentActivity = extras.getString("ParentActivity");
            Log.i(TAG, "onCreate: crop activity: " + productid + "  "+ vehicleid + " " + photoName);
        }

        //Uri imageUri = getIntent().getParcelableExtra(EXTRA_URI);

        //2cropView = (CropIwaView) findViewById(R.id.crop_view);
        cropView = (ImageView) findViewById(R.id.crop_view);

        Uri myUri = Uri.parse(imageUriString);

        //2cropView.setImageUri(myUri);
        cropView.setImageURI(myUri);

        //1configurator = new CropViewConfigurator(cropView, photoName);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_crop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done) {
            //1cropView.crop(configurator.getSelectedSaveConfig());

                Intent intent = new Intent(CropActivity.this, CameraActivity.class);

            if(sharedpreferences.getString(CarExpressService,null)!=null && sharedpreferences.getString(CarExpressService,null).equals("CarkeysExpressHouseKey")){
                //intent.putExtra("ParentActivity","CarkeysExpressHouseKey");

                intent.putExtra("photo name", photoName);
                intent.putExtra("selectedimagepath", imagepath);
                /*intent.putExtra("vehicleid", vehicleid);
                intent.putExtra("productid", productid);
                intent.putExtra("keyringid", keyringid);
                intent.putExtra("price", price);*/
                startActivity(intent);
                finish();

            }
            else{


                intent.putExtra("photo name", photoName);
                intent.putExtra("selectedimagepath", imagepath);
                /*intent.putExtra("vehicleid", vehicleid);
                intent.putExtra("productid", productid);
                intent.putExtra("keyringid", keyringid);
                intent.putExtra("price", price);*/
                startActivity(intent);
                finish();
            }


        }
        return super.onOptionsItemSelected(item);
    }
    public void onDestroy() {

        super.onDestroy();
        finish();

    }
    public void onBackPressed() {

        Intent intent = new Intent(CropActivity.this, CameraActivity.class);



            intent.putExtra("photo name", photoName);
            intent.putExtra("selectedimagepath", imagepath);

            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TakeFewPicture/" + photoName + ".png");
            dir.delete();
            startActivity(intent);
            finish();




    }


}
