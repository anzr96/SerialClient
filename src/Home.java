import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Home implements Initializable {
    @FXML
    private TextField port;
    @FXML
    private TextField baud;
    @FXML
    private TextField stop;
    @FXML
    private TextField parity;
    @FXML
    private TextField data;
    @FXML
    private TextField server;
    @FXML
    private TextField sid1;
    @FXML
    private TextField sid2;
    @FXML
    private Button submit;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File myfile = new File("./data");

        Gson gson = new Gson();
        StringBuilder json = new StringBuilder();
        HomeObj homeObj;

        try {
            FileInputStream fin = new FileInputStream(myfile);
            int i;
            while((i=fin.read())!=-1){
                json.append((char) i);
                System.out.print((char)i);
            }
            fin.close();
            homeObj = gson.fromJson(json.toString(), HomeObj.class);
        } catch (IOException e) {
            e.printStackTrace();
            homeObj = new HomeObj();
        }
        System.out.println("");

        server.setText(homeObj.getServer());
        port.setText(homeObj.getPort());
        baud.setText(String.valueOf(homeObj.getBaud()));
        data.setText(String.valueOf(homeObj.getData()));
        stop.setText(String.valueOf(homeObj.getStop()));
        parity.setText(homeObj.getParity());
        sid1.setText(homeObj.getStudentID1() + "");
        sid2.setText(homeObj.getStudentID2() + "");

        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    HomeObj homeObj = new HomeObj();
                    Main.server = server.getText();
                    homeObj.setServer(server.getText());
                    Main.port = port.getText();
                    homeObj.setPort(port.getText());
                    Main.baud = Integer.parseInt(baud.getText());
                    homeObj.setBaud(Integer.parseInt(baud.getText()));
                    Main.data = Integer.parseInt(data.getText());
                    homeObj.setData(Integer.parseInt(data.getText()));

                    switch (stop.getText()){
                        case "1.5":{
                            Main.stop = 3;
                            break;
                        }
                        default:
                            Main.stop = Integer.parseInt(stop.getText());
                    }
                    homeObj.setStop(Integer.valueOf(stop.getText()));

                    switch (parity.getText().toLowerCase()){
                        case "none":{
                            Main.parity = 0;
                            break;
                        }
                        case "odd":{
                            Main.parity = 1;
                            break;
                        }
                        case "even":{
                            Main.parity = 2;
                            break;
                        }
                        case "mark":{
                            Main.parity = 3;
                            break;
                        }
                        case "space":{
                            Main.parity = 4;
                            break;
                        }
                        default:{
                            JOptionPane.showMessageDialog(null, "Please enter correct parity");
                            return;
                        }
                    }
                    homeObj.setParity(parity.getText());

                    try {
                        Main.obj.setStudentID1(sid1.getText());
                        Main.sid1 = sid1.getText();
                        homeObj.setStudentID1(sid1.getText());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    try {
                        Main.obj.setStudentID2(sid2.getText());
                        Main.sid2 = sid2.getText();
                        homeObj.setStudentID2(sid2.getText());
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    Main.initSerial();

                    String json = gson.toJson(homeObj, HomeObj.class);
                    FileOutputStream fileOutputStream = new FileOutputStream("./data");

                    fileOutputStream.write(json.getBytes());

                    fileOutputStream.close();

                    try {
                        Main.primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/controller.fxml"))));
                        //Main.primaryStage.show();
                    }catch (Exception e){
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Problem in loading new page!");
                        System.exit(1);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Please enter in correct form");
                }
            }
        });
    }
}
