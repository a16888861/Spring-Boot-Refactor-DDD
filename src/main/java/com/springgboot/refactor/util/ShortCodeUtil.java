package com.springgboot.refactor.util;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;
import java.util.stream.IntStream;

/**
 * 短码生成工具
 */
public class ShortCodeUtil {

    private static final String ALL_LETTER = "abcdefghijklmnopqrstuvwxyz";

    /**
     * 几位短码(后期可将此处长度改为接口控制)
     */
    private static Integer CHAR_NUM = 6;
    /**
     * 长度为8位 这些字母有8的阶乘这么多的排列方式
     */
    private static Integer BATCH = countBatch();

    private static Integer countBatch() {
        return 2 * IntStream.rangeClosed(3, CHAR_NUM).reduce(1, (a, b) -> a * b);
    }

    /**
     * 初始化的字母排列数量
     */
    public static Integer INIT_LETTER_SIZE = 7000;

    private static Map<Integer, List<String>> LETTER_GROUP_CACHE = cacheCombination();

    private static Map<Integer, List<String>> cacheCombination() {
        Map<Integer, List<String>> result = Maps.newHashMap();
        for (int i = 1; i <= INIT_LETTER_SIZE; i++) {
            result.put(
                    i,
                    getCombination(
                            Arrays.asList(ALL_LETTER.split("")),
                            CHAR_NUM,
                            i
                    )
            );
        }
        return result;
    }

    public static void setCharNum(int shortCodeLength) {
        CHAR_NUM = shortCodeLength;
        BATCH = countBatch();
        LETTER_GROUP_CACHE = cacheCombination();
    }

    /**
     * 已使用分区的记录
     */
    public static final Map<Integer, Set<Integer>> USED_RECODE = Maps.newHashMap();

    /**
     * 获取字母组合
     *
     * @param elements        一批字母
     * @param batchGroupCount 多少位为一组
     * @param groupIndex      第几种组合
     * @return 组合结果
     */
    public static List<String> getCombination(
            List<String> elements,
            int batchGroupCount,
            int groupIndex) {
        if (batchGroupCount == 0 ||
                elements.size() < batchGroupCount ||
                groupIndex <= 0
        ) {
            return new ArrayList<>();
        }
        int count =
                combination(
                        elements.size() - 1,
                        batchGroupCount - 1);
        if (groupIndex <= count) {
            List<String> result = new ArrayList<>();
            result.add(elements.get(0));
            result.addAll(
                    getCombination(
                            elements.subList(
                                    1,
                                    elements.size()),
                            batchGroupCount - 1,
                            groupIndex));
            return result;
        } else {
            return
                    getCombination(
                            elements.subList(
                                    1,
                                    elements.size()),
                            batchGroupCount,
                            groupIndex - count);
        }
    }

    public static int combination(int n, int m) {
        if (m == 0 || m == n) {
            return 1;
        }
        if (m == 1) {
            return n;
        }
        return combination(n - 1, m - 1) + combination(n - 1, m);
    }

    /**
     * 康托展开代码实现
     *
     * @param a 表示当前的一个排列字符串，最高位在最左边，最低位在最右边
     * @param n 当前排列字符串的长度
     * @return 康托展开值
     */
    public static int cantor(String a, int n) {
        //保存从0~n-1的所有阶乘结果
        int[] factorial = new int[n];
        factorial[0] = 1;

        //求阶乘
        for (int i = 1; i < n; i++) {
            factorial[i] = i * factorial[i - 1];
        }

        int result = 0;//保存康托展开的值
        for (int i = 0; i < n; i++) {
            int smaller = 0;//在当前位之后小于当前位的数字个数
            for (int j = i + 1; j < n; j++) {
                if (a.charAt(j) < a.charAt(i)) {
                    smaller++;
                }
            }
            result += factorial[n - i - 1] * smaller;//累加康托展开的每一项
        }
        return result; //最终的康托展开值
    }

    /**
     * 尼康托展开求解排列
     *
     * @param n 待排列集合的长度
     * @param k 第k个排列(注意，从0开始)
     * @return 所有由小到大全排列中排列在第k位的那个排列
     */
    public static String reverseCantor(int n, int k) {
        //保存从0~n-1的所有阶乘结果
        int[] factorial = new int[n];
        factorial[0] = 1;
        //求阶乘
        for (int i = 1; i < n; i++) {
            factorial[i] = i * factorial[i - 1];
        }
        //标记哪些数字是可以待选择的，false表示可以选择
        boolean[] used = new boolean[n + 1];
        //保存结果
        StringBuilder result = new StringBuilder();
        //从最高位开始依次求解排列的每一个位
        for (int i = 1; i <= n; i++) {
            int num = k / factorial[n - i];//商
            k = k % factorial[n - i];//余数
            //从待选择的集合中选择第num+1个数
            for (int j = 1; j <= n; j++) {
                if (!used[j]) {
                    if (num == 0) {
                        result.append(j);
                        used[j] = true;//标记已选择
                        break;
                    }
                    num--;
                }
            }
        }
        return result.toString();
    }

    /**
     * 获取一个随机字符串
     */
    public static String getRandomString() {
        int partition = RandomUtils.nextInt(1, INIT_LETTER_SIZE);
        List<String> letterList = LETTER_GROUP_CACHE.get(partition);
        StringBuilder result = new StringBuilder();
        int randomNum = getRandomNum(partition);
        for (String s : reverseCantor(
                CHAR_NUM,
                randomNum
        ).split("")) {
            result.append(letterList.get(Integer.parseInt(s) - 1));
        }
        return partition + "/" + result.toString();
    }

    /**
     * 生成随机数 并记录 分区 partition 的第 randomNum 位占用了
     *
     * @param partition 分区数
     * @return 随机数
     */
    public synchronized static Integer getRandomNum(int partition) {
        int randomNum = RandomUtils.nextInt(0, BATCH - 1);
        Set<Integer> recodeSet = USED_RECODE.getOrDefault(partition, new HashSet<>());
        if (recodeSet.contains(randomNum)) {
            getRandomNum(partition);
        }
        recodeSet.add(randomNum);
        USED_RECODE.put(partition, recodeSet);
        return randomNum;
    }

    public static void main(String[] args) {
        System.out.println(BATCH);
        System.out.println(LETTER_GROUP_CACHE);

        System.out.println(getRandomString());

        // 获取这个字符串在这些字母中是第几种排列 到时候库中就记录这个ID + 对应网址 or 内容 即可
        System.out.println(cantor("hbecad", 6));

    }
}
