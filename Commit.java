import java.io.Serializable;
import java.io.File;
import java.util.HashMap;
import java.util.Date;
import java.util.HashSet;

public class Commit implements Serializable {
    private HashMap<File,String> files;
    private HashMap<String,File> oppfiles;
    private String message;
    private int id;
	private String time;

    public Commit(String message) {
    	this.message = message;
    	files = new HashMap<File,String>();
    	oppfiles = new HashMap<String,File>();
    }


    public String getMessage() {
    	return message;
    }

    public void setMessage(String message) {
    	this.message = message;
    }

    public void addFile(File file,String string) {
    	files.put(file,string);
    }

    public void addOppFile(String string,File file) {
    	oppfiles.put(string,file);
    }

    public int getID() {
    	return id;
    }

    public void setID(int num) {
    	id = num;
    } 

    public HashMap<File,String> getFiles() {
    	return files;
    }

     public HashMap<String,File> getOppFiles() {
    	return oppfiles;
    }

    public void addTime(String date) {
    	time = date;
    }

    public String getTime() {
    	return time;
    }

    public void putFiles(HashMap<File,String> files) {
    	this.files = files;
    }
}