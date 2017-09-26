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

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

public class RenewableAuthentication extends Authentication {

  private final Timer timer = new Timer();
  private long period = Duration.ofMinutes(9).toMillis(); // 9 minutes worth of ms.

  public RenewableAuthentication(String subscriptionKey) {
    super(subscriptionKey);
    // schedule a task to renew the token each 9 seconds,
    // starting in 9 seconds from now.
    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        fetchToken();
      }
    }, period, period);
  }

  @Override
  protected synchronized void setToken(String token) {
    super.setToken(token);
  }

  @Override
  public synchronized String getToken() {
    return super.getToken();
  }
}
