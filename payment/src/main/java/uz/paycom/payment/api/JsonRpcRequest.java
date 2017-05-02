package uz.paycom.payment.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import uz.paycom.payment.BuildConfig;

public class JsonRpcRequest {

  private static final String TAG = "JsonRpcRequest";

  public final static String cardsCreateMethod = "cards.create";
  public final static String cardsGetVerifyCodeMethod = "cards.get_verify_code";
  public final static String cardsCreateVerifyMethod = "cards.verify";

  private final String xAuth;
  private HttpURLConnection urlConnection;

  public JsonRpcRequest(String xAuth) {
    this.xAuth = xAuth;
  }

  private String callApi(JSONObject jsonObject) {
    try {
      URL url = new URL(BuildConfig.API_ENDPOINT);
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("POST");
      urlConnection.addRequestProperty("X-Auth", xAuth);
      urlConnection.setDoInput(true);
      urlConnection.setDoOutput(true);
      urlConnection.setUseCaches(false);
      urlConnection.setConnectTimeout(30000);

      OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
      writer.write(jsonObject.toString());
      writer.flush();

      int responseCode = urlConnection.getResponseCode();

      String line = "";
      if (responseCode == HttpURLConnection.HTTP_OK) {
        StringBuffer response = new StringBuffer();
        BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        while ((line = br.readLine()) != null) {
          response.append(line);
          Log.d(TAG, line);
        }
        br.close();
        return response.toString();
      } else {
        throw new IOException("Unexpected responseCode: " + responseCode);
      }

    } catch (IOException e) {
        Log.d(TAG, e.toString());
        return null;
    } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
      }
    }
  }

  public String callApiMethod(JSONObject jsonObject, String method) {
    try {
      jsonObject.accumulate("method", method);
    } catch (JSONException e) {
        Log.d(TAG, e.toString());
    }
    return callApi(jsonObject);
  }

}
