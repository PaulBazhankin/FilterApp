package me.paulqpro.filterapp.activities.windows;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.paulqpro.filterapp.R;

public class WindowFilterSettings extends Fragment {//settings of filter
    Activity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_window_filter_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int cl;
        try {//if color is chosen, load color, otherwise load white
            FileInputStream is = new FileInputStream(new File(activity.getApplicationInfo().dataDir, "colors.tmp"));
            int
                    r = is.read(),
                    g = is.read(),
                    b = is.read();
            cl = Color.rgb(r,g,b);
            is.close();
            activity.findViewById(R.id.view3).setBackgroundColor(cl);
            ((EditText)activity.findViewById(R.id.rTextNumber)).setText(Integer.toString(r));
            ((EditText)activity.findViewById(R.id.gTextNumber)).setText(Integer.toString(g));
            ((EditText)activity.findViewById(R.id.bTextNumber)).setText(Integer.toString(b));
        } catch (IOException e) { }
        activity.findViewById(R.id.applyColorButton).setOnClickListener((View view1) -> {// when apply button is pressed, save chosen color and display it
            View colorT = activity.findViewById(R.id.view3);
            EditText
                    rT = activity.findViewById(R.id.rTextNumber),
                    gT = activity.findViewById(R.id.gTextNumber),
                    bT = activity.findViewById(R.id.bTextNumber);
            try {
                int
                        r = Integer.parseInt(rT.getText().toString()),
                        g = Integer.parseInt(gT.getText().toString()),
                        b = Integer.parseInt(bT.getText().toString());
                if(r > 255) { rT.setText("255"); r = 255; }
                else if (r < 0) { rT.setText("0"); r = 0; }
                if(g > 255) { gT.setText("255"); g = 255; }
                else if (g < 0) { gT.setText("0"); g = 0; }
                if(b > 255) { bT.setText("255"); b = 255; }
                else if (b < 0) { bT.setText("0"); b = 0;}
                colorT.setBackgroundColor(Color.rgb(r,g,b));
                File f = new File(activity.getApplicationInfo().dataDir, "colors.tmp");
                f.createNewFile();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(f);
                } catch (FileNotFoundException e) { }
                fos.write(r);
                fos.write(g);
                fos.write(b);
                fos.flush();
                fos.close();
            }catch (Exception e){ }
        });
    }
}