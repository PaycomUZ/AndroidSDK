package uz.paycom.payment.api;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import uz.paycom.payment.utils.Logger;
import uz.paycom.payment.utils.PaycomSandBox;

public class JsonRpcRequest {

  private static final String TAG = "JsonRpcRequest";

  public final static String cardsCreateMethod = "cards.create";
  public final static String cardsGetVerifyCodeMethod = "cards.get_verify_code";
  public final static String cardsCreateVerifyMethod = "cards.verify";

  private final String xAuth;
  private HttpsURLConnection urlConnection;

  public JsonRpcRequest(String xAuth) {
    this.xAuth = xAuth;
  }

  private String callApi(JSONObject jsonObject) {
    try {
      String urlApi = PaycomSandBox.isSandBox() ? "https://checkout.test.paycom.uz/api"
          : "https://checkout.paycom.uz/api";
      Logger.d(TAG, urlApi);
      URL url = new URL(urlApi);
      urlConnection = (HttpsURLConnection) url.openConnection();

      try {
        urlConnection.setSSLSocketFactory(new TLSSocketFactory());
      } catch (KeyManagementException | NoSuchAlgorithmException e) {
        Logger.d(TAG, e.toString());
      }

      urlConnection.setRequestMethod("POST");
      urlConnection.addRequestProperty("X-Auth", xAuth);
      urlConnection.setDoInput(true);
      urlConnection.setDoOutput(true);
      urlConnection.setUseCaches(false);
      urlConnection.setConnectTimeout(30000);

      OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
      writer.write(jsonObject.toString());
      writer.flush();

      Logger.d(TAG, jsonObject.toString());
      int responseCode = urlConnection.getResponseCode();

      String line = "";
      if (responseCode == HttpURLConnection.HTTP_OK) {
        StringBuffer response = new StringBuffer();
        BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        while ((line = br.readLine()) != null) {
          response.append(line);
          Logger.d(TAG, line);
        }
        br.close();
        return response.toString();
      } else {
        throw new IOException("Unexpected responseCode: " + responseCode);
      }

    } catch (IOException e) {
        Logger.d(TAG, e.toString());
        return null;
    } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
      }
    }
  }

  public String callApiMethod(JSONObject jsonObject, String method) {
    try {
      Logger.d(TAG, method);
      jsonObject.accumulate("method", method);
    } catch (JSONException e) {
        Logger.d(TAG, e.toString());
    }
    return callApi(jsonObject);
  }
}