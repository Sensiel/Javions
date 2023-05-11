package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public final class StatusLineController {

    private final IntegerProperty aircraftCountProperty = new SimpleIntegerProperty();
    private final LongProperty messageCountProperty = new SimpleLongProperty();

    final BorderPane scene;

    public StatusLineController(){
        scene = new BorderPane();
        scene.getStylesheets().add("status.css");
        scene.setLeft(aircraftCount());
        scene.setRight(messageCount());
    }
    public Pane pane(){
        return scene;
    }

    public IntegerProperty getAircraftCountProperty(){
        return aircraftCountProperty;
    }

    public LongProperty getMessageCountProperty(){
        return messageCountProperty;
    }

    private Text aircraftCount(){
        Text result = new Text();
        result.textProperty().bind(Bindings.format("Aéronefs visibles : %d", aircraftCountProperty.get()));
        return result;
    }

    private Text messageCount(){
        Text result = new Text();
        result.textProperty().bind(Bindings.format("Messages reçus : %d", messageCountProperty.get()));
        return result;
    }
}
