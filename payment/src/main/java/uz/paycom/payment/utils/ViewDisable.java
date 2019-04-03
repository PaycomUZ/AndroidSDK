package uz.paycom.payment.utils;

import android.view.View;

class ViewDisable {

  private View view;

  ViewDisable(View view) {
    this.view = view;
  }

  void enableView() {
      view.setEnabled(true);
      view.setAlpha(1f);
      view.setClickable(true);
  };

  void disableView() {
    view.setEnabled(false);
    view.setAlpha(.3f);
    view.setClickable(false);
  }
}