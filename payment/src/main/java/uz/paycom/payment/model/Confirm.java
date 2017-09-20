package uz.paycom.payment.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Api cards.get_verify_code object
 */
public class Confirm implements Parcelable {

  private boolean sent;
  private String phone;

  public Confirm(boolean sent, String phone, int wait) {
    this.sent = sent;
    this.phone = phone;
    this.wait = wait;
  }

  private int wait;

  public boolean isSent() {
    return sent;
  }

  public String getPhone() {
    return phone;
  }

  public int getWait() {
    return wait;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByte(this.sent ? (byte) 1 : (byte) 0);
    dest.writeString(this.phone);
    dest.writeInt(this.wait);
  }

  protected Confirm(Parcel in) {
    this.sent = in.readByte() != 0;
    this.phone = in.readString();
    this.wait = in.readInt();
  }

  public static final Parcelable.Creator<Confirm> CREATOR = new Parcelable.Creator<Confirm>() {
    @Override
    public Confirm createFromParcel(Parcel source) {
      return new Confirm(source);
    }

    @Override
    public Confirm[] newArray(int size) {
      return new Confirm[size];
    }
  };
}