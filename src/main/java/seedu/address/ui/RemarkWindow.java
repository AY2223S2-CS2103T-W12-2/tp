package seedu.address.ui;

import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;

/**
 * The UI component that is responsible for receiving remarks inputs.
 */
public class RemarkWindow extends UiPart<Stage> {
    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "RemarkWindow.fxml";
    private final KeyCodeCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);

    @FXML
    private TextArea textArea;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public RemarkWindow(Stage root) {
        super(FXML, root);
    }

    /**
     * Creates a new HelpWindow.
     */
    public RemarkWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public String showAndGetText(String existingRemark) {
        final String[] content = new String[1];
        logger.fine("Showing help page about the application.");
//        textArea.setOnKeyPressed(ctrlSHandler);
        textArea.setText(existingRemark);
        textArea.positionCaret(existingRemark.length());
//        textArea.setOnKeyPressed(ctrlSHandler);
//        getRoot().showAndWait();
//        getRoot().showAndWait();
//        getRoot().show();
        EventHandler<KeyEvent> ctrlSHandler = new EventHandler<>() {
            @Override
            public void handle(KeyEvent event) {
                if (ctrlS.match(event)) {
                    event.consume();
//                    content[0] = textArea.getText();
                    getRoot().close();
                }
            }
        };
        textArea.setOnKeyPressed(ctrlSHandler);
//        getRoot().showAndWait();
        getRoot().showAndWait();
//        return textArea.getText();
        return textArea.getText();
    }


    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }
}
