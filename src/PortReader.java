import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class PortReader implements SerialPortEventListener {

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0){
            try {
                Main.bufferList.addLast(Main.serialPort.readString(serialPortEvent.getEventValue()));
            }catch (SerialPortException e){
                e.printStackTrace();
            }
        }
    }
}