package com.github.jaksonlin.jacocoparser.util;


import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Util {
    public static byte[] DownloadWebPage(String url) throws Exception {

        URL fileUrl = new URL(url);
        URLConnection connection = fileUrl.openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();
        // read the input stream into a byte array
        byte[] body = new byte[connection.getContentLength()];
        input.read(body);
        return body;

    }
}
