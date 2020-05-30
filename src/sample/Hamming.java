package sample;

import java.util.HashMap;
import java.util.Map;

public class Hamming {

    private int inputData[];
    private Map<Integer, String> detectedBits = new HashMap<>();
    private int numberOfErrors = 0;
    private int numberOfFixedBit = 0;

    public Hamming(int[] inputData) {
        this.inputData = inputData;
    }

    int[] encode()
    {
        int numberOfControlsBits = 0;
        for (int i=0; Math.pow(2, i)<=inputData.length; i++) numberOfControlsBits++;
        int encodedData[] = new int[inputData.length+numberOfControlsBits];
        encodedData[0] = 2;
        encodedData[1] = 2;
        int i=0, j=2;
        while (j<encodedData.length) {
            if(log2(j+1) == Math.floor(log2(j+1))) encodedData[j]=2;
            else{
                encodedData[j]=inputData[i];
                i++;
            }
            j++;
        }
        for (i=encodedData.length-1; i>=0; i--)
            if(log2(i+1) == Math.floor(log2(i+1)))
                encodedData[i]=setControlBit(i,encodedData);

        return encodedData;
    }

    private int setControlBit(int index, int[] data){
        int controlBit;
        if(index==0){
            controlBit = data[index+2];
            for(int i=index+4; i<data.length; i+=2) controlBit^=data[i];
        }
        else {
            controlBit = data[index+1];
            int i=index+2;
            while(i<data.length){
                int j=i;
                if(i==index+2){
                    while(j<i+index-1 && j<data.length){
                        controlBit^=data[j];
                        j++;
                    }
                }
                else{
                    while(j<i+index+1 && j<data.length){
                        controlBit^=data[j];
                        j++;
                    }
                }
                i=j+index+1;
            }
        }
        return controlBit;
    }


    public int [] detectErrors(int [] data){
        numberOfErrors=0;
        numberOfFixedBit=0;
        int indexOfWrongBit=-1;
        for (int i=data.length-1; i>=0; i--){
            if(log2(i+1) == Math.floor(log2(i+1))){
                detectedBits.put(i, "correctControlBit");
                if(data[i]!=setControlBit(i,data)) {
                    if(indexOfWrongBit == -1) indexOfWrongBit = 0;
                    indexOfWrongBit+=i+1;
                }
            }
            else detectedBits.put(i, "correctDataBit");
        }
        if(indexOfWrongBit!=-1) {
            indexOfWrongBit--;
            numberOfErrors++;
            if(indexOfWrongBit<data.length){
                numberOfFixedBit++;
                if(data[indexOfWrongBit]==0)data[indexOfWrongBit]=1;
                else data[indexOfWrongBit]=0;
                if(log2(indexOfWrongBit+1) == Math.floor(log2(indexOfWrongBit+1)))
                    detectedBits.put(indexOfWrongBit, "fixedControlBit");
                else detectedBits.put(indexOfWrongBit, "fixedDataBit");
            }
        }
        return data;
    }

    public int[] decode(int [] data)
    {
        int numberOfControlsBits=0;
        for (int i=0; Math.pow(2, i)<=data.length; i++) numberOfControlsBits++;
        int decodedData[] = new int[data.length-numberOfControlsBits];
        int j=0;
        for (int i=0; i<data.length; i++){
            if(log2(i+1) != Math.floor(log2(i+1))){
                decodedData[j]=data[i];
                j++;
            }
        }
        return decodedData;
    }


    private double log2(double d) {
        return Math.log(d)/Math.log(2.0);
    }

    public Map<Integer, String> getDetectedBits() {
        return detectedBits;
    }

    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    public int getNumberOfFixedBit() {
        return numberOfFixedBit;
    }
}
