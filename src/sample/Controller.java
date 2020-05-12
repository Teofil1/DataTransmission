package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller{

    @FXML
    TextArea inputDataArea;

    @FXML
    TextArea sentEncodeDataArea;

    @FXML
    TextArea receivedEncodeDataArea;

    @FXML
    TextFlow dataWithDetectedErrorsFlow;

    @FXML
    ToggleButton toggleButtonParity;

    @FXML
    ToggleButton toggleButtonCRC;

    @FXML
    Button buttonDisrupt;

    @FXML
    Button buttonDecode;

    @FXML
    ToggleButton toggleButtonHamming;

    @FXML
    Spinner<Integer> disruptSpinner;

    Parity parity;

   /* @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }*/

    @FXML
    public void showEncodedData(ActionEvent event) {
        if(validationOfInputData()){
            if (toggleButtonCRC.isSelected()){

            }
            else if(toggleButtonHamming.isSelected()){

            }
            else {
                parity = new Parity(stringToIntArray(inputDataArea.getText()));
                int [] encodeData = parity.encode();
                sentEncodeDataArea.setText(intArrayToString(encodeData));
                receivedEncodeDataArea.setText(intArrayToString(encodeData));

                SpinnerValueFactory<Integer> spv = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,sentEncodeDataArea.getText().length(),0);
                disruptSpinner.setValueFactory(spv);
                disruptSpinner.setDisable(false);
                buttonDecode.setDisable(false);
                buttonDisrupt.setDisable(false);
            }
        }
    }

    @FXML
    public void showDisruptData(ActionEvent event) {
       int [] disruptedData = disruptData(stringToIntArray(sentEncodeDataArea.getText()), disruptSpinner.getValue());
       receivedEncodeDataArea.setText(intArrayToString(disruptedData));
    }

    @FXML
    public void showDataWithDetectedErrors(ActionEvent event) {
        if (toggleButtonCRC.isSelected()){

        }
        else if(toggleButtonHamming.isSelected()){

        }
        else {
            dataWithDetectedErrorsFlow.getChildren().clear();
            Map<Integer, String> detectedBits = parity.detectErrors(stringToIntArray(receivedEncodeDataArea.getText()));
            String detectedData = receivedEncodeDataArea.getText();
            showColoredData(detectedBits, detectedData);
        }
    }
//
    private void showColoredData(Map<Integer, String> detectedBits, String data){
        for(int i=0; i<data.length(); i++){
            for (Map.Entry<Integer, String> entry : detectedBits.entrySet()) {
                if(i == entry.getKey()) {
                    String color= "#000000";
                    switch (entry.getValue()){
                        case "correctDataBit": color = "#008000"; break;
                        case "wrongDataBit": color = "#FF0000"; break;
                        case "uncertainDataBit": color = "#FFA500"; break;
                        case "correctControlBit": color = "#20A0C7"; break;
                        case "wrongControlBit": color = "#710909"; break;
                        case "uncertainControlBit": color = "#FFFF00"; break;
                    }
                    Text singleBit = new Text();
                    singleBit.setText(String.valueOf(data.charAt(i)));
                    singleBit.setStyle("-fx-fill: "+ color +";-fx-font-weight:bold;");
                    dataWithDetectedErrorsFlow.getChildren().add(i, singleBit);
                }
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

    public int[] disruptData(int[] datas, int numberOfDisraptedBits){
        int[] disruptedData = Arrays.copyOf(datas, datas.length);
        Random rand = new Random();
        List<Integer> listWithAllIndexesOfdata = IntStream.rangeClosed(0, datas.length-1).boxed().collect(Collectors.toList());
        List<Integer> indexesOfDisraptedBits = new ArrayList<>();

        for (int i=0; i<numberOfDisraptedBits; i++){
            int indexOfDisruptedBit = rand.nextInt(listWithAllIndexesOfdata.size());
            indexesOfDisraptedBits.add(listWithAllIndexesOfdata.get(indexOfDisruptedBit));
            listWithAllIndexesOfdata.remove(indexOfDisruptedBit);
        }

        for (int i=0; i<indexesOfDisraptedBits.size(); i++){
            if(disruptedData[indexesOfDisraptedBits.get(i)] == 0) disruptedData[indexesOfDisraptedBits.get(i)] = 1;
            else disruptedData[indexesOfDisraptedBits.get(i)] = 0;
        }
        return disruptedData;
    }

}
