package com.example.reservasdeportes.ui.booking;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.databinding.BookingActivityBinding;
import com.example.reservasdeportes.services.BookingService;
import com.example.reservasdeportes.services.NotificationService;
import com.example.reservasdeportes.ui.facility.FacilityDTO;
import com.example.reservasdeportes.ui.login.LoggedUserData;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private final String TAG = BookingActivity.class.toString();
    private BookingViewModel bookingViewModel;
    private LoggedUserData loggedUserData;
    BookingService bookingService = new BookingService();
    BookingActivityBinding binding;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialogFrom;
    TimePickerDialog timePickerDialogTo;
    TextView tvDatePicker;
    TextView tvTimePickerFrom;
    TextView tvTimePickerTo;
    Button btnSave;
    BookingDTO bookingDTO;
    int[] selectedDate;
    ReservedTime selectedTime;
    ArrayList<ReservedTime> reservedTimes = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = BookingActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loggedUserData = getIntent().getParcelableExtra("loggedUserData");

        bookingViewModel = new BookingViewModel();

        FacilityDTO facilityDTO = getIntent().getParcelableExtra("facilityDTO");
        TextView tvTitle = binding.bookingTitle;
        tvDatePicker = binding.datePicker;
        tvTimePickerFrom = binding.timePickerFrom;
        tvTimePickerTo = binding.timePickerTo;
        btnSave = binding.saveButton;
        Button btnCancel = binding.cancelButton;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        tvDatePicker.setText(simpleDateFormat.format(date));
        tvTitle.setText(facilityDTO.getName());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        timePickerDialogFrom = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false);
        timePickerDialogFrom.setVersion(TimePickerDialog.Version.VERSION_2);
        timePickerDialogFrom.setTimeInterval(1, 5);

        timePickerDialogTo = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false);
        timePickerDialogTo.setVersion(TimePickerDialog.Version.VERSION_2);
        timePickerDialogTo.setTimeInterval(1, 5);

        tvDatePicker.setOnClickListener(v -> showDatePicker());
        tvTimePickerFrom.setOnClickListener(v -> showTimePickerFrom(calendar));
        tvTimePickerFrom.setEnabled(false);
        tvTimePickerTo.setOnClickListener(v -> showTimePickerTo());
        tvTimePickerTo.setEnabled(false);

        btnSave.setOnClickListener(v -> {
            bookingDTO = new BookingDTO();
            bookingDTO.setUserId(getUser());
            bookingDTO.setFacilityId(facilityDTO.getId());
            bookingDTO.setFacilityName(facilityDTO.getName());
            bookingDTO.setDate(selectedDate);
            bookingDTO.setTimeFrom(selectedTime.getTimeFrom());
            bookingDTO.setTimeTo(selectedTime.getTimeTo());
            bookingDTO.setPaid(false);

            bookingService.saveAppointment(this, TAG, loggedUserData.getToken(), bookingDTO, new ServerCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        if (result.getBoolean("success")) {
                            Toast.makeText(BookingActivity.this, R.string.booking_save_success, Toast.LENGTH_LONG).show();
                            alarmUserCheck();
                        } else {
                            Toast.makeText(BookingActivity.this, result.getString("errorData"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(BookingActivity.this, error, Toast.LENGTH_LONG).show();
                }
            });
        });

        btnSave.setEnabled(false);

        btnCancel.setOnClickListener(v -> finish() );

        bookingViewModel.getBookingFormState().observe(this, bookingFormState -> {
            if (bookingFormState == null) {
                return;
            }

            if (!bookingFormState.isDateValid()) {
                if (bookingFormState.getDateError() != null)
                    tvDatePicker.setError(getString(bookingFormState.getDateError()));
                tvTimePickerFrom.setEnabled(false);
            } else {
                timePickerFromSetDisabledDates();
                tvTimePickerFrom.setEnabled(true);
            }

            if (!bookingFormState.isTimeFromValid()) {
                if (bookingFormState.getTimeFromError() != null)
                    tvTimePickerFrom.setError(getString(bookingFormState.getTimeFromError()));
                tvTimePickerTo.setEnabled(false);
            } else {
                timePickerToSetDisabledDates();
                tvTimePickerTo.setEnabled(true);
            }

            if (!bookingFormState.isTimeToValid()) {
                if (bookingFormState.getTimeToError() != null)
                    tvTimePickerTo.setError(getString(bookingFormState.getTimeToError()));
                btnSave.setEnabled(false);
            } else {
                btnSave.setEnabled(true);
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                bookingViewModel.bookingDateChanged(
                        tvDatePicker.getText().toString(),
                        reservedTimes
                );
            }
        };

        TextWatcher afterTimeFromChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                bookingViewModel.bookingTimeFromChanged(tvTimePickerFrom.getText().toString());
            }
        };

        TextWatcher afterTimeToChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                bookingViewModel.bookingTimeToChanged(tvTimePickerTo.getText().toString());
            }
        };

        tvDatePicker.addTextChangedListener(afterTextChangedListener);
        tvTimePickerFrom.addTextChangedListener(afterTimeFromChangedListener);
        tvTimePickerTo.addTextChangedListener(afterTimeToChangedListener);
    }

    private String getUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("logData", MODE_PRIVATE);
        return sharedPreferences.getString("_id", "");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = new int[]{year, monthOfYear, dayOfMonth};
        bookingService.getReservedTimes(this, TAG, loggedUserData.getToken(), selectedDate, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray jsonArray = result.getJSONArray("times");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONArray object = jsonArray.getJSONArray(i);
                        Calendar from = Calendar.getInstance();
                        Calendar to = Calendar.getInstance();
                        from.setTimeInMillis(object.getLong(0));
                        to.setTimeInMillis(object.getLong(1));
                        reservedTimes.clear();
                        reservedTimes.add(new ReservedTime(
                                new int[]{from.get(Calendar.HOUR_OF_DAY), from.get(Calendar.MINUTE)},
                                new int[]{to.get(Calendar.HOUR_OF_DAY), to.get(Calendar.MINUTE)}));
                    }
                    tvDatePicker.setText(String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, monthOfYear + 1, year));
                    tvTimePickerFrom.setText("");
                    tvTimePickerTo.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(BookingActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String minutes = formatMinutes(minute);
        if (view == timePickerDialogFrom) {
            selectedTime = new ReservedTime(new int[]{hourOfDay, minute}, null);
            tvTimePickerFrom.setText(String.format(Locale.getDefault(), "%d:%s", hourOfDay, minutes));
            tvTimePickerTo.setText("");
        } else {
            selectedTime.setTimeTo(new int[]{hourOfDay, minute});
            tvTimePickerTo.setText(String.format(Locale.getDefault(), "%d:%s", hourOfDay, minutes));
            btnSave.setEnabled(true);
        }
    }

    private String formatMinutes(int minute) {
        return minute < 10 ? "0" + minute : "" + minute;
    }

    private String formatMinutes(String minute) {
        return minute.compareTo("10") < 0 ? "0" + minute : "" + minute;
    }

    private void showTimePickerFrom(Calendar calendar) {
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (calendar.get(Calendar.YEAR) == selectedDate[0] &&
                calendar.get(Calendar.MONTH) == selectedDate[1] &&
                calendar.get(Calendar.DAY_OF_MONTH) == selectedDate[2]) {
            if ((calendar.get(Calendar.HOUR_OF_DAY) == 20 && calendar.get(Calendar.MINUTE) > 30) ||
            calendar.get(Calendar.HOUR_OF_DAY) >= 21) {
                tvTimePickerFrom.setError("Hoy ya no es posible reservar, pruebe otro dia");
                return;
            } else {
                timePickerDialogFrom.setMinTime(calendar.get(Calendar.HOUR_OF_DAY), getMinMinute(calendar.get(Calendar.MINUTE)), 0);
            }
        } else {
            timePickerDialogFrom.setMinTime(9, 0, 0);
        }

        timePickerDialogFrom.setMaxTime(20, 30, 0);

        timePickerDialogFrom.show(getSupportFragmentManager(), "TimePickerDialog");
    }

    private void timePickerFromSetDisabledDates() {
        if (reservedTimes.size() > 0) {
            for (ReservedTime reservedTime: reservedTimes) {
                ArrayList<Timepoint> timepoints = new ArrayList<>();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, reservedTime.getTimeFrom()[0]);
                calendar.set(Calendar.MINUTE, reservedTime.getTimeFrom()[1]);
                calendar.add(Calendar.MINUTE, -25);

                int[] timeFrom = new int[]{calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)};
                int[] timeTo = reservedTime.getTimeTo();

                while (!Arrays.equals(timeFrom, timeTo)) {

                    calendar.set(Calendar.HOUR_OF_DAY, timeFrom[0]);
                    calendar.set(Calendar.MINUTE, timeFrom[1]);

                    timepoints.add(new Timepoint(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));

                    calendar.add(Calendar.MINUTE, 5);
                    timeFrom = new int[]{calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)};
                }
                timePickerDialogFrom.setDisabledTimes(timepoints.toArray(new Timepoint[0]));
            }
        }
    }

    private void showTimePickerTo() {
        timePickerDialogTo.show(getSupportFragmentManager(), "TimePickerDialog");
    }

    private void timePickerToSetDisabledDates() {
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.set(Calendar.HOUR_OF_DAY, selectedTime.getTimeFrom()[0]);
        calendarMin.set(Calendar.MINUTE, selectedTime.getTimeFrom()[1]);
        calendarMin.add(Calendar.MINUTE, 30);
        ReservedTime timeStartEnd = new ReservedTime(
                new int[]{calendarMin.get(Calendar.HOUR_OF_DAY), calendarMin.get(Calendar.MINUTE)},
                new int[]{21,0});

        if (reservedTimes.size() > 0) {
            for (ReservedTime reservedTime: reservedTimes) {
                if (timeStartEnd.compareTimeEndTimeStart(reservedTime) > 0) {
                    if (timeStartEnd.compareTimeFrom(reservedTime) < 1) {
                        timeStartEnd.setTimeTo(reservedTime.getTimeFrom());
                    }
                }
            }
        }

        Calendar calendarMax = Calendar.getInstance();
        calendarMax.set(Calendar.HOUR_OF_DAY, timeStartEnd.getTimeTo()[0]);
        calendarMax.set(Calendar.MINUTE, timeStartEnd.getTimeTo()[1]);

        timePickerDialogTo.setMinTime(calendarMin.get(Calendar.HOUR_OF_DAY), calendarMin.get(Calendar.MINUTE), 0);
        timePickerDialogTo.setMaxTime(calendarMax.get(Calendar.HOUR_OF_DAY), calendarMax.get(Calendar.MINUTE), 0);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        datePickerDialog = DatePickerDialog.newInstance(
                this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setMinDate(calendar);
        datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
    }

    private int getMinMinute(int minute) {
        String minString = String.valueOf(minute);
        minString = minString.substring(0, minString.length() - 1) + "5";
        return Integer.parseInt(minString) + 10;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void alarmUserCheck() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, selectedDate[0]);
        calendar.set(Calendar.MONTH, selectedDate[1]);
        calendar.set(Calendar.DAY_OF_MONTH, selectedDate[2]);

        calendar.set(Calendar.HOUR_OF_DAY, selectedTime.getTimeFrom()[0]);
        calendar.set(Calendar.MINUTE, selectedTime.getTimeFrom()[1]);
        calendar.add(Calendar.MINUTE, -30);

        Calendar now = Calendar.getInstance();

        if (calendar.compareTo(now) >= 0) {
            new AlertDialog.Builder(BookingActivity.this)
                    .setTitle(R.string.alert_title)
                    .setMessage(R.string.alert_alarm)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        setAlarm(calendar);
                        Toast.makeText(BookingActivity.this, String.format(Locale.getDefault(), "Alarm set on: %d/%d/%d %d:%d",
                                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)), Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, (dialog, witchButton) -> finish()).show();
        } else {
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarm(Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
