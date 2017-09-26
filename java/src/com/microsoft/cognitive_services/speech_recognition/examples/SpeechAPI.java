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

public class SpeechAPI {

  /** 
   * For mode information on the recognition modes, please refer to 
   * <a href="https://docs.microsoft.com/en-us/azure/cognitive-services/speech/api-reference-rest/bingvoicerecognition#recognition-modes"/>
   */
  public static enum RecognitionMode { Interactive, Conversation, Dictation }

  /** 
   * For mode information on the output format, please refer to 
   * <a href="https://docs.microsoft.com/en-us/azure/cognitive-services/speech/api-reference-rest/bingvoicerecognition#output-format"/>
   */
  public static enum OutputFormat { Simple, Detailed }

  /** 
   * For mode information on the supported languages, please refer to 
   * <a href="https://docs.microsoft.com/en-us/azure/cognitive-services/speech/api-reference-rest/bingvoicerecognition#recognition-language"/>
   */
  public static enum Language { ar_EG, ca_ES, da_DK, de_DE, en_AU, en_CA, en_GB, en_IN, en_NZ, en_US, 
                                es_ES, es_MX, fi_FI, fr_CA, fr_FR, hi_IN, it_IT, ja_JP, ko_KR, nb_NO, 
                                nl_NL, pl_PL, pt_BR, pt_PT, ru_RU, sv_SE, zh_CN, zh_HK, zh_TW  }
}
