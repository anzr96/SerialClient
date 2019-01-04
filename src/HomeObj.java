public class HomeObj {
    private String server;
    private String port;
    private Integer baud;
    private Integer data;
    private Integer stop;
    private String parity;
    private String studentID1;
    private String studentID2;

    HomeObj() {
        server = "http://majiid.ir";
        port = "COM10";
        baud = 115200;
        data = 8;
        stop = 1;
        parity = "None";
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Integer getBaud() {
        return baud;
    }

    public void setBaud(Integer baud) {
        this.baud = baud;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }

    public Integer getStop() {
        return stop;
    }

    public void setStop(Integer stop) {
        this.stop = stop;
    }

    public String getParity() {
        return parity;
    }

    public void setParity(String parity) {
        this.parity = parity;
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
}
