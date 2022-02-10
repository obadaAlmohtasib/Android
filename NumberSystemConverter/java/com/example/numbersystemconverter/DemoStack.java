package com.example.numbersystemconverter;

import java.util.ArrayList;

class DemoStack {

    private ArrayList<Long> bits;
    private int top;

    DemoStack(){ // Constructor
        bits = new ArrayList<>();
        top = -1; // Should points to the Top (recently joined) of stack;
    }

    void push(long bumpUp){
        bits.add(bumpUp);
        top++;
    }

    long peek(){
        if(!isEmpty())
            return bits.get(top);
        return -1;
    }

    long pop(){
        if(!isEmpty())
            return bits.remove(top--);
        return -1;
    }

    boolean isEmpty(){
        return (top<0);
    }

}