import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

/**
* Basic Algorithms Programming Assignment 2: Hijacking the fees
* @author Lev Bernstein
*/


class Node {
    String guide;
    int value;
}

class InternalNode extends Node {
    Node child0, child1, child2;
}

class LeafNode extends Node {
}

class TwoThreeTree {
    Node root;
    int height;
    
    TwoThreeTree() {
        root = null;
        height = -1;
        // empty tree has a height of -1; tree with just root has a height of 0, as the root is 0 away from the leaves
    }
}

class WorkSpace {
    // this class is used to hold return values for the recursive doInsert routine (see below)
    Node newNode;
    int offset;
    boolean guideChanged;
    Node[] scratch;
}

public class twothree {
    
    public static void main(String[] args) throws Exception { // any method using BufferedWriter must be wrapped with throws Exception
        //All queries, incuding range updates(type 2 queries), should run in time O(logn).
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
        Scanner scan = new Scanner(System.in);
        int type;
        //Values for type:
        //1 planetName entranceFee = insert a planet with name planetName and entrance fee entranceFee into the database; 
        //2 planetName secondPlanet entranceFee = increase the entrance fee for all planets between planetName and secondPlanet by entranceFee
        //3 planetName = return the entrance fee for planetName
        int queries; //n <= 100000
        int entranceFee; //0 <= delta(entranceFee) <= 10^5. Entrance fee will also always be less than 2^30.
        String planetName;
        String secondPlanet;
        String[] temp;
        
        queries = scan.nextInt();
        scan.nextLine();
        
        for (int i = 0; i < queries; i++) {
            temp = scan.nextLine().split(" ");
            type = Integer.parseInt(temp[0]);
            planetName = temp[1];
            if (type == 1) {
                entranceFee = Integer.parseInt(temp[2]);
            }
            else if (type == 2) {
                secondPlanet = temp[2];
                entranceFee = Integer.parseInt(temp[3]);
            }
            else if (type == 3) {
                
            }
        }
        
        output.flush(); //flush System.out at the end of main
    }
    
    static void addRange(Node p, String x, String y, int h, String lo, int delta, BufferedWriter output) throws Exception {
        if (h == 0) && (p.guide.compareTo(x) >= 0 && p.guide.compareTo(y) <= 0) {
            p.value += delta;
            return;
        }
        
        
    }
            
    static void insert(String key, int value, TwoThreeTree tree) {
        // insert a key value pair into tree (overwrite existing value if key is already present)
        
        int h = tree.height;
        
        
        if (h == -1) { //for an empty tree:
            LeafNode newLeaf = new LeafNode();
            newLeaf.guide = key;
            newLeaf.value = value;
            tree.root = newLeaf; 
            tree.height = 0;
        }
        
        else {
            WorkSpace ws = doInsert(key, value, tree.root, h);
            
            if (ws != null && ws.newNode != null) {
                // create a new root
                
                InternalNode newRoot = new InternalNode();
                if (ws.offset == 0) {
                    newRoot.child0 = ws.newNode; 
                    newRoot.child1 = tree.root;
                }
                else {
                    newRoot.child0 = tree.root; 
                    newRoot.child1 = ws.newNode;
                }
                
                resetGuide(newRoot);
                tree.root = newRoot;
                tree.height = h+1;
            }
        }
    }
                
    static WorkSpace doInsert(String key, int value, Node p, int h) {
        // auxiliary recursive routine for insert
        
        if (h == 0) {
            // we're at the leaf level, so compare and either update value or insert new leaf
            
            LeafNode leaf = (LeafNode) p; //downcast
            int cmp = key.compareTo(leaf.guide);
            
            if (cmp == 0) {
                leaf.value = value; 
                return null;
            }
            
            // create new leaf node and insert into tree
            LeafNode newLeaf = new LeafNode();
            newLeaf.guide = key; 
            newLeaf.value = value;
            
            int offset = (cmp < 0) ? 0 : 1;
            // offset == 0 => newLeaf inserted as left sibling
            // offset == 1 => newLeaf inserted as right sibling
            
            WorkSpace ws = new WorkSpace();
            ws.newNode = newLeaf;
            ws.offset = offset;
            ws.scratch = new Node[4];
            
            return ws;
        }
        else {
            InternalNode q = (InternalNode) p; // downcast
            int pos;
            WorkSpace ws;
            
            if (key.compareTo(q.child0.guide) <= 0) {
                pos = 0; 
                ws = doInsert(key, value, q.child0, h-1);
            }
            else if (key.compareTo(q.child1.guide) <= 0 || q.child2 == null) {
                pos = 1;
                ws = doInsert(key, value, q.child1, h-1);
            }
            else {
                pos = 2; 
                ws = doInsert(key, value, q.child2, h-1);
            }
            
            if (ws != null) {
                if (ws.newNode != null) {
                    // make ws.newNode child # pos + ws.offset of q
                    
                    int sz = copyOutChildren(q, ws.scratch);
                    insertNode(ws.scratch, ws.newNode, sz, pos + ws.offset);
                    if (sz == 2) {
                        ws.newNode = null;
                        ws.guideChanged = resetChildren(q, ws.scratch, 0, 3);
                    }
                    else {
                        ws.newNode = new InternalNode();
                        ws.offset = 1;
                        resetChildren(q, ws.scratch, 0, 2);
                        resetChildren((InternalNode) ws.newNode, ws.scratch, 2, 2);
                    }
                }
                else if (ws.guideChanged) {
                    ws.guideChanged = resetGuide(q);
                }
            }
                    
            return ws;
        }
    }
                                    
    static int copyOutChildren(InternalNode q, Node[] x) {
        // copy children of q into x, and return # of children
        
        int sz = 2;
        x[0] = q.child0; x[1] = q.child1;
        if (q.child2 != null) {
            x[2] = q.child2; 
            sz = 3;
        }
        return sz;
    }
        
    static void insertNode(Node[] x, Node p, int sz, int pos) {
        // insert p in x[0..sz) at position pos,
        // moving existing extries to the right
        
        for (int i = sz; i > pos; i--)
            x[i] = x[i-1];
        
        x[pos] = p;
    }
    
    static boolean resetGuide(InternalNode q) {
        // reset q.guide, and return true if it changes.
        
        String oldGuide = q.guide;
        if (q.child2 != null)
            q.guide = q.child2.guide;
        else
            q.guide = q.child1.guide;
        
        return q.guide != oldGuide;
    }
    
    static boolean resetChildren(InternalNode q, Node[] x, int pos, int sz) {
        // reset q's children to x[pos..pos+sz), where sz is 2 or 3.
        // also resets guide, and returns the result of that
        
        q.child0 = x[pos]; 
        q.child1 = x[pos+1];
        
        if (sz == 3) 
            q.child2 = x[pos+2];
        else
            q.child2 = null;
        
        return resetGuide(q);
    }
    
}
