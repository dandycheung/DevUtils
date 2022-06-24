package dev.standard.dev_final;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.utils.DevFinal;
import dev.utils.common.FileIOUtils;
import dev.utils.common.FileUtils;
import dev.utils.common.StringUtils;

/**
 * detail: 格式化文件 常量排序
 * @author Ttt
 */
public class FormatFileFinalSortMain {

    public static void main(String[] args) {
        List<String>        sortList  = new ArrayList<>();
        Map<String, String> resultMap = new HashMap<>();
        // 拼接结果数据
        StringBuilder builder = new StringBuilder();
        // 读取待格式化文件内容
        List<String> texts = FileIOUtils.readFileToList(getFormatFilePath());
        for (String text : texts) {
            if (!text.contains(PSFS)) {
                sortAppend(builder, sortList, resultMap);
                builder.append(text).append(DevFinal.SYMBOL.NEW_LINE);
            } else {
                convertPSFS(text, sortList, resultMap);
            }
        }
        String content = builder.toString();
        // 移除最后一行换行符
        content = StringUtils.clearEndsWith(
                content, DevFinal.SYMBOL.NEW_LINE
        );
        boolean result = FileUtils.saveFile(
                getResultFilePath(),
                content.getBytes()
        );
        System.out.println("保存结果: " + result);
    }

    // ==========
    // = 内部方法 =
    // ==========

    private static final String PSFS            = "public static final String ";
    private static final String PSFS_LOWER_CASE = PSFS.toLowerCase();

    private static String getPackagePath() {
        return new File("/interesting/DevStandard/src/main/java/dev/standard/dev_final").getAbsolutePath();
    }

    private static String getFormatFilePath() {
        return new File(getPackagePath(), "format.txt").getAbsolutePath();
    }

    private static String getResultFilePath() {
        return new File(getPackagePath(), "result.txt").getAbsolutePath();
    }

    // =

    /**
     * 转换 public static final String 处理
     * @param original  常量原始文本
     * @param sortList  待排序常量名
     * @param resultMap 带存储结果
     */
    private static void convertPSFS(
            final String original,
            final List<String> sortList,
            final Map<String, String> resultMap
    ) {
        // 以 public static final String DEFAULT = "default"; 为例
        // 全部换为小写 public static final string default = "default";
        String originalLowerCase = original.toLowerCase();
        // 清空字符串前后全部空格、Tab、换行符
        String textTrim = StringUtils.clearSpaceTabLineTrim(originalLowerCase);
        // 获取 default = "default";
        String text = textTrim.substring(PSFS_LOWER_CASE.length()).trim();
        // 获取常量名 default
        String finalName = text.substring(
                0, text.indexOf(DevFinal.SYMBOL.SPACE)
        );
        String finalNameUpperCase = finalName.toUpperCase();
        // 进行替换
        String result = originalLowerCase.replaceAll(
                PSFS_LOWER_CASE + finalName,
                PSFS + finalNameUpperCase
        );
        // 保存处理后的结果
        resultMap.put(finalNameUpperCase, result);
        // 保存待排序常量名
        sortList.add(finalNameUpperCase);
    }

    /**
     * 排序并追加文本
     * @param builder   StringBuilder
     * @param sortList  待排序常量名
     * @param resultMap 带存储结果
     */
    private static void sortAppend(
            final StringBuilder builder,
            final List<String> sortList,
            final Map<String, String> resultMap
    ) {
        if (!sortList.isEmpty()) {
            // Collator 类是用来执行区分语言环境的 String 比较的, 这里选择使用 CHINA
            Collator collator = Collator.getInstance(java.util.Locale.CHINA);
            // 使根据指定比较器产生的顺序对指定对象数组进行排序
            Collections.sort(sortList, collator);

            for (String key : sortList) {
                builder.append(resultMap.get(key))
                        .append(DevFinal.SYMBOL.NEW_LINE);
            }
        }
        sortList.clear();
        resultMap.clear();
    }
}