package com.wfb.utils;

import com.wfb.net.PetriNet;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class PetriUtil {
    public static PetriNet readPetriNet(String petriFile) throws Exception{
        File file1 = new File(petriFile);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file1));
        Object o = ois.readObject();
        if (o instanceof PetriNet) return (PetriNet) o;
        else throw new Exception("Can't read petriNet!!!");
    }
}
