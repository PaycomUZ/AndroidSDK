package uz.paycom.payment.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

public class DateExpireFormat extends ViewDisable implements TextWatcher {

  private boolean lock;

  public DateExpireFormat(View view) {
    super(view);
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
  }

  @Override
  public void afterTextChanged(Editable s) {
    if (s.length() > 4) { enableView(); } else { disableView(); }
    if (lock || s.length() > 4) {
      return;
    }
    lock = true;
    for (int i = 2; i < s.length(); i +=2) {
      if (s.toString().charAt(i) != '/') {
        s.insert(i, "/");
      }
    }
    lock = false;
  }
}