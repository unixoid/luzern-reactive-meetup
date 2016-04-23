package meetup.pong;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Dmytro Rud
 */
public class Master {
    public static void main(String[] args) throws Exception {
        new ClassPathXmlApplicationContext("/meetup/pong/master-context.xml");
        Thread.currentThread().join();
    }
}
