package uz.paycom.payment.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Here is our result for calling outer activity
 */
public class Result implements Parcelable {

  private String number;
  private String expire;
  private String token;
  private boolean recurent;
  private boolean verify;

  public Result(String number, String expire, String token, boolean recurent, boolean verify) {
    this.number = number;
    this.expire = expire;
    this.token = token;
    this.recurent = recurent;
    this.verify = verify;
  }

  public String getExpire() {
    return expire;
  }

  public String getToken() {
    return token;
  }

  public String getNumber() {
    return number;
  }

  public boolean isRecurent() {
    return recurent;
  }

  public boolean isVerify() {
    return verify;
  }

  @Override
  public String toString() {
    return "Result{" +
        "number='" + number + '\'' +
        ", expire='" + expire + '\'' +
        ", token='" + token + '\'' +
        ", recurent=" + recurent +
        ", verify=" + verify +
        '}';
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.number);
    dest.writeString(this.expire);
    dest.writeString(this.token);
    dest.writeByte(this.recurent ? (byte) 1 : (byte) 0);
    dest.writeByte(this.verify ? (byte) 1 : (byte) 0);
  }

  protected Result(Parcel in) {
    this.number = in.readString();
    this.expire = in.readString();
    this.token = in.readString();
    this.recurent = in.readByte() != 0;
    this.verify = in.readByte() != 0;
  }

  public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
    @Override
    public Result createFromParcel(Parcel source) {
      return new Result(source);
    }

    @Override
    public Result[] newArray(int size) {
      return new Result[size];
    }
  };
}