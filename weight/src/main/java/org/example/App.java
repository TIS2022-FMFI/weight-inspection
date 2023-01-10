package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class App
{
    private static final int OPEN_PORT_COOLDOWN = 4000;

    private static final int WEIGHING_NUM_RETRIES = 5;
    private static final int WEIGHING_RETRY_COOLDOWN = 500;
    private static final byte[] WEIGHING_COMMAND = "&".getBytes();
    private static final int WEIGHING_TIMEOUT = 1000;

    private static final int WEIGHING_RETRY_DURATION = 100;
    private static final int WEIGHING_EXPECTED_NUM_BYTES = 10;
    private static SerialPort comPort;

    private static Lock lock = new ReentrantLock();

    //TODO: execute only once global lock

    private static class WeighingException extends Exception {
        public WeighingException(String message) {
            super(message);
        }
    }

    @Getter
    @AllArgsConstructor
    @ToString
    private static class StatusByte {
        private boolean stableWeight;
        private boolean weightOfLivestock;
        private boolean zeroIndicator;
        private boolean netWeightIndicator;
        private boolean grossWeightIndicator;
        private boolean wrongFormat;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    private static class WeighingResult {
        private float weight;
        private boolean stableWeight;
    }


    @Getter
    @AllArgsConstructor
    @ToString
    private static class RawWeighingResult {
        private byte[] bytes;
        private int numBytes;
    }

    public static boolean bitEqualOne(byte comparedByte, int bitIndex) {
        return (comparedByte & (byte) Math.pow(2, bitIndex)) == Math.pow(2, bitIndex);
    }

    public static StatusByte parseStatusByte(byte statusByte) {
        return new StatusByte(
                bitEqualOne(statusByte, 5),
                bitEqualOne(statusByte, 4),
                bitEqualOne(statusByte, 3),
                bitEqualOne(statusByte, 1),
                bitEqualOne(statusByte, 0),
                bitEqualOne(statusByte, 7) ||
                        bitEqualOne(statusByte, 6) ||
                        bitEqualOne(statusByte, 2)
        );
    }

    public static void main( String[] args ) throws WeighingException, InterruptedException {
        getWeight();
    }

    public static float getWeight() throws WeighingException, InterruptedException {
        lock.lock();

        try {
            WeighingResult weighingResult;
            for (int i = 0; i < WEIGHING_NUM_RETRIES; i++) {
                weighingResult = weighingCycle();
                if (weighingResult.isStableWeight()) {
                    lock.unlock();
                    return weighingResult.getWeight();
                }
                Thread.sleep(WEIGHING_RETRY_COOLDOWN);
            }
            throw new WeighingException("Váženie není stabilné. Ustálte zmenu hmotnosti na váhe.");
        }
        finally {
            lock.unlock();
        }
    }
    private static WeighingResult weighingCycle() throws WeighingException, InterruptedException {
        RawWeighingResult rawWeighingResult = weighingCommunication();
        return weighingLogic(rawWeighingResult);
    }
    private static RawWeighingResult weighingCommunication() throws InterruptedException, WeighingException {
        if(comPort == null) {
            try {
                comPort = SerialPort.getCommPort("COM3");
            } catch (SerialPortInvalidPortException e) {
                throw new WeighingException("Port COM je nesprávne nastavený na počítači. Číslo COM port sa musí zhodovať s otvoreným portom na počítači.");
            }
        }

        if(!comPort.isOpen()) {
            comPort.openPort();
            Thread.sleep(OPEN_PORT_COOLDOWN);
        }

        comPort.flushIOBuffers();
        comPort.writeBytes(WEIGHING_COMMAND, WEIGHING_COMMAND.length);

        for (int i = 0; i < WEIGHING_TIMEOUT / WEIGHING_RETRY_DURATION; i++) {
            if(comPort.bytesAvailable() == WEIGHING_EXPECTED_NUM_BYTES) break;
            Thread.sleep(WEIGHING_RETRY_DURATION);
        }

        byte[] readBuffer = new byte[WEIGHING_EXPECTED_NUM_BYTES];
        int numRead = comPort.readBytes(readBuffer, readBuffer.length);

        return new RawWeighingResult(readBuffer, numRead);
    }

    private static WeighingResult weighingLogic(RawWeighingResult rawWeighingResult) throws WeighingException {
        byte[] bytesRead = rawWeighingResult.getBytes();
        int numBytesRead = rawWeighingResult.getNumBytes();

        if(numBytesRead != WEIGHING_EXPECTED_NUM_BYTES) {
            throw new WeighingException("Neočakávaný výstup z váhy. Zapojte váhu a nastavte podľa manuálu: číslo COM port, ktorý používate na príjem na počítači, mód komunikácia na požiadavku Z PC, prefix status bit, bez sufixu. Nastavená hodnota komunikačnej rýchlosti, parita a stop bit sa musí zhodovať s nastavením portu na počítači.");
        }

        if(bytesRead[0] != ' ') {
            throw new WeighingException("Neočakávaný výstup z váhy. Nastavte správny prefix.");
        }

        StatusByte statusByte = parseStatusByte(bytesRead[1]);
        if(statusByte.wrongFormat) {
            throw new WeighingException("Neočakávaný výstup z váhy. Nastavte správny prefix.");
        }
        if(statusByte.grossWeightIndicator) {
            throw new WeighingException("Zle nastavený mód váženia (hrubá hmotnosť). Nastavte správny mód váženia.");
        }
        if(statusByte.netWeightIndicator) {
            throw new WeighingException("Zle nastavený mód váženia (netto hmotnosť). Nastavte správny mód váženia.");
        }
        if(statusByte.grossWeightIndicator) {
            throw new WeighingException("Zle nastavený mód váženia (váženie dobytku). Nastavte správny mód váženia.");
        }

        String weightString = new String(bytesRead);

        float weight;
        try {
            weight = Float.parseFloat(weightString);
        }
        catch (NumberFormatException exception) {
            throw new WeighingException("Neočakávaný formát hodnoty váženia z váhy. Nastavte podľa manuálu: mód komunikácia na požiadavku Z PC, prefix status bit, bez sufixu.");
        }

        if(!statusByte.stableWeight) {
            return new WeighingResult(weight, false);
        }

        return new WeighingResult(weight, true);
    }
}
