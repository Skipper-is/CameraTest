package com.skipper.cameratest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.DecimalFormat;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static ImageView imageView;
    public static TextView textView, canopyPercentage;
    public static SeekBar seekBar;
    public Button takePhoto, processButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePhoto = (Button) findViewById(R.id.button); //There must be a simpler way of doing this?
        processButton = (Button) findViewById(R.id.process);
        imageView = (ImageView) findViewById(R.id.imageView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setEnabled(false);  //Disable the seek bar until a picture is taken
        textView = (TextView) findViewById(R.id.textView);
        canopyPercentage = (TextView) findViewById(R.id.canopyPercentage);

        textView.setText(String.valueOf(seekBar.getProgress())); //Guess I could also set this to 0...


                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                               @Override
                                               public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                textView.setText(String.valueOf(progress));
                                               }

                                               @Override
                                               public void onStartTrackingTouch(SeekBar seekBar) {

                                               }

                                               @Override
                                               public void onStopTrackingTouch(SeekBar seekBar) {

                                               }
                                           });


                takePhoto.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dispatchTakePictureIntent();
                    }
                });
        processButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            imageView.setImageBitmap(runProcessing());
            }
        });
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            seekBar.setEnabled(true);
            processButton.setEnabled(true);
            //TODO save result into memory for phone rotation.
        }
    }



    public static Bitmap runProcessing(){
        Bitmap before = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        if(before !=null) {
            int BLUE = 0;
            Bitmap after = Bitmap.createBitmap(before.getWidth(),before.getHeight(),before.getConfig()); //Set up new bitmap to be created
            int pixel;
            //get the dimensions of original image
            int width, height, total;
            width = before.getWidth();
            height = before.getHeight();
            total = width * height;
            int black = 0, blue = 0;
            Log.d("Bitmap dimensions", "x: " + width + " y:" + height + " total:" + total);

            //now lets go through every line... oh dear me.

            for(int x = 0; x < width;++x){
                for(int y = 0; y < height; ++y){
                    pixel = before.getPixel(x,y);
                    BLUE = Color.blue(pixel);

                    if(BLUE < seekBar.getProgress()){
                        BLUE = 0; // If the value for Blue is below the progress bar then it should be black
                        black++;
                    }else{
                        BLUE = 255; //If the value for Blue is above the value - then it should be white.
                        blue++;
                    }
                    after.setPixel(x,y,Color.argb(255,0,0,BLUE));
                }

            }
            Log.d("Black: " + black," blue: " + blue);
            float cover = ((float)black/(float)total)*100;
            String coverdecimal = String.format("%.3f", cover);
            //end for statement.. just so you dont get lost Skip...
            canopyPercentage.setText("Canopy percentage cover = "+ coverdecimal + "%");
            return after;
        }
        else {

            //TODO  flag false bitmap
            return null;
        }
    }
}
