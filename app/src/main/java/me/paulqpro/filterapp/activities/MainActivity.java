package me.paulqpro.filterapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import me.paulqpro.filterapp.activities.windows.WindowFilterSettings;
import me.paulqpro.filterapp.activities.windows.WindowMain;
import me.paulqpro.filterapp.R;

public class MainActivity extends AppCompatActivity {
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
        findViewById(R.id.toMainScreenBtn).setOnClickListener((View view) -> {//Main screen button
            FragmentTransaction ftr1 = fman.beginTransaction();
            ftr1.replace(R.id.screen, mainW);
            ViewGroup tabRow = findViewById(R.id.tabRow);
            for(int i = 0; i < tabRow.getChildCount(); i++){
                tabRow.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.secondary));
            }
            findViewById(R.id.toMainScreenBtn).setBackgroundColor(getResources().getColor(R.color.tertiary));
            ftr1.commit();
        });
        findViewById(R.id.toFilterSettingsScreenBtn).setOnClickListener((View view) -> {//Filter settings screen button
            FragmentTransaction ftr2 = fman.beginTransaction();
            ftr2.replace(R.id.screen, filterSW);
            ViewGroup tabRow = findViewById(R.id.tabRow);
            for(int i = 0; i < tabRow.getChildCount(); i++){
                tabRow.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.secondary));
            }
            findViewById(R.id.toFilterSettingsScreenBtn).setBackgroundColor(getResources().getColor(R.color.tertiary));
            ftr2.commit();
        });
        /*findViewById(R.id.toAppSettingsScreenBtn).setOnClickListener((View view) -> {//unused. for future
            FragmentTransaction ftr3 = fman.beginTransaction();
            ftr3.replace(R.id.screen, settingsW);
            ViewGroup tabRow = findViewById(R.id.tabRow);
            for(int i = 0; i < tabRow.getChildCount(); i++){
                tabRow.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.secondary));
            }
            findViewById(R.id.toAppSettingsScreenBtn).setBackgroundColor(getResources().getColor(R.color.tertiary));
            ftr3.commit();
        });*/
    }


}