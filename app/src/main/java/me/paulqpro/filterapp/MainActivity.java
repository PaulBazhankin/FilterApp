package me.paulqpro.filterapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Uri image;
    Bitmap bitmap;
    Fragment mainW, filterSW, settingsW;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        File
                or = new File(getApplicationInfo().dataDir, "original.tmp"),
                res = new File(getApplicationInfo().dataDir, "result.tmp"),
                cl = new File(getApplicationInfo().dataDir, "colors.tmp");
        if(or.exists()) or.delete();
        if(res.exists()) res.delete();
        if(cl.exists()) cl.delete();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainW = new WindowMain();
        filterSW = new WindowFilterSettings();
        FragmentManager fman = getFragmentManager();
        FragmentTransaction ftr = fman.beginTransaction();
        ftr.add(R.id.screen, mainW);
        ftr.commit();
        findViewById(R.id.toMainScreenBtn).setOnClickListener((View view) -> {
            FragmentTransaction ftr1 = fman.beginTransaction();
            ftr1.replace(R.id.screen, mainW);
            ViewGroup tabRow = findViewById(R.id.tabRow);
            for(int i = 0; i < tabRow.getChildCount(); i++){
                tabRow.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.secondary));
            }
            findViewById(R.id.toMainScreenBtn).setBackgroundColor(getResources().getColor(R.color.tertiary));
            ftr1.commit();
        });
        findViewById(R.id.toFilterSettingsScreenBtn).setOnClickListener((View view) -> {
            FragmentTransaction ftr2 = fman.beginTransaction();
            ftr2.replace(R.id.screen, filterSW);
            ViewGroup tabRow = findViewById(R.id.tabRow);
            for(int i = 0; i < tabRow.getChildCount(); i++){
                tabRow.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.secondary));
            }
            findViewById(R.id.toFilterSettingsScreenBtn).setBackgroundColor(getResources().getColor(R.color.tertiary));
            ftr2.commit();
        });
        findViewById(R.id.toAppSettingsScreenBtn).setOnClickListener((View view) -> {
            FragmentTransaction ftr3 = fman.beginTransaction();
            ftr3.replace(R.id.screen, settingsW);
            ViewGroup tabRow = findViewById(R.id.tabRow);
            for(int i = 0; i < tabRow.getChildCount(); i++){
                tabRow.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.secondary));
            }
            findViewById(R.id.toAppSettingsScreenBtn).setBackgroundColor(getResources().getColor(R.color.tertiary));
            ftr3.commit();
        });
    }public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("FLT:WND1","file opened");
        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri image = this.image = data.getData();
            try{
                bitmap = BitmapImageHandler.open(image,this,false);
                BitmapImageHandler.saveInternal("original.tmp", bitmap, this);
                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);
                Button btn = findViewById(R.id.applyButton);
                btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary));
                btn.setOnClickListener((View view) ->{
                    WindowMain.OneColorFilterTask filter = new WindowMain.OneColorFilterTask(this);
                    filter.execute(Color.RED);
                });
            } catch (IOException e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


}