// Generated code from Butter Knife. Do not modify!
package com.dev.nunua.Users;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.dev.nunua.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MpesaActivity_ViewBinding implements Unbinder {
  private MpesaActivity target;

  @UiThread
  public MpesaActivity_ViewBinding(MpesaActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MpesaActivity_ViewBinding(MpesaActivity target, View source) {
    this.target = target;

    target.mAmount = Utils.findRequiredViewAsType(source, R.id.etAmount, "field 'mAmount'", EditText.class);
    target.mPhone = Utils.findRequiredViewAsType(source, R.id.etPhone, "field 'mPhone'", EditText.class);
    target.mPay = Utils.findRequiredViewAsType(source, R.id.btnPay, "field 'mPay'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MpesaActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mAmount = null;
    target.mPhone = null;
    target.mPay = null;
  }
}
