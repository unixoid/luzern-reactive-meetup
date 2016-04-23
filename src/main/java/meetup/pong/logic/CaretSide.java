package meetup.pong.logic;

/**
 * @author Dmytro Rud
 */
public enum CaretSide {
    LEFT, RIGHT;

    public CaretSide other() {
        return (this == LEFT) ? RIGHT : LEFT;
    }
}
