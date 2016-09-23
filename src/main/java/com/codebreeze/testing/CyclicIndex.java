package com.codebreeze.testing;

public class CyclicIndex {
    private final int arrayLength;

    private volatile int index;

    public CyclicIndex(final int arrayLength){
        if(arrayLength <= 0)
        {
            throw new IllegalArgumentException("cannot index an array of 0 or less length");
        }

        this.arrayLength = arrayLength;
        this.index = -1;
    }

    public int next(){
        return index = (index + 1) % arrayLength;
    }
}
