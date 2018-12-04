// Generated code from Butter Knife. Do not modify!
package com.along.longbook;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BookDetailActivity_ViewBinding implements Unbinder {
  private BookDetailActivity target;

  @UiThread
  public BookDetailActivity_ViewBinding(BookDetailActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public BookDetailActivity_ViewBinding(BookDetailActivity target, View source) {
    this.target = target;

    target.titleTextView = Utils.findRequiredViewAsType(source, R.id.title, "field 'titleTextView'", TextView.class);
    target.contentTextView = Utils.findRequiredViewAsType(source, R.id.content, "field 'contentTextView'", TextView.class);
    target.categoriesTextView = Utils.findRequiredViewAsType(source, R.id.categories, "field 'categoriesTextView'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BookDetailActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.titleTextView = null;
    target.contentTextView = null;
    target.categoriesTextView = null;
  }
}
