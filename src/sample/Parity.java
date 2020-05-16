package sample;

import java.util.Arrays;
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
        int [] revereseInputData = reverseIntArray(inputData);
        int encodedData[] = new int [lengthInputData+lengthInputData/8];
        for (int i=0; i<lengthInputData/8; i++)
        {
            int numberOfOne = 0;
            for (int j=0; j<8; j++)
            {
                encodedData[i*9+j]=revereseInputData[i*8+j];
                if(inputData[i*8+j] == 1) numberOfOne++;
            }
            if (numberOfOne%2 != 0) encodedData[8+i*9]=1;
            else encodedData[8+i*9]=0;
        }

        return reverseIntArray(encodedData);
    }

    public int[] decode(int [] encodedData)
    {
        int lengthEncodedData = encodedData.length;
        int [] revereseEncodedData = reverseIntArray(encodedData);
        int decodedData[] = new int [lengthEncodedData-lengthEncodedData/8];
        for (int i=0; i<lengthEncodedData/9; i++)
            for (int j=0; j<8; j++)
                decodedData[i*8+j]=revereseEncodedData[i*9+j];
        return reverseIntArray(decodedData);
    }

    public int[] detectErrors(int [] data){
        int lenghtEncodedData = data.length;
        numberOfErrors = 0;
        for (int i=0; i<lenghtEncodedData/9; i++)
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

    private int[] reverseIntArray(int array[]){
        int[] copyArray = Arrays.copyOf(array, array.length);
        for(int i = 0; i < copyArray.length / 2; i++)
        {
            int temp = copyArray[i];
            copyArray[i] = copyArray[copyArray.length - i - 1];
            copyArray[copyArray.length - i - 1] = temp;
        }
        return copyArray;
    }

    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    public Map<Integer, String> getDetectedBits() {
        return detectedBits;
    }
}
