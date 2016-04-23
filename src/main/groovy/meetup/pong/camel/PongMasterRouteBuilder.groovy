package meetup.pong.camel

import meetup.pong.logic.Configuration

/**
 * @author Dmytro Rud
 */
class PongMasterRouteBuilder extends AbstractPongRouteBuilder {

    // injected by Spring
    Configuration config


    @Override
    void configure() throws Exception {
        super.configure()

        // server start
        from(urlBase + 'config')
            .process {
                log.debug('Got configuration request {}', it.in.body)
                checkIfAlreadyLaunched()
                it.in.body = config.toJson()
                log.debug('Send configuration {}', it.in.body)
            }
            .to(urlBase + 'config')
            .to('direct:launch-application')

    }
}
