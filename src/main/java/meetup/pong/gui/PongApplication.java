package meetup.pong.gui;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Getter;
import meetup.pong.PongException;
import meetup.pong.camel.AbstractPongRouteBuilder;
import meetup.pong.logic.CaretSide;
import meetup.pong.logic.Configuration;
import meetup.pong.logic.Geometry;
import meetup.pong.logic.State;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Dmytro Rud
 */
public class PongApplication extends Application {
    public static final String CONFIG_PARAM_NAME = "config";
    public static final String CARET_SIDE_PARAM_NAME = "caretSide";

    private static final int CARET_WIDTH = 20;

    @Getter private final AtomicBoolean started = new AtomicBoolean(false);
    @Getter private final AtomicBoolean finished = new AtomicBoolean(false);

    // actually write-once, but cannot be made final for technical reasons
    private Configuration config;
    private CaretSide caretSide;

    // shapes
    private Circle ball;
    private EnumMap<CaretSide, Rectangle> carets;
    private Text errorMessage;

    // state
    private double oldCaretY;
    private ObjectProperty<State> stateProperty;


    /**
     * Receives the opponent's state, updates and returns own state.
     * @param theirState
     *      State of the opponent.
     * @return
     *      Updated own state.
     */
    public State exchangeState(State theirState) throws InterruptedException {
        if (finished.get()) {
            return null;
        }

        try {
            State state = stateProperty.get();

            if ((theirState.getTickNo() < state.getTickNo()) || (theirState.getTickNo() > state.getTickNo() + 1)) {
                throw new PongException("Synchronization lost, my tick = " +
                        state.getTickNo() + ", their tick = " + theirState.getTickNo());
            }

            double theirOldCaretY = state.getCaretY(caretSide.other());
            double theirNewCaretY = theirState.getCaretY(caretSide.other());
            if (Math.abs(theirOldCaretY - theirNewCaretY) > config.getMaxCaretVelocity()) {
                throw new PongException("Opponent cheats, their caret changed from " +
                        theirOldCaretY + " to " + theirNewCaretY);
            }

            double caretY = carets.get(caretSide).getY();
            oldCaretY = caretY;
            State newState = Geometry.newState(
                    config,
                    state,
                    (caretSide == CaretSide.LEFT) ? caretY : theirNewCaretY,
                    (caretSide == CaretSide.LEFT) ? theirNewCaretY : caretY);

            Thread.sleep(config.getTickDelay());

            stateProperty.set(newState);
            return newState;

        } catch (PongException e) {
            finished.set(true);
            errorMessage.setText(e.getMessage());
            return theirState;
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
        AbstractPongRouteBuilder.setApplication(this);

        Map<String, String> parameters = getParameters().getNamed();
        config = Configuration.fromJson(parameters.get(CONFIG_PARAM_NAME));
        caretSide = CaretSide.valueOf(parameters.get(CARET_SIDE_PARAM_NAME));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, config.getFieldWidth() + 2 * CARET_WIDTH, config.getFieldHeight());

        State state = Geometry.getInitialState(config);
        stateProperty = new SimpleObjectProperty<>(state);

        ball = new Circle(CARET_WIDTH + state.getBallCenterX(), state.getBallCenterY(), config.getBallRadius());
        ball.setFill(Color.BLACK);

        carets = new EnumMap<>(CaretSide.class);
        carets.put(CaretSide.LEFT, new Rectangle(0, state.getLeftCaretY(), CARET_WIDTH, config.getCaretHeight()));
        carets.put(CaretSide.RIGHT, new Rectangle(CARET_WIDTH + config.getFieldWidth(), state.getRightCaretY(), CARET_WIDTH, config.getCaretHeight()));

        oldCaretY = carets.get(caretSide).getY();
        carets.get(caretSide).setFill(Color.GREEN);
        carets.get(caretSide.other()).setFill(Color.BLACK);

        errorMessage = new Text(CARET_WIDTH + config.getFieldWidth() / 2, config.getFieldHeight() / 2, "");
        errorMessage.setFont(new Font(20));
        errorMessage.setFill(Color.RED);

        root.getChildren().add(ball);
        root.getChildren().addAll(carets.values());
        root.getChildren().add(errorMessage);

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                double caretY = carets.get(caretSide).getY();
                switch (event.getCode()) {
                    case DOWN:
                        caretY = Math.min(Math.min(
                                caretY + config.getCaretVelocity(),
                                config.getFieldHeight() - config.getCaretHeight()),
                                oldCaretY + config.getMaxCaretVelocity());
                        carets.get(caretSide).setY(caretY);
                        break;
                    case UP:
                        caretY = Math.max(Math.max(
                                caretY - config.getCaretVelocity(),
                                0),
                                oldCaretY - config.getMaxCaretVelocity());
                        carets.get(caretSide).setY(caretY);
                        break;
                    case ESCAPE:
                        finished.set(true);
                }
            }
        });

        stateProperty.addListener(new ChangeListener<State>() {
            @Override
            public void changed(ObservableValue<? extends State> observable, State oldState, State newState) {
                ball.setCenterX(CARET_WIDTH + newState.getBallCenterX());
                ball.setCenterY(newState.getBallCenterY());
                carets.get(caretSide.other()).setY(newState.getCaretY(caretSide.other()));
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("I am a caret on the " + caretSide.toString());
        primaryStage.show();

        started.set(true);
    }


    public static void main(String[] args) throws Exception {
        launch(args);
    }
}
