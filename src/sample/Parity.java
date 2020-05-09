package sample;

public class Parity {


    static int[] encode(int inputData[])
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

    private static int[] reverseIntArray(int array[]){
        for(int i = 0; i < array.length / 2; i++)
        {
            int temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
        return array;
    }
}
