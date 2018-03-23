package uz.paycom.testpaycom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import uz.paycom.payment.PaymentActivity;
import uz.paycom.payment.model.Result;
import uz.paycom.payment.utils.PaycomSandBox;

import static uz.paycom.payment.PaymentActivity.EXTRA_AMOUNT;
import static uz.paycom.payment.PaymentActivity.EXTRA_ID;
import static uz.paycom.payment.PaymentActivity.EXTRA_LANG;
import static uz.paycom.payment.PaymentActivity.EXTRA_RESULT;
import static uz.paycom.payment.PaymentActivity.EXTRA_SAVE;

public class TestActivity extends AppCompatActivity {

  private static final String TAG = "TestActivity";
  private static final String xAuth = "54e1e3f527e073c0f62eeddf";

  private EditText activityTestSum;
  private CheckBox activityTestMultiple;
  private Button activityTestPayment;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);

    activityTestSum = findViewById(R.id.activity_test_sum);
    activityTestMultiple =  findViewById(R.id.activity_test_multiple);
    activityTestPayment = findViewById(R.id.activity_test_payment);

    activityTestPayment.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(TestActivity.this, PaymentActivity.class);
        intent.putExtra(EXTRA_ID, xAuth);
        //Если чисел после запятой больше 2-ух, то они будут отброшены
        final Double sum = Double.valueOf(activityTestSum.getText().toString());
        intent.putExtra(EXTRA_AMOUNT, sum);
        intent.putExtra(EXTRA_SAVE, activityTestMultiple.isChecked());
        intent.putExtra(EXTRA_LANG, "RU");
        PaycomSandBox.setEnabled(true);
        startActivityForResult(intent, 0);
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      Result result = data.getParcelableExtra(EXTRA_RESULT);
      Log.d(TAG, result.toString());
    } else if (resultCode == RESULT_CANCELED) {
        Log.d(TAG, "Payment canceled");
    }
  }
}