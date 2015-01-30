package com.tomasis.entrance;

/**
 * Created by Dreamwalker on 2015/1/30.
 * Levenshtein Distance算法
 * 算法介绍:

 编辑距离（Edit Distance），又称Levenshtein距离，是指两个字串之间，由一个转成另一个所需的最少编辑操作次数。许可的编辑操作包括将一个字符替换成另一个字符，插入一个字符，删除一个字符。
 算法原理:

 设我们可以使用d[ i , j ]个步骤（可以使用一个二维数组保存这个值），表示将串s[ 1…i ] 转换为 串t [ 1…j ]所需要的最少步骤个数，那么，在最基本的情况下，即在i等于0时，也就是说串s为空，那么对应的d[0,j] 就是 增加j个字符，使得s转化为t，在j等于0时，也就是说串t为空，那么对应的d[i,0] 就是 减少 i个字符，使得s转化为t。

 然后我们考虑一般情况，加一点动态规划的想法，我们要想得到将s[1..i]经过最少次数的增加，删除，或者替换操作就转变为t[1..j]，那么我们就必须在之前可以以最少次数的增加，删除，或者替换操作，使得现在串s和串t只需要再做一次操作或者不做就可以完成s[1..i]到t[1..j]的转换。所谓的“之前”分为下面三种情况：

 1）我们可以在k个操作内将 s[1…i] 转换为 t[1…j-1]

 2）我们可以在k个操作里面将s[1..i-1]转换为t[1..j]

 3）我们可以在k个步骤里面将 s[1…i-1] 转换为 t [1…j-1]

 针对第1种情况，我们只需要在最后将 t[j] 加上s[1..i]就完成了匹配，这样总共就需要k+1个操作。

 针对第2种情况，我们只需要在最后将s[i]移除，然后再做这k个操作，所以总共需要k+1个操作。

 针对第3种情况，我们只需要在最后将s[i]替换为 t[j]，使得满足s[1..i] == t[1..j]，这样总共也需要k+1个操作。而如果在第3种情况下，s[i]刚好等于t[j]，那我们就可以仅仅使用k个操作就完成这个过程。

 最后，为了保证得到的操作次数总是最少的，我们可以从上面三种情况中选择消耗最少的一种最为将s[1..i]转换为t[1..j]所需要的最小操作次数。


 算法实现步骤:

 步骤	说明
 1	设置n为字符串s的长度。("GUMBO")
 设置m为字符串t的长度。("GAMBOL")
 如果n等于0，返回m并退出。
 如果m等于0，返回n并退出。
 构造两个向量v0[m+1] 和v1[m+1]，串联0..m之间所有的元素。
 2	初始化 v0 to 0..m。
 3	检查 s (i from 1 to n) 中的每个字符。
 4	检查 t (j from 1 to m) 中的每个字符
 5	如果 s[i] 等于 t[j]，则编辑代价cost为 0；
 如果 s[i] 不等于 t[j]，则编辑代价cost为1。
 6	设置单元v1[j]为下面的最小值之一：
 a、紧邻该单元上方+1：v1[j-1] + 1
 b、紧邻该单元左侧+1：v0[j] + 1
 c、该单元对角线上方和左侧+cost：v0[j-1] + cost
 7	在完成迭代 (3, 4, 5, 6) 之后，v1[m]便是编辑距离的值。
 */
public class SimFeatureService {

    private static int min(int one, int two, int three) {
        int min = one;
        if (two < min) {
            min = two;
        }
        if (three < min) {
            min = three;
        }
        return min;
    }

    public static int ld(String str1, String str2) {
        int d[][]; // 矩阵
        int n = str1.length();
        int m = str2.length();
        int i; // 遍历str1的
        int j; // 遍历str2的
        char ch1; // str1的
        char ch2; // str2的
        int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) { // 初始化第一列
            d[i][0] = i;
        }
        for (j = 0; j <= m; j++) { // 初始化第一行
            d[0][j] = j;
        }
        for (i = 1; i <= n; i++) { // 遍历str1
            ch1 = str1.charAt(i - 1);
            // 去匹配str2
            for (j = 1; j <= m; j++) {
                ch2 = str2.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1]+ temp);
            }
        }
        return d[n][m];
    }
    public static double sim(String str1, String str2) {
        try {
            double ld = (double)ld(str1, str2);
            return (1-ld/(double)Math.max(str1.length(), str2.length()));
        } catch (Exception e) {
            return 0.1;
        }
    }

    public static void main(String[] args) {
        String str1 = "见笑";
        String str2 = "哈哈";
        System.out.println("ld=" + ld(str1, str2));
        System.out.println("sim=" + sim(str1, str2));
    }
}
