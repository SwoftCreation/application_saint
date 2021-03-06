package com.reservation.saintapl;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ReserveActivity extends AppCompatActivity {
    ConstraintLayout reserveActivityLayout;
    EditText askEditText;
    EditText customerName;
    Spinner reserve_people;
    TextView reserve_final;
    String date;
    String final_reservation;
    int reserve_number;
    boolean covidCheck;
    Button date_button;
    Button time_button;
    CheckBox checkBox_people;
    CheckBox checkBox_corona;
    Button enter_button;
    boolean dateChecked = false;
    boolean timeChecked = false;
    boolean isReservationDone = false;

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "yyyy/MM/dd";
            SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.KOREA);
            date = dateFormat.format(myCalendar.getTime());
            final_reservation = date;
            reserve_final.setText(final_reservation);
        }
    };

    public void exit(){
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if(isReservationDone == false){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("?????? ????????????. ????????? ????????????????");
        builder.setCancelable(false);

        builder.setPositiveButton("???. ??????????????? ???????????????", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exit();
            }
        });

        builder.setNegativeButton("????????????", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();}
        else{
            super.onBackPressed();
        }

    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        askEditText = (EditText) findViewById(R.id.askedMessage);
        customerName = (EditText) findViewById(R.id.customerName);


        reserveActivityLayout = (ConstraintLayout) findViewById(R.id.layout2);
        reserve_people = (Spinner) findViewById(R.id.customerNum);
        reserve_final = (TextView) findViewById(R.id.reserve_finalView);

        // ???????????? ????????????
        checkBox_people = (CheckBox) findViewById(R.id.isChecked1);
        checkBox_corona = (CheckBox) findViewById(R.id.isChecked2);
        covidCheck = false;


        Button homeserviceButton = (Button) findViewById(R.id.home_service_button);
        enter_button = (Button) findViewById(R.id.decideButton);
        date_button = (Button) findViewById(R.id.dateButton);
        time_button = (Button) findViewById(R.id.timeButton);
        // ?????? ????????? ????????? ??????
        reserve_people.setSelection(0);
        //String howManyPerson = reserve_people.getSelectedItem().toString();


        ArrayAdapter peopleAdapter = ArrayAdapter.createFromResource(this,R.array.reserve_people, android.R.layout.simple_spinner_dropdown_item);
        reserve_people.setAdapter(peopleAdapter);

        // ?????? ?????? ??????
        enter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPopup();
            }
        });

        // ?????? ?????? ??????
        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ReserveActivity.this, myDatePicker, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH) ).show();
                        date_button.setText("????????????");
                        dateChecked = true;

            }
        });

        // ?????? ?????? ??????
        time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mtimePicker;
                mtimePicker = new TimePickerDialog(ReserveActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                        if(selectedHour > 20){
                            Toast.makeText(getApplicationContext(), "???????????? ??????????????? ?????? ??????????????????", Toast.LENGTH_LONG);
                        }
                        final_reservation += " "+ selectedHour +"??? "+selectedMinute+"??? ???????????????";
                        reserve_final.setText(final_reservation);
                        time_button.setText("????????????");
                        timeChecked = true;
                    }
                }, hour, minute, true);

                mtimePicker.setTitle("?????? ????????? ???????????????");
                mtimePicker.show();

            }
        });

    }




    void enterPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("????????? ???????????????????????????????");
        builder.setCancelable(false);

        builder.setPositiveButton("???????????? ???????????? ??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean dateTime = false;
                if(dateChecked == true && timeChecked)
                    dateTime = true;

                Information person = new Information();
                reserve_number = person.getReserveNum();

                if(checkBox_corona.isChecked() && checkBox_people.isChecked())
                    covidCheck = true;

                if((customerName.getText().toString() != null) && (reserve_people.getSelectedItem().toString() != Integer.toString(0) )
                        && dateTime != false){
                if(covidCheck == true){
                    sendingMessage();
                    Toast.makeText(getApplicationContext(), "????????? ??????????????? \n"+ reserve_number+" ?????????",Toast.LENGTH_LONG).show();
                    customerName.setText(null);
                    reserve_people.setSelection(0);
                    askEditText.setText(null);
                    enter_button.setText("????????? ?????????????????????");
                    isReservationDone = true;
                } else{
                    Toast.makeText(getApplicationContext(), "???????????? ?????? ????????? ???????????? ???????????????", Toast.LENGTH_LONG).show();
                }}else {
                    Toast.makeText(getApplicationContext(), "????????? ????????? ?????????\n?????? ???????????? ???????????????\n??????????????????", Toast.LENGTH_LONG).show();


            }}

        });

        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "???????????? ?????????????????????", Toast.LENGTH_LONG).show();
            }
        });

        builder.show();
    }



    void sendingMessage(){
        SmsManager smsManager = SmsManager.getDefault();


        String phoneNum = "01097499705";
        String askedMessage = askEditText.getText().toString();
        String peopleNum = reserve_people.getSelectedItem().toString();
        String message_content = "!!!**????????? ??????????????????**!!!"
                +"\n????????????: "+ reserve_number
                +"\n??????: " +customerName.getText().toString()+
                "\n?????????: "+peopleNum
                +"\n????????????: "+final_reservation
                +"\n\n***????????????***\n" + askedMessage;

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList = smsManager.divideMessage(message_content);
        int smsNum = arrayList.size();

        PendingIntent sentIntent = PendingIntent.getBroadcast(this,
                0, new Intent("SMS_SENT_ACTION"), 0);

        PendingIntent deliverIntent = PendingIntent.getBroadcast(this,
                0, new Intent("SMS_DELIVERED_ACTION"), 0);


        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        // ?????? ??????
                        makeSnackbar(ReserveActivity.this, reserveActivityLayout, "??????????????? ?????????????????????. ????????? ?????? ????????? ???????????????. \n???????????? "+reserve_number, Snackbar.LENGTH_LONG);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        // ?????? ??????
                        Toast.makeText(getApplicationContext(), "?????? ??????", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // ????????? ?????? ??????
                        Toast.makeText(getApplicationContext(), "????????? ????????? ????????????", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        // ?????? ??????
                        Toast.makeText(getApplicationContext(), "??????(Radio)??? ??????????????????", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        // PDU ??????
                        Toast.makeText(getApplicationContext(), "PDU Null", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT_ACTION"));


        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        // ?????? ??????
//                        Toast.makeText(getApplicationContext(), "SMS ?????? ??????", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // ?????? ??????
                        Toast.makeText(getApplicationContext(), "SMS ?????? ??????", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_DELIVERED_ACTION"));

        try{
            for( int i = 0 ; i < smsNum ; i++ ){
                smsManager.sendTextMessage(phoneNum,null,arrayList.get(i), sentIntent, deliverIntent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void makeSnackbar(Context context, View view ,String message, int durationTime){
        Snackbar.make(context, view, message, durationTime).show();

    }

}










































































