package com.example.mplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=findViewById(R.id.listView);


        runTimePerm();
    }

    public  void runTimePerm(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                         permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public ArrayList<File> getSongs(File file){
         ArrayList<File> arrayList=new ArrayList<>();
         File[] files=file.listFiles();
         for (File singleFile:files){
             if(singleFile.isDirectory() && !singleFile.isHidden()){
                 arrayList.addAll(getSongs(singleFile));
             }else{
                 if(singleFile.getName().endsWith(".mp3")){
                     arrayList.add(singleFile);
                 }
             }
         }
         return arrayList;
    }
    public void displaySongs(){
        final ArrayList<File> mySongs=getSongs(Environment.getExternalStorageDirectory());
        items=new String[mySongs.size()];
        for (int i=0;i<mySongs.size();i++){
            items[i]=mySongs.get(i).getName().toString().replace(".mp3","");
        }
        customAdapter customAdapter=new customAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            String songName= (String) listView.getItemAtPosition(i);
            Intent intent=new Intent(this,PlayerActivity.class);
            intent.putExtra("mySongs",mySongs);
            intent.putExtra("songName",songName);
            intent.putExtra("pos",i);
            startActivity(intent);
        });
    }

    class customAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view11= getLayoutInflater().inflate(R.layout.list_items,null);
            TextView txtSong=view11.findViewById(R.id.textSong);
            txtSong.setSelected(true);
            txtSong.setText(items[i]);
            return view11;
        }
    }
}