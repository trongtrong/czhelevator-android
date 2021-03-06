package com.bobomee.android.mentions.edit.util;

import com.bobomee.android.mentions.model.FormatRange;
import com.bobomee.android.mentions.model.Range;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @Created By Admin  on 2020/4/29
 * @Email : 163235610@qq.com
 * @Author:Mrczh
 * @Instructions:
 */
public class FormatRangeManager extends RangeManager {

  public CharSequence getFormatCharSequence(String text) {
    if (isEmpty()) {
      return text;
    }

    int lastRangeTo = 0;
    ArrayList<? extends Range> ranges = get();
    Collections.sort(ranges);

    StringBuilder builder = new StringBuilder("");
    CharSequence newChar;
    for (Range range : ranges) {
      if (range instanceof FormatRange) {
        FormatRange formatRange = (FormatRange) range;
        FormatRange.FormatData convert = formatRange.getConvert();
        newChar = convert.formatCharSequence();
        builder.append(text.substring(lastRangeTo, range.getFrom()));
        builder.append(newChar);
        lastRangeTo = range.getTo();
      }
    }

    builder.append(text.substring(lastRangeTo));
    return builder.toString();
  }
}
