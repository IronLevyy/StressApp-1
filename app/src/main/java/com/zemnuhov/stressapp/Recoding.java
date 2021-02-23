package com.zemnuhov.stressapp;

import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Recoding {

    OutputStreamWriter writer;

    public void writeFile(String fileName,String string){
        StartRecoding(fileName);
        try {
            writer.write(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StopRecoding();

    }


    public void StartRecoding(String fileName){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(GlobalValues.getContext().getExternalFilesDir(null),fileName);
            File fhandle = new File(file.getAbsolutePath());
            if (!fhandle.getParentFile().exists()) {
                fhandle.getParentFile().mkdirs();
            }
            try {
                fhandle.createNewFile();
                writer =new OutputStreamWriter(new FileOutputStream(fhandle,true));
            } catch (IOException e) {
                Toast.makeText(GlobalValues.getContext(),"Поток записи не открыт!",Toast.LENGTH_SHORT);
            }
        }

    }

    public void StopRecoding(){
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
