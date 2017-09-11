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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import com.microsoft.cognitive_services.speech_recognition.examples.SpeechAPI.Language;
import com.microsoft.cognitive_services.speech_recognition.examples.SpeechAPI.OutputFormat;
import com.microsoft.cognitive_services.speech_recognition.examples.SpeechAPI.RecognitionMode;

public class SpeechClientREST {

  private static final String REQUEST_URI = "https://speech.platform.bing.com/speech/recognition/%s/cognitiveservices/v1";
  private static final String PARAMETERS = "language=%s&format=%s";

  private RecognitionMode mode = RecognitionMode.Interactive;
  private Language language = Language.en_US;
  private OutputFormat format = OutputFormat.Simple;

  private final Authentication auth;

  public SpeechClientREST(Authentication auth){
    this.auth = auth;
  }

  public RecognitionMode getMode() {
    return mode;
  }

  public void setMode(RecognitionMode mode) {
    this.mode = mode;
  }

  public Language getLanguage() {
    return language;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  public OutputFormat getFormat() {
    return format;
  }

  public void setFormat(OutputFormat format) {
    this.format = format;
  }

  private URL buildRequestURL() throws MalformedURLException {
    String url = String.format(REQUEST_URI, mode.name().toLowerCase());
    String params = String.format(PARAMETERS, language.name().replace('_', '-'), format.name().toLowerCase());
    return new URL(String.format("%s?%s", url, params));
  }

  private HttpURLConnection connect() throws MalformedURLException, IOException {
    HttpURLConnection connection = (HttpURLConnection) buildRequestURL().openConnection();
    connection.setDoInput(true);
    connection.setDoOutput(true); 
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-type", "audio/wav; codec=\"audio/pcm\"; samplerate=16000");
    connection.setRequestProperty("Accept", "application/json;text/xml");
    connection.setRequestProperty("Authorization", "Bearer " + auth.getToken());
    connection.setChunkedStreamingMode(0); // 0 == default chunk size
    connection.connect();

    return connection;
  }

  private String getResponse(HttpURLConnection connection) throws IOException {
    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new RuntimeException(String.format("Something went wrong, server returned: %d (%s)",
          connection.getResponseCode(), connection.getResponseMessage()));
    }

    try (BufferedReader reader = 
        new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
      return reader.lines().collect(Collectors.joining());
    }
  }

  private HttpURLConnection upload(InputStream is, HttpURLConnection connection) throws IOException {
    try (OutputStream output = connection.getOutputStream()) {
      byte[] buffer = new byte[1024];
      int length;
      while ((length = is.read(buffer)) != -1) {
        output.write(buffer, 0, length);
      }
      output.flush();
    }
    return connection;
  }

  private HttpURLConnection upload(Path filepath, HttpURLConnection connection) throws IOException {
    try (OutputStream output = connection.getOutputStream()) {
      Files.copy(filepath, output);
    }
    return connection;
  }

  public String process(InputStream is) throws IOException {
    return getResponse(upload(is, connect()));
  }

  public String process(Path filepath) throws IOException {
    return getResponse(upload(filepath, connect()));
  }
}
