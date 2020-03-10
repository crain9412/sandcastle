package com.jwcrain.sandcastle.huffmancoding;

public class Node implements Comparable<Node> {
    private Character character;
    private float weight;
    private Node parent;
    private Node right;
    private Node left;

    /* Leaf constructor */
    public Node(char character, float weight) {
        this.character = character;
        this.weight = weight;
    }

    /* Internal node constructor */
    public Node(float weight, Node left, Node right) {
        this.weight = weight;
        this.left = left;
        this.right = right;
    }

    public Character getCharacter() {
        return character;
    }

    public float getWeight() {
        return weight;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    @Override
    public int compareTo(Node o) {
        return (weight < o.getWeight()) ? -1 : 1;
    }

    @Override
    public String toString() {
        return "Node{" +
                "character=" + character +
                ", weight=" + weight +
                '}';
    }
}
