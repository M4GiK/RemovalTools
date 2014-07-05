/**
 * Project Removal Tool.
 * Copyright Michał Szczygieł.
 * Created at Jul 3, 2014.
 */
package com.m4gik;

import static com.m4gik.Constants.CHARSET;
import static com.m4gik.Constants.GOOGLE;
import static com.m4gik.Constants.USER_AGENT;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

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

        for (int i = 1; i < args.length; i++) {
            if (i < args.length - 1) {
                concatenated += args[i] + " ";
            } else {
                concatenated += args[i];
            }
        }

        return concatenated;
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

        String query = null;

        if (args.length > 1) {
            query = concatenationProcess(args);
        } else {
            query = args[0];
        }

        Elements links = parseGoogleLinks(query);
        showSearchResults(links);
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
    private static Elements parseGoogleLinks(final String query)
            throws UnsupportedEncodingException, IOException {

        // These tokens are adequate for parsing the HTML from Google. First,
        // find a heading-3 element with an "r" class. Then find the next anchor
        // with the desired link. The last token indicates the end of the URL
        // for the link.
        Document document = Jsoup
                .connect(GOOGLE + URLEncoder.encode(query, CHARSET))
                .userAgent(USER_AGENT).get();

        Elements links = document.select("li.g>h3>a");

        return links;
    }

    /**
     * This method shows search result.
     * 
     * @param links
     *            The instance of {@link Elements}
     * @throws UnsupportedEncodingException
     */
    private static void showSearchResults(final Elements links)
            throws UnsupportedEncodingException {
        for (Element link : links) {
            String url = link.absUrl("href"); // Google returns URLs in format
                                              // "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
            url = URLDecoder.decode(
                    url.substring(url.indexOf('=') + 1, url.indexOf('&')),
                    "UTF-8");

            System.out.println("URL: " + url);
        }
    }
}
