import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

/**
* Basic Algorithms Programming Assignment 2: Hijacking the fees
* @author Lev Bernstein
* Provided code: Node, InternalNode, LeafNode classes.
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


public class twothree {
    
    public static void main(String[] args) throws Exception {
        //All queries, incuding range updates(type 2 queries), should run in time O(logn).
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
        Scanner scan = new Scanner(System.in);
        int type;
        //Values for type:
        //1 planetName entranceFee = insert a planet with name planetName and entrance fee entranceFee into the database; 
        //2 planetName secondPlanet entranceFee = increase the entrance fee for all planets between planetName and secondPlanet by entranceFee
        //3 planetName = return the entrance fee for planetName
        //Use regex for this maybe?
        int queries; //n <= 100000
        String planetName;
        int entranceFee; //0 <= entranceFee <= 10^5. Entrance fee will also always be less than 2^30.
        String secondPlanet;
        
        
        
        output.flush(); //flush System.out at the end of main
        }
    
    }
