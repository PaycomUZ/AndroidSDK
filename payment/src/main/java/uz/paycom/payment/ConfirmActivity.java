package uz.paycom.payment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import uz.paycom.payment.api.JsonParser;
import uz.paycom.payment.api.JsonRpcRequest;
import uz.paycom.payment.model.Confirm;
import uz.paycom.payment.utils.LocaleHelper;

import static uz.paycom.payment.PaymentActivity.EXTRA_LANG;

public class ConfirmActivity extends AppCompatActivity {

  public final static String ARG_CONFIRM = "CONFIRM";
  public final static String ARG_TOKEN = "TOKEN";

  private String token;

  private RelativeLayout activityConfirmErrorLayout;
  private TextView activityConfirmError;
  private TextView activityConfirmErrorMessage;
  private TextView activityConfirmClose;
  private ProgressBar activityConfirmProgress;
  private TextView activityConfirmTimer;
  private ImageView activityRepeatImage;
  private TextView activityConfirmCodeConfirmTitle;
  private EditText activityConfirmCodeConfirm;
  private TextView activityConfirmPhoneNumberTitle;
  private TextView activityConfirmPhoneNumber;
  private Button activityConfirmButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_confirm);

    activityConfirmErrorLayout = (RelativeLayout) findViewById(R.id.activity_confirm_errorLayout);
    activityConfirmError = (TextView) findViewById(R.id.activity_confirm_error);
    activityConfirmErrorMessage = (TextView) findViewById(R.id.activity_confirm_errorMessage);
    activityConfirmClose = (TextView) findViewById(R.id.activity_confirm_close);
    activityConfirmProgress = (ProgressBar) findViewById(R.id.activity_confirm_progress);
    activityConfirmTimer = (TextView) findViewById(R.id.activity_confirm_timer);
    activityRepeatImage = (ImageView) findViewById(R.id.activity_repeat_image);
    activityConfirmCodeConfirmTitle = (TextView) findViewById(R.id.activity_confirm_codeConfirmTitle);
    activityConfirmCodeConfirm = (EditText) findViewById(R.id.activity_confirm_codeConfirm);
    activityConfirmPhoneNumberTitle = (TextView) findViewById(R.id.activity_confirm_phoneNumberTitle);
    activityConfirmPhoneNumber = (TextView) findViewById(R.id.activity_confirm_phoneNumber);
    activityConfirmButton = (Button) findViewById(R.id.activity_confirm_button);

    Context context = LocaleHelper.onAttach(this, getIntent().getStringExtra(EXTRA_LANG));
    Resources resources = context.getResources();
    this.setTitle(resources.getString(R.string.paycomTitle));
    activityConfirmError.setText(resources.getString(R.string.error));
    activityConfirmClose.setText(resources.getString(R.string.close));
    activityConfirmCodeConfirmTitle.setText(resources.getString(R.string.codeConfirm));
    activityConfirmPhoneNumberTitle.setText(resources.getString(R.string.codeSent));
    activityConfirmButton.setText(resources.getString(R.string.confirm));
    activityConfirmCodeConfirmTitle.setText(resources.getString(R.string.codeConfirm));

    initUI(null);
    token = getIntent().getStringExtra(ARG_TOKEN);
    //Shared OnClickListener not allowed in library module project
    activityConfirmButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        activityConfirmErrorLayout.setVisibility(View.GONE);
        activityConfirmButton.setEnabled(false);
        activityConfirmProgress.setVisibility(View.VISIBLE);
        new ConfirmTask().execute();
      }
    });

    activityConfirmClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        activityConfirmErrorLayout.setVisibility(View.GONE);
      }
    });

    activityRepeatImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        activityConfirmErrorLayout.setVisibility(View.GONE);
        activityConfirmButton.setEnabled(false);
        activityConfirmProgress.setVisibility(View.VISIBLE);
        new RetryTask().execute();
      }
    });
  }

  private void initUI(Confirm confirm) {
    if (confirm == null) {
      confirm = getIntent().getParcelableExtra(ARG_CONFIRM);
    }

    activityConfirmPhoneNumber.setText(confirm.getPhone());
    activityRepeatImage.setVisibility(View.GONE);
    activityConfirmTimer.setVisibility(View.VISIBLE);
    new CountDownTimer(confirm.getWait(), 1000) {
      public void onTick(long millisUntilFinished) {
        activityConfirmTimer.setText("0:" + String.valueOf(millisUntilFinished / 1000));
      }
      public void onFinish() {
        activityRepeatImage.setVisibility(View.VISIBLE);
        activityConfirmTimer.setVisibility(View.GONE);
      }
    }.start();
  }

  private class ConfirmTask extends AsyncTask<Void, Void, String> {

    private String code;
    private boolean hasError;
    private JsonParser jsonParser;

    public ConfirmTask() {
      this.jsonParser = new JsonParser();
    }

    @Override protected void onPreExecute() {
      code = activityConfirmCodeConfirm.getText().toString();
    }

    @Override protected String doInBackground(Void... params) {
      JsonRpcRequest jsonRpcRequest = new JsonRpcRequest(PaymentActivity.id);

      JSONObject jsonObject = jsonParser.getCardsVerify(token, code);
      String result = jsonRpcRequest.callApiMethod(jsonObject, JsonRpcRequest.cardsCreateVerifyMethod);

      if (result == null) return null;
      if (jsonParser.checkError(result) != null) {
        hasError = true;
        return jsonParser.checkError(result);
      }

      return result;
    }

    @Override protected void onPostExecute(String s) {
      if (s == null) {
        showError(getString(R.string.tryAgainMessage));
      } else if (hasError) {
          showError(s);
      } else {
          Intent intent = new Intent();
          intent.putExtra(PaymentActivity.EXTRA_RESULT, jsonParser.getResult(s));
          setResult(RESULT_OK, intent);
          finish();
      }
      activityConfirmButton.setEnabled(true);
      activityConfirmProgress.setVisibility(View.GONE);
    }
  }

  private class RetryTask extends AsyncTask<Void, Void, String> {

    private boolean hasError;
    private JsonParser jsonParser;

    public RetryTask() {
      jsonParser = new JsonParser();
    }

    @Override
    protected String doInBackground(Void... params) {
      JsonRpcRequest jsonRpcRequest = new JsonRpcRequest(PaymentActivity.id);
      JSONObject jsonObject = jsonParser.getCardsVerifyCode(token);

      String result = jsonRpcRequest.callApiMethod(jsonObject, JsonRpcRequest.cardsGetVerifyCodeMethod);
      if (result == null) return null;
      if (jsonParser.checkError(result) != null) {
        hasError = true;
        return jsonParser.checkError(result);
      }

      return result;
    }

    @Override
    protected void onPostExecute(String s) {
      if (s == null) {
        showError(getString(R.string.tryAgainMessage));
      } else if (hasError) {
          showError(s);
      } else {
          initUI(jsonParser.getConfirm(s));
      }
      activityConfirmButton.setEnabled(true);
      activityConfirmProgress.setVisibility(View.GONE);
    }
  }

  private void showError(String s) {
    if (!TextUtils.isEmpty(s)) {
      activityConfirmErrorLayout.setVisibility(View.VISIBLE);
      activityConfirmErrorMessage.setText(s);
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    setResult(RESULT_CANCELED);
  }
}
