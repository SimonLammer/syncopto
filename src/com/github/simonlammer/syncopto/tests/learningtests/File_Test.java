package com.github.simonlammer.syncopto.tests.learningtests;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class File_Test {
    @Test
    public void comparisonTest() {
        File fileA = new File("a");
        File fileB = new File("b");
        double comparison = fileA.compareTo(fileB);
        assertTrue(comparison < 0);
    }
}
