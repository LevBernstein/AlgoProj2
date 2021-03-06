import java.io.*;
import java.util.*;

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
        // empty tree has a height of -1; tree with just root has a height of 0, as the root is 0 away from the leaf (itself).
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
        // All queries, incuding range updates(type 2 queries), should run in time O(logn).
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
        Scanner scan = new Scanner(System.in);
        int type;
        // Values for type:
        // 1 planetName entranceFee = insert a planet with name planetName and entrance fee entranceFee into the database; 
        // 2 planetName secondPlanet entranceFee = increase the entrance fee for all planets between planetName and secondPlanet by entranceFee
        // 3 planetName = return the entrance fee for planetName
        int queries; // n <= 100000
        int entranceFee; // 0 <= delta(entranceFee) <= 10^5. Entrance fee will also always be less than 2^30.
        String planetName;
        String secondPlanet;
        String[] temp;
        TwoThreeTree tree = new TwoThreeTree();
        queries = scan.nextInt();
        scan.nextLine();
        
        for (int i = 0; i < queries; i++) {
            temp = scan.nextLine().split(" ");
            type = Integer.parseInt(temp[0]);
            planetName = temp[1]; // every input type has planetName as the first term after the int denoting input type
            if (type == 1) {
                entranceFee = Integer.parseInt(temp[2]);
                insert(planetName, entranceFee, tree);
            }
            else if (type == 2) {
                secondPlanet = temp[2];
                entranceFee = Integer.parseInt(temp[3]);
                if (planetName.compareTo(secondPlanet) <= 0)
                    addRange(tree.root, planetName, secondPlanet, tree.height, "", entranceFee);
                else
                    addRange(tree.root, secondPlanet, planetName, tree.height, "", entranceFee); // if inputs are swapped (z a instead of a z), run addRange with secondPlanet first
            }
            else if (type == 3) {
                entranceFee = lookup(planetName, tree.root, 0);
                output.write(String.valueOf(entranceFee) + "\n");
            }
        }
        
        output.flush(); // flush System.out at the end of main
    }
            
    static int lookup(String target, Node p, int total) { // keep track of the cumulative value for the node we're searching for in 'total'
        if (p instanceof LeafNode) {
            if (target.compareTo(p.guide) == 0)
                return total + p.value;
            return -1; // a miss, return -1
            }
        
        InternalNode internal = (InternalNode) p; // only cast to InternalNode once we're sure p isn't a leaf node
        if (target.compareTo(internal.child0.guide) <= 0)
            return lookup(target, internal.child0, total + internal.value); //explore left subtree
        if (target.compareTo(internal.child0.guide) > 0 && target.compareTo(internal.child1.guide) <= 0)
            return lookup(target, internal.child1, total + internal.value); //explore middle/right subtree
        if (internal.child2 != null && target.compareTo(internal.child1.guide) > 0)
            return lookup(target, internal.child2, total + internal.value); //explore right subtree
        
        return -1; // something has gone wrong, some kind of other miss, so return -1
    }
    
    static void addRange(Node p, String x, String y, int h, String lo, int delta) { 
        // basically the same as printRange from the last assignment, but with some minor changes
        if (h == 0) {
            if (p.guide.compareTo(x) >= 0 && p.guide.compareTo(y) <= 0) {
                p.value += delta; // inside the desired search path, so increment value by the change
            }
            return; // if we're at a leaf node, return no matter what
        }
        if (y.compareTo(lo) <= 0) // outside the desired search path, so return
            return;
        String hi = p.guide;
        if (hi.compareTo(x) < 0) // outside the desired search path, so return
            return;
        if (x.compareTo(lo) <= 0 && hi.compareTo(y) <= 0) {
            p.value += delta; // inside the desired search path, so increment value by the change
            return;
        }
        InternalNode internal = (InternalNode) p; // must cast to an internal node in order to access child0, child1, child2
        // then run addRange on p's children
        addRange(internal.child0, x, y, h - 1, lo, delta);
        addRange(internal.child1, x, y, h - 1, internal.child0.guide, delta);
        if (internal.child2 != null) // if the node has a third child:
            addRange(internal.child2, x, y, h - 1, internal.child1.guide, delta);
    }
            
    static void insert(String key, int value, TwoThreeTree tree) {
        // insert a key value pair into tree (overwrite existing value if key is already present)
        int h = tree.height;
        if (h == -1) { // for an empty tree:
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
            
            //new logic for shifted value, zeroing out, lazy update:
            q.child0.value += q.value;
            q.child1.value += q.value;
            if (q.child2 != null)
                q.child2.value += q.value;
            q.value = 0;
            
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
