package com.jwcrain.sandcastle.crainhashmap;

public class List {
    private Node head;

    public List() {

    }

    public void addNode(Node node) {
        Node oldHead = this.head;
        node.setNext(oldHead);
        this.head = node;
    }

    public void removeHead() {
        if (this.head == null || this.head.getNext() == null) {
            return;
        }

        this.head = this.head.getNext();
    }

    public Node getHead() {
        return this.head;
    }

    public void setHead(Node head) {
        this.head = head;
    }
}
