package com.github.uuidcode.docker.hub.test;

import org.junit.Test;

public class TreeTest {
    @Test
    public void test() {
        Tree.of().run("maven:3.5-jdk-8");
    }
}
