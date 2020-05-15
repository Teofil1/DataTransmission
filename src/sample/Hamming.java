package sample;

import java.util.HashMap;
import java.util.Map;

public class Hamming {

    private int inputData[];
    private Map<Integer, String> detectedBits = new HashMap<>();
    private int errers = 0;

    public Hamming(int[] inputData) {
        this.inputData = inputData;
    }

    int[] encode()
    {
        int numberOfControlsBits=0;
        for (int i=0; Math.pow(2, i)<=inputData.length; i++) numberOfControlsBits++;
        int encodedData[] = new int[inputData.length+numberOfControlsBits];
        encodedData[0] = 2;
        encodedData[1]= 2;
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
            for(int i=index+4; i<data.length; i+=2){
                controlBit^=data[i];
            }
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
                System.out.println();
                i=j+index+1;
            }
        }
        return controlBit;
    }



    private double log2(double d) {
        return Math.log(d)/Math.log(2.0);
    }
}
