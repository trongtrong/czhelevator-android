package com.kingyon.elevator.uis.actiivty2.input;

import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.czh.myversiontwo.activity.ActivityUtils;
import com.kingyon.elevator.uis.actiivty2.input.utils.HtmlParserUtil;
import com.kingyon.elevator.utils.utilstwo.ConentUtils;

import org.xml.sax.XMLReader;

import java.util.Map;

import static com.czh.myversiontwo.utils.Constance.ACTIVITY_MAIN2_TOPIC_DETAILS;
import static com.czh.myversiontwo.utils.Constance.ACTIVITY_USER_CENTER;

/**
 * Created By Admin  on 2020/4/28
 * Email : 163235610@qq.com
 * @Author:Mrczh
 * Instructions: 标签点击
 */
public class HtmlTagHandler implements Html.TagHandler {
  private static final String TAG = "tag";
  private static final String USER = "user";
  private static final String ID = "id";
  public static final String NAME = "name";

  @Override
  public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
    if (tag.toLowerCase().equals(TAG)||tag.toLowerCase().equals(USER)) {
      if (opening) {
        Map<String, String> map = HtmlParserUtil.parseStart(tag, output, xmlReader);
        String id = map.get(ID);
        String name = map.get(NAME);
        output.setSpan(new TagBean(name,id), output.length(), output.length(), Spannable.SPAN_MARK_MARK);
      } else {
        endTag(tag, output, xmlReader);
      }
    }
  }

  private Object getLast(Spanned text, Class kind) {
              /*
               * This knows that the last returned object from getSpans()
			         * will be the most recently added.
			         */
    Object[] objs = text.getSpans(0, text.length(), kind);

    if (objs.length == 0) {
      return null;
    } else {
      return objs[objs.length - 1];
    }
  }

  private void endTag(String tag, Editable text, XMLReader xmlReader) {
    //myfont标签不能裸着，即必须有html等标签包裹，或者前面有其他内容，否则字体大小不能起作用
    //即getlast变成从后面取，最后的内容的范围是0到文本全长度
    int len = text.length();
    Object obj = getLast(text, TagBean.class);
    int where = text.getSpanStart(obj);
    text.removeSpan(obj);
    Log.e("AAA", "where:" + where + ",len:" + len);
    if (where != len) {
      final TagBean t = (TagBean) obj;

      if (null != t) {
        text.setSpan(new ClickableSpan() {
          @Override public void onClick(View widget) {
            /*内容点击*/
            LogUtils.e(t.id,t.name,tag,text,xmlReader);
            if (tag.equals("tag")){
              if (!ConentUtils.topicStr.equals(t.id)) {
                ActivityUtils.setActivity(ACTIVITY_MAIN2_TOPIC_DETAILS, "topicid", t.id);
              }
            }else {
              /*用户中心*/
              if (!ConentUtils.aiteStr.equals(t.id)) {
                ActivityUtils.setActivity(ACTIVITY_USER_CENTER, "type", "1", "otherUserAccount", t.id);
              }
            }
//            Toast.makeText(widget.getContext(), t.toString(), Toast.LENGTH_SHORT).show();
          }
        }, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      }
    }
  }
}