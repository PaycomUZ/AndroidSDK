package uz.paycom.payment.api;

import android.util.Log;

import org.json.JSONObject;

import uz.paycom.payment.model.Confirm;
import uz.paycom.payment.model.Result;

/**
 * Parse json api objects
 */
public class JsonParser {

  private static final String TAG = "JsonParser";

  public JSONObject getCardsCreate(String number, String expire,
      Double amount, Boolean save) {
    JSONObject root = new JSONObject();
    JSONObject params = new JSONObject();
    JSONObject card = new JSONObject();
    try {
      card.accumulate("number", number);
      card.accumulate("expire", expire);
      params.accumulate("card", card);
      params.accumulate("amount", amount);
      params.accumulate("save", save);
      root.accumulate("params", params);
    } catch (Exception e) {
        Log.d(TAG, e.toString());
    }

    return root;
  }

  public JSONObject getCardsVerifyCode(String token) {
    JSONObject root = new JSONObject();
    JSONObject params = new JSONObject();

    try {
      params.accumulate("token", token);
      root.accumulate("params", params);
    } catch (Exception e) {
        Log.d(TAG, e.toString());
    }

    return root;
  }

  public String getCardToken(String result) {
    try {
      JSONObject jsonObject = new JSONObject(result);
      return jsonObject.getJSONObject("result")
          .getJSONObject("card")
          .getString("token");
    } catch (Exception e) {
        Log.d(TAG, e.toString());
        return null;
    }
  }

  public String getCardToken(JSONObject jsonObject) {
    try {
      return jsonObject.getString("token");
    } catch (Exception e) {
        Log.d(TAG, e.toString());
        return null;
    }
  }

  public JSONObject getCardsVerify(String token, String code) {
    JSONObject root = new JSONObject();
    JSONObject params = new JSONObject();

    try {
      params.accumulate("token", token);
      params.accumulate("code", code);
      root.accumulate("params", params);
    } catch (Exception e) {
        Log.d(TAG, e.toString());
    }

    return root;
  }

  public String checkError(String result) {
    try {
      JSONObject jsonObject = new JSONObject(result);
      return jsonObject.getJSONObject("error")
          .getString("message");
    } catch (Exception e) {
        Log.d(TAG, e.toString());
        return null;
    }
  }

  public Result getResult(String json) {
    try {
      JSONObject jsonObject = new JSONObject(json);
      JSONObject resultObject = jsonObject.getJSONObject("result");
      JSONObject card = resultObject.getJSONObject("card");
      return new Result(card.getString("number")
          ,card.getString("expire")
          ,card.getString("token")
          ,card.getBoolean("recurrent")
          ,card.getBoolean("verify"));
    } catch (Exception e) {
        Log.d(TAG, e.toString());
        return null;
    }
  }

  public Confirm getConfirm(String json) {
    try {
      JSONObject jsonObject = new JSONObject(json);
      JSONObject result = jsonObject.getJSONObject("result");
      return new Confirm(result.getBoolean("sent")
              ,result.getString("phone")
              ,result.getInt("wait"));
    } catch (Exception e) {
      Log.d(TAG, e.toString());
      return null;
    }
  }
}