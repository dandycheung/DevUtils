package dev.capture;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.DevHttpCapture;
import dev.utils.DevFinal;
import dev.utils.LogPrintUtils;
import dev.utils.app.PathUtils;
import dev.utils.common.ConvertUtils;
import dev.utils.common.DateUtils;
import dev.utils.common.DevCommonUtils;
import dev.utils.common.FileUtils;
import dev.utils.common.StringUtils;
import dev.utils.common.encrypt.MD5Utils;
import dev.utils.common.validator.ValidatorUtils;

class Utils {

    private Utils() {
    }

    // ========
    // = Gson =
    // ========

    private static final Gson GSON = createGson(false).create();

    /**
     * 创建 GsonBuilder
     * @param serializeNulls 是否序列化 null 值
     * @return {@link GsonBuilder}
     */
    private static GsonBuilder createGson(final boolean serializeNulls) {
        GsonBuilder builder = new GsonBuilder();
        if (serializeNulls) builder.serializeNulls();
        return builder;
    }

    // ==========
    // = 转换方法 =
    // ==========

    /**
     * 将对象转换为 JSON String
     * @param object {@link Object}
     * @return JSON String
     */
    protected static String toJson(final Object object) {
        if (object == null) return null;
        try {
            return GSON.toJson(object);
        } catch (Exception e) {
            LogPrintUtils.eTag(DevHttpCapture.TAG, e, "toJson");
        }
        return null;
    }

    /**
     * 将 JSON String 映射为指定类型对象
     * @param json     JSON String
     * @param classOfT {@link Class} T
     * @param <T>      泛型
     * @return instance of type
     */
    protected static <T> T fromJson(
            final String json,
            final Class<T> classOfT
    ) {
        if (json == null) return null;
        try {
            return GSON.fromJson(json, classOfT);
        } catch (Exception e) {
            LogPrintUtils.eTag(DevHttpCapture.TAG, e, "fromJson");
        }
        return null;
    }

    // =======
    // = 排序 =
    // =======

    /**
     * 通过文件名进行排序
     * @param files 待排序文件数组
     * @return 排序后的文件名
     */
    private static List<File> sortFileByName(final File[] files) {
        List<File> lists = new ArrayList<>();
        if (files != null) {
            lists.addAll(Arrays.asList(files));
            Collections.sort(lists, (file1, file2) -> {
                int value1 = ConvertUtils.toInt(file1.getName());
                int value2 = ConvertUtils.toInt(file2.getName());
                return Integer.compare(value2, value1);
            });
        }
        return lists;
    }

    // =============
    // = 文件操作相关 =
    // =============

    // Http Capture Storage Path
    private static       String sStoragePath;
    // 文件后缀
    private static final String FILE_EXTENSION      = ".json";
    private static final String DATA_FILE_EXTENSION = "_data.json";

    /**
     * 获取抓包存储路径
     * @return 抓包存储路径
     */
    protected static String getStoragePath() {
        if (TextUtils.isEmpty(sStoragePath)) {
            sStoragePath = PathUtils.getInternal().getAppDataPath(
                    DevHttpCapture.TAG
            );
        }
        return sStoragePath;
    }

    /**
     * 获取指定模块抓包存储路径
     * @param moduleName 模块名 ( 要求唯一性 )
     * @return 指定模块抓包存储路径
     */
    protected static String getModulePath(final String moduleName) {
        return FileUtils.getAbsolutePath(
                FileUtils.getFile(getStoragePath(), moduleName)
        );
    }

    /**
     * 获取指定模块指定抓包存储文件
     * @param modulePath 模块路径
     * @param fileName   抓包存储文件名
     * @return 指定模块指定抓包存储文件
     */
    private static File getModuleHttpCaptureFile(
            final String modulePath,
            final String fileName
    ) {
        return FileUtils.getFile(modulePath, fileName);
    }

    /**
     * 获取指定模块指定抓包存储文件
     * @param captureFile 抓包存储文件
     * @return 指定模块指定抓包存储文件
     */
    protected static File getModuleHttpCaptureFile(
            final CaptureFile captureFile
    ) {
        String modulePath = getModulePath(captureFile.getModuleName());
        String filePath   = getTimeFile(modulePath, captureFile.getTime());
        return getModuleHttpCaptureFile(filePath, captureFile.getFileName());
    }

