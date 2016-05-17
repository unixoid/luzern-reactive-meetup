package meetup.pong.logic;

import lombok.extern.slf4j.Slf4j;
import meetup.pong.PongException;
import org.apache.commons.lang3.NotImplementedException;

import static java.lang.Math.*;

/**
 * @author Dmytro Rud
 */
@Slf4j
public class Geometry {

    private enum AngleChangeAlgorithm {MIRROR, PROPORTIONAL}

    private static final AngleChangeAlgorithm angleChangeAlgorithm = AngleChangeAlgorithm.PROPORTIONAL;


    public static State getInitialState(Configuration config) {
        return new State(
                0L,
                (config.getFieldHeight() - config.getCaretHeight() + 1) / 2.0,
                (config.getFieldHeight() - config.getCaretHeight() + 1) / 2.0,
                config.getFieldWidth() / 2.0,
                config.getFieldHeight() / 2.0,
                cos(config.getBallInitialAngle()),
                sin(config.getBallInitialAngle())
        );
    }

    public static State newState(
            Configuration config,
            State oldState,
            double leftCaretY,
            double rightCaretY) throws PongException
    {
        int upperBoundaryY = config.getBallRadius() - 1;
        int lowerBoundaryY = config.getFieldHeight() - config.getBallRadius();
        double ballYCoeff = oldState.getBallYCoeff();
        double ballCenterY = oldState.getBallCenterY() - config.getBallVelocity() * ballYCoeff;
        if (ballCenterY < config.getBallRadius()) {
            // bounced on the top
            ballCenterY = 2 * upperBoundaryY - ballCenterY;
            ballYCoeff = -ballYCoeff;
        } else if (ballCenterY >= config.getFieldHeight() - config.getBallRadius()) {
            // bounced on the bottom
            ballCenterY = 2 * lowerBoundaryY - ballCenterY;
            ballYCoeff = -ballYCoeff;
        }

        int leftBoundaryX = config.getBallRadius() - 1;
        int rightBoundaryX = config.getFieldWidth() - config.getBallRadius();
        double ballXCoeff = oldState.getBallXCoeff();
        double ballCenterX = oldState.getBallCenterX() + config.getBallVelocity() * ballXCoeff;
        if (ballCenterX < leftBoundaryX) {
            // bounced on the left
            if ((ballCenterY < leftCaretY) || (ballCenterY >= leftCaretY + config.getCaretHeight())) {
                throw new PongException("Left player lost");
            }
            ballCenterX = 2 * leftBoundaryX - ballCenterX;

            switch (angleChangeAlgorithm) {
                case MIRROR:
                    ballXCoeff = -ballXCoeff;
                    break;
                case PROPORTIONAL:
                    double newAngle = proportionalAngle(leftCaretY, ballCenterY, config);
                    ballXCoeff = cos(newAngle);
                    ballYCoeff = signum(ballYCoeff) * sin(newAngle);
                    break;
                default:
                    throw new NotImplementedException("Algorithm " + angleChangeAlgorithm + " not implemented");
            }

        } else if (ballCenterX > rightBoundaryX) {
            // bounced on the right
            if ((ballCenterY < rightCaretY) || (ballCenterY >= rightCaretY + config.getCaretHeight())) {
                throw new PongException("Right player lost");
            }
            ballCenterX = 2 * rightBoundaryX - ballCenterX;

            switch (angleChangeAlgorithm) {
                case MIRROR:
                    ballXCoeff = -ballXCoeff;
                    break;
                case PROPORTIONAL:
                    double newAngle = proportionalAngle(rightCaretY, ballCenterY, config);
                    ballXCoeff = -cos(newAngle);
                    ballYCoeff = signum(ballYCoeff) * sin(newAngle);
                    break;
                default:
                    throw new NotImplementedException("Algorithm " + angleChangeAlgorithm + " not implemented");
            }
        }

        return new State(
                oldState.getTickNo() + 1L,
                leftCaretY,
                rightCaretY,
                ballCenterX,
                ballCenterY,
                ballXCoeff,
                ballYCoeff);
    }


    private static double proportionalAngle(double caretY, double ballCenterY, Configuration config) {
        double halfCaretHeight = config.getCaretHeight() / 2.0;
        double angle = abs(caretY + halfCaretHeight - ballCenterY) / halfCaretHeight * 0.5 * 0.9 * PI;
        log.debug("In proportionalAngle(): caretY={}, ballCenterY={} --> angle={}", caretY, ballCenterY, angle);
        return angle;
    }

}
