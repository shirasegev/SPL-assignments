package bgu.spl.mics.example;

import bgu.spl.mics.Subscriber;

public interface Creator {
    Subscriber create(String name, String[] args);
}
