package uz.paycom.payment.utils;

import android.view.View;

public class ViewDisable {

  public ViewDisable(View view) {
    this.view = view;
  }

  private View view;

  protected void enableView() {
      view.setEnabled(true);
      view.setAlpha(1f);
      view.setClickable(true);
  };

  protected void disableView() {
    view.setEnabled(false);
    view.setAlpha(.3f);
    view.setClickable(false);
  }

}