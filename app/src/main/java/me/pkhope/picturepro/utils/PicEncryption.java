package me.pkhope.picturepro.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;
import ar.com.hjg.pngj.chunks.ChunkHelper;
import ar.com.hjg.pngj.chunks.PngChunk;
import ar.com.hjg.pngj.chunks.PngChunkTextVar;

/**
 * Created by pkhope on 2016/7/26.
 */
public class PicEncryption {

    public static String getOriginPixels(Bitmap source, Bitmap mosaic){

        String result = "";
        int width = mosaic.getWidth();
        int height = mosaic.getHeight();
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                int pixel = mosaic.getPixel(j,i);
                int alpha = Color.alpha(pixel);
                if (alpha == 254){
                    result += " " + source.getPixel(j,i);
//                    source.setPixel(j,i,16711680);
                }
            }
        }

        return result;
    }

    public static void save(String path,Bitmap source, Bitmap mosaic){

        File file = new File(path);

        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(),source.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawBitmap(source,0,0,null);
//        canvas.save();

        String text = getOriginPixels(source,mosaic);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(source,0,0,null);
        canvas.drawBitmap(mosaic,0,0,null);
        canvas.save();

        int index = 0;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                int pixel = mosaic.getPixel(j,i);
                int alpha = Color.alpha(pixel);
                if (alpha == 254){
                    bitmap.setPixel(j,i,pixel);
                    index++;
                }
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//            mosaic.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        bitmap.recycle();

        PngReader pngr = new PngReader(file);
        String tmpFilePath = path.replace(file.getName(),"tmp.png");
        File tmpFile = new File(tmpFilePath);
        PngWriter pngw = new PngWriter(tmpFile, pngr.imgInfo, true);
        pngw.copyChunksFrom(pngr.getChunksList(), ChunkCopyBehaviour.COPY_ALL);
        pngw.getMetadata().setText("data", text);
        for (int row = 0; row < pngr.imgInfo.rows; row++) {
            IImageLine l1 = pngr.readRow();
            pngw.writeRow(l1);
        }
        pngr.end();
        pngw.end();
    }

    public static Bitmap load(String path){

        File file = new File(path);
        PngReader pngr = new PngReader(file);
        pngr.readSkippingAllRows();

//        String text = pngr.getMetadata().getTxtForKey("data");
        String text = getText(pngr);
        pngr.end();
        String [] strPixels = text.split(" ");
        int [] pixels = new int[strPixels.length];
        for (int i = 1; i < strPixels.length; i++){
             pixels[i-1] = Integer.parseInt(strPixels[i]);
        }

        int index = 0;
        Bitmap bitmap = BitmapFactory.decodeFile(path).copy(Bitmap.Config.ARGB_8888,true);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                int pixel = bitmap.getPixel(j,i);
                int alpha = Color.alpha(pixel);
                if (alpha == 254){
                    bitmap.setPixel(j,i,pixels[index++]);
//                    bitmap.setPixel(j,i,Color.RED);
                }
            }
        }

        return bitmap;
    }

    public static String getText(PngReader pngr){
        for (PngChunk c : pngr.getChunksList().getChunks()) {
            if (!ChunkHelper.isText(c)){
                continue;
            }
            PngChunkTextVar ct = (PngChunkTextVar) c;
            String key = ct.getKey();
            if (key.equals("data")){
                return ct.getVal();
            }
        }
        return null;
    }
}
