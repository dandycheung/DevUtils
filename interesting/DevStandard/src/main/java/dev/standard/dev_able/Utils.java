package dev.standard.dev_able;

import dev.utils.DevFinal;
import dev.utils.common.FileUtils;
import dev.utils.common.StringUtils;

import java.io.File;
import java.util.List;

/**
 * detail: DevFinal 字符串常量排序工具类
 * @author Ttt
 */
final class Utils {

    private Utils() {
    }

    // ============
    // = 统一实体类 =
    // ============

    public static class Replace {

        public static final String AAAAA = "AAAAA";
        public static final String bbbbb = "bbbbb";
        public static final String CCCCC = "CCCCC";

        // 功能名 -> 替换 AAAAA
        public final String name;

        // 方法名 -> 替换 bbbbb
        public final String methodName;

        // 补充注释 -> 替换 CCCCC
        public final String annotation;

        public Replace(
                final String name,
                final String methodName
        ) {
            this(name, methodName, "");
        }

        public Replace(
                final String name,
                final String methodName,
                final String annotation
        ) {
            this.name       = name;
            this.methodName = methodName;
            this.annotation = annotation;
        }

        public String getFileName() {
            return name + "able.java";
        }
    }

    // ==============
    // = 对外公开方法 =
    // ==============

    /**
     * 生成 able 文件
     * @param list 待生成信息
     */
    public static void generateAbleFile(final List<Replace> list) {
        for (Replace info : list) {
            String formatTxt = getFormatTXT();
            String content = formatTxt.replaceAll(
                    Replace.AAAAA, info.name
            ).replaceAll(
                    Replace.bbbbb, info.methodName
            ).replaceAll(
                    Replace.CCCCC, info.annotation
            );
            File file = FileUtils.getFile(
                    getGenerateDirectory(),
                    info.getFileName()
            );
            // 保存文件
            saveFile(content, FileUtils.getAbsolutePath(file));
        }
    }

    // ==========
    // = 内部方法 =
    // ==========

    private static String FORMAT_TXT = null;

    /**
     * 获取 format.txt 文本内容
     * @return format.txt 文本内容
     */
    private static String getFormatTXT() {
        if (FORMAT_TXT == null) {
            FORMAT_TXT = FileUtils.readFile(getFormatFilePath());
        }
        return FORMAT_TXT;
    }

    // ==========
    // = 保存结果 =
    // ==========

    /**
     * 保存文件
     * @param content  保存内容
     * @param filePath 文件路径
     * @return {@code true} success, {@code false} fail
     */
    private static boolean saveFile(
            final String content,
            final String filePath
    ) {
        // 移除最后一行换行符
        String finalContent = StringUtils.clearEndsWith(
                content, DevFinal.SYMBOL.NEW_LINE
        );
        boolean result = FileUtils.saveFile(
                filePath, finalContent.getBytes()
        );
        System.out.println("保存结果: " + result);
        return result;
    }

    // ==========
    // = 路径相关 =
    // ==========

    private static String getPackagePath() {
        return new File(
                System.getProperty("user.dir"),
                "/interesting/DevStandard/src/main/java/dev/standard/dev_able"
        ).getAbsolutePath();
    }

    private static String getFormatFilePath() {
        return new File(getPackagePath(), "format.txt").getAbsolutePath();
    }

    private static String getGenerateDirectory() {
        return new File(
                System.getProperty("user.dir"),
                "/lib/DevApp/src/main/java/dev/utils/common/able"
        ).getAbsolutePath();
    }
}