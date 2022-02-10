package com.example.numbersystemconverter;

import java.util.ArrayList;

class DemoQueue {

    private int front, rear, nItems;
    private ArrayList<Integer> numberSystems;

    DemoQueue() { // constructor
        numberSystems = new ArrayList<>();
        nItems = 0; // number Of items;
        front = 0; // Should points to the First in queue;
        rear = -1; // Should points to the Last in queue;
    }

    int peekFront(){
        if(!isEmpty()) {
            return front;
        }
        return -1;
    }

    int peekRear(){
        if(!isEmpty()) {
            return rear;
        }
        return -1;
    }

    int remove(){
        if(!isEmpty()) {
            --nItems;
            return numberSystems.remove(front);
        }
        return -1;
    }

    void insert(int bumpUp){
        ++rear; ++nItems;
        numberSystems.add(bumpUp);
    }

    boolean isEmpty(){
        return nItems == 0;
    }

    int size(){
        return nItems;
    }

}