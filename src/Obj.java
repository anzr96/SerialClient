import java.util.ArrayList;

public class Obj {
	public String name;
	private String studentID1;
	private String studentID2;
	public int score;
	public int sec, min, hr;
	public int year, month, day;
	public int heart;
	public int level;
	public Position mainCar;
	public ArrayList<Position> enemyCars;
	public int turboCharge;
	public int speed;
	public int gameSeconds;

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

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getHr() {
		return hr;
	}

	public void setHr(int hr) {
		this.hr = hr;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHeart() {
		return heart;
	}

	public void setHeart(int heart) {
		this.heart = heart;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
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

	public int getTurboCharge() {
		return turboCharge;
	}

	public void setTurboCharge(int turboCharge) {
		this.turboCharge = turboCharge;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getGameSeconds() {
		return gameSeconds;
	}

	public void setGameSeconds(int gameSeconds) {
		this.gameSeconds = gameSeconds;
	}
}
