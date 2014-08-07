/**
 * Project Removal Tool.
 * Copyright Michał Szczygieł.
 * Created at Jul 3, 2014.
 */
package com.m4gik;

import static com.m4gik.Constants.USER_AGENT;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This is main class which is responsible for gathering information form google
 * search results. This program encodes command-line arguments as a Google
 * search query, downloads the results, and saves the corresponding links as
 * output.
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

        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                if (i < args.length - 1) {
                    concatenated += args[i] + " ";
                } else {
                    concatenated += args[i];
                }
            }
        }

        return concatenated;
    }

    /**
     * This method gets search result and put it to {@link List}.
     * 
     * @param elements
     *            The elements from which the data will be extracted.
     * @return The {@link List} of urls.
     * @throws UnsupportedEncodingException
     */
    private static List<String> getSearchResults(Elements elements)
            throws UnsupportedEncodingException {
        List<String> searchResult = new ArrayList<String>();

        if (elements.size() != 0) {
            for (Element link : elements) {
                String url = link.absUrl("href"); // Google returns URLs in
                                                  // format
                                                  // "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>"

                searchResult.add(URLDecoder.decode(
                        url.substring(url.indexOf('=') + 1, url.indexOf('&')),
                        "UTF-8"));
            }
        }

        return searchResult;
    }

    /**
     * The main method.
     * 
     * @param args
     *            The query to search. (Minimum one argument is need)
     * @throws Exception
     */
    public static void main(String... args) throws Exception {

        if (args.length < 1) {
            System.out.println("Minimum one argument is need");
            System.exit(0);
        }

        String query = concatenationProcess(args);
        Boolean isResult = true;
        Integer pageNumber = 0;
        List<String> dataToSave = new ArrayList<String>();

        while (isResult) {
            List<String> urls = getSearchResults(parseGoogleLinks(query,
                    pageNumber));
            if (urls.size() == 0) {
                isResult = false;
            } else {
                pageNumber += 10;
                dataToSave.addAll(urls);
            }
        }

        dataToSave = reduceDuplicateResults(dataToSave);
        saveToFile(dataToSave);
        showSearchResults(dataToSave);
    }

    /**
     * Parses HTML output from a Google search and returns a list of
     * corresponding links for the query.
     * 
     * @param query
     *            The query for Google search results.
     * 
     * @return A list of links for the query.
     * @throws UnsupportedEncodingException
     * 
     * @throws IOException
     *             Thrown if there is an error parsing the results from Google
     *             or if one of the links returned by Google is not a valid URL.
     */
    private static Elements parseGoogleLinks(final String query,
            Integer pageNumber) throws UnsupportedEncodingException,
            IOException {

        // These tokens are adequate for parsing the HTML from Google. First,
        // find a heading-3 element with an "r" class. Then find the next anchor
        // with the desired link. The last token indicates the end of the URL
        // for the link.
        String searchQuery = "https://www.google.pl/search?client=ubuntu&channel=fs&q=site%3Aftp%3A%2F%2F87.239.220.142%2FM4GiK%2FEmployment%2FDescom%2FJava%2Fworkspaces%2FMTG&ie=utf-8&oe=utf-8&gfe_rd=cr&ei=lJW6U-miJ8yH8QeIxoGgCg#channel=fs&q=site%3Aftp%3A%2F%2F87.239.220.142%2FM4GiK%2FEmployment%2FDescom%2FJava%2Fworkspaces%2FMTG";
        // Document document = Jsoup
        // .connect(
        // GOOGLE + URLEncoder.encode(query, CHARSET) + "&start="
        // + pageNumber).userAgent(USER_AGENT).get();

        Elements links = null;

        try {
            Document document = Jsoup
                    .connect(searchQuery + "&start=" + pageNumber)
                    .userAgent(USER_AGENT).timeout(10000).followRedirects(true)
                    .get();

            links = document.select("li.g>h3>a");

            Thread.sleep(1000 * randInt(1, 10)); // To avoid error 503
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (HttpStatusException ex) {
            ex.printStackTrace();
        }

        return links;
    }

    /**
     * Returns a psuedo-random number between min and max, inclusive. The
     * difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     * 
     * @param min
     *            Minimim value
     * @param max
     *            Maximim value. Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    /**
     * This method reduces the same search results.
     * 
     * @param dataToSave
     *            The data to save.
     * @return Reduced list.
     */
    private static List<String> reduceDuplicateResults(List<String> dataToSave) {
        HashMap<String, String> reducedMap = new HashMap<String, String>();

        for (String string : dataToSave) {
            reducedMap.put(string, string);
        }

        List<String> reducedList = new ArrayList<String>();

        for (Entry<String, String> entry : reducedMap.entrySet()) {
            reducedList.add(entry.getKey());
        }

        return reducedList;
    }

    /**
     * This method saves data to file.
     * 
     * @param dataToSave
     *            The data to save.
     */
    private static void saveToFile(List<String> dataToSave) {
        try {
            PrintWriter writer = new PrintWriter("urls.txt", "UTF-8");

            for (String string : dataToSave) {
                writer.println(string);
            }

            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method shows search result.
     * 
     * @param links
     *            The instance of {@link Collection<? extends String>}
     */
    private static void showSearchResults(
            final Collection<? extends String> links) {
        for (String link : links) {
            System.out.println("URL: " + link);
        }
    }
}