    /**
     * 获取指定模块指定抓包数据存储文件
     * @param captureFile 抓包存储文件
     * @return 指定模块指定抓包数据存储文件
     */
    protected static File getModuleHttpCaptureDataFile(
            final CaptureFile captureFile
    ) {
        String filePath = StringUtils.replaceEndsWith(
                FileUtils.getAbsolutePath(getModuleHttpCaptureFile(captureFile)),
                FILE_EXTENSION, DATA_FILE_EXTENSION
        );
        return FileUtils.getFile(filePath);
    }

    // =

    /**
     * 获取唯一文件名
     * @param filePath  文件路径 {@link #getModulePath(String)}
     * @param isEncrypt 是否加密数据
     * @return 唯一文件名
     */
    private static String getUniqueFileName(
            final String filePath,
            final boolean isEncrypt
    ) {
        if (TextUtils.isEmpty(filePath)) return null;
        while (true) {
            String md5Value = MD5Utils.md5(DevCommonUtils.getRandomUUIDToString());
            String fileName;
            // 属于加密的文件名前加前缀
            if (isEncrypt) {
                fileName = "encrypt_" + md5Value + FILE_EXTENSION;
            } else {
                fileName = md5Value + FILE_EXTENSION;
            }
            // 文件不存在才返回文件名
            if (!FileUtils.isFileExists(filePath, fileName)) {
                return fileName;
            }
        }
    }

    /**
     * 获取时间间隔文件夹路径
     * @param modulePath 模块名
     * @param millis     创建时间 ( 本地时间戳 )
     * @return 时间间隔文件夹路径
     */
    private static String getTimeFile(
            final String modulePath,
            final long millis
    ) {
        String yyyyMMdd = DateUtils.formatTime(millis, DevFinal.TIME.yyyyMMdd);
        String HH       = DateUtils.formatTime(millis, DevFinal.TIME.HH);
        int    mm       = ConvertUtils.toInt(DateUtils.formatTime(millis, DevFinal.TIME.mm));
        String mmStr;
        // 存储间隔以 10 分钟为单位
        if (mm < 10) { // 00-09
            mmStr = "00";
        } else if (mm < 20) { // 10-19
            mmStr = "10";
        } else if (mm < 30) { // 20-29
            mmStr = "20";
        } else if (mm < 40) { // 30-39
            mmStr = "30";
        } else if (mm < 50) { // 40-49
            mmStr = "40";
        } else { // 50-59
            mmStr = "50";
        }
        // 存储文件夹路径
        return FileUtils.getAbsolutePath(
                FileUtils.getFile(modulePath, yyyyMMdd + File.separator + HH + mmStr)
        );
    }

    /**
     * 存储 Http 抓包数据
     * @param captureFile 抓包存储文件
     * @return {@code true} success, {@code false} fail
     */
    protected static boolean saveHttpCaptureFile(final CaptureFile captureFile) {
        if (captureFile != null) {
            // 获取指定模块抓包存储路径
            String modulePath = getModulePath(captureFile.getModuleName());
            // 存储文件夹路径
            String filePath = getTimeFile(modulePath, captureFile.getTime());
            // 创建文件夹
            FileUtils.createFolder(filePath);
            // 获取文件名
            String fileName = getUniqueFileName(filePath, captureFile.isEncrypt());
            if (fileName != null) {
                captureFile.setFileName(fileName);
                // 将对象转换为 JSON String
                String json = captureFile.toJson();
                // 存储抓包数据文件
                FileUtils.saveFile(
                        captureFile.getDataFile(),
                        StringUtils.getBytes(captureFile.innerHttpCaptureData)
                );
                // 存储处理
                return FileUtils.saveFile(
                        getModuleHttpCaptureFile(filePath, fileName),
                        StringUtils.getBytes(json)
                );
            }
        }
        return false;
    }

    /**
     * 通过 File 读取文件映射为抓包存储文件
     * @param file 抓包存储文件地址
     * @return 抓包存储文件
     */
    private static CaptureFile fromCaptureFile(final File file) {
        if (FileUtils.isFile(file)) {
            String json = FileUtils.readFile(file);
            return fromJson(json, CaptureFile.class);
        }
        return null;
    }

