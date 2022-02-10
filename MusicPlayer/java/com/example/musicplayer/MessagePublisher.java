package com.example.musicplayer;

import java.util.ArrayList;

class MessagePublisher {

    private static MessagePublisher publisher;
    private final ArrayList<Observer> observers;

    private MessagePublisher() { observers = new ArrayList<>(); }

    public static MessagePublisher getInstance()
    {
        if (publisher == null)
            return (publisher = new MessagePublisher());

        return publisher;
    }

    public void attach(Observer o)
    {
        observers.add(o);
    }

    public void detach(Observer o)
    {
        observers.remove(o);
    }

    public void notifyUpdate(String message) {
        for (Observer observer : observers)
        {
            observer.update(message);
        }
    }


}