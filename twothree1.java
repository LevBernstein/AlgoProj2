import java.io.*;
import java.util.*;

/**
* Basic Algorithms Programming Assignment 2
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

