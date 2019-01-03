import java.util.ArrayList;

public class Obj {
	public String name;
	private String studentID1;
	private String studentID2;
	public String score;
	public String sec;
	public String min;
	public String hr;
	public String year;
	public String month;
	public String day;
	public String heart;
	public String level;
	public Position mainCar;
	public ArrayList<Position> enemyCars;
	public String turboCharge;
	public String speed;
	public String gameSeconds;

	Obj() {
		mainCar = new Position("00", "00");
		enemyCars = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStudentID1() {
		return studentID1;
	}

	public void setStudentID1(String studentID1) {
		this.studentID1 = studentID1;
	}

	public String getStudentID2() {
		return studentID2;
	}

	public void setStudentID2(String studentID2) {
		this.studentID2 = studentID2;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getSec() {
		return sec;
	}

	public void setSec(String sec) {
		this.sec = sec;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getHr() {
		return hr;
	}

	public void setHr(String hr) {
		this.hr = hr;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getHeart() {
		return heart;
	}

	public void setHeart(String heart) {
		this.heart = heart;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Position getMainCar() {
		return mainCar;
	}

	public void setMainCar(Position mainCar) {
		this.mainCar = mainCar;
	}

	public ArrayList<Position> getEnemyCars() {
		return enemyCars;
	}

	public void setEnemyCars(ArrayList<Position> enemyCars) {
		this.enemyCars = enemyCars;
	}

	public String getTurboCharge() {
		return turboCharge;
	}

	public void setTurboCharge(String turboCharge) {
		this.turboCharge = turboCharge;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getGameSeconds() {
		return gameSeconds;
	}

	public void setGameSeconds(String gameSeconds) {
		this.gameSeconds = gameSeconds;
	}
}
