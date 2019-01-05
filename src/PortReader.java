import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class PortReader implements SerialPortEventListener {
    private String tempBuffer = "";

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0){
            try {
                tempBuffer += Main.serialPort.readString(serialPortEvent.getEventValue());
                if (tempBuffer.charAt(tempBuffer.length() - 1) == '\n'){
                    Main.bufferList.addLast(tempBuffer);
                    tempBuffer = "";
                }
            }catch (SerialPortException e){
                e.printStackTrace();
            }
        }
    }
}