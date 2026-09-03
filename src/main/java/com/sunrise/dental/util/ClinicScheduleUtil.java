package com.sunrise.dental.util;

import com.sunrise.dental.exception.ValidationException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ClinicScheduleUtil {

    public static final LocalTime OPEN_TIME = LocalTime.of(8, 30);
    public static final LocalTime LAST_SLOT_TIME = LocalTime.of(17, 0);
    public static final int SLOT_MINUTES = 30;

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private ClinicScheduleUtil() {
    }

    public static List<LocalTime> getAppointmentSlots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime slot = OPEN_TIME;
        while (!slot.isAfter(LAST_SLOT_TIME)) {
            slots.add(slot);
            slot = slot.plusMinutes(SLOT_MINUTES);
        }
        return slots;
    }

    public static void validateAppointmentTime(LocalTime time) {
        if (time == null) {
            throw new ValidationException("Appointment time is required");
        }
        if (time.isBefore(OPEN_TIME) || time.isAfter(LAST_SLOT_TIME)) {
            throw new ValidationException("Appointments are only available between 8:30 AM and 5:00 PM");
        }
        if (time.getMinute() % SLOT_MINUTES != 0) {
            throw new ValidationException("Please choose a time slot in 30-minute intervals (e.g. 8:30 AM, 9:00 AM)");
        }
    }

    public static String formatDisplayTime(LocalTime time) {
        return time == null ? "" : time.format(DISPLAY_FORMAT);
    }
}