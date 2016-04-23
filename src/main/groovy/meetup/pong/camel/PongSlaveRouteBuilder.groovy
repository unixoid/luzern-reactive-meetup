package meetup.pong.camel

import meetup.pong.logic.Configuration
import meetup.pong.logic.Geometry

/**
 * @author Dmytro Rud
 */
class PongSlaveRouteBuilder extends AbstractPongRouteBuilder {

    @Override
    void configure() throws Exception {
        super.configure()

        // client start
        from('quartz2://start?trigger.repeatCount=0')
            .setBody(constant(''))
            .process {
                log.debug('Send configuration request')
            }
            .to(urlBase + 'config')


        from(urlBase + 'config')
            .process {
                Configuration config = Configuration.fromJson(it.in.body)
                log.debug('Got configuration: {}', config)
                it.in.headers['_config'] = it.in.body
                it.in.body = Geometry.getInitialState(config).toJson()
            }
            .to(urlBase + 'update')
            .setBody(header('_config'))
            .to('direct:launch-application')
    }
}