    /**
     * 获取指定模块所有抓包数据
     * @param moduleName 模块名 ( 要求唯一性 )
     * @param isEncrypt  是否加密数据
     * @return 指定模块所有抓包数据
     */
    protected static List<CaptureItem> getModuleHttpCaptures(
            final String moduleName,
            final boolean isEncrypt
    ) {
        List<CaptureItem> lists = new ArrayList<>();
        // 获取指定模块抓包存储路径
        String filePath   = getModulePath(moduleName);
        File   moduleFile = FileUtils.getFile(filePath);
        if (FileUtils.isFileExists(moduleFile)) {
            // 循环年月日文件夹
            List<File> yyyyMMddFiles = sortFileByName(moduleFile.listFiles());
            for (File ymdFile : yyyyMMddFiles) {
                if (FileUtils.isDirectory(ymdFile)) {
                    String ymdName = ymdFile.getName();
                    if (ValidatorUtils.isNumber(ymdName) &&
                            StringUtils.isLength(ymdName, 8)) {
                        // 循环时分文件夹
                        List<File> hhmmFiles = sortFileByName(ymdFile.listFiles());
                        if (hhmmFiles.size() != 0) {
                            CaptureItem captureItem = new CaptureItem()
                                    .setYyyyMMdd(ymdName);
                            for (File hmFile : hhmmFiles) {
                                if (FileUtils.isDirectory(hmFile)) {
                                    String hmName = hmFile.getName();
                                    if (ValidatorUtils.isNumber(hmName) &&
                                            StringUtils.isLength(hmName, 4)) {
                                        // 循环抓包存储文件
                                        File[] files = hmFile.listFiles();
                                        if (files != null && files.length != 0) {
                                            List<CaptureFile> captureList = new ArrayList<>();
                                            for (File file : files) {
                                                if (FileUtils.isFile(file)) {
                                                    // 获取文件名
                                                    String fileName = file.getName();
                                                    // 不属于数据文件才读取
                                                    if (!fileName.endsWith(DATA_FILE_EXTENSION)) {
                                                        // 判断是否加密文件
                                                        boolean isEncryptFile = fileName.startsWith("encrypt_");
                                                        if (isEncrypt) {
                                                            // 要求获取加密文件并且属于加密文件才处理
                                                            if (isEncryptFile) {
                                                                CaptureFile captureFile = fromCaptureFile(file);
                                                                if (captureFile != null) {
                                                                    captureList.add(captureFile);
                                                                }
                                                            }
                                                        } else {
                                                            // 要求获取非加密文件并且属于非加密文件才处理
                                                            if (!isEncryptFile) {
                                                                CaptureFile captureFile = fromCaptureFile(file);
                                                                if (captureFile != null) {
                                                                    captureList.add(captureFile);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (captureList.size() != 0) {
                                                // 最新的在最前面
                                                Collections.sort(captureList, (o1, o2) -> {
                                                    float diff = o1.getTime() - o2.getTime();
                                                    if (diff > 0) {
                                                        return -1;
                                                    } else if (diff < 0) {
                                                        return 1;
                                                    }
                                                    return 0;
                                                });
                                                captureItem.getData().put(hmName, captureList);
                                            }
                                        }
                                    }
                                }
                            }
                            if (captureItem.getData().size() != 0) {
                                lists.add(captureItem);
                            }
                        }
                    }
                }
            }
        }
        return lists;
    }

    /**
     * 获取全部模块所有抓包数据
     * @param isEncrypt 是否加密数据
     * @return 全部模块所有抓包数据
     */
    protected static Map<String, List<CaptureItem>> getAllModule(
            final boolean isEncrypt
    ) {
        Map<String, List<CaptureItem>> maps = new LinkedHashMap<>();
        // 抓包存储路径
        String filePath = getStoragePath();
        File   rootFile = FileUtils.getFile(filePath);
        if (FileUtils.isFileExists(rootFile)) {
            File[] files = rootFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (FileUtils.isDirectory(file)) {
                        String moduleName = file.getName();
                        maps.put(
                                moduleName,
                                getModuleHttpCaptures(moduleName, isEncrypt)
                        );
                    }
                }
            }
        }
        return maps;
    }
}