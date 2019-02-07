import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import jssc.SerialPort;
import jssc.SerialPortException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
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
	private TextArea enemyx;
	@FXML
	private TextArea enemyy;
	@FXML
	private TextArea textArea;
	@FXML
	private Label upid;

	private static HttpURLConnection con;

	private Boolean score_arrived = false, time_arrived = false, date_arrived = false, heart_arrived = false,
			level_arrived = false, position_main_car_arrived = false, position_enemy_car_arrived = false,
			turboCharge_Arrived = false, speed_arrived = false, gameSeconds_arrived = false, load_command = false,
			upid_arrived = false;

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
			} catch (Exception e) {
				e.printStackTrace();

				score_arrived = false;
				time_arrived = false;
				date_arrived = false;
				heart_arrived = false;
				level_arrived = false;
				position_main_car_arrived = false;
				position_enemy_car_arrived = false;
				turboCharge_Arrived = false;
				speed_arrived = false;
				gameSeconds_arrived = false;
				load_command = false;
				upid_arrived = false;
			}

		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	private void loadScene() {
		if (Main.obj.studentID2 == null || Main.obj.studentID2.equals("null") || Main.obj.studentID2.equals("")) {
			name.setText(Main.obj.studentID1);
		} else {
			name.setText(Main.obj.studentID1 + "-" + Main.obj.studentID2);
		}
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
		upid.setText(Main.obj.uploadID + "");
		mainx.setText(Main.obj.mainCar.x + "");
		mainy.setText(Main.obj.mainCar.y + "");

		enemyx.clear();
		enemyy.clear();
		for (Position enemyCar : Main.obj.enemyCars) {
			Label label = new Label();
			label.setLayoutX(0);
			label.setLayoutY(0);
			enemyx.appendText(enemyCar.x + "\n");
			enemyy.appendText(enemyCar.y + "\n");
		}
	}

	private void parseBuffer(String receivedData) {
		if (receivedData.length() > 10 && !position_enemy_car_arrived)
			return;
		textArea.appendText(receivedData + "\n");

		/*
		 * gettime command
		 */
		if (receivedData.trim().contains("gettime")) {
			Calendar calendar = Calendar.getInstance();
			String time = "" + (calendar.get(Calendar.YEAR) + "").substring(2, 4)
					+ ("0" + (calendar.get(Calendar.MONTH) + 1))
					+ (calendar.get(Calendar.DATE) < 10 ? "0" + calendar.get(Calendar.DATE)
							: calendar.get(Calendar.DATE) + "")
					+ (calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + calendar.get(Calendar.HOUR_OF_DAY)
							: calendar.get(Calendar.HOUR_OF_DAY) + "")
					+ (calendar.get(Calendar.MINUTE) < 10 ? "0" + calendar.get(Calendar.MINUTE)
							: calendar.get(Calendar.MINUTE) + "")
					+ (calendar.get(Calendar.SECOND) < 10 ? "0" + calendar.get(Calendar.SECOND)
							: calendar.get(Calendar.SECOND) + "");
			try {
				send(time.getBytes());
			} catch (SerialPortException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Sending Unsuccessful\n" + e.getMessage());
			}
			return;
		}

		/*
		 * upid command
		 */
		if (receivedData.trim().contains("upid")) {
			upid_arrived = true;
			return;
		}
		if (receivedData.length() > 1 && upid_arrived) {
			Main.obj.uploadID = receivedData.trim();
			upid_arrived = false;
			return;
		}

		/*
		 * save command
		 */
		if (receivedData.trim().contains("save")) {
			progress.setProgress(0.5);
			if (sendData(createSendingFormat(Main.obj))) {
				JOptionPane.showMessageDialog(null, "Sending Successful");
			} else {
				JOptionPane.showMessageDialog(null, "Sending Unsuccessful");
			}

			progress.setProgress(0);
			return;
		}

		/*
		 * load command
		 */
		if (receivedData.trim().length() < 6 && receivedData.trim().contains("load")) {
			load_command = true;
			return;
		}
		if (receivedData.length() > 1 && load_command) {
			try {
				progress.setProgress(0.1);
				Main.obj = jsonParser(getData(receivedData.trim()));
				if (Main.obj == null) {
					JOptionPane.showMessageDialog(null, "Problem in received data");
					progress.setProgress(0);
					return;
				}
				progress.setProgress(0.6);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Problem in receiving from server");
			}

			try {
				sendSerial(Main.obj);
				JOptionPane.showMessageDialog(null, "Receiving successful");
				progress.setProgress(0);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Problem in sending on serial port");
			}
			load_command = false;
			return;
		}

		/*
		 * score arrived
		 */
		if (receivedData.trim().contains("score")) {
			score_arrived = true;
			return;
		}
		if (receivedData.length() > 1 && score_arrived) {
			Main.obj.score = receivedData.trim();
			score_arrived = false;
			return;
		}

		/*
		 * heart arrived
		 */
		if (receivedData.trim().contains("heart")) {
			heart_arrived = true;
			return;
		}
		if (receivedData.length() >= 1 && heart_arrived) {
			Main.obj.heart = receivedData.trim();
			heart_arrived = false;
			return;
		}

		/*
		 * level arrived
		 */
		if (receivedData.trim().contains("level")) {
			level_arrived = true;
			return;
		}
		if (receivedData.length() >= 2 && level_arrived) {
			Main.obj.level = receivedData.trim();
			level_arrived = false;
			return;
		}

		/*
		 * time arrived
		 */
		if (receivedData.trim().contains("time")) {
			time_arrived = true;
			return;
		}
		if (receivedData.length() >= 6 && time_arrived) {
			Main.obj.hr = receivedData.trim().substring(0, 2);
			Main.obj.min = receivedData.trim().substring(2, 4);
			Main.obj.sec = receivedData.trim().substring(4);
			time_arrived = false;
			return;
		}

		/*
		 * date arrived
		 */
		if (receivedData.trim().contains("date")) {
			date_arrived = true;
			return;
		}
		if (receivedData.length() >= 6 && date_arrived) {
			Main.obj.year = receivedData.trim().substring(0, 2);
			Main.obj.month = receivedData.trim().substring(2, 4);
			Main.obj.day = receivedData.trim().substring(4);
			date_arrived = false;
			return;
		}

		/*
		 * turbo charge arrived
		 */
		if (receivedData.trim().contains("turbo")) {
			turboCharge_Arrived = true;
			return;
		}
		if (receivedData.length() >= 2 && turboCharge_Arrived) {
			Main.obj.turboCharge = receivedData.trim();
			turboCharge_Arrived = false;
			return;
		}

		/*
		 * speed arrived
		 */
		if (receivedData.trim().contains("speed")) {
			speed_arrived = true;
			return;
		}
		if (receivedData.length() >= 2 && speed_arrived) {
			Main.obj.speed = receivedData.trim();
			speed_arrived = false;
			return;
		}

		/*
		 * game seconds arrived
		 */
		if (receivedData.trim().contains("gsec")) {
			gameSeconds_arrived = true;
			return;
		}
		if (receivedData.length() > 1 && gameSeconds_arrived) {
			Main.obj.gameSeconds = receivedData.trim();
			gameSeconds_arrived = false;
			return;
		}

		/*
		 * main car position arrived
		 */
		if (receivedData.trim().contains("mcp")) {
			position_main_car_arrived = true;
			return;
		}
		if (receivedData.length() >= 4 && position_main_car_arrived) {
			Main.obj.mainCar.x = receivedData.trim().substring(0, 2);
			Main.obj.mainCar.y = receivedData.trim().substring(2, 4);
			position_main_car_arrived = false;
			return;
		}

		/*
		 * enemy position's arrived
		 */
		if (receivedData.trim().contains("ecp")) {
			position_enemy_car_arrived = true;
			return;
		}
		if (position_enemy_car_arrived) {
			Main.obj.getEnemyCars().clear();
			receivedData = receivedData.substring(0, receivedData.indexOf('\n'));
			if (receivedData.length() % 4 == 0) {
				for (int i = 0; i < receivedData.length(); i = i + 4) {
					Position position = new Position(receivedData.trim().substring(i, i + 2),
							receivedData.trim().substring(i + 2, i + 4));
					Main.obj.enemyCars.add(position);
				}
			}

			position_enemy_car_arrived = false;
		}

		progress.setProgress(1);
	}

	private boolean sendData(String data) {
		try {
			System.out.println(data);
			URL myurl = new URL(Main.server + "/Micro/upload.php?" + data);

			con = (HttpURLConnection) myurl.openConnection();

			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Java client");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			progress.setProgress(0.6);

			if (con.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
				progress.setProgress(1);
				JOptionPane.showMessageDialog(null, "Created successfully");
				con.disconnect();
				return true;
			} else if (con.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
				progress.setProgress(1);
				JOptionPane.showMessageDialog(null, "Updated successfully");
				con.disconnect();
				return true;
			} else if (con.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
				progress.setProgress(0);
				con.disconnect();
				JOptionPane.showMessageDialog(null, "Bad Request");
				return false;
			} else {
				progress.setProgress(0);
				con.disconnect();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			progress.setProgress(0);
			con.disconnect();
			return false;
		}
	}

	private String getData(String uploadID) {
		String tempSID = Main.obj.studentID1;
		if (!(Main.obj.studentID2.equals("null") || Main.obj.studentID2 == null))
			tempSID = tempSID + "-" + Main.obj.studentID2;
		String studentID = JOptionPane.showInputDialog("Please enter your studentID's", tempSID);
		if (studentID == null || studentID.equals(""))
			studentID = Main.obj.studentID1 + "-" + Main.obj.studentID2;
		String url = Main.server + "/Micro/rawlist.php?" + "studentID=" + studentID + "&uploadID=" + uploadID;
		StringBuilder content;

		try {

			URL myurl = new URL(url);
			con = (HttpURLConnection) myurl.openConnection();

			con.setRequestMethod("GET");
			progress.setProgress(0.2);
			double i = 1;

			try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

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

		} catch (Exception e) {
			e.printStackTrace();
			progress.setProgress(0);
			return null;
		} finally {

			con.disconnect();
		}

		return content.toString();
	}

	private Obj jsonParser(String jsonStr) {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) jsonParser.parse(jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Obj obj = Main.obj;
		obj.setUploadID((String) jsonObject.get("UploadID"));
		obj.setScore(String.valueOf(jsonObject.get("Score")));
		obj.setHeart(String.valueOf(jsonObject.get("Heart")));
		obj.setLevel(String.valueOf(jsonObject.get("Level")));
		obj.setSpeed(String.valueOf(jsonObject.get("Speed")));
		obj.setTurboCharge(String.valueOf(jsonObject.get("Turbo")));
		obj.setGameSeconds(String.valueOf(jsonObject.get("gsec")));

		String s = (String) jsonObject.get("Time");
		obj.setHr(s.substring(0, 2));
		obj.setMin(s.substring(2, 4));
		obj.setSec(s.substring(4));

		s = (String) jsonObject.get("Date");
		obj.setYear(s.substring(0, 2));
		obj.setMonth(s.substring(2, 4));
		obj.setDay(s.substring(4));

		s = (String) jsonObject.get("MainCarPos");
		obj.setMainCar(new Position(s.substring(0, 2), s.substring(2)));

		s = (String) jsonObject.get("EnemyCarPos");
		ArrayList<Position> positions = new ArrayList<>();
		for (int i = 0; i < s.length(); i += 4) {
			String tmp = s.substring(i, i + 4);
			Position position = new Position(tmp.substring(0, 2), tmp.substring(2));
			positions.add(position);
		}

		obj.setEnemyCars(positions);

		return obj;
	}

	private void sendSerial(Obj obj) throws SerialPortException {

		/*
		 * send uploadID
		 */
		send(("upid").getBytes());
		send(("\n").getBytes());

		send(obj.getUploadID().getBytes());
		send(("\n").getBytes());

		/*
		 * send heart
		 */
		send(("heart").getBytes());
		send(("\n").getBytes());

		send((obj.getHeart()).getBytes());
		send(("\n").getBytes());

		/*
		 * send level
		 */
		send(("level").getBytes());
		send(("\n").getBytes());

		send((obj.getLevel()).getBytes());
		send(("\n").getBytes());

		progress.setProgress(0.7);

		/*
		 * send score
		 */
		send(("score").getBytes());
		send(("\n").getBytes());

		send((obj.getScore()).getBytes());
		send(("\n").getBytes());

		/*
		 * send turbo charge
		 */
		send(("turbo").getBytes());
		send(("\n").getBytes());

		send((obj.getTurboCharge()).getBytes());
		send(("\n").getBytes());

		/*
		 * send speed
		 */
		send(("speed").getBytes());
		send(("\n").getBytes());

		send((obj.getSpeed()).getBytes());
		send(("\n").getBytes());

		progress.setProgress(0.8);

		/*
		 * send game second
		 */
		send(("gsec").getBytes());
		send(("\n").getBytes());

		send((obj.getGameSeconds()).getBytes());
		send(("\n").getBytes());

		/*
		 * send time
		 */
		send(("time").getBytes());
		send(("\n").getBytes());

		send((obj.getHr() + obj.getMin() + obj.getSec()).getBytes());
		send(("\n").getBytes());

		/*
		 * send date
		 */
		send(("date").getBytes());
		send(("\n").getBytes());

		send((obj.getYear() + obj.getMonth() + obj.getDay()).getBytes());
		send(("\n").getBytes());

		progress.setProgress(0.9);

		/*
		 * send main car position
		 */
		send(("mcp").getBytes());
		send(("\n").getBytes());

		send((obj.getMainCar().getX() + obj.getMainCar().getY()).getBytes());
		send(("\n").getBytes());

		/*
		 * send enemy car position
		 */
		send(("ecp").getBytes());
		send(("\n").getBytes());

		StringBuilder enemy = new StringBuilder();
		for (Position position : obj.getEnemyCars()) {
			enemy.append(position.getX()).append(position.getY());
		}
		send(enemy.toString().getBytes());
		send(("\n").getBytes());

		send(("end\n").getBytes());
		
		progress.setProgress(1);
	}

	private void send(byte[] buffer) throws SerialPortException {

		Main.serialPort.writeBytes(buffer);

	}

	private String createSendingFormat(Obj obj) {
		StringBuilder stringBuilder = new StringBuilder();

		if (obj.getStudentID2() != null && !obj.getStudentID2().equals("null") && !obj.getStudentID2().equals("")) {
			stringBuilder.append("studentID=").append(obj.getStudentID1()).append("-").append(obj.getStudentID2())
					.append("&");
		} else {
			stringBuilder.append("studentID=").append(obj.getStudentID1()).append("&");
		}

		/* uploadID */
		if (obj.getUploadID() == null)
			obj.setUploadID(Math.floor(Math.random()) + "");
		stringBuilder.append("uploadID=").append(obj.getUploadID()).append("&");

		/* score */
		stringBuilder.append("score=").append(obj.getScore()).append("&");

		/* heart */
		stringBuilder.append("heart=").append(obj.getHeart()).append("&");

		/* level */
		stringBuilder.append("level=").append(obj.getLevel()).append("&");

		/* time */
		stringBuilder.append("time=").append(obj.getHr()).append(obj.getMin()).append(obj.getSec()).append("&");

		/* date */
		stringBuilder.append("date=").append(obj.getYear()).append(obj.getMonth()).append(obj.getDay()).append("&");

		/* turbo */
		stringBuilder.append("turbo=").append(obj.getTurboCharge()).append("&");

		/* speed */
		stringBuilder.append("speed=").append(obj.getSpeed()).append("&");

		/* game seconds */
		stringBuilder.append("gsec=").append(obj.getGameSeconds()).append("&");

		/* main car position */
		stringBuilder.append("mcp=").append(obj.getMainCar().getX()).append(obj.getMainCar().getY()).append("&");

		/* enemy car position */
		stringBuilder.append("ecp=");
		for (Position position : obj.getEnemyCars()) {
			stringBuilder.append(position.getX()).append(position.getY());
		}
		if (obj.getEnemyCars().isEmpty()) {
			stringBuilder.append("0000");
		}

		return stringBuilder.toString();
	}
}
