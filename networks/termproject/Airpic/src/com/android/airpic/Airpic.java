/**
 * Copyright (c) 2010, Christopher Carlisle
 * 
 * This work is licensed under the Creative Commons 
 * Attribution-Noncommercial-No Derivative Works 3.0 United States 
 * License. To view a copy of this license, visit 
 * 
 * http://creativecommons.org/licenses/by-nc-nd/3.0/us/ 
 * 
 * or send a letter to Creative Commons, 171 Second Street, 
 * Suite 300, San Francisco, California, 94105, USA.
 */

package com.android.airpic;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Airpic extends Activity 
{
    private static final String TAG = "Airpic: "; //used for output
    protected Button _photoButton; //button on screen 
    protected Button _uploadButton; //button on screen 
    protected ImageView _image;  //image that will be drawn
    protected TextView _field;  //text field displayed
    protected String _path;  //folder to check
    protected String _file;  //image path
    protected boolean _taken; //flag of pic taken
    protected boolean viewingSettings=false; //flag if settings are being viewed
    protected boolean uploadingPhoto=false; //flag to see if upload is occurring
    
    //File info
    String currentDateTimeString;
    Date currentDate;
    
    protected static final String PHOTO_TAKEN   = "photo_taken";  
    
    @Override  
    public void onCreate(Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);  
      
        setContentView(R.layout.camera);  
		Log.i(TAG, "setContent");
        _image = ( ImageView ) findViewById( R.id.image );  //displays image in program
        _field = ( TextView ) findViewById( R.id.field );  //displays No photo take text
        _photoButton = ( Button ) findViewById( R.id.button );  //this is the photo button 
        _photoButton.setOnClickListener( new PhotoButtonClickHandler() ); //event handler for button
        _uploadButton = ( Button ) findViewById( R.id.button2 ); //this is the upload button
        _uploadButton.setOnClickListener( new UploadButtonClickHandler() ); //event handler for button
        Log.i(TAG, "Created UI");
      
        //set path to store image
        _file = String.format("%s%s%s", Environment.getExternalStorageDirectory(), getResources().getText(R.string.pathForPicture), getResources().getText(R.string.nameForPicture));
        Log.i(TAG,"===========================");
        Log.i(TAG, _file.toString());
        Log.i(TAG,"===========================");
        
        //check if images folder exists
        _path = String.format("%s%s", Environment.getExternalStorageDirectory(), getResources().getText(R.string.pathForPicture));
       File picFolder = new File(_path);
       if(!picFolder.exists())
       {
    	   if(picFolder.mkdir()) //attempts to create folder
    	   {
    		   Log.i(TAG,"****************************");
    		   Log.i(TAG,"Images Folder created");
    		   Log.i(TAG,"****************************");
    	   }
    	   else
    	   {
    		   Log.e(TAG,"****************************");
    		   Log.e(TAG,"Folder creation failed!!!!!");
    		   Log.e(TAG,"****************************");
    	   }
       }
    }  
      
    //Abstract class that is an event handler for upload button
    public class UploadButtonClickHandler implements View.OnClickListener
    {
    	public void onClick( View view )
    	{
    		Log.i(TAG, "!!!!!!!!!!!!!!!!!!!");
    		Log.i(TAG, "Upload clicked");
    		Log.i(TAG, "!!!!!!!!!!!!!!!!!!!");
    		try 
    		{
				FileServer.upload();
			} 
    		catch (IOException e) 
    		{
				e.printStackTrace();
			}
    		
    	}
    }
    
    //Abstract class that is an event handler for photo button 
    public class PhotoButtonClickHandler implements View.OnClickListener  
    {  
    	//what to do when button is clicked
        public void onClick( View view ){  
            startCameraActivity();  //starts the camera activity
        }  
    }  

    //Starts camera intent
    protected void startCameraActivity()  
    {  
        File file = new File( _file );  //creates a file path from _file string
        Uri outputFileUri = Uri.fromFile( file ); //creates uri for file
  
        //Start intent
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );  
        intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );//output file here
  
        //Start Activity and wait for result
        startActivityForResult( intent, 1 ); //activity for result is used b/c we need to know when the camera exits  
    } 
    
    @Override  //abstract function which runs with result of activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
    	Log.i(TAG, "==================================");
        Log.i( "Airpic", "resultCode: " + resultCode );
        Log.i(TAG, "==================================");
        switch( resultCode )  
        {  
            case 0:  //program exited without returning image
            	Log.i(TAG, "==================================");
                Log.i( "Airpic", "User cancelled" );  
                Log.i(TAG, "==================================");
                break;  
      
            default:
                onPhotoTaken();  
                break;  
        }  
    } 
    
    protected void onPhotoTaken()  
    {  
        _taken = true;  //set that a photo has been taken
      
        //setup some options to display image in program of pic we just took
        BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inSampleSize = 2; //reduce size of image
      
        //Decode image and set it to the image display
        Bitmap bitmap = BitmapFactory.decodeFile( _file, options );  
        _image.setImageBitmap(bitmap);  
      
      //get rid of _field so it doesn't take any layout space
        _field.setVisibility( View.GONE );  
    }  
    
    @Override  //save instance state during rotation
    protected void onSaveInstanceState( Bundle outState ) 
    {  
        outState.putBoolean( Airpic.PHOTO_TAKEN, _taken );  
    }  
    
    @Override  
    protected void onRestoreInstanceState( Bundle savedInstanceState)
    {
    	Log.i(TAG, "=============================");
        Log.i( "Airpic", "onRestoreInstanceState()");
    	Log.i(TAG, "=============================");
        if( savedInstanceState.getBoolean( Airpic.PHOTO_TAKEN ) ) 
        {  
            onPhotoTaken();  
        }  
    }
        
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if(viewingSettings)
        	{
        		viewingSettings=false;
        		setContentView(R.layout.camera);
        	}
        	else
        		this.finish();
        		
        }
        return false;
    }
      
}

