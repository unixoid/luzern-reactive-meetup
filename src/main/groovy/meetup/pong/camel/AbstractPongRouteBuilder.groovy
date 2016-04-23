package meetup.pong.camel

import javafx.application.Application
import lombok.extern.slf4j.Slf4j
import org.apache.camel.builder.RouteBuilder
import meetup.pong.gui.PongApplication
import meetup.pong.logic.CaretSide
import meetup.pong.logic.State

import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Dmytro Rud
 */
@Slf4j
abstract class AbstractPongRouteBuilder extends RouteBuilder {

    private static final AtomicBoolean APPLICATION_LAUNCHED = new AtomicBoolean(false)
    volatile static PongApplication application


    // injected by Spring
    String urlBase
    CaretSide caretSide

    static void checkIfAlreadyLaunched() {
        if (APPLICATION_LAUNCHED.getAndSet(true)) {
            throw new Exception('Already launched')
        }
    }

    @Override
    void configure() throws Exception {
        from('direct:launch-application')
            .process {
                String configurationJson = it.in.getBody(String.class)
                Application.launch(
                        PongApplication.class,
                        "--${PongApplication.CONFIG_PARAM_NAME}=${configurationJson}",
                        "--${PongApplication.CARET_SIDE_PARAM_NAME}=${caretSide}",
                )
            }


        from(urlBase + 'update')
            .process {
                while (! application?.started?.get()) {
                    // wait the app to start properly
                }

                log.debug('Got update: {}', it.in.body)
                State state = State.fromJson(it.in.body)
                State newState = application.exchangeState(state)
                if (newState) {
                    it.in.body = newState.toJson()
                    log.debug('Send update: {}', it.in.body)
                } else {
                    throw new Exception('Finished')
                }
            }
            .to(urlBase + 'update')

    }
}
