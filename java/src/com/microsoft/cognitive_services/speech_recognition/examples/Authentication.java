/*
Copyright (c) Microsoft Corporation
All rights reserved. 
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of this 
software and associated documentation files (the "Software"), to deal in the Software 
without restriction, including without limitation the rights to use, copy, modify, merge, 
publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons 
to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or 
substantial portions of the Software.
THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.microsoft.cognitive_services.speech_recognition.examples;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * This class demonstrates how to get a valid OAuth token.
 */
public class Authentication {
  private static final String FETCH_TOKEN_URI = "https://api.cognitive.microsoft.com/sts/v1.0/issueToken";
  private final String subscriptionKey;
  private String token;

  public Authentication(String subscriptionKey) {
    this.subscriptionKey = subscriptionKey;
    fetchToken();
  }

  protected void setToken(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  protected void fetchToken() {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(FETCH_TOKEN_URI).openConnection();
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
      connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
      connection.setFixedLengthStreamingMode(0);
      connection.connect();

      if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
          setToken(reader.lines().collect(Collectors.joining()));
        }
      } else {
        System.out.format("Something went wrong, server returned: %d (%s)", 
            connection.getResponseCode(), connection.getResponseMessage());
      }
      
    } catch (Exception e) {
      token = null;
      System.out.format("Failed to fetch an access token. Details: %s", e.getMessage());
    }
  }

}
