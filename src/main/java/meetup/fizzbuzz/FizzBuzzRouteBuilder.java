package meetup.fizzbuzz;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author Dmytro Rud
 */
public class FizzBuzzRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:input")
            .split(body(), new StringListAggregationStrategy())
                .parallelProcessing()
                .executorService(new ScheduledThreadPoolExecutor(100))
                .to("direct:decide");

        from("direct:decide")
            .choice()
                .when(new DivisibilityPredicate(15)).setBody(constant("FizzBuzz"))
                .when(new DivisibilityPredicate(3)).setBody(constant("Fizz"))
                .when(new DivisibilityPredicate(5)).setBody(constant("Buzz"));

    }


    private static class DivisibilityPredicate implements Predicate {
        private final int divider;

        public DivisibilityPredicate(int divider) {
            this.divider = divider;
        }

        public boolean matches(Exchange exchange) {
            int x = exchange.getIn().getBody(int.class);
            return (x % divider == 0);
        }
    }


    private static class StringListAggregationStrategy implements AggregationStrategy {
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange != null) {
                String list = oldExchange.getIn().getBody(String.class);
                String element = newExchange.getIn().getBody(String.class);
                newExchange.getIn().setBody(list + ' ' + element);
            }
            return newExchange;
        }
    }
}
