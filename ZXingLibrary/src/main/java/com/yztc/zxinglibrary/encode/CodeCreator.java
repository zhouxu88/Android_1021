package com.yztc.zxinglibrary.encode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class CodeCreator {

    /**
     * 生成QRCode（二维码）,中间没有logo图标
     *
     * @param
     * @return
     * @throws WriterException
     */
    public static Bitmap createQRCode(String url) throws WriterException {

        if (url == null || url.equals("")) {
            return null;
        }

        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放
        // ,这样会模糊导致识别失败
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //二维码汉子编码
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter().encode(url,
                BarcodeFormat.QR_CODE, 300, 300, hints);

        int width = matrix.getWidth();
        int height = matrix.getHeight();

        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


    // 图片宽度的一般
    private static int IMAGE_WIDTH;
    private static int IMAGE_HEIGHT;
    private static int IMAGE_HALF_WIDTH;
    private static int FRAME_WIDTH = 2;

    /**
     * 生成QRCode（二维码）,中间有logo图标的
     *
     * @param
     * @return
     * @throws WriterException
     */
    public static Bitmap createQRCodeWithIcon(String url, Bitmap bitmapIcon) throws WriterException {

        if (url == null || url.equals("")) {
            return null;
        }
        bitmapIcon = getBitmapScale(bitmapIcon);

        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放
        // ,这样会模糊导致识别失败
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter().encode(url,
                BarcodeFormat.QR_CODE, 300, 300, hints);

        int width = matrix.getWidth();
        int height = matrix.getHeight();

        // 二维矩阵转为一维像素数组
        int halfW = matrix.getWidth() / 2;
        int halfH = matrix.getHeight() / 2;

        // 读取源图像
        IMAGE_WIDTH = bitmapIcon.getWidth();
        IMAGE_HEIGHT = bitmapIcon.getHeight();
        IMAGE_HALF_WIDTH = IMAGE_WIDTH / 2;

        int[][] srcPixels = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
        for (int i = 0; i < bitmapIcon.getWidth(); i++) {
            for (int j = 0; j < bitmapIcon.getHeight(); j++) {
                srcPixels[i][j] = bitmapIcon.getPixel(i, j);
            }
        }

        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 读取图片
                if (x > halfW - IMAGE_HALF_WIDTH
                        && x < halfW + IMAGE_HALF_WIDTH
                        && y > halfH - IMAGE_HALF_WIDTH
                        && y < halfH + IMAGE_HALF_WIDTH) {
                    pixels[y * width + x] = srcPixels[x - halfW
                            + IMAGE_HALF_WIDTH][y - halfH + IMAGE_HALF_WIDTH];
                }
                // 在图片四周形成边框
                else if ((x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH
                        && x < halfW - IMAGE_HALF_WIDTH + FRAME_WIDTH
                        && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                        + IMAGE_HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW + IMAGE_HALF_WIDTH - FRAME_WIDTH
                        && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH
                        && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                        + IMAGE_HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH
                        && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH
                        && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                        - IMAGE_HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH
                        && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH
                        && y > halfH + IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                        + IMAGE_HALF_WIDTH + FRAME_WIDTH)) {
                    pixels[y * width + x] = 0xfffffff;
                } else {
                    // 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
                    pixels[y * width + x] = matrix.get(x, y) ? 0xff000000
                            : 0xfffffff;
                }

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


    //缩放ICON大小,防止太大,影响识别
    private static Bitmap getBitmapScale(Bitmap icon) {
        int width = 0;
        int height = 0;
        int sampleSize = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        height = icon.getHeight();
        width = icon.getWidth();
        while ((height / sampleSize > 50)
                || (width / sampleSize > 50)) {
            sampleSize++;
        }
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.PNG, 100, out);
        byte[] bytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }


    public static String getERCode(Activity context) {
        //获取当前屏幕的大小
        int width = context.getWindow().getDecorView().getRootView().getWidth();
        int height = context.getWindow().getDecorView().getRootView().getHeight();
        //生成相同大小的图片
        Bitmap temBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //找到当前页面的根布局
        View view = context.getWindow().getDecorView().getRootView();
        //设置缓存
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //从缓存中获取当前屏幕的图片
        temBitmap = view.getDrawingCache();

        Result data = getBitmapData(temBitmap);

        view.destroyDrawingCache();//释放缓存占用的资源
        if (data != null) {
            return data.getText();
        } else {
            return "解析失败";
        }
    }


    /**
     * 解析 Bitmap  获取其中数据
     *
     * @param bitmap
     * @return
     */
    private static Result getBitmapData(Bitmap bitmap) {

        //解析转换类型UTF-8
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

        int image_width = bitmap.getWidth();
        int image_height = bitmap.getHeight();

        int[] pixels = new int[image_width * image_height];
        bitmap.getPixels(pixels, 0, image_width, 0, 0, image_width, image_height);

        //新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(image_width, image_height, pixels);
        //将图片转换成二进制图片
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        //初始化解析对象
        QRCodeReader reader = new QRCodeReader();

        //开始解析
        Result result = null;
        try {
            result = reader.decode(binaryBitmap, hints);
        } catch (Exception e) {
        }
        return result;
    }


    //=============================================
    //
    //   截图保存图片到本地,再分析图片
    //
    //
    //=============================================

    /**
     * 截屏
     * 这种方法状态栏是空白，显示不了状态栏的信息
     *
     * @param context
     * @return
     */
    private static String saveCurrentImage(Activity context) {
        //获取当前屏幕的大小
        int width = context.getWindow().getDecorView().getRootView().getWidth();
        int height = context.getWindow().getDecorView().getRootView().getHeight();
        //生成相同大小的图片
        Bitmap temBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //找到当前页面的根布局
        View view = context.getWindow().getDecorView().getRootView();
        //设置缓存
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //从缓存中获取当前屏幕的图片
        temBitmap = view.getDrawingCache();
        SimpleDateFormat df = new SimpleDateFormat("yyyymmddhhmmss");
        String time = df.format(new Date());

        return saveBitmap(context, view, temBitmap, time);
    }

    /**
     * 保存 图片
     *
     * @param context
     * @param temBitmap
     * @param time
     * @return
     */
    private static String saveBitmap(Context context, View view, Bitmap temBitmap, String time) {

        File cache = new File(context.getCacheDir(), "image");
        if (!cache.exists()) {
            cache.mkdirs();
        }
        File file = new File(cache, System.currentTimeMillis() + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            temBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            view.destroyDrawingCache();//释放缓存占用的资源
            return file.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 识别二维码  带本地缓存
     * 1. 屏幕截图
     * 2. 保存到本地缓存
     * 3. 从缓存读取Bitmap
     * 4. 识别二维码
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static String getERCodeWithLocal(Activity context) {
        String bitmapPath = saveCurrentImage(context);
//      图片二次加载
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options);
//        options.inSampleSize = options.outHeight / 400;
//        if (options.inSampleSize <= 0) {
//            options.inSampleSize = 1; //防止其值小于或等于0
//        }
//        options.inJustDecodeBounds = false;
//        bitmap = BitmapFactory.decodeFile(bitmapPath, options);


        Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath);
        Result data = getBitmapData(bitmap);
        if (data != null) {
            return data.getText();
        } else {
            return "解析失败";
        }

    }


}
