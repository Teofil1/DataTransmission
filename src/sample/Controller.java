package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.w3c.dom.ls.LSOutput;

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
        dataWithDetectedErrorsArea.getChildren().clear();

        if (toggleButtonCRC.isSelected()){

        }
        else if(toggleButtonHamming.isSelected()){
            int [] detectedData = hamming.detectErrors(stringToIntArray(receivedEncodeDataArea.getText()));
            showColoredData(hamming.getDetectedBits(), detectedData);
            int [] decodedData = hamming.decode(detectedData);
            receivedDataArea.setText(intArrayToString(decodedData));

            showStatistics(stringToIntArray(receivedEncodeDataArea.getText()),
                           stringToIntArray(sentEncodeDataArea.getText()),
                           stringToIntArray(inputDataArea.getText()));
        }
        else {
            int [] detectedData = parity.detectErrors(stringToIntArray(receivedEncodeDataArea.getText()));
            showColoredData(parity.getDetectedBits(), detectedData);
            int [] decodedData = parity.decode(detectedData);
            receivedDataArea.setText(intArrayToString(decodedData));

            showStatistics(stringToIntArray(receivedEncodeDataArea.getText()),
                    stringToIntArray(sentEncodeDataArea.getText()),
                    stringToIntArray(inputDataArea.getText()));
        }
    }

    /*private int[] getTextFromDetectedErrorsArea(){
        StringBuilder detectedData = new StringBuilder();
        for (Node node : dataWithDetectedErrorsArea.getChildren())
            if (node instanceof Text)
                detectedData.append(((Text) node).getText());
        return stringToIntArray(detectedData.toString());
    }*/



    private void showStatistics(int [] receivedEncodeData, int [] sentEncodeData, int [] inputData){
        int numberOfAllErrors = 0;
        numberOfSentDataBit.setText(String.valueOf(receivedEncodeData.length-(receivedEncodeData.length-inputData.length)));
        numberOfControlBit.setText(String.valueOf(receivedEncodeData.length-inputData.length));
        for(int i=0; i<sentEncodeData.length; i++)
            if(sentEncodeData[i] != receivedEncodeData[i]) numberOfAllErrors++;
        if (toggleButtonCRC.isSelected()){

        }
        else if(toggleButtonHamming.isSelected()){
            numberOfFoundErrors.setText(String.valueOf(hamming.getNumberOfErrors()));
            numberOfFixedErrors.setText(String.valueOf(hamming.getNumberOfFixedBit()));
            numberOfUndetectedErrors.setText(String.valueOf(numberOfAllErrors-hamming.getNumberOfErrors()));
        }
        else {
            numberOfFoundErrors.setText(String.valueOf(parity.getNumberOfErrors()));
            numberOfFixedErrors.setText("0");
            numberOfUndetectedErrors.setText(String.valueOf(numberOfAllErrors-parity.getNumberOfErrors()));
        }

    }

    private void showColoredData(Map<Integer, String> detectedBits, int [] data){
        String detectedData = intArrayToString(data);
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

    public int[] disruptData(int[] data, int numberOfDisraptedBits){
        int[] disruptedData = Arrays.copyOf(data, data.length);
        Random rand = new Random();
        List<Integer> listWithAllIndexesOfdata = IntStream.rangeClosed(0, data.length-1).boxed().collect(Collectors.toList());
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
