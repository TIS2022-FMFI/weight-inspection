package org.example;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;

public class App
{
    private static final byte[] WEIGHING_COMMAND = "&".getBytes();
    private static final int OPEN_PORT_COOLDOWN = 3000;
    private static final int WEIGHING_TIMEOUT = 500;
    private static final int EXPECTED_NUM_BYTES = 10;
    private static SerialPort comPort;

    //TODO: execute only once global lock
    //max number of retries
    //save exception for frontend error message

    public static void main( String[] args ) {
        try {
            if(comPort == null) {
                try {
                    comPort = SerialPort.getCommPort("COM3");
                } catch (SerialPortInvalidPortException e) {
                    throw new Exception("Váha je nedostupná. Zapojte váhu alebo nastavte komunikačný protokol COM3 podľa manuálu.");
                }
            }

            if(!comPort.isOpen()) {
                Thread.sleep(OPEN_PORT_COOLDOWN);
                comPort.openPort();
            }

            comPort.flushIOBuffers();
            comPort.writeBytes(WEIGHING_COMMAND, WEIGHING_COMMAND.length);
            Thread.sleep(WEIGHING_TIMEOUT);

            if(comPort.bytesAvailable() != EXPECTED_NUM_BYTES)
                throw new Exception("Neočakávaný výstup z váhy. Nastavte podľa manuálu mód komunikácia na požiadavku Z PC, prefix status bit, bez sufixu.");

            byte[] readBuffer = new byte[EXPECTED_NUM_BYTES];
            int numRead = comPort.readBytes(readBuffer, readBuffer.length);
            System.out.println("Read " + numRead + " bytes.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
