import com.tomasis.dao.YoukuDao;
import com.tomasis.model.YoukuBasic;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by Dreamwalker on 2015/1/7.
 */
public class YoukuTest {
    public static void main(String[] args){
        ApplicationContext ac = new ClassPathXmlApplicationContext("/config/Spring.xml");
        YoukuDao youkuDao=(YoukuDao)ac.getBean("youkuDao");
        //YoukuBasic yb = youkuDao.findById(85);
       // System.out.println(youkuDao.findBasicUrlById(85));
        //.out.println();
        youkuDao.updateJsonInfo("haha",85);

    }
}
