package dev.utils.app.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;

import dev.DevUtils;

/**
 * detail: 图片相关工具类
 * @author Ttt
 */
public final class ImageUtils {

    private ImageUtils() {
    }

    // 日志 TAG
    private static final String TAG = ImageUtils.class.getSimpleName();

    // =

    /**
     * 获取 bitmap
     * @param file
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return
     */
    public static Bitmap getBitmap(final File file, final int maxWidth, final int maxHeight) {
        if (file == null) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    /**
     * 获取 bitmap
     * @param filePath
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return
     */
    public static Bitmap getBitmap(final String filePath, final int maxWidth, final int maxHeight) {
        if (isSpace(filePath)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 获取 bitmap
     * @param resId     资源 id
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return
     */
    public static Bitmap getBitmap(@DrawableRes final int resId, final int maxWidth, final int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        final Resources resources = DevUtils.getContext().getResources();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    /**
     * 获取 bitmap
     * @param fd        文件描述
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return
     */
    public static Bitmap getBitmap(final FileDescriptor fd, final int maxWidth, final int maxHeight) {
        if (fd == null) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }


    /**
     * 缩放图片
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @return 缩放后的图片
     */
    public static Bitmap scale(final Bitmap src, final int newWidth, final int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    /**
     * 缩放图片
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @param recycle   是否回收
     * @return 缩放后的图片
     */
    public static Bitmap scale(final Bitmap src, final int newWidth, final int newHeight, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 缩放图片
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @return 缩放后的图片
     */
    public static Bitmap scale(final Bitmap src, final float scaleWidth, final float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }

    /**
     * 缩放图片
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @param recycle     是否回收
     * @return 缩放后的图片
     */
    public static Bitmap scale(final Bitmap src, final float scaleWidth, final float scaleHeight, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 裁剪图片
     * @param src    源图片
     * @param x      开始坐标 x
     * @param y      开始坐标 y
     * @param width  裁剪宽度
     * @param height 裁剪高度
     * @return 裁剪后的图片
     */
    public static Bitmap clip(final Bitmap src, final int x, final int y, final int width, final int height) {
        return clip(src, x, y, width, height, false);
    }

    /**
     * 裁剪图片
     * @param src     源图片
     * @param x       开始坐标 x
     * @param y       开始坐标 y
     * @param width   裁剪宽度
     * @param height  裁剪高度
     * @param recycle 是否回收
     * @return 裁剪后的图片
     */
    public static Bitmap clip(final Bitmap src, final int x, final int y, final int width, final int height, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = Bitmap.createBitmap(src, x, y, width, height);
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 倾斜图片
     * @param src 源图片
     * @param kx  倾斜因子 x
     * @param ky  倾斜因子 y
     * @return 倾斜后的图片
     */
    public static Bitmap skew(final Bitmap src, final float kx, final float ky) {
        return skew(src, kx, ky, 0, 0, false);
    }

    /**
     * 倾斜图片
     * @param src     源图片
     * @param kx      倾斜因子 x
     * @param ky      倾斜因子 y
     * @param recycle 是否回收
     * @return 倾斜后的图片
     */
    public static Bitmap skew(final Bitmap src, final float kx, final float ky, final boolean recycle) {
        return skew(src, kx, ky, 0, 0, recycle);
    }

    /**
     * 倾斜图片
     * @param src 源图片
     * @param kx  倾斜因子 x
     * @param ky  倾斜因子 y
     * @param px  平移因子 x
     * @param py  平移因子 y
     * @return 倾斜后的图片
     */
    public static Bitmap skew(final Bitmap src, final float kx, final float ky, final float px, final float py) {
        return skew(src, kx, ky, px, py, false);
    }

    /**
     * 倾斜图片
     * @param src     源图片
     * @param kx      倾斜因子 x
     * @param ky      倾斜因子 y
     * @param px      平移因子 x
     * @param py      平移因子 y
     * @param recycle 是否回收
     * @return 倾斜后的图片
     */
    public static Bitmap skew(final Bitmap src, final float kx, final float ky, final float px, final float py, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Matrix matrix = new Matrix();
        matrix.setSkew(kx, ky, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 转为圆形图片
     * @param src 源图片
     * @return 圆形图片
     */
    public static Bitmap toRound(final Bitmap src) {
        return toRound(src, 0, 0, false);
    }

    /**
     * 转为圆形图片
     * @param src     源图片
     * @param recycle 是否回收
     * @return 圆形图片
     */
    public static Bitmap toRound(final Bitmap src, final boolean recycle) {
        return toRound(src, 0, 0, recycle);
    }

    /**
     * 转为圆形图片
     * @param src         源图片
     * @param borderSize  边框尺寸
     * @param borderColor 边框颜色
     * @return 圆形图片
     */
    public static Bitmap toRound(final Bitmap src, @IntRange(from = 0) final int borderSize, @ColorInt final int borderColor) {
        return toRound(src, borderSize, borderColor, false);
    }

    /**
     * 转为圆形图片
     * @param src         源图片
     * @param borderSize  边框尺寸
     * @param borderColor 边框颜色
     * @param recycle     是否回收
     * @return 圆形图片
     */
    public static Bitmap toRound(final Bitmap src, @IntRange(from = 0) final int borderSize, @ColorInt final int borderColor, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        int size = Math.min(width, height);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        float center = size / 2f;
        RectF rectF = new RectF(0, 0, width, height);
        rectF.inset((width - size) / 2f, (height - size) / 2f);
        Matrix matrix = new Matrix();
        matrix.setTranslate(rectF.left, rectF.top);
        if (width != height) {
            matrix.preScale((float) size / width, (float) size / height);
        }
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        canvas.drawRoundRect(rectF, center, center, paint);
        if (borderSize > 0) {
            paint.setShader(null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            float radius = center - borderSize / 2f;
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 转为圆角图片
     * @param src    源图片
     * @param radius 圆角的度数
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(final Bitmap src, final float radius) {
        return toRoundCorner(src, radius, 0, 0, false);
    }

    /**
     * 转为圆角图片
     * @param src     源图片
     * @param radius  圆角的度数
     * @param recycle 是否回收
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(final Bitmap src, final float radius, final boolean recycle) {
        return toRoundCorner(src, radius, 0, 0, recycle);
    }

    /**
     * 转为圆角图片
     * @param src         源图片
     * @param radius      圆角的度数
     * @param borderSize  边框尺寸
     * @param borderColor 边框颜色
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(final Bitmap src, final float radius, @IntRange(from = 0) final int borderSize, @ColorInt final int borderColor) {
        return toRoundCorner(src, radius, borderSize, borderColor, false);
    }

    /**
     * 转为圆角图片
     * @param src         源图片
     * @param radius      圆角的度数
     * @param borderSize  边框尺寸
     * @param borderColor 边框颜色
     * @param recycle     是否回收
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(final Bitmap src, final float radius, @IntRange(from = 0) final int borderSize, @ColorInt final int borderColor, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        RectF rectF = new RectF(0, 0, width, height);
        float halfBorderSize = borderSize / 2f;
        rectF.inset(halfBorderSize, halfBorderSize);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        if (borderSize > 0) {
            paint.setShader(null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawRoundRect(rectF, radius, radius, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 添加圆角边框
     * @param src          源图片
     * @param borderSize   边框尺寸
     * @param color        边框颜色
     * @param cornerRadius 圆角半径
     * @return 圆角边框图
     */
    public static Bitmap addCornerBorder(final Bitmap src, @IntRange(from = 1) final int borderSize, @ColorInt final int color, @FloatRange(from = 0) final float cornerRadius) {
        return addBorder(src, borderSize, color, false, cornerRadius, false);
    }

    /**
     * 添加圆角边框
     * @param src          源图片
     * @param borderSize   边框尺寸
     * @param color        边框颜色
     * @param cornerRadius 圆角半径
     * @param recycle      是否回收
     * @return 圆角边框图
     */
    public static Bitmap addCornerBorder(final Bitmap src, @IntRange(from = 1) final int borderSize, @ColorInt final int color, @FloatRange(from = 0) final float cornerRadius, final boolean recycle) {
        return addBorder(src, borderSize, color, false, cornerRadius, recycle);
    }

    /**
     * 添加圆形边框
     * @param src        源图片
     * @param borderSize 边框尺寸
     * @param color      边框颜色
     * @return 圆形边框图
     */
    public static Bitmap addCircleBorder(final Bitmap src, @IntRange(from = 1) final int borderSize, @ColorInt final int color) {
        return addBorder(src, borderSize, color, true, 0, false);
    }

    /**
     * 添加圆形边框
     * @param src        源图片
     * @param borderSize 边框尺寸
     * @param color      边框颜色
     * @param recycle    是否回收
     * @return 圆形边框图
     */
    public static Bitmap addCircleBorder(final Bitmap src, @IntRange(from = 1) final int borderSize, @ColorInt final int color, final boolean recycle) {
        return addBorder(src, borderSize, color, true, 0, recycle);
    }

    /**
     * 添加边框
     * @param src          源图片
     * @param borderSize   边框尺寸
     * @param color        边框颜色
     * @param isCircle     是否画圆
     * @param cornerRadius 圆角半径
     * @param recycle      是否回收
     * @return 边框图
     */
    private static Bitmap addBorder(final Bitmap src, @IntRange(from = 1) final int borderSize, @ColorInt final int color,
                                    final boolean isCircle, final float cornerRadius, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        int width = ret.getWidth();
        int height = ret.getHeight();
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderSize);
        if (isCircle) {
            float radius = Math.min(width, height) / 2f - borderSize / 2f;
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        } else {
            int halfBorderSize = borderSize >> 1;
            RectF rectF = new RectF(halfBorderSize, halfBorderSize, width - halfBorderSize, height - halfBorderSize);
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        }
        return ret;
    }

    /**
     * 添加倒影
     * @param src              源图片的
     * @param reflectionHeight 倒影高度
     * @return 带倒影图片
     */
    public static Bitmap addReflection(final Bitmap src, final int reflectionHeight) {
        return addReflection(src, reflectionHeight, false);
    }

    /**
     * 添加倒影
     * @param src              源图片的
     * @param reflectionHeight 倒影高度
     * @param recycle          是否回收
     * @return 带倒影图片
     */
    public static Bitmap addReflection(final Bitmap src, final int reflectionHeight, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        // 原图与倒影之间的间距
        final int REFLECTION_GAP = 0;
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionBitmap = Bitmap.createBitmap(src, 0, srcHeight - reflectionHeight, srcWidth, reflectionHeight, matrix, false);
        Bitmap ret = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight, src.getConfig());
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.drawBitmap(reflectionBitmap, 0, srcHeight + REFLECTION_GAP, null);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient shader = new LinearGradient(0, srcHeight, 0, ret.getHeight() + REFLECTION_GAP, 0x70FFFFFF, 0x00FFFFFF, Shader.TileMode.MIRROR);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, srcHeight + REFLECTION_GAP, srcWidth, ret.getHeight(), paint);
        if (!reflectionBitmap.isRecycled()) reflectionBitmap.recycle();
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 添加文字水印
     * @param src      源图片
     * @param content  水印文本
     * @param textSize 水印字体大小
     * @param color    水印字体颜色
     * @param x        起始坐标 x
     * @param y        起始坐标 y
     * @return 带有文字水印的图片
     */
    public static Bitmap addTextWatermark(final Bitmap src, final String content, final int textSize,
                                          @ColorInt final int color, final float x, final float y) {
        return addTextWatermark(src, content, textSize, color, x, y, false);
    }

    /**
     * 添加文字水印
     * @param src      源图片
     * @param content  水印文本
     * @param textSize 水印字体大小
     * @param color    水印字体颜色
     * @param x        起始坐标 x
     * @param y        起始坐标 y
     * @param recycle  是否回收
     * @return 带有文字水印的图片
     */
    public static Bitmap addTextWatermark(final Bitmap src, final String content, final float textSize,
                                          @ColorInt final int color, final float x, final float y, final boolean recycle) {
        if (isEmptyBitmap(src) || content == null) return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(ret);
        paint.setColor(color);
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);
        canvas.drawText(content, x, y + textSize, paint);
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 添加图片水印
     * @param src       源图片
     * @param watermark 图片水印
     * @param x         起始坐标 x
     * @param y         起始坐标 y
     * @param alpha     透明度
     * @return 带有图片水印的图片
     */
    public static Bitmap addImageWatermark(final Bitmap src, final Bitmap watermark, final int x, final int y, final int alpha) {
        return addImageWatermark(src, watermark, x, y, alpha, false);
    }

    /**
     * 添加图片水印
     * @param src       源图片
     * @param watermark 图片水印
     * @param x         起始坐标 x
     * @param y         起始坐标 y
     * @param alpha     透明度
     * @param recycle   是否回收
     * @return 带有图片水印的图片
     */
    public static Bitmap addImageWatermark(final Bitmap src, final Bitmap watermark, final int x, final int y, final int alpha, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        if (!isEmptyBitmap(watermark)) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Canvas canvas = new Canvas(ret);
            paint.setAlpha(alpha);
            canvas.drawBitmap(watermark, x, y, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 判断 bitmap 对象是否为空
     * @param src 源图片
     * @return {@code true} yes, {@code false} no
     */
    private static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    // = 压缩有关 =

    /**
     * 按缩放压缩
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @return 缩放压缩后的图片
     */
    public static Bitmap compressByScale(final Bitmap src, final int newWidth, final int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    /**
     * 按缩放压缩
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @param recycle   是否回收
     * @return 缩放压缩后的图片
     */
    public static Bitmap compressByScale(final Bitmap src, final int newWidth, final int newHeight, final boolean recycle) {
        return scale(src, newWidth, newHeight, recycle);
    }

    /**
     * 按缩放压缩
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @return 缩放压缩后的图片
     */
    public static Bitmap compressByScale(final Bitmap src, final float scaleWidth, final float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }

    /**
     * 按缩放压缩
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @param recycle     是否回收
     * @return 缩放压缩后的图片
     */
    public static Bitmap compressByScale(final Bitmap src, final float scaleWidth, final float scaleHeight, final boolean recycle) {
        return scale(src, scaleWidth, scaleHeight, recycle);
    }

    /**
     * 按质量压缩
     * @param src     源图片
     * @param quality 质量
     * @return 质量压缩后的图片
     */
    public static Bitmap compressByQuality(final Bitmap src, @IntRange(from = 0, to = 100) final int quality) {
        return compressByQuality(src, quality, false);
    }

    /**
     * 按质量压缩
     * @param src     源图片
     * @param quality 质量
     * @param recycle 是否回收
     * @return 质量压缩后的图片
     */
    public static Bitmap compressByQuality(final Bitmap src, @IntRange(from = 0, to = 100) final int quality, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 按质量压缩
     * @param src         源图片
     * @param maxByteSize 允许最大值字节数
     * @return 质量压缩压缩过的图片
     */
    public static Bitmap compressByQuality(final Bitmap src, final long maxByteSize) {
        return compressByQuality(src, maxByteSize, false);
    }

    /**
     * 按质量压缩
     * @param src         源图片
     * @param maxByteSize 允许最大值字节数
     * @param recycle     是否回收
     * @return 质量压缩压缩过的图片
     */
    public static Bitmap compressByQuality(final Bitmap src, final long maxByteSize, final boolean recycle) {
        if (isEmptyBitmap(src) || maxByteSize <= 0) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, 100, baos);
        byte[] bytes;
        if (baos.size() <= maxByteSize) { // 最好质量的不大于最大字节, 则返回最佳质量
            bytes = baos.toByteArray();
        } else {
            baos.reset();
            src.compress(CompressFormat.JPEG, 0, baos);
            if (baos.size() >= maxByteSize) { // 最差质量不小于最大字节, 则返回最差质量
                bytes = baos.toByteArray();
            } else {
                // 二分法寻找最佳质量
                int st = 0;
                int end = 100;
                int mid = 0;
                while (st < end) {
                    mid = (st + end) / 2;
                    baos.reset();
                    src.compress(CompressFormat.JPEG, mid, baos);
                    int len = baos.size();
                    if (len == maxByteSize) {
                        break;
                    } else if (len > maxByteSize) {
                        end = mid - 1;
                    } else {
                        st = mid + 1;
                    }
                }
                if (end == mid - 1) {
                    baos.reset();
                    src.compress(CompressFormat.JPEG, st, baos);
                }
                bytes = baos.toByteArray();
            }
        }
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 按采样大小压缩
     * @param src        源图片
     * @param sampleSize 采样率大小
     * @return 按采样率压缩后的图片
     */

    public static Bitmap compressBySampleSize(final Bitmap src, final int sampleSize) {
        return compressBySampleSize(src, sampleSize, false);
    }

    /**
     * 按采样大小压缩
     * @param src        源图片
     * @param sampleSize 采样率大小
     * @param recycle    是否回收
     * @return 按采样率压缩后的图片
     */
    public static Bitmap compressBySampleSize(final Bitmap src, final int sampleSize, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 按采样大小压缩
     * @param src       源图片
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return 按采样率压缩后的图片
     */
    public static Bitmap compressBySampleSize(final Bitmap src, final int maxWidth, final int maxHeight) {
        return compressBySampleSize(src, maxWidth, maxHeight, false);
    }

    /**
     * 按采样大小压缩
     * @param src       源图片
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @param recycle   是否回收
     * @return 按采样率压缩后的图片
     */
    public static Bitmap compressBySampleSize(final Bitmap src, final int maxWidth, final int maxHeight, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 计算采样大小
     * @param options   选项
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return 采样大小
     */
    private static int calculateInSampleSize(final BitmapFactory.Options options, final int maxWidth, final int maxHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while ((width >>= 1) >= maxWidth && (height >>= 1) >= maxHeight) {
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }

    // =

    /**
     * 判断字符串是否为 null 或全为空白字符
     * @param str 待校验字符串
     * @return {@code true} yes, {@code false} no
     */
    private static boolean isSpace(final String str) {
        if (str == null) return true;
        for (int i = 0, len = str.length(); i < len; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // =

    /**
     * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
     * @param options
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static int caculateInSampleSize(final BitmapFactory.Options options, final int targetWidth, final int targetHeight) {
        if (options == null) return 0;

        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = 1;
        if (width > targetWidth || height > targetHeight) {
            int widthRadio = Math.round(width * 1.0f / targetWidth);
            int heightRadio = Math.round(height * 1.0f / targetHeight);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }
}