import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.*;

public class Controller implements Initializable{
    @FXML
    private ProgressBar progress;
    @FXML
    private Label name;
    @FXML
    private Label score;
    @FXML
    private Label heart;
    @FXML
    private Label level;
    @FXML
    private Label year;
    @FXML
    private Label month;
    @FXML
    private Label day;
    @FXML
    private Label hour;
    @FXML
    private Label minute;
    @FXML
    private Label second;
    @FXML
    private Label turbo;
    @FXML
    private Label speed;
    @FXML
    private Label gsec;
    @FXML
    private Label mainx;
    @FXML
    private Label mainy;
    @FXML
    private Label enemyx1;
    @FXML
    private Label enemyy1;
    @FXML
    private TextArea textArea;

    private static HttpURLConnection con;

    private Boolean score_arrived = false, time_arrived = false,
            date_arrived = false, heart_arrived = false, level_arrived = false,
            position_main_car_arrived = false, position_enemy_car_arrived = false,
            turboCharge_Arrived = false, speed_arrived = false, gameSeconds_arrived = false,
            load_command = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textArea.setEditable(false);

        try {
            Main.serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), ev -> {

            try {
                if (!Main.bufferList.isEmpty()) {
                    parseBuffer(Main.bufferList.removeFirst());
                }
                loadScene();
            }catch (Exception e){
                e.printStackTrace();

                score_arrived = false; time_arrived = false;
                date_arrived = false; heart_arrived = false; level_arrived = false;
                position_main_car_arrived = false; position_enemy_car_arrived = false;
                turboCharge_Arrived = false; speed_arrived = false; gameSeconds_arrived = false;
                load_command = false;
            }

        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void loadScene(){
        name.setText(Main.obj.studentID1 + "-" + Main.obj.studentID2);
        score.setText(Main.obj.score + "");
        heart.setText(Main.obj.heart + "");
        level.setText(Main.obj.level + "");
        speed.setText(Main.obj.speed + "");
        turbo.setText(Main.obj.turboCharge + "");
        gsec.setText(Main.obj.gameSeconds + "");
        year.setText(Main.obj.year + "");
        month.setText(Main.obj.month + "");
        day.setText(Main.obj.day + "");
        hour.setText(Main.obj.hr + "");
        minute.setText(Main.obj.min + "");
        second.setText(Main.obj.sec + "");
        mainx.setText(Main.obj.mainCar.x + "");
        mainy.setText(Main.obj.mainCar.y + "");


        for (Position enemyCar : Main.obj.enemyCars) {
            Label label = new Label();
            label.setLayoutX(0);
            label.setLayoutY(0);
            enemyx1.setText(enemyCar.x + "");
            enemyy1.setText(enemyCar.y + "");
        }
    }

    private void parseBuffer(String receivedData){
        if (receivedData.length() > 10)
            return;
        textArea.appendText(receivedData + "\n");

        /*
          save command
         */
        if (receivedData.trim().contains("save")){
            System.out.println("sss");
            progress.setProgress(0.5);
            //sendData(new Gson().toJson(Main.obj, Obj.class));
            if(sendData(createSendingFormat(Main.obj))){
                JOptionPane.showMessageDialog(null, "Sending Successful");
            }else {
                JOptionPane.showMessageDialog(null, "Sending Unsuccessful");
            }

            progress.setProgress(0);
            return;
        }

        /*
          load command
         */
        if (receivedData.trim().length() < 6 && receivedData.trim().contains("load")){
            load_command = true;
            return;
        }
        if (receivedData.length() > 1 && load_command){
            try {
                progress.setProgress(0.1);
                Main.obj = new Gson().fromJson(getData(receivedData.trim()), Obj.class);
                progress.setProgress(0.6);
            }catch (Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Problem in receiving from server");
            }

            try {
                sendSerial(Main.obj);
                JOptionPane.showMessageDialog(null, "Receiving successful");
                progress.setProgress(0);
            }catch (Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Problem in sending on serial port");
            }
            load_command = false;
            return;
        }

        /*
          score arrived
         */
        if (receivedData.trim().contains("score")){
            score_arrived = true;
            return;
        }
        if (receivedData.length() > 1 && score_arrived){
            Main.obj.score = receivedData.trim();
            score_arrived = false;
            return;
        }

        /*
          heart arrived
         */
        if (receivedData.trim().contains("heart")){
            heart_arrived = true;
            return;
        }
        if (receivedData.length() >= 1 && heart_arrived){
            Main.obj.heart = receivedData.trim();
            heart_arrived = false;
            return;
        }

        /*
          level arrived
         */
        if (receivedData.trim().contains("level")){
            level_arrived = true;
            return;
        }
        if (receivedData.length() >= 2 && level_arrived){
            Main.obj.level = receivedData.trim();
            level_arrived = false;
            return;
        }

        /*
          time arrived
         */
        if (receivedData.trim().contains("time")){
            time_arrived = true;
            return;
        }
        if (receivedData.length() >= 6 && time_arrived){
            Main.obj.hr = receivedData.trim().substring(0,2);
            Main.obj.min = receivedData.trim().substring(2,4);
            Main.obj.sec = receivedData.trim().substring(4);
            time_arrived = false;
            return;
        }

        /*
          date arrived
         */
        if (receivedData.trim().contains("date")){
            date_arrived = true;
            return;
        }
        if (receivedData.length() >= 6 && date_arrived){
            Main.obj.year = receivedData.trim().substring(0,2);
            Main.obj.month = receivedData.trim().substring(2,4);
            Main.obj.day = receivedData.trim().substring(4);
            date_arrived = false;
            return;
        }

        /*
          turbo charge arrived
         */
        if (receivedData.trim().contains("turbo")){
            turboCharge_Arrived = true;
            return;
        }
        if (receivedData.length() >= 2 && turboCharge_Arrived){
            Main.obj.turboCharge = receivedData.trim();
            turboCharge_Arrived = false;
            return;
        }

        /*
          speed arrived
         */
        if (receivedData.trim().contains("speed")){
            speed_arrived = true;
            return;
        }
        if (receivedData.length() >= 2 && speed_arrived){
            Main.obj.speed = receivedData.trim();
            speed_arrived = false;
            return;
        }

        /*
          game seconds arrived
         */
        if (receivedData.trim().contains("gsec")){
            gameSeconds_arrived = true;
            return;
        }
        if (receivedData.length() > 1 && gameSeconds_arrived){
            Main.obj.gameSeconds = receivedData.trim();
            gameSeconds_arrived = false;
            return;
        }

        /*
          main car position arrived
         */
        if (receivedData.trim().contains("mcp")){
            position_main_car_arrived = true;
            return;
        }
        if (receivedData.length() >= 4 && position_main_car_arrived){
            Main.obj.mainCar.x = receivedData.trim().substring(0,2);
            Main.obj.mainCar.y = receivedData.trim().substring(2,4);
            position_main_car_arrived = false;
            return;
        }

        /*
          enemy position's arrived
         */
        if (receivedData.trim().contains("ecp")){
            position_enemy_car_arrived = true;
            return;
        }
        if (receivedData.length() > 4 && position_enemy_car_arrived){
            if (receivedData.length() % 4 == 0){
                for (int i = 0; i < receivedData.length(); i = i + 4){
                    Position position = new Position(receivedData.trim().substring(i, i + 2),
                            receivedData.trim().substring(i + 2, i + 4));
                    Main.obj.enemyCars.add(position);
                }
            }

            position_enemy_car_arrived = false;
        }
    }

    private boolean sendData(String data){
        try {
            URL myurl = new URL(Main.server + "/Micro/upload.php?" + data);

            con = (HttpURLConnection) myurl.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            progress.setProgress(0.6);

            /*
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(json.getBytes());
            }catch (Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to send data!\n" + e.getMessage());
            }*/

            /*StringBuilder content;

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                double i = 1;

                while ((line = in.readLine()) != null) {
                    progress.setProgress(0.6 + i / 100);
                    i++;
                    content.append(line);
                    content.append(System.lineSeparator());
                }
                progress.setProgress(0.9);
            }

            System.out.println(content.toString());

            JOptionPane.showMessageDialog(null, "Your packet ID: " + content.toString());*/

            con.disconnect();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            progress.setProgress(0);
            con.disconnect();
            return false;
        }
    }

    private String getData(String id){
        String url = Main.server + "/" + id;
        StringBuilder content = new StringBuilder();

        try {

            URL myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();

            con.setRequestMethod("GET");
            progress.setProgress(0.2);
            double i = 1;

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    progress.setProgress(0.3 + i / 100);
                    i++;
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

            progress.setProgress(0.45);

            System.out.println(content.toString());

        }catch (Exception e){
            e.printStackTrace();
            progress.setProgress(0);
        } finally {

            con.disconnect();
        }

        return content.toString();
    }

    private void sendSerial(Obj obj) throws SerialPortException {

        /*
          send heart
         */
        send(("heart").getBytes());
        send((obj.getHeart() + "").getBytes());

        /*
          send level
         */
        send(("level").getBytes());
        send((obj.getLevel() + "").getBytes());
        progress.setProgress(0.7);

        /*
          send score
         */
        send(("score").getBytes());
        send((obj.getScore() + "").getBytes());

        /*
          send turbo charge
         */
        send(("turbo").getBytes());
        send((obj.getTurboCharge() + "").getBytes());

        /*
          send speed
         */
        send(("speed").getBytes());
        send((obj.getSpeed() + "").getBytes());
        progress.setProgress(0.8);

        /*
          send game second
         */
        send(("gsec").getBytes());
        send((obj.getGameSeconds() + "").getBytes());

        /*
          send time
         */
        send(("time").getBytes());
        send((obj.getHr() + obj.getMin() + obj.getSec()).getBytes());

        /*
          send date
         */
        send(("date").getBytes());
        send((obj.getYear() + obj.getMonth() + obj.getDay()).getBytes());
        progress.setProgress(0.9);

        /*
          send main car position
         */
        send(("mcp").getBytes());
        send((obj.getMainCar().getX() + obj.getMainCar().getY()).getBytes());

        /*
          send enemy car position
         */
        send(("ecp").getBytes());
        StringBuilder enemy = new StringBuilder();
        for (Position position : obj.getEnemyCars()) {
            enemy.append(position.getX()).append(position.getY());
        }
        send(enemy.toString().getBytes());
        progress.setProgress(1);
    }

    private void send(byte[] buffer) throws SerialPortException {

        Main.serialPort.writeBytes(buffer);

    }

    private String createSendingFormat(Obj obj){
        StringBuilder stringBuilder = new StringBuilder();

        /*score*/
        stringBuilder.append("score=").append(obj.getScore()).append("&");

        /*heart*/
        stringBuilder.append("heart=").append(obj.getHeart()).append("&");

        /*time*/
        stringBuilder.append("time=")
                .append(obj.getHr())
                .append(obj.getMin())
                .append(obj.getSec())
                .append("&");

        /*date*/
        stringBuilder.append("date=")
                .append(obj.getYear())
                .append(obj.getMonth())
                .append(obj.getDay())
                .append("&");

        /*turbo*/
        stringBuilder.append("turbo=").append(obj.getTurboCharge()).append("&");

        /*speed*/
        stringBuilder.append("speed=").append(obj.getSpeed()).append("&");

        /*game seconds*/
        stringBuilder.append("gsec=").append(obj.getGameSeconds()).append("&");

        /*main car position*/
        stringBuilder.append("mcp=")
                .append(obj.getMainCar().getX())
                .append(obj.getMainCar().getY())
                .append("&");

        /*enemy car position*/
        stringBuilder.append("ecp=");
        for (Position position : obj.getEnemyCars()) {
            stringBuilder.append(position.getX()).append(position.getY());
        }
        if (obj.getEnemyCars().isEmpty()){
            stringBuilder.append("0000");
        }

        return stringBuilder.toString();
    }
}
