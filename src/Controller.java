import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    @FXML
    private JFXButton suggestionOne;

    @FXML
    private JFXButton suggestionOTwo;

    @FXML
    private JFXButton suggestionThree;

    @FXML
    private JFXButton suggestionFour;

    @FXML
    private JFXTextArea resultTextArea;

    @FXML
    private JFXCheckBox editableCheckBox;

    @FXML
    private JFXTextField mainTextField;

    DataBase dataBase;

    private List<String> results;

    private int minSuggestionIndex;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setUpDataBase();
        setUpLogic();


//        dataBase.getData().forEach((word, point) -> System.out.println(word + " === " + point));

    }

    public void toggleEditable(ActionEvent actionEvent) {
        resultTextArea.setEditable(editableCheckBox.isSelected());
    }

    public void clearAction(ActionEvent actionEvent) {
        resultTextArea.clear();
    }

    public void copyAction(ActionEvent actionEvent) {
        StringSelection stringSelection = new StringSelection(resultTextArea.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    private void setUpDataBase() {
        Task task = new Task<Void>() {

            @Override public Void call() {

                dataBase = DataBase.getInstance();

                return null;
            }
        };

        new Thread(task).start();
    }

    private void setUpLogic() {

        mainTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            minSuggestionIndex = 0;  // Reset to 0 every time text changes

            Task task = new Task<Void>() {

                @Override public Void call() {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            //Manipulate the text and search for only the last word piece
                            String[] words = newValue.split(" ");

                            //Get results
                            results = dataBase.getData().keySet().stream().filter(p -> p.startsWith(words[words.length - 1])).collect(Collectors.toList());

                            //Sort results (shortest to longest)
                            results.sort(new Comparator());

                            setSuggestions();


                        }
                    });

                    return null;
                }
            };

            new Thread(task).start();
        });

    }

    private void setSuggestions() {  // Try to set words to buttons, set numbers is any exception is thrown
        try {
            suggestionOne.setText(results.get(minSuggestionIndex));
        }catch (IndexOutOfBoundsException e) {
            suggestionOne.setText("1");
        }

        try {
            suggestionOTwo.setText(results.get(minSuggestionIndex + 1));
        }catch (IndexOutOfBoundsException e) {
            suggestionOTwo.setText("2");
        }

        try {
            suggestionThree.setText(results.get(minSuggestionIndex + 2));
        }catch (IndexOutOfBoundsException e) {
            suggestionThree.setText("3");
        }

        try {
            suggestionFour.setText(results.get(minSuggestionIndex + 3));
        }catch (IndexOutOfBoundsException e) {
            suggestionFour.setText("4");
        }
    }

    public void nextSuggestionsAction(ActionEvent actionEvent) {
        if (!suggestionFour.getText().equals("4")) {  // Change only if there are more words to be shown
            minSuggestionIndex += 4;
            setSuggestions();
        }
    }

    public void previousSuggestionsAction(ActionEvent actionEvent) {
        if (minSuggestionIndex > 0) {
            minSuggestionIndex -= 4;
            setSuggestions();
        }
    }
}
