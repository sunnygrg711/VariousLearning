package com.rbs.rates.business.dfcompare.util;

import com.google.common.collect.Lists;
import com.rbs.rates.domain.reportable.RTrade;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by upadkti on 21/09/2017.
 */
public class JaversComparator {

  public static JaversComparator INSTANCE = new JaversComparator();
  private Javers javersInstance = null;

  private JaversComparator() {
    javersInstance = getOrCreateReconcilerWithIgnoredFields(Lists.newArrayList());
  }

  private Javers getOrCreateReconcilerWithIgnoredFields(List<String> ignoredFields) {
    return JaversBuilder.javers()
//        .registerValueObject(new ValueObjectDefinition(DateTime.class, Lists.newArrayList("iChronology")))
//        .registerValueObject(new ValueObjectDefinition(RTrade.class, ignoredFields))
//        .registerValueObject(new ValueObjectDefinition(List.class))
//        .registerValueObject(new ValueObjectDefinition(ArrayList.class))
//        .registerValueObject(new ValueObjectDefinition(Set.class))
      .build();
  }

  public List<String> compare(RTrade expected, RTrade actual) {
    Diff diff = javersInstance.compare(expected, actual);
    return getDiff(diff);
  }

  private List<String> getDiff(Diff diff) {
    List<String> differences = new ArrayList<>();
    if(diff.hasChanges()) {
      for(Change change : diff.getChanges()) {
        // ignore null != "" diffs
        if(change instanceof ValueChange) {
          ValueChange valueChange = (ValueChange) change;
          if(!isEmptyStringToNullDiff(change)) {
            String propName = valueChange.getAffectedGlobalId().toString() + valueChange.getPropertyName();
            differences.add(propName + "," + valueChange.getLeft() + "," + valueChange.getRight());
          }
        }
      }
    }
    return differences;
  }

  private boolean isEmptyStringToNullDiff(Change change) {
    boolean response = false;
    if (change instanceof ValueChange) {
      ValueChange valueChange = (ValueChange) change;
      Object lhs = valueChange.getLeft();
      Object rhs = valueChange.getRight();
      if (lhs != null) {
        if ("".equals(lhs.toString()) && rhs == null) {
          response = true;
        }
      }
    }
    return response;
  }
}
