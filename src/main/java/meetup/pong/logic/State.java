package meetup.pong.logic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.IOException;

/**
 * @author Dmytro Rud
 */
public class State {
    @JsonProperty
    @Getter private long tickNo;

    @JsonProperty
    @Getter private double leftCaretY;
    @JsonProperty
    @Getter private double rightCaretY;

    @JsonProperty
    @Getter private double ballCenterX;
    @JsonProperty
    @Getter private double ballCenterY;
    @JsonProperty
    @Getter private double ballXCoeff;
    @JsonProperty
    @Getter private double ballYCoeff;

    private State() {
        // for JSON deserialization only
    }

    public State(
            long tickNo,
            double leftCaretY,
            double rightCaretY,
            double ballCenterX,
            double ballCenterY,
            double ballXCoeff,
            double ballYCoeff)
    {
        this.tickNo = tickNo;
        this.leftCaretY = leftCaretY;
        this.rightCaretY = rightCaretY;
        this.ballCenterX = ballCenterX;
        this.ballCenterY = ballCenterY;
        this.ballXCoeff = ballXCoeff;
        this.ballYCoeff = ballYCoeff;
    }

    public double getCaretY(CaretSide caretSide) {
        return (caretSide == CaretSide.LEFT) ? leftCaretY : rightCaretY;
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    public static State fromJson(String s) throws IOException {
        return new ObjectMapper().readValue(s, State.class);
    }

    @Override
    public String toString() {
        return "State{" +
                "tickNo=" + tickNo +
                ", leftCaretY=" + leftCaretY +
                ", rightCaretY=" + rightCaretY +
                ", ballCenterX=" + ballCenterX +
                ", ballCenterY=" + ballCenterY +
                ", ballXCoeff=" + ballXCoeff +
                ", ballYCoeff=" + ballYCoeff +
                '}';
    }
}
