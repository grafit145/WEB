package ru.netology;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private static LinkedHashMap<String, ArrayList<String>> parameters;
    private final Map<String, String> headers;
    private final InputStream in;

    public Request(String method, String path, LinkedHashMap<String, ArrayList<String>> parameters, Map<String, String> headers, InputStream in) {
        this.method = method;
        this.path = path;
        Request.parameters = parameters;
        this.headers = headers;
        this.in = in;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getIn() {
        return in;
    }

    public static Request fromInputStream(InputStream inputStream) throws IOException {
        var in = new BufferedReader(new InputStreamReader(inputStream));
        // read only request line for simplicity
        // must be in form GET /path HTTP/1.1
        final var requestLine = in.readLine();
        final var parts = requestLine.split(" ");

        if (parts.length != 3) {
            // just close socket
            throw new IOException("Invalild request");
        }

        var method = parts[0];
        var path = "";
        var fullPath = parts[1];
        var parameters = new LinkedHashMap<String, ArrayList<String>>();

        if (fullPath.contains("?")) {
            final var partsPath = fullPath.split("\\?");
            path = partsPath[0];
            parameters = getQueryParams(partsPath[1]);
        } else {
            path = fullPath;
        }

        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            //Accept: application/json
            var i = line.indexOf(":");
            var headerName = line.substring(0, i);
            var headerValue = line.substring((i + 2));
            headers.put(headerName, headerValue);
        }
        Request request = new Request(method, path, parameters, headers, inputStream);

        request.getQueryParam("name");
        request.queryToString();

        return request;
    }

    public static LinkedHashMap<String, ArrayList<String>> getQueryParams(String query) throws UnsupportedEncodingException {
        LinkedHashMap<String, ArrayList<String>> query_pairs = new LinkedHashMap<String, ArrayList<String>>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
            if (query_pairs.containsKey(key)) {
                query_pairs.get(key).add(value);
            } else {
                query_pairs.put(key, new ArrayList<String>());
                query_pairs.get(key).add(value);
            }
        }
        return query_pairs;
    }

    public static void getQueryParam(String name) {
        if (parameters.containsKey(name)) {
            System.out.println(name + " = " + parameters.get(name));
        } else {
            System.out.println("No parameter with name = " + name);
        }
    }

    public void queryToString() {
        System.out.println("Request {\n" +
                "method= '" + method + "',\n" +
                "path= '" + path + "',\n" +
                "parameters= '" + parameters + "',\n" +
                "}");
    }
}
