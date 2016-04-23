package meetup.pong.logic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.IOException;

import static org.apache.commons.lang3.Validate.isTrue;

/**
 * @author Dmytro Rud
 */
public class Configuration {

    /** Tick delay in ms */
    @JsonProperty
    @Getter private long tickDelay;

    /** Field width in pixel */
    @JsonProperty
    @Getter private int fieldWidth;
    /** Field height in pixel */
    @JsonProperty
    @Getter private int fieldHeight;

    /** Caret width in pixel */
    @JsonProperty
    @Getter private int caretHeight;

    /** Caret velocity, pixel per key press */
    @JsonProperty
    @Getter private int caretVelocity;

    /** Maximal caret velocity, pixel per tick */
    @JsonProperty
    @Getter private int maxCaretVelocity;

    /**
     * Radius of the ball, in pixel.
     * The ball always has a central pixel, thus its diameter in pixel equals 2*r-1
     */
    @JsonProperty
    @Getter private int ballRadius;

    /** Initial direction of the ball, in radians */
    @JsonProperty
    @Getter private double ballInitialAngle;

    /** Velocity of the ball, in pixel per tick */
    @JsonProperty
    @Getter private double ballVelocity;

    private Configuration() {
        // for JSON deserialization only
    }

    public Configuration(
            long tickDelay,
            int fieldWidth,
            int fieldHeight,
            int caretHeight,
            int caretVelocity,
            int maxCaretVelocity,
            int ballRadius,
            double ballInitialAngle,
            double ballVelocity)
    {
        isTrue(tickDelay > 0L);

        isTrue(fieldWidth > 0);
        isTrue(fieldHeight > 0);

        isTrue(caretHeight > 0);
        isTrue(caretHeight < fieldHeight);

        isTrue(caretVelocity > 0);
        isTrue(maxCaretVelocity > 0);
        isTrue(caretVelocity <= maxCaretVelocity);

        isTrue(ballRadius > 0);
        isTrue(ballRadius * 2 - 1 < fieldWidth);
        isTrue(ballRadius * 2 - 1 < fieldHeight);

        ballInitialAngle = ballInitialAngle % (2.00 * Math.PI);
        isTrue(ballInitialAngle != 0.00);
        isTrue(ballInitialAngle != Math.PI * 0.50);
        isTrue(ballInitialAngle != Math.PI);
        isTrue(ballInitialAngle != Math.PI * 1.50);

        isTrue(ballVelocity > 0.00);
        isTrue(ballVelocity < fieldWidth / 2);
        isTrue(ballVelocity < fieldHeight / 2);

        this.tickDelay = tickDelay;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.caretHeight = caretHeight;
        this.caretVelocity = caretVelocity;
        this.maxCaretVelocity = maxCaretVelocity;
        this.ballRadius = ballRadius;
        this.ballInitialAngle = ballInitialAngle;
        this.ballVelocity = ballVelocity;
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    public static Configuration fromJson(String s) throws IOException {
        return new ObjectMapper().readValue(s, Configuration.class);
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "tickDelay=" + tickDelay +
                ", fieldWidth=" + fieldWidth +
                ", fieldHeight=" + fieldHeight +
                ", caretHeight=" + caretHeight +
                ", caretVelocity=" + caretVelocity +
                ", maxCaretVelocity=" + maxCaretVelocity +
                ", ballRadius=" + ballRadius +
                ", ballInitialAngle=" + ballInitialAngle +
                ", ballVelocity=" + ballVelocity +
                '}';
    }
}
