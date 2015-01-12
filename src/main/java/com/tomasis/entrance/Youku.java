package com.tomasis.entrance;

import com.tomasis.dao.YoukuDao;
import com.tomasis.dao.YoukuDaoImpl;
import com.tomasis.model.YoukuBasic;
import com.tomasis.util.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dreamwalker on 2015/1/4.
 */
public class Youku {
    private static int i=1;
    private static String client_id = "f667ef258a8f5790";
    private static String youkuVideo_basic_url = "https://openapi.youku.com/v2/videos/show_basic.json";
    private static String nextPage="";
    private static String basicTag="自拍";


    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    public static void main(String[] args){
        batchAddJsonInfoToYoukuBasic(7093);
    }
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    private static void crawlYoukuBasicInfo(){
        ApplicationContext ac = new ClassPathXmlApplicationContext("/config/Spring.xml");
        YoukuDao youkuDao=(YoukuDao)ac.getBean("youkuDao");
        while(!nextPage.equals("end")){
            System.out.println("start crawling------"+nextPage);
            List<YoukuBasic> ybs=getVideoName("http://www.youku.com/v_showlist/c176g0d1s1p14.html");
            for(YoukuBasic yb :ybs){
                youkuDao.insert(yb);
            }
            System.out.println("end crawling------"+nextPage);
            System.out.println("-------------------!!!!!!!!-------------------------");
            try {
                Thread.sleep(10000);
            }catch(Exception e){
                e.printStackTrace();
            }
        }


    }

    private static List<YoukuBasic> getVideoName(String url){
        List<YoukuBasic> ybs = new ArrayList<YoukuBasic>();

        try{
            Document doc = (nextPage.equals("")?Jsoup.connect(url) : Jsoup.connect("http://www.youku.com"+nextPage))
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31")
                    .get();
            Elements es = doc.getElementsByClass("p-small");
            if(es.size()==0){
                es = doc.getElementsByClass("v");//youku页面两种不同的class，结构相同

            }
            Elements pages = doc.getElementsByClass("next");
            //分辨有无下一页
            Element eee = pages.get(0);
            if(eee.hasAttr("title")){
                nextPage=eee.child(0).attr("href");
            }else{
                nextPage="end";
            }
            //System.out.println(pages.get(0).hasAttr("title"));
            for(Element e :es){
                YoukuBasic yb = new YoukuBasic();
                //p p-small --->p-thumb ---->img--->src
                String picUrl = e.child(0).child(0).attr("src");
                yb.setPicUrl(picUrl);
                //p p-small --->第二个孩子p-link
                Element plink = e.child(1);
                //p-link --->第一个孩子 a
                yb.setName(plink.child(0).attr("title"));
                yb.setBasicUrl(plink.child(0).attr("href"));
                yb.setBasicTag(basicTag);
                ybs.add(yb);

                //System.out.println("----------------------------------------------------------------");
                //System.out.println(e.html());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ybs;
    }

    /*
    ---------------------------------------------------------------------
     */

    private static void batchAddJsonInfoToYoukuBasic(int startRow){
        ApplicationContext ac = new ClassPathXmlApplicationContext("/config/Spring.xml");
        YoukuDao youkuDao=(YoukuDao)ac.getBean("youkuDao");
        i = startRow;
        while(true){
            //if(i >3740 && i <3936) continue;
            addJsonInfoToYoukuBasic(ac,youkuDao, i);
            i++;
        }
    }

    private static void addJsonInfoToYoukuBasic( ApplicationContext ac,YoukuDao youkuDao,int id){
        String show_url=null;//视频播放url

        String urlFromDB = youkuDao.findBasicUrlById(id);
        if(urlFromDB.equals(YoukuDao.EMPTY)){
            return;
        }else{
            show_url = getJson(urlFromDB);
            if(show_url.equals("")){
                return;
            }
        }
        //open API youku
        Map<String,String> paramMap = new HashMap<String, String>();
        paramMap.put("client_id",client_id);
        paramMap.put("video_url",show_url);
        String json = HttpUtil.httpClient_post(youkuVideo_basic_url,paramMap);
        youkuDao.updateJsonInfo(json,id);
        System.out.println("update: "+id);
        try {
            Thread.sleep(4500);
        }catch (Exception e){
            e.printStackTrace();
        }

        //System.out.println(json);

       // System.out.println(urlFromDB);
    }
    private static String getJson(String url){
        //使用youkubasic库中的原始url获取真正的播放页面url
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31")
                    .get();
            //判断是不是播放页面
            Elements plays= doc.getElementsByClass("player_title");//播放页面特有的类
            Elements ee = doc.getElementsByClass("player_html5");
            if(plays.size() == 0 &&ee.size()==0){
               // if(doc.getElementsByClass("player_html5").size() > 0) return url;//仍然是直接播放页面
                Elements linkpanels =doc.getElementsByClass("linkpanel");
                if(linkpanels.size() > 0){//剧集基本信息页面，有第一集的link
                    //获取第一集的link
                    Elements es =linkpanels.get(0).getElementsByTag("a");
                    //有可能剧集链接折叠起来了，找不到第一集的链接，只有最新集的链接
                    if(es.size() == 0){
                        Elements baseInfos = doc.getElementsByClass("baseinfo");
                        if(baseInfos.size()==0) System.out.println("baseinfo类没有找到，尴尬");
                        String link = baseInfos.get(0).child(0).child(0).attr("href");
                        return link;
                    }else{
                        String link = es.get(0).attr("href");
                        return link;
                    }
                }else{
                    Elements baseInfos = doc.getElementsByClass("baseinfo");
                    if(baseInfos.size()==0) System.out.println("wtf,1234556-------------------------------------不正常，需处理");
                    String link = baseInfos.get(0).child(0).child(0).attr("href");
                    return link;


                }
            }else {
                    return url;
                //正好是播放页面

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

}
