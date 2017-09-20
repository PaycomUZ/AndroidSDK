package uz.paycom.payment.utils;

public class PaycomSandBox {

  private static boolean isSandBox;

  public static void setEnabled(boolean enabled) {
    isSandBox = enabled;
  };

  public static boolean isSandBox() {
    return isSandBox;
  }
}