package sample;

import java.util.HashMap;
import java.util.Map;

public class CRC {

    private int inputData[];
    private Map<Integer, String> detectedBits = new HashMap<>();
    private int numberOfErrors = 0;
    private int [] key;

    public CRC(int[] inputData, int [] key) {
        this.inputData = inputData;
        this.key = key;
    }

    int[] encode()
    {
        return null;
    }

    public int[] decode(int [] data)
    {
        return data;
    }

    public int[] detectErrors(int [] data){
        return data;
    }

    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    public Map<Integer, String> getDetectedBits() {
        return detectedBits;
    }


}
