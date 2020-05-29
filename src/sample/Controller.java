package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    RadioButton crc12RadioButton;

    @FXML
    RadioButton crc16RadioButton;

    @FXML
    RadioButton sdlcRadioButton;

    @FXML
    RadioButton atmRadioButton;

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

    @FXML
    Spinner<Integer> generateSpinner;

    Parity parity;
    Hamming hamming;
    CRC crc;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> spv = new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 128, 8, 8);
        generateSpinner.setValueFactory(spv);
        toggleButtonCRC.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            radioGroupWithKeys.setDisable(!radioGroupWithKeys.isDisable());
        }));
        Text text = new Text("Zakodowana wiadomość po korekcji");
        text.setStyle("-fx-fill: #ababab;-fx-font-weight:normal;");
        ;
        dataWithDetectedErrorsArea.getChildren().add(text);
    }

    @FXML
    public void generateInputData(ActionEvent event) {
        String inputData = "";
        Random rand = new Random();
        for (int i = 0; i < generateSpinner.getValue(); i++) {
            inputData += rand.nextInt(2);
        }
        inputDataArea.setText(inputData);
    }


    @FXML
    public void showEncodedData(ActionEvent event) {
        if (validationOfInputData()) {
            int[] encodeData;
            if (toggleButtonCRC.isSelected()) {
                String key;
                if (crc12RadioButton.isSelected()) key = "1100000001111";
                else if (crc16RadioButton.isSelected()) key = "11000000000000101";
                else if (sdlcRadioButton.isSelected()) key = "10001000000100001";
                else key = "100000111";

                crc = new CRC(stringToIntArray(inputDataArea.getText()), stringToIntArray(key));
                encodeData = crc.encode();
            } else if (toggleButtonHamming.isSelected()) {
                hamming = new Hamming(stringToIntArray(inputDataArea.getText()));
                encodeData = hamming.encode();
            } else {
                parity = new Parity(stringToIntArray(inputDataArea.getText()));
                encodeData = parity.encode();
            }
            sentEncodeDataArea.setText(intArrayToString(encodeData));
            receivedEncodeDataArea.setText(intArrayToString(encodeData));
            SpinnerValueFactory<Integer> spv = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, sentEncodeDataArea.getText().length(), 0);
            disruptSpinner.setValueFactory(spv);
            disruptSpinner.setDisable(false);
            buttonDecode.setDisable(false);
            buttonDisrupt.setDisable(false);
        }
    }

    @FXML
    public void showDisruptData(ActionEvent event) {
        int[] disruptedData = disruptData(stringToIntArray(sentEncodeDataArea.getText()), disruptSpinner.getValue());
        receivedEncodeDataArea.setText(intArrayToString(disruptedData));
    }

    @FXML
    public void showDecodedData(ActionEvent event) {
        if(validationOfDisruptData()) {
            dataWithDetectedErrorsArea.getChildren().clear();
            if (toggleButtonCRC.isSelected()) {
                int[] detectedData = crc.detectErrors(stringToIntArray(receivedEncodeDataArea.getText()));
                showColoredData(crc.getDetectedBits(), detectedData);
                int[] decodedData = crc.decode(detectedData);
                receivedDataArea.setText(intArrayToString(decodedData));
            } else if (toggleButtonHamming.isSelected()) {
                int[] detectedData = hamming.detectErrors(stringToIntArray(receivedEncodeDataArea.getText()));
                showColoredData(hamming.getDetectedBits(), detectedData);
                int[] decodedData = hamming.decode(detectedData);
                receivedDataArea.setText(intArrayToString(decodedData));
            } else {
                int[] detectedData = parity.detectErrors(stringToIntArray(receivedEncodeDataArea.getText()));
                showColoredData(parity.getDetectedBits(), detectedData);
                int[] decodedData = parity.decode(detectedData);
                receivedDataArea.setText(intArrayToString(decodedData));
            }
            showStatistics(stringToIntArray(receivedEncodeDataArea.getText()),
                    stringToIntArray(sentEncodeDataArea.getText()),
                    stringToIntArray(inputDataArea.getText()));
        }
    }


    @FXML
    public void clearAll(ActionEvent event) {
        inputDataArea.setText("");
        sentEncodeDataArea.setText("");
        receivedEncodeDataArea.setText("");
        dataWithDetectedErrorsArea.getChildren().clear();
        receivedDataArea.setText("");
        numberOfFoundErrors.setText("0");
        numberOfFixedErrors.setText("0");
        numberOfUndetectedErrors.setText("0");
        numberOfSentDataBit.setText("0");
        numberOfControlBit.setText("0");
        disruptSpinner.setValueFactory(null);
        disruptSpinner.setDisable(true);
        buttonDisrupt.setDisable(true);
        buttonDecode.setDisable(true);

    }

    private void showStatistics(int[] receivedEncodeData, int[] sentEncodeData, int[] inputData) {
        int numberOfAllErrors = 0;
        numberOfSentDataBit.setText(String.valueOf(receivedEncodeData.length - (receivedEncodeData.length - inputData.length)));
        numberOfControlBit.setText(String.valueOf(receivedEncodeData.length - inputData.length));
        for (int i = 0; i < sentEncodeData.length; i++)
            if (sentEncodeData[i] != receivedEncodeData[i]) numberOfAllErrors++;
        if (toggleButtonCRC.isSelected()) {
            numberOfFoundErrors.setText(String.valueOf(crc.getNumberOfErrors()));
            numberOfFixedErrors.setText("0");
            numberOfUndetectedErrors.setText(String.valueOf(numberOfAllErrors - crc.getNumberOfErrors()));
        } else if (toggleButtonHamming.isSelected()) {
            numberOfFoundErrors.setText(String.valueOf(hamming.getNumberOfErrors()));
            numberOfFixedErrors.setText(String.valueOf(hamming.getNumberOfFixedBit()));
            numberOfUndetectedErrors.setText(String.valueOf(numberOfAllErrors - hamming.getNumberOfErrors()));
        } else {
            numberOfFoundErrors.setText(String.valueOf(parity.getNumberOfErrors()));
            numberOfFixedErrors.setText("0");
            numberOfUndetectedErrors.setText(String.valueOf(numberOfAllErrors - parity.getNumberOfErrors()));
        }

    }

    private void showColoredData(Map<Integer, String> detectedBits, int[] data) {
        String detectedData = intArrayToString(data);
        for (int i = 0; i < detectedData.length(); i++) {
            for (Map.Entry<Integer, String> entry : detectedBits.entrySet()) {
                if (i == entry.getKey()) {
                    String color = "#000000";
                    switch (entry.getValue()) {
                        case "correctDataBit":
                            color = "#12af12";
                            break;
                        case "wrongDataBit":
                            color = "#fd6868";
                            break;
                        case "uncertainDataBit":
                            color = "#ffbf58";
                            break;
                        case "fixedDataBit":
                            color = "#388fa6";
                            break;
                        case "correctControlBit":
                            color = "#0b5f0b";
                            break;
                        case "wrongControlBit":
                            color = "#bd1010";
                            break;
                        case "uncertainControlBit":
                            color = "#b58105";
                            break;
                        case "fixedControlBit":
                            color = "#0d355c";
                            break;
                    }
                    Text singleBit = new Text();
                    singleBit.setText(String.valueOf(detectedData.charAt(i)));
                    singleBit.setStyle("-fx-fill: " + color + ";-fx-font-weight:bold;");
                    dataWithDetectedErrorsArea.getChildren().add(i, singleBit);
                }
            }
        }
    }

    private int[] stringToIntArray(String text) {
        String[] splitedText = text.split("");
        int[] results = new int[splitedText.length];

        int i = 0;
        for (String textValue : splitedText) {
            results[i] = Integer.parseInt(textValue);
            i++;
        }
        return results;
    }

    private String intArrayToString(int[] array) {
        String results = "";
        for (int i = 0; i < array.length; i++) results += array[i];
        return results;
    }

    private boolean validationOfInputData() {
        final String regex = "[0|1]*";
        String inputData = inputDataArea.getText();
        if (!inputData.equals("")) {
            if (inputData.matches(regex)) {
                if (inputData.length() % 8 == 0) return true;
                else {
                    showErrorDialog("Długość kodu powinna być podzielna przez 8!");
                    return false;
                }
            } else {
                showErrorDialog("Dane wejściowe powinne zawierać tylko jedynki i zera");
                return false;
            }
        } else {
            showErrorDialog("Dane wejściowe nie mogą być puste");
            return false;
        }
    }

    private boolean validationOfDisruptData() {
        final String regex = "[0|1]*";
        String receivedEncodeData = receivedEncodeDataArea.getText();
        if (receivedEncodeData.matches(regex)) {
            if (receivedEncodeData.length() == sentEncodeDataArea.getText().length()) return true;
            else {
                showErrorDialog("Przy zakłóceniu nie wolno zmieniać długość danych!");
                return false;
            }
        } else {
            showErrorDialog("Zakłócone dane powinne zawierać tylko jedynki i zera");
            return false;
        }
    }


    private void showErrorDialog(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    public int[] disruptData(int[] data, int numberOfDisraptedBits) {
        int[] disruptedData = Arrays.copyOf(data, data.length);
        Random rand = new Random();
        List<Integer> listWithAllIndexesOfdata = IntStream.rangeClosed(0, data.length - 1).boxed().collect(Collectors.toList());
        List<Integer> indexesOfDisraptedBits = new ArrayList<>();

        for (int i = 0; i < numberOfDisraptedBits; i++) {
            int indexOfDisruptedBit = rand.nextInt(listWithAllIndexesOfdata.size());
            indexesOfDisraptedBits.add(listWithAllIndexesOfdata.get(indexOfDisruptedBit));
            listWithAllIndexesOfdata.remove(indexOfDisruptedBit);
        }

        for (int i = 0; i < indexesOfDisraptedBits.size(); i++) {
            if (disruptedData[indexesOfDisraptedBits.get(i)] == 0) disruptedData[indexesOfDisraptedBits.get(i)] = 1;
            else disruptedData[indexesOfDisraptedBits.get(i)] = 0;
        }
        return disruptedData;
    }

}
