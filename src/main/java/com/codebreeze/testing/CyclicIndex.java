package com.codebreeze.testing;

import org.apache.commons.lang3.Validate;

public class CyclicIndex {
    private final int arrayLength;

    private volatile int index;

    public CyclicIndex(final int arrayLength){
        Validate.isTrue(arrayLength > 0, "cannot index an array of 0 or less length");

        this.arrayLength = arrayLength;
        this.index = -1;
    }

    public int next(){
        return index = (index + 1) % arrayLength;
//        if(index + 1 == arrayLength){
//            index = 0;
//        } else {
//            index++;
//        }
//        return index;
    }
}
