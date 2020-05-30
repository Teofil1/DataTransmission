package sample;

import java.util.HashMap;
import java.util.Map;

public class Parity {

    private int inputData[];
    private Map<Integer, String> detectedBits = new HashMap<>();
    private int numberOfErrors = 0;

    public Parity(int[] inputData) {
        this.inputData = inputData;
    }

    public int[] encode()
    {
        int lengthInputData = inputData.length;
        int encodedData[] = new int [lengthInputData+lengthInputData/8];
        for (int i=0; i<lengthInputData/8; i++)
        {
            int numberOfOne = 0;
            for (int j=1; j<9; j++)
            {
                encodedData[i*9+j]=inputData[i*8+j-1];
                if(inputData[i*8+j-1] == 1) numberOfOne++;
            }
            if (numberOfOne%2 != 0) encodedData[i*9]=1;
            else encodedData[i*9]=0;
        }
        return encodedData;
    }

    public int[] decode(int [] encodedData)
    {
        int decodedData[] = new int [encodedData.length-encodedData.length/8];
        for (int i=0; i<encodedData.length/9; i++)
            for (int j=0; j<8; j++)
                decodedData[i*8+j]=encodedData[i*9+j+1];
        return decodedData;
    }

    public int[] detectErrors(int [] data){
        numberOfErrors = 0;
        for (int i=0; i<data.length/9; i++)
        {
            int numberOfOne = 0;
            for (int j=0; j<9; j++)
            {
                if(data[i*9+j] == 1) numberOfOne++;
            }
            if (numberOfOne%2 != 0) {
                numberOfErrors++;
                for (int j=8; j>0; j--) detectedBits.put(i*9+j, "uncertainDataBit");
                detectedBits.put(i*9, "uncertainControlBit");
            } else {
                for (int j=8; j>0; j--) detectedBits.put(i*9+j, "correctDataBit");
                detectedBits.put(i*9, "correctControlBit");
            }
        }
        return data;
    }

    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    public Map<Integer, String> getDetectedBits() {
        return detectedBits;
    }
}
