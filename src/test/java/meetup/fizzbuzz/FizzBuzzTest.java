package meetup.fizzbuzz;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * @author Dmytro Rud
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@ContextConfiguration(locations = {"/meetup/fizzbuzz/context.xml"})
public class FizzBuzzTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    public void testMeetup() {
        int[] array = new int[10000];
        for (int i = 0; i < array.length; ++i) {
            array[i] = i - 15;
        }

        long startTimestamp = System.currentTimeMillis();
        System.out.println("start");
        String result = producerTemplate.requestBody("direct:input", array, String.class);
        long endTimestamp = System.currentTimeMillis();
        System.out.println(result);
        System.out.println(endTimestamp - startTimestamp);
    }

}
