// Generated code from Butter Knife. Do not modify!
package com.along.longbook;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CategorySearchActivity_ViewBinding implements Unbinder {
  private CategorySearchActivity target;

  @UiThread
  public CategorySearchActivity_ViewBinding(CategorySearchActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public CategorySearchActivity_ViewBinding(CategorySearchActivity target, View source) {
    this.target = target;

    target.mResultList = Utils.findRequiredViewAsType(source, R.id.category_list, "field 'mResultList'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CategorySearchActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mResultList = null;
  }
}
