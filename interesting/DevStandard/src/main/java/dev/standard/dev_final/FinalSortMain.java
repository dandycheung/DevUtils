package dev.standard.dev_final;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.utils.DevFinal;
import dev.utils.common.StringUtils;

/**
 * detail: DevFinal 常量排序
 * @author Ttt
 */
public class FinalSortMain {

    public static void main(String[] args) {
        String              FORMAT = "public static final String %s = \"%s\";";
        List<String>        lists  = new ArrayList<>();
        Map<String, String> maps   = new HashMap<>();
        Field[]             fields = FinalSortMain.class.getDeclaredFields();
        for (Field field : fields) {
            String descriptor = Modifier.toString(field.getModifiers());
            descriptor = StringUtils.isEmpty(descriptor) ? "" : descriptor + " ";
            maps.put(field.getName(), descriptor);

            lists.add(field.getName());
        }
        // Collator 类是用来执行区分语言环境的 String 比较的, 这里选择使用 CHINA
        Collator collator = Collator.getInstance(java.util.Locale.CHINA);
        // 使根据指定比较器产生的顺序对指定对象数组进行排序
        Collections.sort(lists, collator);

        StringBuilder builder = new StringBuilder();
        for (String key : lists) {
            builder.append(DevFinal.SYMBOL.NEW_LINE)
                    .append(String.format(FORMAT, key.toUpperCase(), key.toLowerCase()));
        }
        // 最终内容
        String content = builder.toString();
        System.out.println(content);

        String debug = "";
    }
}