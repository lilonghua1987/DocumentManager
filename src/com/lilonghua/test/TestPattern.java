/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilonghua.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author wpf
 */
public class TestPattern {

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("^(https?://)?([\\da-z\\.-]+)oschina\\.net([/\\w \\.-]*)*/?$");
        String matcher = "http://www.oschina.net/";
        matcher = "http://news.oschina.net/";
        if(pattern.matcher(matcher).matches()) {
            System.out.println(matcher);
        }
    }
}
