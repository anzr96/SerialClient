import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jssc.SerialPort;
import jssc.SerialPortException;

import javax.swing.*;
import java.util.LinkedList;

public class Main extends Application {
    public static Stage primaryStage;
    static jssc.SerialPort serialPort;
    static Obj obj = new Obj();
    static String port = "COM10", server = "localhost";
    static int baud = 115200, stop = 1, data = 8, parity;
    static LinkedList<String> bufferList = new LinkedList<>();
    static String sid1, sid2;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Main.primaryStage = primaryStage;

        Main.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        try{
            Main.primaryStage.setScene(new Scene( FXMLLoader.load(getClass().getResource("/home.fxml"))));
        }catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Scene can not be load!");
            System.exit(0);
        }
        Main.primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void initSerial(){
        serialPort = new SerialPort(Main.port);

        try {
            serialPort.openPort();
        } catch (SerialPortException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null, "Serial Port " + Main.port + " does not exist!");
            System.exit(0);
        }
        try {
            serialPort.setParams(Main.baud,
                    Main.data,
                    Main.stop,
                    Main.parity);
        } catch (SerialPortException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Serial param\'s have problems!");
            System.exit(0);
        }
    }
}
