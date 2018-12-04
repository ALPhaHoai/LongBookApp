// Generated code from Butter Knife. Do not modify!
package com.along.longbook;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ListBookActivity_ViewBinding implements Unbinder {
  private ListBookActivity target;

  @UiThread
  public ListBookActivity_ViewBinding(ListBookActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ListBookActivity_ViewBinding(ListBookActivity target, View source) {
    this.target = target;

    target.mSearchField = Utils.findRequiredViewAsType(source, R.id.search_field, "field 'mSearchField'", EditText.class);
    target.CateName = Utils.findRequiredViewAsType(source, R.id.cate_name, "field 'CateName'", TextView.class);
    target.mSearchBtn = Utils.findRequiredViewAsType(source, R.id.search_btn, "field 'mSearchBtn'", ImageButton.class);
    target.mResultList = Utils.findRequiredViewAsType(source, R.id.result_list, "field 'mResultList'", RecyclerView.class);
    target.mDrawerLayout = Utils.findRequiredViewAsType(source, R.id.parent_layout, "field 'mDrawerLayout'", DrawerLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ListBookActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mSearchField = null;
    target.CateName = null;
    target.mSearchBtn = null;
    target.mResultList = null;
    target.mDrawerLayout = null;
  }
}
