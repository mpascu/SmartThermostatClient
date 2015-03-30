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

public final class Alarm {
    /**
     * Alarms start with an invalid id when it hasn't been saved to the database.
     */
    public static final long INVALID_ID = -1;
    public static long count = 0;
    private static final int DELETE_AFTER_USE_INDEX = 8;

    // Public fields
    // TODO: Refactor instance names
    public long id;
    public boolean enabled;
    public String hourStart;
    public String minutesStart;
    public String hourEnd;
    public String minutesEnd;
    public DaysOfWeek daysOfWeek;
    public String label;
    public boolean deleteAfterUse;

    public Alarm(String hourInit, String minutesInit, String hourEnd,String minutesEnd) {
        this.id = ++count;
        this.hourStart = hourInit;
        this.minutesStart = minutesInit;
        this.hourEnd = hourEnd;
        this.minutesEnd = minutesEnd;
        this.daysOfWeek = new DaysOfWeek(0);
        this.label = "";
        this.deleteAfterUse = false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Alarm)) return false;
        final Alarm other = (Alarm) o;
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
                ", label='" + label + '\'' +
                ", deleteAfterUse=" + deleteAfterUse +
                '}';
    }

    public String startTimeToString() {
        return this.hourStart+":"+this.minutesStart;
    }

    public String endTimeToString() {
        return this.hourEnd+":"+this.minutesEnd;
    }
}
