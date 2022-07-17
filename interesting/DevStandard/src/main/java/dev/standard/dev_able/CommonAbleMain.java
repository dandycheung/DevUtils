package dev.standard.dev_able;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * detail: common able 文件生成入口
 * @author Ttt
 */
public class CommonAbleMain {

    public static void main(String[] args) {
        Utils.generateAbleFile(REPLACE_LIST);
    }

    private static final List<Utils.Replace> REPLACE_LIST;

    static {
        List<Utils.Replace> list = new ArrayList<>();
        list.add(new Utils.Replace("By", "by"));
        REPLACE_LIST = Collections.unmodifiableList(list);
    }
}