package meetup.pong.logic;

import lombok.extern.slf4j.Slf4j;
import meetup.pong.PongException;

import static java.lang.Math.*;

/**
 * @author Dmytro Rud
 */
public class Geometry {

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
        double ballCenterY = oldState.getBallCenterY() - config.getBallInitialAngle() * ballYCoeff;
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
            if ((ballCenterY < leftCaretY) || (ballCenterY >= leftCaretY + config.getCaretHeight())) {
                throw new PongException("Left player lost.");
            }
            ballCenterX = 2 * leftBoundaryX - ballCenterX;
            ballXCoeff = -ballXCoeff;
        } else if (ballCenterX > rightBoundaryX) {
            if ((ballCenterY < rightCaretY) || (ballCenterY >= rightCaretY + config.getCaretHeight())) {
                throw new PongException("Right player lost.");
            }
            ballCenterX = 2 * rightBoundaryX - ballCenterX;
            ballXCoeff = -ballXCoeff;
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

}
