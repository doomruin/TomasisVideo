package com.tomasis.service.textrank;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.*;

/**
 * TextRank关键词提取
 * @author hankcs
 * @author Dreamwalker
 */
public class TextRankKeyword
{
    public static final int nKeyword = 10;
    /**
     * 阻尼系数（ＤａｍｐｉｎｇＦａｃｔｏｒ），一般取值为0.85
     */
    static final float d = 0.85f;
    /**
     * 最大迭代次数
     */
    static final int max_iter = 200;
    static final float min_diff = 0.001f;

    public TextRankKeyword()
    {
        // jdk bug : Exception in thread "main" java.lang.IllegalArgumentException: Comparison method violates its general contract!
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
    }

    public String getKeyword(String title, String content)
    {
        List<Term> termList = ToAnalysis.parse(title + content);
//        System.out.println(termList);
        List<String> wordList = new ArrayList<String>();
        for (Term t : termList)
        {
            if (shouldInclude(t))
            {
                wordList.add(t.getName());
            }
        }
//        System.out.println(wordList);
        Map<String, Set<String>> words = new HashMap<String, Set<String>>();
        Queue<String> que = new LinkedList<String>();
        for (String w : wordList)
        {
            if (!words.containsKey(w))
            {
                words.put(w, new HashSet<String>());
            }
            que.offer(w);
            if (que.size() > 5)
            {
                que.poll();
            }

            for (String w1 : que)
            {
                for (String w2 : que)
                {
                    if (w1.equals(w2))
                    {
                        continue;
                    }

                    words.get(w1).add(w2);
                    words.get(w2).add(w1);
                }
            }
        }
//        System.out.println(words);
        Map<String, Float> score = new HashMap<String, Float>();
        for (int i = 0; i < max_iter; ++i)
        {
            Map<String, Float> m = new HashMap<String, Float>();
            float max_diff = 0;
            for (Map.Entry<String, Set<String>> entry : words.entrySet())
            {
                String key = entry.getKey();
                Set<String> value = entry.getValue();
                m.put(key, 1 - d);
                for (String other : value)
                {
                    int size = words.get(other).size();
                    if (key.equals(other) || size == 0) continue;
                    m.put(key, m.get(key) + d / size * (score.get(other) == null ? 0 : score.get(other)));
                }
                max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 0 : score.get(key))));
            }
            score = m;
            if (max_diff <= min_diff) break;
        }
        List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String, Float>>(score.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Float>>()
        {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2)
            {
                return (o1.getValue() - o2.getValue() > 0 ? -1 : 1);
            }
        });
//        System.out.println(entryList);
        String result = "";
        for (int i = 0; i < nKeyword; ++i)
        {
            result += entryList.get(i).getKey() + '#';
        }
        return result;
    }

    /*
        numLimit为最低的字数，如果关键词低于这个字数会被自动过滤掉,deleteV为是否去掉动词
     */
    public List<String> getKeyword(String title, String content, int numLimit,boolean deleteV){
        List<String> result = new ArrayList<String>();
        List<Term> termList = ToAnalysis.parse(title + content);
        //使用ansj的中文分词将句子分解
        List<String> wordList = new ArrayList<String>();
        for (Term t : termList){
            if(deleteV){
                //如果去掉动词，动词在分解词中占很大比重
                // 但大部分分解出来的动词都没有实际意义
                if (!t.getNatrue().natureStr.startsWith("v")&&shouldInclude(t)){
                    wordList.add(t.getName());
                }
            }else{
                if (shouldInclude(t)){
                    //判断分解词是否使用，按照词性判断
                    wordList.add(t.getName());
                }
            }
        }
        Map<String, Set<String>> words = new HashMap<String, Set<String>>();
        //使用新的容器储存词语，键为词语本身，
        Queue<String> que = new LinkedList<String>();//邻近词队列
        for (String w : wordList){
            if (!words.containsKey(w)){//如果没有这个词
                words.put(w, new HashSet<String>());
                //添加到词语Map中
            }
            que.offer(w);//将词语的邻近词添加到队列
            if (que.size() > 5){
                //邻近词队列长度不能超过5个
                que.poll();
            }
            for (String w1 : que){
                for (String w2 : que){
                    if (w1.equals(w2))continue;
                    words.get(w1).add(w2);
                    words.get(w2).add(w1);
                    //将邻近此队列中的词语全部加入词语容器
                }
            }
        }
        Map<String, Float> score = new HashMap<String, Float>();
        for (int i = 0; i < max_iter; ++i){
        //最大迭代次数max_iter=200
            Map<String, Float> m = new HashMap<String, Float>();
            float max_diff = 0;
            //TextRank公式转换为R=AX的矩阵形式，求解R，迭代直到R与X的差距较小
            for (Map.Entry<String, Set<String>> entry : words.entrySet()){
            //取出所有的词语以及邻近词语
                String key = entry.getKey();
                Set<String> value = entry.getValue();
                m.put(key, 1 - d);//d为阻尼系数
                for (String other : value)
                {
                    int size = words.get(other).size();
                    //a的邻近词语b的邻近词语数量
                    if (key.equals(other) || size == 0) continue;
                    //取出来的是自己，跳过
                    m.put(key, m.get(key) + d / size * (score.get(other) == null ? 0 : score.get(other)));
                    //R（vi）=（1-d）+d*R(vj)/out（vi）
                    //如果其他词语没有对当前词语评分，则置0
                }
                max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 0 : score.get(key))));
                //比较当前max_diff与新一次迭代结果的大小，取小的重新赋值
            }
            score = m;
            if (max_diff <= min_diff) break;
            //当max_dift <min_diff=0.001是认为收敛结束，跳出循环
        }
        List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String, Float>>(score.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Float>>(){
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2){
                return (o1.getValue() - o2.getValue() > 0 ? -1 : 1);
            }
        });
        //将最终的评分列表按照得分值排序
        if(entryList.size() == 0){
            return result;
        }else if(entryList.size() <=1){
            result.add(entryList.get(1).getKey());
        }else{
            for (int i = 0; i < entryList.size(); ++i){
                String a = entryList.get(i).getKey();
                if(a.length() >=numLimit){
                    result.add(a);
                }
            }
        }
        //将列表转化为String输出
        return result;
    }















    public static void main(String[] args)
    {
        String content = "程序员(英文Programmer)是从事程序开发、维护的专业人员。一般将程序员分为程序设计人员和程序编码人员，但两者的界限并不非常清楚，特别是在中国。软件从业人员分为初级程序员、高级程序员、系统分析员和项目经理四大类。";
        System.out.println(new TextRankKeyword().getKeyword("", content));

    }

    /**
     * 是否应当将这个term纳入计算，词性属于名词、动词、副词、形容词
     * @param term
     * @return 是否应当
     */
    public boolean shouldInclude(Term term)
    {
        if (
                term.getNatrue().natureStr.startsWith("n") ||
                term.getNatrue().natureStr.startsWith("v") ||
                term.getNatrue().natureStr.startsWith("d") ||
                term.getNatrue().natureStr.startsWith("a")
                )
        {
            // TODO 你需要自己实现一个停用词表
//            if (!StopWordDictionary.contains(term.getName()))
//            {
                return true;
//            }
        }

        return false;
    }
}
