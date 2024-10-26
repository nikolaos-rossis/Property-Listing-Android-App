package com.example.dsphase2;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static File saveImage(Context context, byte[] imageBytes, String imageName) {
        // Create a file in the internal storage
        File imageFile = new File(context.getFilesDir(), imageName);

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(imageBytes);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFile;
    }
}
