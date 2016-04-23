package meetup.pong;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Dmytro Rud
 */
public class Slave {
    public static void main(String[] args) throws Exception {
        new ClassPathXmlApplicationContext("/meetup/pong/slave-context.xml");
        Thread.currentThread().join();
    }
}
