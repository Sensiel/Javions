package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * Manage the status line
 * @author Zablocki Victor (361602)
 */
public final class StatusLineController {
    private final IntegerProperty aircraftCountProperty = new SimpleIntegerProperty();
    private final LongProperty messageCountProperty = new SimpleLongProperty();
    final BorderPane scene;

    /**
     * Public Consructor that constructs the scene
     */
    public StatusLineController(){
        scene = new BorderPane();
        scene.getStylesheets().add("status.css");
        scene.setLeft(aircraftCount());
        scene.setRight(messageCount());
    }

    /**
     * Getter for the JavaFX pane
     * @return the pane containing the status line
     */
    public Pane pane(){
        return scene;
    }

    /**
     * Getter for the property of the number of aircraft currently visible
     * @return the editable property containing the number of aircraft currently visible
     */
    public IntegerProperty getAircraftCountProperty(){
        return aircraftCountProperty;
    }

    /**
     * Getter for the property of the total number of received aircraft
     * @return the editable property of the number of messages received since the beginning of the program execution
     */
    public LongProperty getMessageCountProperty(){
        return messageCountProperty;
    }

    private Text aircraftCount(){
        Text result = new Text();
        result.textProperty().bind(Bindings.format("Aéronefs visibles : %d", aircraftCountProperty));
        return result;
    }

    private Text messageCount(){
        Text result = new Text();
        result.textProperty().bind(Bindings.format("Messages reçus : %d", messageCountProperty));
        return result;
    }
}
