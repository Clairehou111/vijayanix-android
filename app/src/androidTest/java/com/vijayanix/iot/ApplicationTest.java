package com.vijayanix.iot;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);

        // create map
        Map<String, Boolean> map = new WeakHashMap<String, Boolean>();

        // create a set from map
        Set<String> set = Collections.newSetFromMap(map);

        // add values in set
        set.add("Java");
        set.add("C");
        set.add("C++");

        // set and map values are
        System.out.println("Set is: " + set);
        System.out.println("Map is: " + map);


       String post = "{\"body\": {\"response\": {\"status\": 1}},\"method\":\"POST\"}";
       String get = "{\"method\":\"GET\"}";

    }
}