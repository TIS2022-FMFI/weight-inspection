package org.example;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public class App
{
    private static final byte[] WEIGHING_COMMAND = "&".getBytes();
    private static final int OPEN_PORT_COOLDOWN = 4000;
    private static final int WEIGHING_TIMEOUT = 1000;
    private static final int EXPECTED_NUM_BYTES = 10;
    private static SerialPort comPort;

    //TODO: execute only once global lock
    //max number of retries
    //[32, 32, 32, 32, 49, 50, 53, 46, 53, 55]

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

    public static void main( String[] args ) {
        for (int i = 0; i < 10; i++) {
            try {
                weight();
            }
            catch (WeighingException exception) {
                System.out.println(exception.getMessage());
            }
            catch (Exception exception) {
                System.out.println("ERR");
            }
        }
    }

    public static WeighingResult weight() throws InterruptedException, WeighingException {
        if(comPort == null) {
            try {
                comPort = SerialPort.getCommPort("COM3");
            } catch (SerialPortInvalidPortException e) {
                throw new WeighingException("Port COM3 je nesprávne nastavený na počítači.");
            }
        }

        if(!comPort.isOpen()) {
            comPort.openPort();
            Thread.sleep(OPEN_PORT_COOLDOWN);
        }

        comPort.flushIOBuffers();
        comPort.writeBytes(WEIGHING_COMMAND, WEIGHING_COMMAND.length);
        Thread.sleep(WEIGHING_TIMEOUT);

        if(comPort.bytesAvailable() != EXPECTED_NUM_BYTES)
            throw new WeighingException("Neočakávaný výstup z váhy. Zapojte váhu a nastavte podľa manuálu: COM3 port, mód komunikácia na požiadavku Z PC, prefix status bit, bez sufixu. Nastavená hodnota komunikačnej rýchlosti, parity a stop bit môže byť ľubovoľná.");

        byte[] readBuffer = new byte[EXPECTED_NUM_BYTES];
        int numRead = comPort.readBytes(readBuffer, readBuffer.length);

        if(readBuffer[0] != ' ') {
            throw new WeighingException("Neočakávaný výstup z váhy. Nastavte správny prefix.");
        }

        StatusByte statusByte = parseStatusByte(readBuffer[1]);
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
        if(!statusByte.stableWeight) {
            return new WeighingResult(0, false);
        }

        return new WeighingResult(0, true);
    }
}
