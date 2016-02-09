import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;


public class Gitlet implements Serializable {
    String curr;
    HashMap<String, CommitTree> gitmap;
    boolean added;
    HashSet<File> stage;
    HashMap<File, String> track;
    HashSet<String> staged;
    HashMap<Integer, HashMap<String, File>> all;
    HashSet<File> remove;
    HashSet<Commit> commits;
    int idcount;
    HashMap<File, String> alladds;
    File commit;
    File head;
    int change = 0;
    HashSet<String> stagenames;
    HashMap<String, Integer> heads;
    public static void main(String[] args) {
        String command = args[0];
        Gitlet g = checkGitlet();
        switch (command) {
            case "init":
                if (g != null) {
                    String msg = 
                        "A gitlet version control system already exists in the current directory.";
                    System.out.println(msg);
                    return;
                } else {
                    g = new Gitlet();
                }
                g.init(g);
                save(g);
                return;  
            case "add":
                if (g == null) {
                    System.out.println("Must initialize gitlet first");
                    return;
                }
                g.add(args[1], g);
                g.addNormal(args[1], g);
                return;
            case "commit": 
                g.commit(args[1], g);
                save(g);
                return;
            case "log":
                g.log(g);
                return;
            case "rm":
                g.rm(args[1], g);
                save(g);
                return;
            case "global-log":
                g.global(g);
                return;
            case "branch":
                g.branch(args[1], g);
                save(g);
                return;
            case "checkout":
                if (args.length == 2) {
                    g.checkout(args[1], g);
                } else if (args.length > 2) {
                    g.checkout2(args[1], args[2], g);
                }
                save(g);
                return;
            case "status":
                g.status(g);
                return;  
            case "rm-branch":
                g.rmBranch(args[1], g);
                save(g);
                return;
            case "reset":
                g.reset(args[1], g);
                return;
            case "find":
                g.find(args[1], g);
                return;
            case "merge":
                g.merge(args[1], g);
                save(g);
                return;      
            case "rebase":
                g.rebase(args[1], g);
                save(g);
                return;
            case "i-rebase":
                g.iRebase(args[1], g);
                save(g);
                return;
            default:
                System.out.println("Please enter a valid command");
                return;
        }    
    }
    private static Gitlet checkGitlet() {
        Gitlet gitl = null;
        File gitletfile = new File("C:\\Users\\AnnaMarie\\cs61b\\acy\\proj2\\.gitlet\\gitlet.ser");
        if (gitletfile.exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(gitletfile);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                gitl = (Gitlet) objectIn.readObject();

            } catch (IOException e) {
                System.out.println(gitletfile);
                String msg = "IOException while loading gitlet.";
                System.out.println(msg);
            } catch (ClassNotFoundException e) {
                String msg = "ClassNotFoundException while loading gitlet.";
                System.out.println(msg);
            }
        }
        return gitl;
    }

    private static void save(Gitlet gitl) {
        if (gitl == null) {
            return;
        }
        try {
            File gitletfile = new File(
                "C:\\Users\\AnnaMarie\\cs61b\\acy\\proj2\\.gitlet\\gitlet.ser");
            FileOutputStream fileOut = new FileOutputStream(gitletfile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(gitl);
        } catch (IOException e) {
            String msg = "IOException while saving gitlet.";
            System.out.println(msg);
        }
    }

    public void copy(FileInputStream in, FileOutputStream out) {
        try {
            String a = "1024";
            byte[] buf = new byte[Integer.parseInt(a)];
            int bytesRead;
            while ((bytesRead = in.read(buf)) > 0) {
                out.write(buf, 0, bytesRead);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
            System.out.println("IOException while loading");
        }
            
    }   

    public boolean identical(FileInputStream in, FileInputStream out, Gitlet g) {
        try {
            int a = in.read();
            int b = out.read();
            while (a != -1 || b != -1) {
                if (a != b) {
                    System.out.println("got to unequal immediately");
                    return false;
                }
                a = in.read();
                b = out.read();
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
            System.out.println("IOException while loading");
        }
        return true;
    }

    public void add(String add, Gitlet g) {
        try {
            if (!new File(add).exists() || new File(add).isDirectory()) {
                System.out.println("File does not exist");
                return;
            }
            boolean identical = true;
            if (new File(".gitlet\\commits\\" + g.head.getName()).exists()) {
                for (String f
                    : g.gitmap.get(g.curr).getHeadCommit().getOppFiles().keySet()) {
                    if (f.equals(add) 
                        && !f.contains("/")) {  //Need to somehow get to no changes in the file
                        FileInputStream in = new FileInputStream(
                            g.gitmap.get(g.curr).getHeadCommit().getOppFiles().get(f));
                        FileInputStream out = new FileInputStream(
                            new File(System.getProperty("user.dir") + "\\" + add));
                        identical = g.identical(in, out, g);
                        if (!identical) {
                            FileInputStream input = new FileInputStream(
                                System.getProperty("user.dir") + "\\" + add);
                            File temps = new File(".gitlet\\commits\\" + (g.idcount + 1));
                            temps.mkdir();
                            File stag = new File(".gitlet\\commits\\" 
                                + (g.idcount + 1) + "\\" + add);
                            stag.createNewFile();
                            FileOutputStream output = new FileOutputStream(stag);
                            g.copy(input, output);
                            g.added = true;
                            g.track.put(new File(".gitlet\\commits\\" 
                                + (g.idcount + 1) + "\\" + add), 
                                System.getProperty("user.dir") + "\\" + add);
                            g.staged.add(add);
                            g.alladds.put(new File(".gitlet\\commits\\" 
                                + (g.idcount + 1) + "\\" 
                                + new File(add).getName()), System.getProperty("user.dir") 
                                + "\\" + add);
                        } else {
                            System.out.println("No changes to the file.");
                            return;
                        }
                    } else if (f.equals(new File(add).getName()) && add.contains("/")) {
                        FileInputStream in2 = new FileInputStream(
                            g.gitmap.get(g.curr).getHeadCommit().getOppFiles().get(f));
                        FileInputStream out2 = new FileInputStream(add);
                        identical = g.identical(in2, out2, g);
                        if (!identical) {
                            File temps = new File(".gitlet\\commits\\" + (g.idcount + 1));
                            temps.mkdir();
                            g.change += 1;
                            File stag = new File(".gitlet\\commits\\" 
                                + (g.idcount + 1) + "\\" + add + g.change);
                            stag.createNewFile();
                            FileInputStream input = new FileInputStream(add);
                            FileOutputStream output = new FileOutputStream(stag);
                            g.copy(input, output);
                            g.added = true;
                            g.track.put(stag, add);
                            g.staged.add(add);
                            g.alladds.put(stag, add);
                        } else {
                            System.out.println("No changes to the file.");
                            return;
                        }
                    }
                    Path who = Paths.get(".gitlet\\commits\\" + (g.idcount + 1) + "\\" + add);
                    File what = (who).toFile();
                    g.added = true;
                    g.stage.add(what);
                    g.stagenames.add(what.getName());
                    save(g);
                    return; 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
            System.out.println("IOException while loading");
        }
    } 

    public void addNormal(String add, Gitlet g) {
        try {
            g.add(add, g);
            File temp = new File(System.getProperty("user.dir") 
                + "\\" + ".gitlet\\commits\\" + (g.idcount + 1));
            temp.mkdirs();
            File sta;
            if (add.contains("/")) {
                g.change += 1;
                sta = new File(temp + "\\" + new File(add).getName() + g.change);
                sta.createNewFile();
            } else {
                sta = new File(temp + "\\" + new File(add).getName());
                sta.createNewFile();
            }
            FileInputStream in = new FileInputStream(add);
            FileOutputStream out = new FileOutputStream(sta);
            g.copy(in, out);
            g.stage.add(sta);
            g.stagenames.add(sta.getName());               
            g.alladds.put(sta, add);
            g.track.put(sta, add);
            g.staged.add(add);                  
            g.added = true;                 
            save(g);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
            System.out.println("IOException while loading");
        }
    }

    public void rebase(String add, Gitlet g) {
        Scanner reader = new Scanner(System.in);
        System.out.println("Warning: The command you entered may alter " 
            + "the files in your working directory." 
            + " Uncommitted changes may be lost. Are you sure you want to continue? (yes/no)");
        String a = reader.next();
        if (a.equals("y") || a.equals("yes")) {
            if (!g.heads.keySet().contains(add)) {
                System.out.println(" A branch with that name does not exist.");
                return;
            }
            if (add.equals(g.curr)) {
                System.out.println("Cannot merge a branch with itself.");
                return;
            }
            CommitTree now = g.gitmap.get(g.curr);
            CommitTree other = g.gitmap.get(add);
            HashSet<Commit> noww = new HashSet<Commit>();
            CommitTree otherr = new CommitTree();
            Commit split = null;
            Commit nhead = now.getCommit(now.getHeadParentNode());
            Commit ohead = now.getCommit(other.getHeadParentNode());
            while (nhead != null) {
                if (nhead.getID() == ohead.getID()) {
                    split = nhead;
                    break;
                }
                noww.add(nhead);
                otherr.add(ohead);
                ohead = other.getParent(ohead);
                nhead = now.getParent(nhead);
            }
            otherr.setParent(g.gitmap.get(g.curr).getHead());
        }
    }

    public void branch(String add, Gitlet g) {
        if (g == null) {
            System.out.println("Must initialize gitlet first");
            return;
        }
        if (g.heads.keySet().contains(add)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        CommitTree ci = new CommitTree(g.gitmap.get(g.curr).getHead());
        g.gitmap.put(add, ci);
        g.heads.put(add, g.idcount);
    }

    public void merge(String add, Gitlet g) {
        Scanner reader = new Scanner(System.in);
        System.out.println("Warning: The command you entered may alter the files" 
            + " in your working directory. Uncommitted changes may be lost. " 
            + "Are you sure you want to continue? (yes/no)");
        String a = reader.next();
        if (a.equals("y") || a.equals("yes")) {
            try {
                if (!g.heads.keySet().contains(add)) {
                    System.out.println(" A branch with that name does not exist.");
                    return;
                }
                if (add.equals(g.curr)) {
                    System.out.println("Cannot merge a branch with itself.");
                    return;
                }
                CommitTree now = g.gitmap.get(g.curr);
                CommitTree other = g.gitmap.get(add);
                HashSet<Commit> noww = new HashSet<Commit>();
                HashSet<Commit> otherr = new HashSet<Commit>();
                Commit split = null;
                Commit nhead = now.getCommit(now.getHeadParentNode());
                Commit ohead = now.getCommit(other.getHeadParentNode());
                while (nhead != null) {
                    if (nhead.getID() == ohead.getID()) {
                        split = nhead;
                        break;
                    }
                    noww.add(nhead);
                    otherr.add(ohead);
                    nhead = now.getParent(nhead);
                }
                boolean curridentical;
                boolean otheridentical;
                Commit currhead = now.getHeadCommit();
                if (split != null) {
                    int i = split.getID();
                    System.out.println(g.gitmap.get(g.curr).getHeadCommit().getOppFiles());
                    for (String f: g.gitmap.get(add).getHeadCommit().getOppFiles().keySet()) {
                        if (Integer.parseInt(g.gitmap.get(add).
                            getHeadCommit().getOppFiles().get(f).getParentFile().getName()) > i) {
                            if (g.gitmap.get(g.curr).
                                getHeadCommit().getOppFiles().keySet().contains(f)) {
                                FileInputStream s = new FileInputStream(
                                    g.gitmap.get(add).getHeadCommit().getOppFiles().get(f));
                                FileInputStream curre = new FileInputStream(
                                    g.gitmap.get(g.curr).getHeadCommit().getOppFiles().get(f));
                                curridentical = g.identical(s, curre, g);
                                System.out.println(f);
                                if (!curridentical) {
                                    File conflict = new File(System.getProperty("user.dir") + "\\" 
                                        + new File(f).getName().split("\\.")[0] + ".conflicted");
                                    conflict.createNewFile();
                                    FileInputStream b = new FileInputStream(
                                        g.gitmap.get(add).getHeadCommit().getOppFiles().get(f));
                                    FileOutputStream v = new FileOutputStream(conflict);
                                    g.copy(b, v);
                                }
                            } else {
                                FileInputStream inn = new FileInputStream(
                                    g.gitmap.get(add).getHeadCommit().getOppFiles().get(f));
                                FileOutputStream outt = new FileOutputStream(f);
                                g.copy(inn, outt);
                            }
                        }
                    }
                } 
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println(e);
                System.out.println("IOException while loading");
            }
        }
    }

    public void find(String add, Gitlet g) {
        if (g == null) {
            System.out.println("Must initialize gitlet first");
            return;
        }
        for (Commit ca: g.commits) {
            if (ca.getMessage().equals(add)) {
                System.out.println(ca.getID());
            }
        }
    }

    public void rmBranch(String add, Gitlet g) {
        String branch = add;
        if (!g.gitmap.keySet().contains(branch)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (g.curr.equals(branch) || branch.equals("master")) {
            System.out.println("Cannot remove the current branch.");
            return;
        } else {
            g.heads.remove(branch);
            g.gitmap.get(branch).removeHead();
        }
    }

    public void status(Gitlet g) {
        System.out.println("=== Branches ===");
        for (String s: g.heads.keySet()) {
            if (s.equals(g.curr)) {
                System.out.println("*" + s);
            } else {
                System.out.println(s);
            }
        }
        System.out.println("\n");
        System.out.println("=== Staged Files ===");
        for (String s: g.staged) {
            System.out.println(s);
        }
        System.out.println("\n");
        System.out.println("=== Files Marked For Removal ===");
        for (File s: g.remove) {
            System.out.println(s.toPath());
        }
    }

    public void commit(String add, Gitlet g) {
        try {
            DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dat = new Date();
            String time = date.format(dat);
            if (g == null) {
                System.out.println("Must initialize gitlet first");
                return;
            }
            Commit c = new Commit(add);  //Later, make every commit keep its original path in a map
            if (!g.remove.isEmpty()) { 
                for (File f: g.remove) {
                    if (g.track.keySet().contains(f)) {
                        g.track.remove(f);
                    }
                }
            }
            HashMap<String, File> temp2 = new HashMap<String, File>();
            if (g.added || !g.remove.isEmpty()) {
                for (File f: g.track.keySet()) {
                    c.addFile(f, g.track.get(f));
                    c.addOppFile(g.track.get(f), f);
                }
                for (File t: g.gitmap.get(g.curr).getHeadCommit().getFiles().keySet()) {
                    if (g.track.keySet().contains(t)) {
                        c.addFile(t, g.track.get(t));
                        c.addOppFile(g.track.get(t), t);
                        temp2.put(g.track.get(t), t);
                        File te = new File(
                            ".gitlet\\commits\\" + (g.idcount + 1) + "\\" + t.getName());
                        te.createNewFile();
                        FileInputStream ina = new FileInputStream(t);
                        FileOutputStream outa = new FileOutputStream(te);
                        g.copy(ina, outa);
                    }
                }
                c.addTime(time);  //dont forget to add to commit folder as well!!!
                g.idcount += 1;
                c.setID(g.idcount);
                g.all.put(g.idcount, temp2);
                g.gitmap.get(g.curr).add(c);
                g.commits.add(c);
                g.stage.clear();
                g.stagenames.clear();
                g.heads.put(g.curr, g.idcount);
                g.remove.clear();
                g.track.clear();
                g.staged.clear();
                g.added = false;
                int num = c.getID();
                File comm = new File(".gitlet\\commits\\" + num);
                if (!comm.exists()) {
                    comm.mkdir();
                }                 
                g.head = comm;
            } else {
                System.out.println("No changes added to the commit.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
            System.out.println("IOException while loading");
        }
    }

    public void init(Gitlet g) {
        g.gitmap = new HashMap<String, CommitTree>();
        File gitlet = new File(".gitlet");
        g.track = new HashMap<File, String>();
        g.stage = new HashSet<File>();
        g.remove = new HashSet<File>();
        g.commits = new HashSet<Commit>();
        g.stagenames = new HashSet<String>();
        g.all = new HashMap<Integer, HashMap<String, File>>();
        gitlet.mkdir();
        g.heads = new HashMap<String, Integer>();
        File tracked = new File(".gitlet\\master\\tracked");
        tracked.mkdir();
        g.alladds = new HashMap<File, String>();
        g.commit = new File(".gitlet\\commits");
        g.commit.mkdir();
        g.staged = new HashSet<String>();
        g.curr = "master";
        CommitTree masters = new CommitTree();
        Commit initial = new Commit("initial commit");
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dat = new Date();
        String time = date.format(dat);
        initial.addTime(time);
        initial.setID(g.idcount);
        masters.add(initial);
        g.head = new File(".gitlet\\commits\\0");
        g.head.mkdir();
        g.gitmap.put("master", masters);
        g.heads.put("master", 0);
        g.commits.add(initial);
      
    }

    public void rm(String add, Gitlet g) {
        if (g == null) {
            System.out.println("Must initialize gitlet first");
            return;
        }
        CommitTree comm = g.gitmap.get(g.curr);
        Commit rem = 
            comm.getCommit(comm.getHead());
        if (Arrays.asList(g.head.listFiles()).contains(new File(g.head + "\\" + add))) {
            g.remove.add(new File(add));
        } else {
            System.out.println("No reason to remove the file.");
        }
    }

    public void log(Gitlet g) {
        if (g == null) {
            System.out.println("Must initialize gitlet first");
            return;
        }
        CommitTree com = g.gitmap.get(g.curr);
        Commit next = com.getCommit(com.getHead());
        while (next != null) {
            System.out.println("====");
            System.out.println("Commit " + next.getID() + ".");
            System.out.println(next.getTime());
            System.out.println(next.getMessage());
            next = com.getParent(next);
            if (next == null) {
                break;
            }
            System.out.print("\n");
        }
    }

    public void iRebase(String add, Gitlet g) {
        Scanner reader = new Scanner(System.in);
        System.out.println("Warning: The command you entered may alter the files" 
            + " in your working directory. Uncommitted changes may be lost. " 
            + "Are you sure you want to continue? (yes/no)");
        String a = reader.next();
        if (a.equals("y") || a.equals("yes")) {
            if (!g.heads.keySet().contains(add)) {
                System.out.println(" A branch with that name does not exist.");
                return;
            }
            if (add.equals(g.curr)) {
                System.out.println("Cannot merge a branch with itself.");
                return;
            }
            CommitTree now = g.gitmap.get(g.curr);
            CommitTree other = g.gitmap.get(add);
            HashSet<Commit> noww = new HashSet<Commit>();
            CommitTree otherr = new CommitTree();
            Commit split = null;
            Commit nhead = now.getCommit(now.getHeadParentNode());
            Commit qhead = now.getCommit(now.getHeadParentNode());
            Commit ohead = other.getCommit(other.getHeadParentNode());
            int check = 0;
            while (ohead != null) {
                if (qhead.getID() == ohead.getID()) {
                    split = qhead;
                    break;
                }
                ohead = other.getParent(ohead);
                qhead = now.getParent(nhead);
            }
            while (now.getParent(nhead) != null) {
                noww.add(nhead);
                nhead = now.getParent(nhead);
            }
            for (Commit comit: noww) {
                Scanner scan = new Scanner(System.in);
                System.out.println("Currently replaying:");
                System.out.println("Commit:" + ohead.getID());
                System.out.println(ohead.getTime());
                System.out.println(ohead.getMessage());
                System.out.println(
                    "Would you like to (c)ontinue, "
                     + "(s)kip this commit, or change this commit's (m)essage?");
                String p = scan.next();
                if (p.equals("c") && check != noww.size() && check != 0) {
                    DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date dat = new Date();
                    String time = date.format(dat);
                    Commit temp = new Commit(comit.getMessage());
                    temp.addTime(time);
                    temp.putFiles(comit.getFiles());
                    g.idcount += 1;
                    temp.setID(g.idcount);
                    otherr.add(temp);
                    check += 1;
                } else if (p.equals("s")) {
                    check = check;
                } else if (p.equals("m")) {
                    Scanner mess = new Scanner(System.in);
                    System.out.println("Please enter a new message for this commit.");
                    String message = mess.next();
                    DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date dat = new Date();
                    String time = date.format(dat);
                    Commit temp = new Commit(message);
                    temp.addTime(time);
                    temp.putFiles(comit.getFiles());
                    g.idcount += 1;
                    temp.setID(g.idcount);
                    otherr.add(temp);
                    check += 1;
                }         
            
            }
            otherr.setParent(g.gitmap.get(g.curr).getHead());
        }
    }

    public void global(Gitlet g) {
        if (g == null) {
            System.out.println("Must initialize gitlet first");
            return;
        }
        for (Commit co: g.commits) {
            System.out.println("====");
            System.out.println("Commit " + co.getID() + ".");
            System.out.println(co.getTime());
            System.out.println(co.getMessage());
            System.out.print("\n");
        }
    }

    public void checkout(String args, Gitlet g) {
        try {
            Scanner reader = new Scanner(System.in);
            System.out.println(
                "Warning: The command you entered may alter the files in your working directory." 
                + " Uncommitted changes may be lost. Are you sure you want to continue? (yes/no)");
            String a = reader.next();
            if (a.equals("y") || a.equals("yes")) {
                if (args.equals(g.curr)) {
                    System.out.println("No need to checkout the current branch.");
                    return;
                }
                if (g.gitmap.keySet().contains(args)) {
                    g.curr = args;
                    CommitTree co = g.gitmap.get(g.curr);
                    Commit h = co.getHeadCommit();
                    for (String f: h.getOppFiles().keySet()) {
                        if (f.contains("\\")) {
                            FileInputStream e = new FileInputStream(h.getOppFiles().get(f));
                            FileOutputStream k = new FileOutputStream(f);
                            g.copy(e, k);
                        } else {
                            FileInputStream l = new FileInputStream(h.getOppFiles().get(f));
                            FileOutputStream s = new FileOutputStream(
                                System.getProperty("user.dir") + "\\" + f);
                            g.copy(l, s);
                        }
                    }
                    
                } else if (g.gitmap.get(g.curr).
                    getHeadCommit().getOppFiles().keySet().contains(args)) {
                    if (!args.contains("\\")) {
                        FileInputStream first = new FileInputStream(g.gitmap.
                            get(g.curr).getHeadCommit().getOppFiles().get(args));
                        FileOutputStream second = new FileOutputStream(args);
                        g.copy(first, second);
                        
                    } else {
                        FileInputStream i;
                        i = new FileInputStream(g.gitmap.
                            get(g.curr).getHeadCommit().getOppFiles().get(args));
                        FileOutputStream o = new FileOutputStream(args);
                        g.copy(i, o);
                            
                    } 
                } else {
                    System.out.println(
                        "File does not exist in the most recent commit,or no such branch exists.");
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
            System.out.println("IOException while loading");
        }
    }
    public void checkout2(String one, String two, Gitlet g) {
        try {
            int id = (Integer) Integer.parseInt(one);
            String file = two;
            if (file.contains("\\")) {
                if (g.all.get(id).keySet().contains(file)) {
                    FileInputStream d = new FileInputStream(g.all.get(id).get(file));
                    FileOutputStream x = new FileOutputStream(file);
                    g.copy(d, x);
                } else {
                    System.out.println("File does not exist in that commit.");
                    return;
                }
            } else {
                if (new File(".gitlet\\commits\\" + id).exists()) {
                    File[] files = new File(".gitlet\\commits\\" + id).listFiles();
                    int check = 0;
                    for (File a: files) {
                        if (a.getName().equals(file)) {
                            FileInputStream w = new FileInputStream(a);
                            FileOutputStream q;
                            q = new FileOutputStream(System.getProperty("user.dir") + "\\" + file);
                            g.copy(w, q);
                            check += 1;
                        } 
                    }
                    if (check == 0) {
                        System.out.println("File does not exist in that commit.");
                        return;
                    }
                } 
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
            System.out.println("IOException while loading");
        }
    }

    public void reset(String args, Gitlet g) {
        try { 
            File y = new File(".gitlet\\commits\\" + args);
            if (!y.exists()) {
                System.out.println("No commit with that id exists.");
                return;
            } else {
                HashMap<File, String> reset = null;
                for (Commit commm: g.commits) {
                    if (commm.getID() == Integer.parseInt(args)) {
                        reset = commm.getFiles();
                    }
                }    
                for (File a: reset.keySet()) {
                    if (g.alladds.keySet().contains(a)) {
                        if (g.alladds.get(a).contains("/")) {
                            FileInputStream e = new FileInputStream(a);
                            FileOutputStream k = new FileOutputStream(g.alladds.get(a));
                            g.copy(e, k);
                        } else {
                            FileInputStream l = new FileInputStream(a);
                            FileOutputStream s = new FileOutputStream(
                                System.getProperty("user.dir") + "\\" + g.alladds.get(a));
                            g.copy(l, s);
                        }
                    }
                }
                g.heads.put(g.curr, Integer.parseInt(args));
                g.head = y;
                save(g);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
            System.out.println("IOException while loading");
        }
       
    }
}





    
