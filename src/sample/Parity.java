package sample;

public class Parity {

    private int inputData[];
    private int indexesOfCorrectBit[];
    private int indexesOfWrongBit[];
    private int indexesOfUncertainBit[];
    private int indexesOfCorrectControlBit[];
    private int indexesOfWrongControlBit[];
    private int indexesOfUncertainControlBit[];

    public Parity(int[] inputData) {
        this.inputData = inputData;
        indexesOfCorrectBit = new int[inputData.length];
        indexesOfWrongBit = new int[inputData.length];
        indexesOfUncertainBit = new int[inputData.length];
        indexesOfCorrectControlBit = new int[inputData.length/8];
        indexesOfWrongControlBit = new int[inputData.length/8];
        indexesOfUncertainControlBit = new int[inputData.length/8];
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

    public int[] decode()
    {
        return null;
    }


    private int[] reverseIntArray(int array[]){
        for(int i = 0; i < array.length / 2; i++)
        {
            int temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
        return array;
    }
}
