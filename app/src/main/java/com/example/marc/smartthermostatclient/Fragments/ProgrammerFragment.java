package com.example.marc.smartthermostatclient.Fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.example.marc.smartthermostatclient.DataStructure.Alarm;
import com.example.marc.smartthermostatclient.DataStructure.DaysOfWeek;
import com.example.marc.smartthermostatclient.R;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashSet;

/**
 * Created by Marc on 25/03/2015.
 */
public class ProgrammerFragment extends Fragment {
    ListView mProgramsList;
    private static final int ANIMATION_DURATION = 300;
    private static final int EXPAND_DURATION = 300;
    private static final int COLLAPSE_DURATION = 250;
    private static final long INVALID_ID = -1;
    private static final float EXPAND_DECELERATION = 1f;
    private static final float COLLAPSE_DECELERATION = 0.7f;
    private Interpolator mExpandInterpolator;
    private Interpolator mCollapseInterpolator;


    private static final int ROTATE_180_DEGREE = 180;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.programs_list, container, false);
        mProgramsList = (ListView) v.findViewById(R.id.programs_list);
        Alarm[] alarms = new Alarm[3];
        alarms[0]= new Alarm("9","00", "12","00");
        alarms[1]= new Alarm("15","00", "20","00");
        alarms[2]= new Alarm("9","00", "22","00");
        ProgrammerItemAdapter mAdapter = new ProgrammerItemAdapter(getActivity(),alarms);
        mProgramsList.setAdapter(mAdapter);

        mExpandInterpolator = new DecelerateInterpolator(EXPAND_DECELERATION);
        mCollapseInterpolator = new DecelerateInterpolator(COLLAPSE_DECELERATION);
        return v;
    }
    private void showLabelDialog(final Alarm alarm) {
        //show label dialog
    }

    public void setLabel(Alarm alarm, String label) {
        alarm.label = label;
        //asyncUpdateAlarm(alarm, false);
    }

    private class ProgrammerItemAdapter extends ArrayAdapter {
        private final LayoutInflater mFactory;
        private final Alarm[] programsList;
        private Context mContext;
        private final String[] mShortWeekDayStrings;
        private final String[] mLongWeekDayStrings;
        private final int mCollapseExpandHeight;
        private final HashSet<Long> mSelectedAlarms = new HashSet<Long>();
        private final int mBackgroundColorExpanded;
        private final int mBackgroundColor;
        private final int mColorLit;
        private final int mColorDim;
        private final Typeface mRobotoNormal;
        private final Typeface mRobotoBold;

        private Bundle mPreviousDaysOfWeekMap = new Bundle();
        private final HashSet<Long> mRepeatChecked = new HashSet<Long>();
        private final HashSet<Long> mExpanded = new HashSet<Long>();
        private ItemHolder mExpandedItemHolder;

        public class ItemHolder {

            // views for optimization
            LinearLayout alarmItem;
            TextView clockStart;
            TextView clockEnd;
            Switch onoff;
            TextView daysOfWeek;
            TextView label;
            ImageView delete;
            View expandArea;
            View summary;
            TextView clickableLabel;
            CheckBox repeat;
            LinearLayout repeatDays;
            ViewGroup[] dayButtonParents = new ViewGroup[7];
            ToggleButton[] dayButtons = new ToggleButton[7];
            View hairLine;
            View arrow;
            View collapseExpandArea;
            View footerFiller;

            // Other states
            Alarm alarm;
        }

        private final int[] DAY_ORDER = new int[]{
                Calendar.MONDAY,
                Calendar.TUESDAY,
                Calendar.WEDNESDAY,
                Calendar.THURSDAY,
                Calendar.FRIDAY,
                Calendar.SATURDAY,
                Calendar.SUNDAY,
        };

        public ProgrammerItemAdapter(Context context, Alarm[] values) {

            super(context, R.layout.programmer_list_item, values);
            this.mContext = context;
            this.programsList=values;
            mFactory = LayoutInflater.from(context);
            DateFormatSymbols dfs = new DateFormatSymbols();
            mShortWeekDayStrings = dfs.getShortWeekdays();
            mLongWeekDayStrings = dfs.getWeekdays();
            Resources res = mContext.getResources();
            mCollapseExpandHeight = (int) res.getDimension(R.dimen.collapse_expand_height);

            mBackgroundColorExpanded = res.getColor(R.color.alarm_whiteish);
            mBackgroundColor = R.drawable.alarm_background_normal;

            mColorLit = res.getColor(R.color.clock_white);
            mColorDim = res.getColor(R.color.clock_gray);
            mRobotoBold = Typeface.create("sans-serif-condensed", Typeface.BOLD);
            mRobotoNormal = Typeface.create("sans-serif-condensed", Typeface.NORMAL);

        }

        public void removeSelectedId(int id) {
            mSelectedAlarms.remove(id);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.programmer_list_item, parent, false);
            setNewHolder(rowView);

            Object tag = rowView.getTag();
            final ItemHolder itemHolder = (ItemHolder) tag;

            itemHolder.alarmItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isAlarmExpanded(programsList[position])) {
                        collapseAlarm(itemHolder, true);
                    } else {
                        expandAlarm(itemHolder, true);
                    }
                }
            });
            Alarm current = programsList[position];
            itemHolder.alarm=current;
            itemHolder.clockStart.setText(current.startTimeToString());
            itemHolder.clockEnd.setText(current.endTimeToString());

            itemHolder.clockStart.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            itemHolder.alarm.minutesStart=Integer.toString(selectedMinute);
                            itemHolder.alarm.hourStart=Integer.toString(selectedHour);
                            NumberFormat formatter = new DecimalFormat("00");
                            itemHolder.clockStart.setText( selectedHour + ":" + formatter.format(selectedMinute));
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();

                }
            });
            itemHolder.clockEnd.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            itemHolder.alarm.minutesEnd=Integer.toString(selectedMinute);
                            itemHolder.alarm.hourEnd=Integer.toString(selectedHour);
                            NumberFormat formatter = new DecimalFormat("00");
                            itemHolder.clockEnd.setText( selectedHour + ":" + formatter.format(selectedMinute));
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();

                }
            });
            //AlarmUtils.showTimeEditDialog(getChildFragmentManager(),                      alarm, AlarmClockFragment.this                        , DateFormat.is24HourFormat(getActivity()));
            //expandAlarm(itemHolder, true);
            itemHolder.alarmItem.post(mScrollRunnable);


            return rowView;
        }

        private void setNewHolder(View view) {
            // standard view holder optimization
            final ItemHolder holder = new ItemHolder();
            holder.alarmItem = (LinearLayout) view.findViewById(R.id.alarm_item);
            holder.clockStart = (TextView) view.findViewById(R.id.digital_clock_start);
            holder.clockEnd = (TextView) view.findViewById(R.id.digital_clock_end);
            holder.onoff = (Switch) view.findViewById(R.id.onoff);
            holder.onoff.setTypeface(mRobotoNormal);
            holder.daysOfWeek = (TextView) view.findViewById(R.id.daysOfWeek);
            holder.label = (TextView) view.findViewById(R.id.label);
            holder.delete = (ImageView) view.findViewById(R.id.delete);
            holder.summary = view.findViewById(R.id.summary);
            holder.expandArea = view.findViewById(R.id.expand_area);
            holder.hairLine = view.findViewById(R.id.hairline);
            holder.arrow = view.findViewById(R.id.arrow);
            holder.repeat = (CheckBox) view.findViewById(R.id.repeat_onoff);
            holder.clickableLabel = (TextView) view.findViewById(R.id.edit_label);
            holder.repeatDays = (LinearLayout) view.findViewById(R.id.repeat_days);
            holder.collapseExpandArea = view.findViewById(R.id.collapse_expand);
            //holder.footerFiller = view.findViewById(R.id.alarm_footer_filler);


            // Build button for each day.
            for (int i = 0; i < 7; i++) {
                final ViewGroup viewgroup = (ViewGroup) mFactory.inflate(R.layout.day_button,
                        holder.repeatDays, false);
                final ToggleButton button = (ToggleButton) viewgroup.getChildAt(0);
                final int dayToShowIndex = DAY_ORDER[i];
                button.setText(mShortWeekDayStrings[dayToShowIndex]);
                button.setTextOn(mShortWeekDayStrings[dayToShowIndex]);
                button.setTextOff(mShortWeekDayStrings[dayToShowIndex]);
                button.setContentDescription(mLongWeekDayStrings[dayToShowIndex]);
                holder.repeatDays.addView(viewgroup);
                holder.dayButtons[i] = button;
                holder.dayButtonParents[i] = viewgroup;
            }
            view.setTag(holder);
        }

        private void bindExpandArea(final ItemHolder itemHolder, final Alarm alarm) {
            // Views in here are not bound until the item is expanded.

            if (alarm.label != null && alarm.label.length() > 0) {
                itemHolder.clickableLabel.setText(alarm.label);
                itemHolder.clickableLabel.setTextColor(mColorLit);
            } else {
                itemHolder.clickableLabel.setText(R.string.label);
                itemHolder.clickableLabel.setTextColor(mColorDim);
            }
            itemHolder.clickableLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLabelDialog(alarm);
                }
            });

            if (mRepeatChecked.contains(alarm.id) || !itemHolder.alarm.daysOfWeek.isRepeating()) {
                itemHolder.repeat.setChecked(true);
                itemHolder.repeatDays.setVisibility(View.GONE);
            } else {
                itemHolder.repeat.setChecked(false);
                itemHolder.repeatDays.setVisibility(View.VISIBLE);
            }
            itemHolder.repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final boolean checked = ((CheckBox) view).isChecked();
                    if (!checked) {
                        // Show days
                        itemHolder.repeatDays.setVisibility(View.VISIBLE);
                        mRepeatChecked.remove(alarm.id);


                        // Set all previously set days
                        // or
                        // Set all days if no previous.
                        final int bitSet = mPreviousDaysOfWeekMap.getInt("" + alarm.id);
                        alarm.daysOfWeek.setBitSet(bitSet);
                        if (!alarm.daysOfWeek.isRepeating()) {
                            alarm.daysOfWeek.setDaysOfWeek(true, DAY_ORDER);
                        }
                        updateDaysOfWeekButtons(itemHolder, alarm.daysOfWeek);
                    } else {
                        itemHolder.repeatDays.setVisibility(View.GONE);
                        mRepeatChecked.add(alarm.id);

                        // Remember the set days in case the user wants it back.
                        final int bitSet = alarm.daysOfWeek.getBitSet();
                        mPreviousDaysOfWeekMap.putInt("" + alarm.id, bitSet);

                        // Remove all repeat days
                        alarm.daysOfWeek.clearAllDays();
                    }
                    //asyncUpdateAlarm(alarm, false);
                }
            });

            updateDaysOfWeekButtons(itemHolder, alarm.daysOfWeek);
            for (int i = 0; i < 7; i++) {
                final int buttonIndex = i;

                itemHolder.dayButtonParents[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemHolder.dayButtons[buttonIndex].toggle();
                        final boolean checked = itemHolder.dayButtons[buttonIndex].isChecked();
                        int day = DAY_ORDER[buttonIndex];
                        alarm.daysOfWeek.setDaysOfWeek(checked, day);
                        if (checked) {
                            turnOnDayOfWeek(itemHolder, buttonIndex);
                        } else {
                            turnOffDayOfWeek(itemHolder, buttonIndex);

                            // See if this was the last day, if so, un-check the repeat box.
                            if (!alarm.daysOfWeek.isRepeating()) {
                                itemHolder.repeatDays.setVisibility(View.GONE);
                                itemHolder.repeat.setTextColor(mColorDim);
                                mRepeatChecked.remove(alarm.id);

                                // Set history to no days, so it will be everyday when repeat is
                                // turned back on
                                mPreviousDaysOfWeekMap.putInt("" + alarm.id,
                                        DaysOfWeek.NO_DAYS_SET);
                            }
                        }
                        //asyncUpdateAlarm(alarm, false);
                    }
                });
            }
        }

        // Used for scrolling an expanded item in the list to make sure it is fully visible.
        private long mScrollAlarmId = ProgrammerFragment.INVALID_ID;
        private final Runnable mScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (mScrollAlarmId != ProgrammerFragment.INVALID_ID) {
                    View v = getViewById(mScrollAlarmId);
                    if (v != null) {
                        Rect rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        mProgramsList.requestChildRectangleOnScreen(v, rect, false);
                    }
                    mScrollAlarmId = ProgrammerFragment.INVALID_ID;
                }
            }
        };

        private boolean isAlarmExpanded(Alarm alarm) {
            return mExpanded.contains(alarm.id);
        }

        /**
         * Expands the alarm for editing.
         *
         * @param itemHolder The item holder instance.
         */
        private void expandAlarm(final ItemHolder itemHolder, boolean animate) {
            mExpanded.add(itemHolder.alarm.id);
            bindExpandArea(itemHolder, itemHolder.alarm);
            // Scroll the view to make sure it is fully viewed
            mScrollAlarmId = itemHolder.alarm.id;

            // Save the starting height so we can animate from this value.
            final int startingHeight = itemHolder.alarmItem.getHeight();

            // Set the expand area to visible so we can measure the height to animate to.
            itemHolder.alarmItem.setBackgroundColor(mBackgroundColorExpanded);
            itemHolder.expandArea.setVisibility(View.VISIBLE);

            if (!animate) {
                // Set the "end" layout and don't do the animation.
                itemHolder.arrow.setRotation(180);
                // We need to translate the hairline up, so the height of the collapseArea
                // needs to be measured to know how high to translate it.
                final ViewTreeObserver observer = mProgramsList.getViewTreeObserver();
                observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        // We don't want to continue getting called for every listview drawing.
                        if (observer.isAlive()) {
                            observer.removeOnPreDrawListener(this);
                        }
                        int hairlineHeight = itemHolder.hairLine.getHeight();
                        int collapseHeight =
                                itemHolder.collapseExpandArea.getHeight() - hairlineHeight;
                        itemHolder.hairLine.setTranslationY(-collapseHeight);
                        return true;
                    }
                });
                return;
            }

            // Add an onPreDrawListener, which gets called after measurement but before the draw.
            // This way we can check the height we need to animate to before any drawing.
            // Note the series of events:
            //  * expandArea is set to VISIBLE, which causes a layout pass
            //  * the view is measured, and our onPreDrawListener is called
            //  * we set up the animation using the start and end values.
            //  * the height is set back to the starting point so it can be animated down.
            //  * request another layout pass.
            //  * return false so that onDraw() is not called for the single frame before
            //    the animations have started.
            final ViewTreeObserver observer = mProgramsList.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    // We don't want to continue getting called for every listview drawing.
                    if (observer.isAlive()) {
                        observer.removeOnPreDrawListener(this);
                    }
                    // Calculate some values to help with the animation.
                    final int endingHeight = itemHolder.alarmItem.getHeight();
                    final int distance = endingHeight - startingHeight;
                    final int collapseHeight = itemHolder.collapseExpandArea.getHeight();
                    int hairlineHeight = itemHolder.hairLine.getHeight();
                    final int hairlineDistance = collapseHeight - hairlineHeight;

                    // Set the height back to the start state of the animation.
                    itemHolder.alarmItem.getLayoutParams().height = startingHeight;
                    // To allow the expandArea to glide in with the expansion animation, set a
                    // negative top margin, which will animate down to a margin of 0 as the height
                    // is increased.
                    // Note that we need to maintain the bottom margin as a fixed value (instead of
                    // just using a listview, to allow for a flatter hierarchy) to fit the bottom
                    // bar underneath.
                    FrameLayout.LayoutParams expandParams = (FrameLayout.LayoutParams)
                            itemHolder.expandArea.getLayoutParams();
                    expandParams.setMargins(0, -distance, 0, collapseHeight);
                    itemHolder.alarmItem.requestLayout();

                    // Set up the animator to animate the expansion.
                    ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f)
                            .setDuration(EXPAND_DURATION);
                    animator.setInterpolator(mExpandInterpolator);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            Float value = (Float) animator.getAnimatedValue();

                            // For each value from 0 to 1, animate the various parts of the layout.
                            itemHolder.alarmItem.getLayoutParams().height =
                                    (int) (value * distance + startingHeight);
                            FrameLayout.LayoutParams expandParams = (FrameLayout.LayoutParams)
                                    itemHolder.expandArea.getLayoutParams();
                            expandParams.setMargins(
                                    0, (int) -((1 - value) * distance), 0, collapseHeight);
                            itemHolder.arrow.setRotation(180 * value);
                            itemHolder.hairLine.setTranslationY(-hairlineDistance * value);
                            itemHolder.summary.setAlpha(1 - value);

                            itemHolder.alarmItem.requestLayout();
                        }
                    });
                    // Set everything to their final values when the animation's done.
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Set it back to wrap content since we'd explicitly set the height.
                            itemHolder.alarmItem.getLayoutParams().height =
                                    ViewGroup.LayoutParams.WRAP_CONTENT;
                            itemHolder.arrow.setRotation(180);
                            itemHolder.hairLine.setTranslationY(-hairlineDistance);
                            itemHolder.summary.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            // TODO we may have to deal with cancelations of the animation.
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                        }
                    });
                    animator.start();

                    // Return false so this draw does not occur to prevent the final frame from
                    // being drawn for the single frame before the animations start.
                    return false;
                }
            });
        }


        private void collapseAlarm(final ItemHolder itemHolder, boolean animate) {
            mExpanded.remove(itemHolder.alarm.id);

            // Save the starting height so we can animate from this value.
            final int startingHeight = itemHolder.alarmItem.getHeight();

            // Set the expand area to gone so we can measure the height to animate to.
            itemHolder.alarmItem.setBackgroundResource(mBackgroundColor);
            itemHolder.expandArea.setVisibility(View.GONE);

            if (!animate) {
                // Set the "end" layout and don't do the animation.
                itemHolder.arrow.setRotation(0);
                itemHolder.hairLine.setTranslationY(0);
                return;
            }

            // Add an onPreDrawListener, which gets called after measurement but before the draw.
            // This way we can check the height we need to animate to before any drawing.
            // Note the series of events:
            //  * expandArea is set to GONE, which causes a layout pass
            //  * the view is measured, and our onPreDrawListener is called
            //  * we set up the animation using the start and end values.
            //  * expandArea is set to VISIBLE again so it can be shown animating.
            //  * request another layout pass.
            //  * return false so that onDraw() is not called for the single frame before
            //    the animations have started.
            final ViewTreeObserver observer = mProgramsList.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (observer.isAlive()) {
                        observer.removeOnPreDrawListener(this);
                    }

                    // Calculate some values to help with the animation.
                    final int endingHeight = itemHolder.alarmItem.getHeight();
                    final int distance = endingHeight - startingHeight;
                    int hairlineHeight = itemHolder.hairLine.getHeight();
                    final int hairlineDistance = mCollapseExpandHeight - hairlineHeight;

                    // Re-set the visibilities for the start state of the animation.
                    itemHolder.expandArea.setVisibility(View.VISIBLE);
                    itemHolder.summary.setVisibility(View.VISIBLE);
                    itemHolder.summary.setAlpha(1);

                    // Set up the animator to animate the expansion.
                    ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f)
                            .setDuration(COLLAPSE_DURATION);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            Float value = (Float) animator.getAnimatedValue();

                            // For each value from 0 to 1, animate the various parts of the layout.
                            itemHolder.alarmItem.getLayoutParams().height =
                                    (int) (value * distance + startingHeight);
                            FrameLayout.LayoutParams expandParams = (FrameLayout.LayoutParams)
                                    itemHolder.expandArea.getLayoutParams();
                            expandParams.setMargins(
                                    0, (int) (value * distance), 0, mCollapseExpandHeight);
                            itemHolder.arrow.setRotation(180 * (1 - value));
                            itemHolder.hairLine.setTranslationY(-hairlineDistance * (1 - value));
                            itemHolder.summary.setAlpha(value);

                            itemHolder.alarmItem.requestLayout();
                        }
                    });
                    animator.setInterpolator(mCollapseInterpolator);
                    // Set everything to their final values when the animation's done.
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Set it back to wrap content since we'd explicitly set the height.
                            itemHolder.alarmItem.getLayoutParams().height =
                                    ViewGroup.LayoutParams.WRAP_CONTENT;

                            FrameLayout.LayoutParams expandParams = (FrameLayout.LayoutParams)
                                    itemHolder.expandArea.getLayoutParams();
                            expandParams.setMargins(0, 0, 0, mCollapseExpandHeight);

                            itemHolder.expandArea.setVisibility(View.GONE);
                            itemHolder.arrow.setRotation(0);
                            itemHolder.hairLine.setTranslationY(0);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            // TODO we may have to deal with cancelations of the animation.
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                        }
                    });
                    animator.start();

                    return false;
                }
            });
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        private View getViewById(long id) {
            for (int i = 0; i < mProgramsList.getCount(); i++) {
                View v = mProgramsList.getChildAt(i);
                if (v != null) {
                    ItemHolder h = (ItemHolder) (v.getTag());
                    if (h != null && h.alarm.id == id) {
                        return v;
                    }
                }
            }
            return null;
        }

        private void updateDaysOfWeekButtons(ItemHolder holder, DaysOfWeek daysOfWeek) {
            HashSet<Integer> setDays = daysOfWeek.getSetDays();
            for (int i = 0; i < 7; i++) {
                if (setDays.contains(DAY_ORDER[i])) {
                    turnOnDayOfWeek(holder, i);
                } else {
                    turnOffDayOfWeek(holder, i);
                }
            }
        }

        private void turnOffDayOfWeek(ItemHolder holder, int dayIndex) {
            holder.dayButtons[dayIndex].setChecked(false);
            holder.dayButtons[dayIndex].setTextColor(mColorDim);
            holder.dayButtons[dayIndex].setTypeface(mRobotoNormal);
        }

        private void turnOnDayOfWeek(ItemHolder holder, int dayIndex) {
            holder.dayButtons[dayIndex].setChecked(true);
            holder.dayButtons[dayIndex].setTextColor(mColorLit);
            holder.dayButtons[dayIndex].setTypeface(mRobotoBold);
        }

        public long[] getExpandedArray() {
            int index = 0;
            long[] ids = new long[mExpanded.size()];
            for (long id : mExpanded) {
                ids[index] = id;
                index++;
            }
            return ids;
        }

        public long[] getSelectedAlarmsArray() {
            int index = 0;
            long[] ids = new long[mSelectedAlarms.size()];
            for (long id : mSelectedAlarms) {
                ids[index] = id;
                index++;
            }
            return ids;
        }
    }

}