/**
 * Project Removal Tool.
 * Copyright Michał Szczygieł.
 * Created at Jul 3, 2014.
 */
package com.m4gik;

import static com.m4gik.Constants.ADDRESS;
import static com.m4gik.Constants.CHARSET;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.Gson;
import com.m4gik.GoogleResults.Result;

/**
 * This is main class which is responsible for gathering information form google
 * search results.
 * 
 * @author m4gik <michal.szczygiel@wp.pl>
 * 
 */
public class Main {

    /**
     * This method makes concatenation of given arguments.
     * 
     * @param args
     *            The strings to concatenation.
     * @return concatenated {@link String}
     */
    private static String concatenationProcess(String[] args) {
        String concatenated = args[0] + " ";

        for (int i = 1; i < args.length; i++) {
            concatenated += args[i] + " ";
        }

        System.out.println(concatenated);

        return concatenated;
    }

    /**
     * The main method.
     * 
     * @param args
     *            The query to search. (Minimum one argument is need)
     * @throws IOException
     */
    public static void main(String... args) throws IOException {

        if (args.length < 1) {
            System.out.println("Minimum one argument is need");
            System.exit(0);
        }

        String query = null;

        if (args.length > 1) {
            query = concatenationProcess(args);
        } else {
            query = args[0];
        }

        GoogleResults results = prepareSearch(query);
        showSearchResults(results);

    }

    /**
     * This method prepares search.
     * 
     * @param query
     *            The query to search.
     * @return The {@link GoogleResults}
     * @throws IOException
     */
    private static GoogleResults prepareSearch(String query) throws IOException {

        URL url = new URL(ADDRESS + URLEncoder.encode(query, CHARSET));
        Reader reader = new InputStreamReader(url.openStream(), CHARSET);

        return new Gson().fromJson(reader, GoogleResults.class);

    }

    /**
     * This method shows search result.
     * 
     * @param results
     *            The instance of {@link {@link GoogleResults}
     */
    private static void showSearchResults(GoogleResults results) {
        int total = results.getResponseData().getResults().size();
        System.out.println("total: " + total);

        // Show title and URL of each results
        for (Result result : results.getResponseData().getResults()) {
            System.out.println("Title: " + result.getTitle());
            System.out.println("URL: " + result.getUrl() + "\n");
        }
    }
}
