/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.marc.smartthermostatclient.DataStructure;

public final class TimeProgram {
    /**
     * Alarms start with an invalid id when it hasn't been saved to the database.
     */
    public static final long INVALID_ID = -1;
    public static long count = 0;
    private static final int DELETE_AFTER_USE_INDEX = 8;

    // Public fields
    public long id;
    public boolean enabled;
    public String hourStart;
    public String minutesStart;
    public String hourEnd;
    public String minutesEnd;
    public DaysOfWeek daysOfWeek;
    public boolean deleteAfterUse;

    public TimeProgram(String hourInit, String minutesInit, String hourEnd, String minutesEnd) {
        this.id = ++count;
        this.hourStart = hourInit;
        this.minutesStart = minutesInit;
        this.hourEnd = hourEnd;
        this.minutesEnd = minutesEnd;
        this.daysOfWeek = new DaysOfWeek(0);
        this.deleteAfterUse = false;
    }

    public TimeProgram() {
        this.id = ++count;
        this.hourStart = "12";
        this.minutesStart = "00";
        this.hourEnd = "13";
        this.minutesEnd = "00";
        this.daysOfWeek = new DaysOfWeek(0);
        this.deleteAfterUse = false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TimeProgram)) return false;
        final TimeProgram other = (TimeProgram) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public String toString() {
        return "Alarm{" +
                ", id=" + id +
                ", enabled=" + enabled +
                ", hour=" + hourStart +
                ", minutes=" + minutesStart +
                ", daysOfWeek=" + daysOfWeek +
                ", deleteAfterUse=" + deleteAfterUse +
                '}';
    }
    public String toJson() {
        return "{" +
                "\"id\":" +id +
                ", \"enabled\":" + "\""+enabled+"\"" +
                ", \"startHour\":" + hourStart +
                ", \"startMinutes\":" + Integer.parseInt(minutesStart) +
                ", \"endHour\":" +hourEnd +
                ", \"endMinutes\":" +Integer.parseInt(minutesEnd) +
                ", \"daysOfWeek\":" + daysOfWeek.getSetDays().toString() +
                '}';
    }
    public String startTimeToString() {
        return this.hourStart+":"+this.minutesStart;
    }

    public String endTimeToString() {
        return this.hourEnd+":"+this.minutesEnd;
    }
}
