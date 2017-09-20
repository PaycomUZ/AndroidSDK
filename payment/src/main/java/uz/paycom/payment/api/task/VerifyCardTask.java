package uz.paycom.payment.api.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import uz.paycom.payment.ConfirmActivity;
import uz.paycom.payment.PaymentActivity;
import uz.paycom.payment.R;
import uz.paycom.payment.api.JsonParser;
import uz.paycom.payment.api.JsonRpcRequest;

/**
 * VerifyCardTask - call several cards methods api
 * and update activity
 */
public class VerifyCardTask extends AsyncTask<Void, Void, String> {

  private Double amount;
  private boolean hasError, save;
  private JsonParser jsonParser;
  private String id, number, expire, token;
  private WeakReference<PaymentActivity> weakActivity;

  public VerifyCardTask(PaymentActivity activity) {
    jsonParser = new JsonParser();
    weakActivity = new WeakReference<>(activity);
  }

  @Override protected void onPreExecute() {
    PaymentActivity activity = weakActivity.get();
    if (activity != null) {
      id = activity.id;
      amount = activity.amount * 100; //Amount in teens
      number = activity.activityMainCardNumber.getText().toString().replace(" ", "");
      expire = activity.activityMainDateExpire.getText().toString().replace("/", "");
      save = activity.activityMainCardRemember.isChecked();
    }
  }

  @Override protected String doInBackground(Void... params) {
    JsonRpcRequest jsonRpcRequest = new JsonRpcRequest(id);

    JSONObject jsonObject = jsonParser.getCardsCreate(number, expire, amount, save);
    String result = jsonRpcRequest.callApiMethod(jsonObject, JsonRpcRequest.cardsCreateMethod);

    if (result == null) return null;
    if (jsonParser.checkError(result) != null) {
      hasError = true;
      return jsonParser.checkError(result);
    }

    token = jsonParser.getCardToken(result);
    jsonObject = jsonParser.getCardsVerifyCode(token);
    result = jsonRpcRequest.callApiMethod(jsonObject, JsonRpcRequest.cardsGetVerifyCodeMethod);

    return result;
  }

  @Override protected void onPostExecute(String s) {
    PaymentActivity activity = weakActivity.get();
    if (activity == null) return;
    if (s == null) {
      activity.showError(activity.getString(R.string.tryAgainMessage));
    } else if (hasError) {
      activity.showError(s);
    } else {
      Intent intent = new Intent(activity, ConfirmActivity.class);
      intent.putExtra(ConfirmActivity.ARG_CONFIRM, jsonParser.getConfirm(s));
      intent.putExtra(ConfirmActivity.ARG_TOKEN, token);
      intent.putExtra(PaymentActivity.EXTRA_LANG, activity.lang);
      intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
      activity.startActivity(intent);
      activity.finish();
    }
    activity.activityMainContinue.setEnabled(true);
    activity.activityMainProgress.setVisibility(View.GONE);
  }
}