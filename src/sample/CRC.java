package sample;

import java.util.Arrays;
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

    int [] encode()
    {
        int [] crc = calculateCrc(inputData, key);
        int encodedData[] = new int[crc.length+inputData.length];
        System.arraycopy(crc, 0, encodedData, 0, crc.length);
        System.arraycopy(inputData, 0, encodedData, crc.length, inputData.length);
        return encodedData;
    }

    int [] calculateCrc(int [] inputData, int [] key){
        int[] result = new int[inputData.length+key.length-1];
        int[] reverseKey = reverseIntArray(key);
        System.arraycopy(inputData, 0, result, key.length-1, inputData.length);
        for (int i=result.length-1; i>key.length-2; i--)
            if (result[i]==1){
                for (int j=0; j<key.length; j++)
                    result[i-j]^=reverseKey[key.length-1-j];
            }
        return Arrays.copyOf(result, key.length-1);
    }



    public int[] decode(int [] data)
    {
        return Arrays.copyOfRange(data, key.length-1, data.length);
    }

    public int[] detectErrors(int [] data){
        numberOfErrors = 0;
        int [] crc = calculateCrc(data, key);
        boolean isDisrupt=false;
        for (int i = 0; i < crc.length; i++) {
            if(crc[i]!=0) isDisrupt=true;
        }
        if(isDisrupt) {
            numberOfErrors++;
            for (int i=0; i<crc.length; i++) detectedBits.put(i,"uncertainControlBit");
            for (int i=crc.length; i<data.length; i++) detectedBits.put(i,"uncertainDataBit");
        }
        else {
            for (int i=0; i<crc.length; i++) detectedBits.put(i,"correctControlBit");
            for (int i=crc.length; i<data.length; i++) detectedBits.put(i,"correctDataBit");
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
