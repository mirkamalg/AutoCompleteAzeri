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
    private JFXButton suggestionTwo;

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
        copyToClipBoard(resultTextArea.getText());
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

                            //If empty, reset suggestion buttons
                            if (newValue.isEmpty()) {
                                suggestionOne.setText("1");
                                suggestionTwo.setText("2");
                                suggestionThree.setText("3");
                                suggestionFour.setText("4");
                            } else {
                                //Manipulate the text and search for only the last word piece
                                String[] words = newValue.split(" ");

                                //Get results
                                results = dataBase.getData().keySet().stream().filter(p -> p.startsWith(words[words.length - 1])).collect(Collectors.toList());

                                //Sort results (shortest to longest)
                                results.sort(new Comparator());

                                setSuggestions();
                            }


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
            suggestionTwo.setText(results.get(minSuggestionIndex + 1));
        }catch (IndexOutOfBoundsException e) {
            suggestionTwo.setText("2");
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

    public void suggestionOneClicked(ActionEvent actionEvent) {
        String suggestion = suggestionOne.getText();
        if (!suggestion.equals("1")) {
            applyCompletion(suggestion);
            copyToClipBoard(suggestion);
        }
    }

    public void suggestionTwoClicked(ActionEvent actionEvent) {
        String suggestion = suggestionTwo.getText();
        if (!suggestion.equals("2")) {
            applyCompletion(suggestion);
            copyToClipBoard(suggestion);
        }
    }

    public void suggestionThreeClicked(ActionEvent actionEvent) {
        String suggestion = suggestionThree.getText();
        if (!suggestion.equals("3")) {
            applyCompletion(suggestion);
            copyToClipBoard(suggestion);
        }
    }

    public void suggestionFourClicked(ActionEvent actionEvent) {
        String suggestion = suggestionFour.getText();
        if (!suggestion.equals("4")) {
            applyCompletion(suggestion);
            copyToClipBoard(suggestion);
        }
    }

    private void applyCompletion(String chosenSuggestion) {
        //TODO Find a better way for completions

        String text = mainTextField.getText();
        String[] words = text.split(" ");

        String newText = text.replace(words[words.length - 1], chosenSuggestion);
        resultTextArea.setText(resultTextArea.getText() + newText + " ");
        mainTextField.clear();
    }

    private void copyToClipBoard(String text) {
        Task task = new Task<Void>() {

            @Override public Void call() {

                StringSelection stringSelection = new StringSelection(text);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);

                return null;
            }
        };

        new Thread(task).start();
    }
}
