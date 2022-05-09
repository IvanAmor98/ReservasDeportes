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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.databinding.BookingActivityBinding;
import com.example.reservasdeportes.model.BookingDTO;
import com.example.reservasdeportes.model.FacilityTypes;
import com.example.reservasdeportes.services.BookingService;
import com.example.reservasdeportes.services.NotificationService;
import com.example.reservasdeportes.model.FacilityDTO;
import com.example.reservasdeportes.model.LoggedUserDTO;
import com.example.reservasdeportes.ui.paypal.PaypalActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//Activity que permite crear una nueva reserva
public class BookingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private final String TAG = BookingActivity.class.toString();
    private BookingViewModel bookingViewModel;
    private LoggedUserDTO loggedUserDTO;
    private final BookingService bookingService = new BookingService();
    private TextView tvDatePicker;
    private ListView listType;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private BookingDTO bookingDTO;
    private int[] selectedDate;
    private boolean isToday;
    private ReservedTime selectedTime;
    private FacilityTypes selectedType;
    private final ArrayList<ReservedTime> reservedTimes = new ArrayList<>();
    private String[] availableFromTimes;
    private ArrayAdapter<String> fromAdapter;
    private ArrayAdapter<String> toAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.reservasdeportes.databinding.BookingActivityBinding binding = BookingActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loggedUserDTO = getIntent().getParcelableExtra("loggedUserDTO");

        bookingViewModel = new BookingViewModel(getResources().getStringArray(R.array.hours).length);

        FacilityDTO facilityDTO = getIntent().getParcelableExtra("facilityDTO");
        TextView tvTitle = binding.bookingTitle;
        tvDatePicker = binding.datePicker;
        listType = binding.lvType;
        spinnerFrom = binding.spinnerFrom;
        spinnerTo = binding.spinnerTo;
        Button btnSave = binding.saveButton;
        Button btnCancel = binding.cancelButton;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        tvDatePicker.setText(simpleDateFormat.format(date));
        tvTitle.setText(facilityDTO.getName());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        tvDatePicker.setOnClickListener(v -> showDatePicker());

        ArrayAdapter<FacilityTypes> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, facilityDTO.getFacilityTypes());
        listType.setAdapter(adapter);
        listType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedDate == null) return;

                selectedType = adapter.getItem(position);
                bookingService.getReservedTimes(BookingActivity.this, TAG, loggedUserDTO.getToken(), selectedDate, selectedType, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            reservedTimes.clear();
                            JSONArray jsonArray = result.getJSONArray("times");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONArray object = jsonArray.getJSONArray(i);
                                Calendar calendar = Calendar.getInstance();
                                calendar.clear();
                                calendar.setTimeInMillis(object.getLong(0));
                                ReservedTime reservedTime = new ReservedTime(calendar.get(Calendar.HOUR_OF_DAY));
                                calendar.setTimeInMillis(object.getLong(1));
                                reservedTime.setTimeTo(calendar.get(Calendar.HOUR_OF_DAY));
                                reservedTimes.add(reservedTime);
                            }
                            availableFromTimes = getAvailableFromTimes();
                            setAvailableFromHours();
                            bookingViewModel.typeChanged(selectedType, availableFromTimes);
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
        });

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;

                spinnerFrom.setSelection(position);
                if (!fromAdapter.getItem(position).equals(""))
                    selectedTime = new ReservedTime(Integer.parseInt(fromAdapter.getItem(position)));
                bookingViewModel.timeFromChanged(fromAdapter.getItem(position));
                setAvailableToHours();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;

                spinnerTo.setSelection(position, true);
                if (!toAdapter.getItem(position).equals(""))
                    selectedTime.setTimeTo(Integer.parseInt(toAdapter.getItem(position)));
                bookingViewModel.timeToChanged(fromAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerFrom.setEnabled(false);
        spinnerTo.setEnabled(false);

        btnSave.setOnClickListener(v -> {
            bookingDTO = new BookingDTO();
            bookingDTO.setUserId(getUser());
            bookingDTO.setFacilityId(facilityDTO.getId());
            bookingDTO.setFacilityName(facilityDTO.getName());
            bookingDTO.setType(selectedType);
            bookingDTO.setDate(selectedDate);
            bookingDTO.setTimeFrom(selectedTime.getTimeFrom());
            bookingDTO.setTimeTo(selectedTime.getTimeTo());
            bookingDTO.setPaid(false);

            bookingService.saveAppointment(this, TAG, loggedUserDTO.getToken(), bookingDTO, new ServerCallback() {
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

            if (bookingFormState.hasErrors()) {
                if (bookingFormState.getDateError() != null)
                    Toast.makeText(this, bookingFormState.getDateError(), Toast.LENGTH_LONG).show();
                if (bookingFormState.getTypeError() != null)
                    Toast.makeText(this, bookingFormState.getTypeError(), Toast.LENGTH_LONG).show();
                if (bookingFormState.getTimeFromError() != null)
                    Toast.makeText(this, bookingFormState.getTimeFromError(), Toast.LENGTH_LONG).show();
                if (bookingFormState.getTimeToError() != null)
                    Toast.makeText(this, bookingFormState.getTimeToError(), Toast.LENGTH_LONG).show();
                return;
            }

            if (!bookingFormState.isDateValid()) {
                listType.setEnabled(false);
            } else {
                tvDatePicker.setError(null);
                if (!listType.isEnabled()) {
                    listType.setEnabled(true);
                }
            }

            if (!bookingFormState.isTypeValid()) {
                spinnerFrom.setEnabled(false);
            } else {
                if (!spinnerFrom.isEnabled()) {
                    spinnerFrom.setEnabled(true);
                }
            }

            if (bookingFormState.isTimeFromValid()) {
                if (!spinnerTo.isEnabled()) {
                    spinnerTo.setEnabled(true);
                }
            } else {
                spinnerTo.setEnabled(false);
            }

            btnSave.setEnabled(bookingFormState.isTimeToValid());

        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                bookingViewModel.bookingDateChanged(
                        tvDatePicker.getText().toString()
                );
            }
        };
        tvDatePicker.addTextChangedListener(afterTextChangedListener);

    }

    private String getUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("logData", MODE_PRIVATE);
        return sharedPreferences.getString("_id", "");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = new int[]{year, monthOfYear, dayOfMonth};
        Calendar checkToday = Calendar.getInstance();
        isToday = checkToday.get(Calendar.YEAR) == selectedDate[0] &&
                checkToday.get(Calendar.MONTH) == selectedDate[1] &&
                checkToday.get(Calendar.DAY_OF_MONTH) == selectedDate[2];
        listType.clearChoices();
        listType.setEnabled(false);
        spinnerFrom.setEnabled(false);
        spinnerTo.setEnabled(false);
        tvDatePicker.setText(String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, monthOfYear + 1, year));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setMinDate(calendar);
        datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
    }

    private void setAvailableFromHours() {
        fromAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, availableFromTimes);
        spinnerFrom.setAdapter(fromAdapter);
    }

    private void setAvailableToHours() {
        toAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, getAvailableToTimes());
        spinnerTo.setAdapter(toAdapter);
    }

    private String[] getAvailableFromTimes() {
        ArrayList<String> tempAvailableTimes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.hours)));
        for (ReservedTime reservedTime: reservedTimes) {
            for (int i = reservedTime.getTimeFrom(); i < reservedTime.getTimeTo(); i++) {
                tempAvailableTimes.remove(String.valueOf(i));
            }
        }
        if (isToday) {
            ArrayList<String> availableTimes = new ArrayList<>();
            int now = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            for (String time : tempAvailableTimes) {
                if (time.equals("") || Integer.parseInt(time) > now) availableTimes.add(time);
            }
            return availableTimes.toArray(new String[0]);
        } else  {
            return tempAvailableTimes.toArray(new String[0]);
        }
    }

    private String[] getAvailableToTimes() {
        int maxTime = 21;
        for (ReservedTime reservedTime: reservedTimes) {
            if (reservedTime.getTimeFrom() < maxTime && reservedTime.getTimeFrom() > Integer.parseInt((String) spinnerFrom.getSelectedItem())) {
                maxTime = reservedTime.getTimeFrom();
            }
        }
        ArrayList<String> availableTimes = new ArrayList<>();
        for (int i = selectedTime.getTimeFrom() + 1; i <= maxTime; i++) {
            availableTimes.add(String.valueOf(i));
        }
        return availableTimes.toArray(new String[0]);
    }

    private void payUserCheck(){
        new AlertDialog.Builder(BookingActivity.this)
                .setMessage(getString(R.string.alert_pay))
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    Intent intent = new Intent(BookingActivity.this, PaypalActivity.class);
                    intent.putExtra("bookingDTO", bookingDTO);
                    intent.putExtra("loggedUserDTO", loggedUserDTO);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(android.R.string.no, (dialog, witchButton) -> finish()).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void alarmUserCheck() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, selectedDate[0]);
        calendar.set(Calendar.MONTH, selectedDate[1]);
        calendar.set(Calendar.DAY_OF_MONTH, selectedDate[2]);

        calendar.set(Calendar.HOUR_OF_DAY, selectedTime.getTimeFrom());
        calendar.set(Calendar.MINUTE, 0);
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
                        payUserCheck();
                    })
                    .setNegativeButton(android.R.string.no, (dialog, witchButton) -> payUserCheck()).show();
        } else {
            payUserCheck();
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
