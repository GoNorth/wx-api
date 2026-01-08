package com.github.niefy.modules.biz.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 模板编号生成器
 * 格式: [poster_type首字母]_[YYYYMMDDHHmmss]
 * 示例: BKZP_20240115103000
 *
 * @author niefy
 * @date 2025-01-08
 */
public class TemplateNoGenerator {

    /**
     * 生成模板编号
     * 格式: [poster_type首字母]_[YYYYMMDDHHmmss]
     *
     * @param posterType 海报类型，如：爆款招牌
     * @param createTime 创建时间
     * @return 模板编号，如：BKZP_20240115103000
     */
    public static String generateTemplateNo(String posterType, Date createTime) {
        // 获取拼音首字母
        String prefix = getPinyinInitials(posterType);
        
        // 格式化日期时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateTimeStr = sdf.format(createTime != null ? createTime : new Date());
        
        // 拼接模板编号
        return prefix + "_" + dateTimeStr;
    }

    /**
     * 获取中文拼音首字母（大写）
     *
     * @param chinese 中文字符串
     * @return 拼音首字母，如：爆款招牌 -> BKZP
     */
    private static String getPinyinInitials(String chinese) {
        if (!StringUtils.hasText(chinese)) {
            return "TMP"; // 默认前缀
        }

        StringBuilder result = new StringBuilder();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        char[] chars = chinese.toCharArray();
        for (char c : chars) {
            // 如果是中文字符
            if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        // 取第一个拼音的首字母
                        result.append(pinyinArray[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    // 转换失败，跳过该字符
                }
            } else if (Character.isLetter(c)) {
                // 如果是英文字母，直接使用大写
                result.append(Character.toUpperCase(c));
            }
            // 其他字符（数字、标点等）跳过
        }

        // 如果结果为空，使用默认前缀
        if (result.length() == 0) {
            return "TMP";
        }

        return result.toString();
    }
}

