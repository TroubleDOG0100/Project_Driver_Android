package com.bingostudio.project_driver;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    private Game game;
    private GameView gameView;

    private static final int WRITE_EXTERNAL_DATA_CODE = 2;
    private static final int READ_EXTERNAL_DATA_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_DATA_CODE);
        }

        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_DATA_CODE);
        }

        //Game view is created in the main [Game] constructor.
        game = new Game(this);
        this.gameView = game.getView();
        setContentView(this.gameView);
    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println("Returned to activity.");
    }

    protected void onDestroy(){
        game.stop();
        super.onDestroy();
    }

    @Override
    public Resources getResources() {
        return super.getResources();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int tX = (int) event.getX();
        int tY = (int) event.getY();
        int action = event.getAction();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                game.screenPressed(tX,tY);
                break;
            case MotionEvent.ACTION_UP:
                // Action only fires if one pointer is left.
                game.screenUnPressed();
                break;
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_DATA_CODE:

                break;
            case WRITE_EXTERNAL_DATA_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    System.out.println("Should write: " + grantResults[0] + " / " + permissions[0]);
                }
                break;

            default:
                break;
        }
    }

}
