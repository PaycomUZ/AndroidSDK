package uz.paycom.payment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import uz.paycom.payment.api.task.VerifyCardTask;
import uz.paycom.payment.utils.CardNumberFormat;
import uz.paycom.payment.utils.DateExpireFormat;
import uz.paycom.payment.utils.LocaleHelper;
import uz.paycom.payment.utils.PaycomSandBox;

public class PaymentActivity extends AppCompatActivity {

  public static String id;

  public static final String EXTRA_ID = "ID";
  public static final String EXTRA_LANG = "LANG";
  public static final String EXTRA_SAVE = "SAVE";
  public static final String EXTRA_AMOUNT = "AMOUNT";
  public static final String EXTRA_RESULT = "RESULT";

  private TextView activityMainClose;
  private TextView activityMainPaymentSum;
  private TextView activityMainErrorMessage;
  private TextView activityMainPaymentSumTitle;
  private TextView activityMainErrorLayoutError;
  private TextView activityMainCardNumberTitle;
  private TextView activityMainUzcardOnlyText;
  private TextView activityMainDateExpireTitle;
  private RelativeLayout activityMainErrorLayout;

  private DecimalFormat decimalFormat;

  public EditText activityMainCardNumber;
  public EditText activityMainDateExpire;
  public CheckBox activityMainCardRemember;
  public Button activityMainContinue;
  public ProgressBar activityMainProgress;

  public String lang;
  public Double amount;
  public boolean save;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.paycom_payment_main);

    activityMainClose = findViewById(R.id.activity_main_close);
    activityMainContinue = findViewById(R.id.activity_main_continue);
    activityMainProgress = findViewById(R.id.activity_main_progress);
    activityMainErrorLayout = findViewById(R.id.activity_main_errorLayout);
    activityMainCardNumber = findViewById(R.id.activity_main_cardNumber);
    activityMainPaymentSum = findViewById(R.id.activity_main_paymentSum);
    activityMainDateExpire = findViewById(R.id.activity_main_dateExpire);
    activityMainErrorMessage = findViewById(R.id.activity_main_errorMessage);
    activityMainCardRemember = findViewById(R.id.activity_main_cardRemember);
    activityMainUzcardOnlyText = findViewById(R.id.activity_main_uzcardOnlyText);
    activityMainCardNumberTitle = findViewById(R.id.activity_main_cardNumberTitle);
    activityMainPaymentSumTitle = findViewById(R.id.activity_main_paymentSumTitle);
    activityMainDateExpireTitle = findViewById(R.id.activity_main_dateExpireTitle);
    activityMainErrorLayoutError = findViewById(R.id.activity_main_errorLayout_error);

    initUI();
    activityMainCardNumber.addTextChangedListener(new
            CardNumberFormat(activityMainContinue, activityMainDateExpire));
    activityMainDateExpire.addTextChangedListener(new
            DateExpireFormat(activityMainContinue, activityMainCardNumber));

    activityMainContinue.setEnabled(false);
    activityMainContinue.setAlpha(.3f);
    activityMainContinue.setClickable(false);
    activityMainContinue.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String number = activityMainCardNumber.getText().toString().replace(" ", "");
        if (!isOnline(number) && !PaycomSandBox.isSandBox()) {
          showError(getString(R.string.uzcardOnly));
          return;
        }
        if (!isValid(number)) showError(getString(R.string.invalidCard));
        new VerifyCardTask(PaymentActivity.this).execute();
        activityMainContinue.setEnabled(false);
        activityMainProgress.setVisibility(View.VISIBLE);
        activityMainErrorLayout.setVisibility(View.GONE);
      }
    });

    activityMainClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        activityMainErrorLayout.setVisibility(View.GONE);
      }
    });
  }

  private void initUI() {
    //Set language from intent param
    lang = getIntent().getStringExtra(EXTRA_LANG);
    Context context = LocaleHelper.onAttach(this, lang);
    Resources resources = context.getResources();

    this.setTitle(resources.getString(R.string.paycomTitle));
    activityMainErrorLayoutError.setText(resources.getString(R.string.error));
    activityMainClose.setText(resources.getString(R.string.close));
    activityMainPaymentSumTitle.setText(resources.getString(R.string.paymentSum));
    activityMainCardNumberTitle.setText(resources.getString(R.string.cardNumber));
    activityMainUzcardOnlyText.setText(resources.getString(R.string.uzcardOnly));
    activityMainDateExpireTitle.setText(resources.getString(R.string.dateExpire));
    activityMainDateExpire.setHint(resources.getString(R.string.dateExpireHint));
    activityMainContinue.setText(resources.getString(R.string.continueText));
    activityMainCardRemember.setText(resources.getString(R.string.cardRemember));

    id = getIntent().getStringExtra(EXTRA_ID);
    amount = getIntent().getDoubleExtra(EXTRA_AMOUNT, 0.00);
    //Round to 2 fraction digits after comma
    amount = Math.floor(amount * 100.0) / 100.0;
    save = getIntent().getBooleanExtra(EXTRA_SAVE, false);
    if (amount <= 0) {setResult(RESULT_CANCELED); finish();}
    activityMainPaymentSum.setText(formatMoney(amount, true) + " " + resources.getString(R.string.card_balance_currency));
    activityMainCardRemember.setVisibility(save ? View.VISIBLE : View.GONE);
  }

  public static boolean isValid(String code) {
    // Valid only for even code length
    int sum = 0;

    boolean alternate = false;

    for (int i = code.length() - 1; i >= 0; i--) {
      int n = Integer.parseInt(code.substring(i, i + 1));
      if (alternate) {
        n *= 2;
        if (n > 9) {
          n = (n % 10) + 1;
        }
      }
      sum += n;
      alternate = !alternate;
    }
    return (sum % 10 == 0);
  }

  public String formatMoney(double value, boolean showDecimal) {
    if (decimalFormat == null) {
      decimalFormat = new DecimalFormat("###,###,###.00");
      decimalFormat.setGroupingSize(3);
      decimalFormat.setMinimumFractionDigits(0);

      DecimalFormatSymbols s = new DecimalFormatSymbols();
      s.setGroupingSeparator(' ');
      DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
      s.setDecimalSeparator(symbols.getDecimalSeparator());
      decimalFormat.setDecimalFormatSymbols(s);
    }

    decimalFormat.setMinimumFractionDigits(showDecimal ? 2 : 0);
    decimalFormat.setMaximumFractionDigits(showDecimal ? 2 : 0);

    return decimalFormat.format(value);
  }

  public static boolean isOnline(String code) {
    return code.substring(0, 4).equals("8600");
  }

  public void showError(String s) {
    if (!TextUtils.isEmpty(s)) {
      activityMainErrorLayout.setVisibility(View.VISIBLE);
      activityMainErrorMessage.setText(s);
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    setResult(RESULT_CANCELED);
  }
}