package me.paulqpro.filterapp.activities.windows;

import static android.app.Activity.RESULT_OK;
import static me.paulqpro.filterapp.misc.MainActivityRequests.*;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import me.paulqpro.filterapp.misc.BitmapImageHandler;
import me.paulqpro.filterapp.R;

public class WindowMain extends Fragment {//main screen
    Activity activity;
    Uri image;
    Bitmap bitmap;
    Context context = this.getContext();

    View.OnClickListener
            openClickListener = (View view) -> {
                int cl = Color.WHITE;
                try {
                    FileInputStream is = new FileInputStream(new File(activity.getApplicationInfo().dataDir, "colors.tmp"));//if color is chosen, load color, otherwise load white
                    int
                            r = is.read(),
                            g = is.read(),
                            b = is.read();
                    cl = Color.rgb(r,g,b);
                    is.close();
                } catch (IOException e) { }
                WindowMain.OneColorFilterTask filter = new WindowMain.OneColorFilterTask(activity);
                filter.execute(cl);
            },
            saveClickListener = (View view) -> {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/png", "image/jpeg"});
                startActivityForResult(intent, SAVE_FILE);//Built-in save file dialogue
            };

    void initBtns(){
        Button btn1 = activity.findViewById(R.id.applyButton);
        btn1.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.primary));
        btn1.setOnClickListener(openClickListener);
        Button btn2 = activity.findViewById(R.id.saveButton);
        btn2.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.primary));
        btn2.setOnClickListener(saveClickListener);
    }

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
        return inflater.inflate(R.layout.fragment_window_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bitmap bmp;
        try{
            bmp = BitmapImageHandler.open(Uri.fromFile(new File(activity.getApplicationInfo().dataDir, "result.tmp")),activity,false);
            initBtns();
        } catch (IOException e){
            try{
                bmp = BitmapImageHandler.open(Uri.fromFile(new File(activity.getApplicationInfo().dataDir, "original.tmp")),activity,false);
                initBtns();
            } catch (IOException e1){
                bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            }
        }
        ((ImageView)activity.findViewById(R.id.imageView)).setImageBitmap(bmp);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/png", "image/jpeg"});
        activity.findViewById(R.id.openImageButton).setOnClickListener((View view1) -> {
            Log.d("FLT:WND1","file open dialog");
            startActivityForResult(intent, OPEN_FILE);//Built-in open file dialogue
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode) {
                case OPEN_FILE:
                    Uri image = this.image = data.getData();
                    try {
                        bitmap = BitmapImageHandler.open(image, activity, false);
                        BitmapImageHandler.saveInternal("original.tmp", bitmap, activity);
                        ((ImageView) activity.findViewById(R.id.imageView)).setImageBitmap(bitmap);
                        initBtns();
                        Log.d("FLT:WND1","file opened");
                    } catch (IOException e) {
                        Log.e("FLT:WND1","file not opened. "+e.getMessage());
                        Toast.makeText(activity, "Error opening file", Toast.LENGTH_LONG).show();
                    }
                    break;
                case SAVE_FILE:
                    try {
                        Bitmap bmp;
                        try{
                            bmp = BitmapImageHandler.open(Uri.fromFile(new File(activity.getApplicationInfo().dataDir, "result.tmp")),activity,false);//if filter was applied, load result
                        } catch (IOException e){
                            try{
                                bmp = BitmapImageHandler.open(Uri.fromFile(new File(activity.getApplicationInfo().dataDir, "original.tmp")),activity,false);//otherwise, load original
                            } catch (IOException e1){
                                bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);//if original doesn't exist.
                            }
                        }
                        BitmapImageHandler.saveExternal(data.getData(),bmp,activity);
                        Log.d("FLT:WND1","file saved");
                    } catch (IOException e) {
                        Log.e("FLT:WND1","file not saved. "+e.getMessage());
                        Toast.makeText(activity, "Error saving file", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        } else {
            Log.e("FLT:WND1","file not saved. "+data.getData().toString());
            Toast.makeText(activity, "Error saving file", Toast.LENGTH_LONG).show();
        }
    }

     class OneColorFilterTask extends AsyncTask<Integer, Integer, Void> {
        float R,G,B;

        /**
         *Only first argument is used
         */
        @Override
        protected Void doInBackground(@ColorInt Integer... colors) {//apply 'AND' filter (removes all colors, except specified color. result is almost monochrome image (shades of gray act like dimming)
            R = Color.red(colors[0]) / 255f;
            G = Color.green(colors[0]) / 255f;
            B = Color.blue(colors[0]) / 255f;
            Bitmap bitmap;
            ProgressBar pb = activity.findViewById(me.paulqpro.filterapp.R.id.imageLoadBar);
            try{
                bitmap = BitmapImageHandler.open(Uri.fromFile(new File(context.getApplicationInfo().dataDir, "original.tmp")), context, true);
                int w = bitmap.getWidth(), h = bitmap.getHeight();
                int[] cls = new int[w*h];
                bitmap.getPixels(cls,0,w,0,0,w,h);//operations with an array are slightly faster then operations with bitmap
                pb.setMax(w*h);
                for (int i = 0; i < w * h; i++) {
                    float r, g, b;
                    int cl = cls[i];
                    r = Color.red(cl) / 255f;
                    g = Color.green(cl) / 255f;
                    b = Color.blue(cl) / 255f;
                    r *= R;
                    g *= G;
                    b *= B;
                    cls[i] = Color.argb(1f, r, g, b);
                    pb.setProgress(pb.getProgress() + 1);
                }
                bitmap.setPixels(cls,0,w,0,0,w,h);
                BitmapImageHandler.saveInternal("result.tmp", bitmap, context);//save result
            } catch (IOException e){
                this.cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){//set image, when complete
            try {
                ((ImageView) activity.findViewById(me.paulqpro.filterapp.R.id.imageView)).setImageBitmap(BitmapImageHandler.open(Uri.fromFile(new File(context.getApplicationInfo().dataDir, "result.tmp")), context, false));
            } catch (IOException e){

            }
        }

        public OneColorFilterTask(Activity activity){
            context = activity;
        }
    }
}