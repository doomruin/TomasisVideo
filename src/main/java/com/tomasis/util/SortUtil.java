package com.tomasis.util;

import java.util.*;

/**
 * Created by Dreamwalker on 2015/2/2.
 */
public class SortUtil {
    /*
        取出哈是hashMap中百分比能量的条目，用String输出,输入的HashMap已排序
     */
    public static String extractByPower(Map<String, Double> sortedMap, int percent){
        if(percent < 0 || percent >100 || sortedMap==null) return "";
        String[] keyArray=sortedMap.keySet().toArray(new String[0]);
        double currSum=0,allSum=0;
        int curr=0;
        for(String key :keyArray){
            allSum+=sortedMap.get(key);
        }
        double percentSum = allSum*percent/100;
        for(String key : keyArray){
            if(sortedMap.get(key)==0.1)continue;
            curr+=1;
            currSum+=sortedMap.get(key);
            if(currSum> percentSum)break;
        }
        //获取到了满足能量时的index,需要减一
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<curr-1 ; i++){
            sb.append(keyArray[i]);
            sb.append(",");
        }
        System.out.println(keyArray[curr]);
        sb.append(keyArray[curr]);
        return sb.toString();

    }
    /*
            将hashmap按值排序
         */
    public static Map<String, Double> sortMapByValue(Map<String, Double> oriMap) {
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        if (oriMap != null && !oriMap.isEmpty()) {
            List<Map.Entry<String, Double>> entryList = new ArrayList<Map.Entry<String, Double>>(oriMap.entrySet());
            Collections.sort(entryList,
                    new Comparator<Map.Entry<String, Double>>() {
                        public int compare(Map.Entry<String, Double> entry1,
                                           Map.Entry<String, Double> entry2) {

                            return (int)(entry2.getValue().doubleValue()*1000000) - (int)(entry1.getValue().doubleValue()*1000000);
                        }
                    });
            Iterator<Map.Entry<String, Double>> iter = entryList.iterator();
            Map.Entry<String, Double> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
        }
        return sortedMap;
    }
}
