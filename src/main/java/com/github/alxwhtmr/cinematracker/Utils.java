package com.github.alxwhtmr.cinematracker;

import javafx.scene.Scene;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * The {@code Utils} class contains logger methods
 * and methods for grabbing (getting) url resources,
 * such as json files and html pages
 *
 * @since 28.01.2015
 */
public class Utils {
    public static void logDebug(Object o) {
        if (Constants.Logs.Debug.ENABLED == true) {
            System.out.println(String.format(Constants.Logs.FORMAT, Constants.Logs.Debug.TITLE, new Date(), o));
        }
    }

    public static void logErr(Object o) {
        if (Constants.Logs.Errors.ENABLED == true) {
            System.out.println(String.format(Constants.Logs.FORMAT, Constants.Logs.Errors.TITLE, new Date(), o));
        }
    }

    public static void logInfo(Object o) {
        if (Constants.Logs.Info.ENABLED == true) {
            System.out.println(String.format(Constants.Logs.FORMAT, Constants.Logs.Info.TITLE, new Date(), o));
        }
    }

    public static Element getHtmlBody(String url) {
        Utils.logDebug(Utils.class);
        Document html = null;
        try {
            html = Jsoup.connect(url).get();
        } catch (IOException e) {
            Utils.logErr(e);
//            e.printStackTrace();
        }
        Document doc = Jsoup.parseBodyFragment(html.toString());
        Element body = doc.body();
        return body;
    }

    public static String getJsonFileFromUrl(String urlString, String query)  {
        String fullUrl = urlString + query.charAt(0) + "/" + query + ".json";
        StringBuffer b = new StringBuffer();
        String toDelete = String.format(Constants.IMDB.CHARS_TO_DELETE, query);
        String result = null;
        URL url = null;
        try {
            url = new URL(fullUrl);
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                b.append(inputLine);
            }
            in.close();
            result = b.substring(toDelete.length(), b.length()-1);
        } catch (IOException e) {
            Utils.logInfo(Utils.class.getName() + ": " + e);
        }

        return result;
    }

    public static void setStyle(Scene scene, String css) {
        URI uri = null;
        File fname = new File(css);
        uri = fname.toURI();
        scene.getStylesheets().add(uri.toString());
    }
}
