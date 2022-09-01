package dev.utils.common.format;

import dev.utils.common.BigDecimalUtils;

/**
 * detail: 单位数组范围格式化
 * @author Ttt
 */
public class UnitSpanFormatter {

    // 单位格式化精度
    private int     precision;
    // 是否自动补 0 ( 只有 int、long 有效 )
    private boolean appendZero;
    // 格式化异常默认值
    private String  defaultValue;

    // =============
    // = 对外公开方法 =
    // =============

    /**
     * 获取 UnitSpanFormatter
     * @param precision 单位格式化精度
     * @return {@link UnitSpanFormatter}
     */
    public static UnitSpanFormatter get(
            final int precision
    ) {
        return new UnitSpanFormatter()
                .setPrecision(precision);
    }

    /**
     * 获取 UnitSpanFormatter
     * @param precision    单位格式化精度
     * @param defaultValue 格式化异常默认值
     * @return {@link UnitSpanFormatter}
     */
    public static UnitSpanFormatter get(
            final int precision,
            final String defaultValue
    ) {
        return new UnitSpanFormatter()
                .setPrecision(precision)
                .setDefaultValue(defaultValue);
    }

    /**
     * 获取 UnitSpanFormatter
     * @param precision  单位格式化精度
     * @param appendZero 是否自动补 0 ( 只有 int、long 有效 )
     * @return {@link UnitSpanFormatter}
     */
    public static UnitSpanFormatter get(
            final int precision,
            final boolean appendZero
    ) {
        return new UnitSpanFormatter()
                .setPrecision(precision)
                .setAppendZero(appendZero);
    }

    /**
     * 获取 UnitSpanFormatter
     * @param precision    单位格式化精度
     * @param appendZero   是否自动补 0 ( 只有 int、long 有效 )
     * @param defaultValue 格式化异常默认值
     * @return {@link UnitSpanFormatter}
     */
    public static UnitSpanFormatter get(
            final int precision,
            final boolean appendZero,
            final String defaultValue
    ) {
        return new UnitSpanFormatter()
                .setPrecision(precision)
                .setAppendZero(appendZero)
                .setDefaultValue(defaultValue);
    }

    // ===========
    // = get/set =
    // ===========

    /**
     * 获取单位格式化精度
     * @return 单位格式化精度
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * 设置单位格式化精度
     * @param precision 单位格式化精度
     * @return {@link UnitSpanFormatter}
     */
    public UnitSpanFormatter setPrecision(final int precision) {
        this.precision = precision;
        return this;
    }

    /**
     * 是否自动补 0
     * @return {@code true} yes, {@code false} no
     */
    public boolean isAppendZero() {
        return appendZero;
    }

    /**
     * 设置是否自动补 0
     * @param appendZero 是否自动补 0
     * @return {@link UnitSpanFormatter}
     */
    public UnitSpanFormatter setAppendZero(final boolean appendZero) {
        this.appendZero = appendZero;
        return this;
    }

    /**
     * 获取格式化异常默认值
     * @return 格式化异常默认值
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * 设置格式化异常默认值
     * @param defaultValue 格式化异常默认值
     * @return {@link UnitSpanFormatter}
     */
    public UnitSpanFormatter setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    // ============
    // = 格式化方法 =
    // ============

    // ==========
    // = double =
    // ==========

    /**
     * 格式化
     * @param values 待格式化值
     * @param units  对应值单位
     * @return 单位数组范围格式化字符串
     */
    public String format(
            final double[] values,
            final String[] units
    ) {
        return format(values, units, null);
    }

    /**
     * 格式化
     * @param values    待格式化值
     * @param units     对应值单位
     * @param operation BigDecimal 操作包装类
     * @return 单位数组范围格式化字符串
     */
    public String format(
            final double[] values,
            final String[] units,
            final BigDecimalUtils.Operation operation
    ) {
        if (precision > 0 && values != null && units != null) {
            if (precision >= values.length && precision >= units.length) {
                BigDecimalUtils.Operation op = null;
                if (operation != null) op = operation.clone();
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < precision; i++) {
                    if (op != null) {
                        String value = op.setBigDecimal(values[i]).round()
                                .toPlainString();
                        builder.append(value);
                    } else {
                        builder.append(values[i]);
                    }
                    builder.append(units[i]);
                }
                return builder.toString();
            }
        }
        return defaultValue;
    }
}