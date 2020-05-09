package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;

public class Controller {

    @FXML
    TextArea inputDataArea;

    @FXML
    TextArea encodeDataArea;

    @FXML
    ToggleButton toggleButtonParity;

    @FXML
    ToggleButton toggleButtonCRC;

    @FXML
    ToggleButton toggleButtonHamming;

    @FXML
    public void showEncodedData(ActionEvent event) {
        if(validationOfInputData()){
            if (toggleButtonCRC.isSelected()){

            }
            else if(toggleButtonHamming.isSelected()){

            }
            else {
                int [] encodeData = Parity.encode(stringToIntArray(inputDataArea.getText()));
                encodeDataArea.setText(intArrayToString(encodeData));
            }
        }
    }

    private int[] stringToIntArray(String text){
        String[] splitedText = text.split("");
        int[] results = new int[ splitedText.length ];

        int i = 0;
        for ( String textValue : splitedText ) {
            results[i] = Integer.parseInt( textValue );
            i++;
        }
        return results;
    }

    private String intArrayToString(int [] array){
        String results = "";
        for (int i = 0 ; i < array.length; i++) results += array[i];
        return results;
    }

    private boolean validationOfInputData(){
        final String regex = "[0|1]*";
        String inputData = inputDataArea.getText();
        if(!inputData.equals("")){
            if(inputData.matches(regex)){
                if(inputData.length()%8 == 0) return true;
                else {
                    showErrorDialog("Dugość kodu powinna być podzielna przez 8!");
                    return false;
                }
            }
            else{
                showErrorDialog("Dane wejściowe powinne zawierać tylko jedynki i zera");
                return false;
            }
        }else {
            showErrorDialog("Dane wejściowe nie mogą być puste");
            return false;
        }
    }

    private void showErrorDialog(String errorMessage){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }




}
