package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    @FXML
    TextArea inputDataArea;

    @FXML
    HBox radioGroupWithKeys;

    @FXML
    TextArea sentEncodeDataArea;

    @FXML
    TextArea receivedEncodeDataArea;

    @FXML
    TextFlow dataWithDetectedErrorsArea;

    @FXML
    TextArea receivedDataArea;

    @FXML
    ToggleButton toggleButtonParity;

    @FXML
    ToggleButton toggleButtonHamming;

    @FXML
    ToggleButton toggleButtonCRC;

    @FXML
    Button buttonDisrupt;

    @FXML
    Button buttonDecode;

    @FXML
    Text numberOfSentDataBit;

    @FXML
    Text numberOfControlBit;

    @FXML
    Text numberOfFoundErrors;

    @FXML
    Text numberOfFixedErrors;

    @FXML
    Text numberOfUndetectedErrors;

    @FXML
    Spinner<Integer> disruptSpinner;

    Parity parity;
    Hamming hamming;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleButtonCRC.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            radioGroupWithKeys.setDisable(!radioGroupWithKeys.isDisable());
        }));
        Text text = new Text("Zakodowana wiadomość po korekcji");
        text.setStyle("-fx-fill: #ababab;-fx-font-weight:normal;");;
        dataWithDetectedErrorsArea.getChildren().add(text);
    }


    @FXML
    public void showEncodedData(ActionEvent event) {
        if(validationOfInputData()){
            if (toggleButtonCRC.isSelected()){

            }
            else if(toggleButtonHamming.isSelected()){
                hamming = new Hamming(stringToIntArray(inputDataArea.getText()));
                int [] encodeData = hamming.encode();
                sentEncodeDataArea.setText(intArrayToString(encodeData));
                receivedEncodeDataArea.setText(intArrayToString(encodeData));
            }
            else {
                parity = new Parity(stringToIntArray(inputDataArea.getText()));
                int [] encodeData = parity.encode();
                sentEncodeDataArea.setText(intArrayToString(encodeData));
                receivedEncodeDataArea.setText(intArrayToString(encodeData));
            }
        }
        SpinnerValueFactory<Integer> spv = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,sentEncodeDataArea.getText().length(),0);
        disruptSpinner.setValueFactory(spv);
        disruptSpinner.setDisable(false);
        buttonDecode.setDisable(false);
        buttonDisrupt.setDisable(false);
    }

    @FXML
    public void showDisruptData(ActionEvent event) {
       int [] disruptedData = disruptData(stringToIntArray(sentEncodeDataArea.getText()), disruptSpinner.getValue());
       receivedEncodeDataArea.setText(intArrayToString(disruptedData));
    }

    @FXML
    public void showDecodedData(ActionEvent event) {
        if (toggleButtonCRC.isSelected()){

        }
        else if(toggleButtonHamming.isSelected()){

        }
        else {
            dataWithDetectedErrorsArea.getChildren().clear();
            Map<Integer, String> detectedBits = parity.detectErrors(stringToIntArray(receivedEncodeDataArea.getText()));
            showColoredData(detectedBits);
            StringBuilder detectedData = new StringBuilder();
            for (Node node : dataWithDetectedErrorsArea.getChildren())
                if (node instanceof Text)
                    detectedData.append(((Text) node).getText());

            int [] decodedData = parity.decode(stringToIntArray(detectedData.toString()));
            receivedDataArea.setText(intArrayToString(decodedData));
        }
        showStatistics();
    }

    private void showStatistics(){
        StringBuilder detectedData = new StringBuilder();
        for (Node node : dataWithDetectedErrorsArea.getChildren())
            if (node instanceof Text)
                detectedData.append(((Text) node).getText());
        String encodedData = sentEncodeDataArea.getText();

        int [] detectedDataAsIntArray = stringToIntArray(detectedData.toString());
        int [] encodedDataAsIntArray = stringToIntArray(encodedData);

        int allErrors = 0;

        for(int i=0; i<encodedData.length(); i++)
            if(encodedDataAsIntArray[i] != detectedDataAsIntArray[i]) allErrors++;

        if (toggleButtonCRC.isSelected()){

        }
        else if(toggleButtonHamming.isSelected()){

        }
        else {
            numberOfSentDataBit.setText(String.valueOf(encodedData.length()-encodedData.length()/9));
            numberOfControlBit.setText(String.valueOf(encodedData.length()/9));
            numberOfFoundErrors.setText(String.valueOf(parity.getErrors()));
            numberOfFixedErrors.setText("0");
            numberOfUndetectedErrors.setText(String.valueOf(allErrors-parity.getErrors()));
        }

    }

    private void showColoredData(Map<Integer, String> detectedBits){
        String detectedData = receivedEncodeDataArea.getText();
        for(int i=0; i<detectedData.length(); i++){
            for (Map.Entry<Integer, String> entry : detectedBits.entrySet()) {
                if(i == entry.getKey()) {
                    String color= "#000000";
                    switch (entry.getValue()){
                        case "correctDataBit": color = "#12af12"; break;
                        case "wrongDataBit": color = "#fd6868"; break;
                        case "uncertainDataBit": color = "#ffbf58"; break;
                        case "fixedDataBit": color = "#388fa6"; break;
                        case "correctControlBit": color = "#0b5f0b"; break;
                        case "wrongControlBit": color = "#bd1010"; break;
                        case "uncertainControlBit": color = "#b58105"; break;
                        case "fixedControlBit": color = "#0d355c"; break;
                    }
                    Text singleBit = new Text();
                    singleBit.setText(String.valueOf(detectedData.charAt(i)));
                    singleBit.setStyle("-fx-fill: "+ color +";-fx-font-weight:bold;");
                    dataWithDetectedErrorsArea.getChildren().add(i, singleBit);
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
