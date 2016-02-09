import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import edu.princeton.cs.introcs.StdIn;
import edu.princeton.cs.introcs.In;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;



public class GitletTest {
	 public static void main(String[] args) {
        System.exit(jh61b.junit.textui.runClasses(GitletTest.class));
    }

    @Test
    public void testBasics() {
    	Gitlet g = new Gitlet();
    	g("init");
    	File test = new File("pie.txt");
    	g("add", test);
    	g("commit", "test commit 1");
    	g("log");

    }


}