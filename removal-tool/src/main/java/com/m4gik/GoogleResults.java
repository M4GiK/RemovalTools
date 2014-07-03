/**
 * Project Removal Tool.
 * Copyright Michał Szczygieł.
 * Created at Jul 3, 2014.
 */
package com.m4gik;

import java.util.List;

/**
 * This class is responsible for gathering information form google search
 * results.
 * 
 * @author m4gik <michal.szczygiel@wp.pl>
 * 
 */
public class GoogleResults {

    static class ResponseData {

        private List<Result> results;

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

        public String toString() {
            return "Results[" + results + "]";
        }
    }

    static class Result {

        private String title;

        private String url;

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String toString() {
            return "Result[url:" + url + ",title:" + title + "]";
        }
    }

    private ResponseData responseData;

    public ResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

    public String toString() {
        return "ResponseData[" + responseData + "]";
    }
}
