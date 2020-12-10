package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddEditDeviceActivity extends AppCompatActivity {

    private static final String TAG = "AddDevice";
    private static final int MODE_CREATE = 1;
    private static final int MODE_EDIT = 2;

    private EditText textTitle;
    private EditText textContent;
    private Button buttonSave;
    private Button buttonCancel;

    private Room room;
    private Device device;
    private boolean needRefresh;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_device);

        this.textTitle = (EditText)this.findViewById(R.id.editText_note_title);
        this.textContent = (EditText)this.findViewById(R.id.editText_note_content);

        this.buttonSave = (Button)findViewById(R.id.button_save);
        this.buttonCancel = (Button)findViewById(R.id.button_cancel);

        this.buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                buttonSaveClicked();
            }
        });

        this.buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                buttonCancelClicked();
            }
        });

        Intent intent = this.getIntent();
        this.device = (Device) intent.getSerializableExtra("device");
        if(device== null)  {
            this.room =(Room) intent.getSerializableExtra("room");
            this.mode = MODE_CREATE;
        } else  {
            Log.i(TAG, "onCreate ... ");
            this.mode = MODE_EDIT;
            this.textTitle.setText(Integer.toString(device.getDeviceId()));
            this.textContent.setText(device.getDescription());
        }
    }
    // User Click on the Save button.
    public void buttonSaveClicked()  {
        MyDatabaseHelper db = new MyDatabaseHelper(this);

        String title = this.textTitle.getText().toString();
        String content = this.textContent.getText().toString();

        if(title.equals("") || content.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Please enter id & discription", Toast.LENGTH_LONG).show();
            return;
        }

        if(mode == MODE_CREATE ) {
            this.device= new Device(Integer.parseInt(title),this.room,content);
            db.addDevice(this.device);
        } else  {
            //this.device.setDeviceId(Integer.parseInt(title));
            this.device.setDescription(content);
            db.updateDevice(this.device);
        }

        this.needRefresh = true;

        // Back to MainActivity.
        this.onBackPressed();
    }

    // User Click on the Cancel button.
    public void buttonCancelClicked()  {
        // Do nothing, back MainActivity.
        this.onBackPressed();
    }

    // When completed this Activity,
    // Send feedback to the Activity called it.
    @Override
    public void finish() {

        // Create Intent
        Intent data = new Intent();

        // Request MainActivity refresh its ListView (or not).
        data.putExtra("needRefresh", needRefresh);

        // Set Result
        this.setResult(Activity.RESULT_OK, data);
        super.finish();
    }

}