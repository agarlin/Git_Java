import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class CommitTree implements Serializable {
	private Node<Commit> pointer;
	private int num;

    public CommitTree() {
    	// pointer.parent = null;
    	pointer = null;
    }

    public CommitTree(Node<Commit> c) {
    	pointer = c;
    }

    public Commit getHeadParent() {
    	return pointer.parent.commit;
    }


    public Commit getParent(Commit n) {
    	Node<Commit> saver = pointer;
    	while(saver.parent != null) {
    		if (saver.commit == n) {
    			return saver.parent.commit;
    		}
    		saver = saver.parent;
    	}
    	return null;

    }

    public Commit getOriginalParent() {
    	Node<Commit> saver = pointer;
    	while(saver != null) {
    		if (saver.parent == null) {
    			return saver.commit;
    		}
    		saver = saver.parent;
    	}
    	return null;

    }

    public Node<Commit> getHeadParentNode() {
    	return pointer.parent;
    }

    public Node<Commit> getParentNode(Node<Commit> node) {
    	return node.parent;

    }

    public void add(Commit d) {
    	Node<Commit> temp = new Node<Commit>(d);
    	temp.parent = pointer;
    	pointer = temp;
    	pointer.id = num + 1;
    	num += 1;

    }

    public Node<Commit> getHead() {
    	return pointer;
    }

    public int getHeadID() {
    	return pointer.id;
    }

    public Commit getCommit(Node<Commit> n) {
    	return n.commit;
    }
    
    public Commit getHeadCommit() {
    	return pointer.commit;
    }
    public void removeHead() {
    	pointer = null;
    }

    public void setParent(Node<Commit> c) {
    	pointer.parent = c;
    }

    public static class Node<Commit> implements Serializable {
    	private Commit commit;
    	private Node<Commit> parent;
    	private int id;
  //   	private HashSet<File> files;
  //   	private String message;
  //   	private int id;
		// private Double time;

		public Node(Commit c) {
			commit = c;
		}
    }
} 