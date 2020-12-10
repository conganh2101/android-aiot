package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private ListView listView;
    private Button btnAddDevice;
    private Button btnAddLink;
    private Room room;
    private TextView roomTitle;
    private static final int MENU_ITEM_LAUNCH = 111;
    private static final int MENU_ITEM_EDIT = 222;
    private static final int MENU_ITEM_CREATE = 333;
    private static final int MENU_ITEM_DELETE = 444;

    private static final int MY_REQUEST_CODE = 1001;
    private static final int MY_REQUEST_PLAYING_CODE = 1002;

    private final List<Device> linkList = new ArrayList<Device>();
//    private final List<Camera> camList = new ArrayList<Camera>();
//    private final List<Position> posList = new ArrayList<Position>();
    private ArrayAdapter<Device> listViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

//        this.btnAddLink=findViewById(R.id.button2);
//        this.btnAddLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity2.this, AddEditDeviceActivity.class);
//                // Start AddEditNoteActivity, (with feedback).
//                startActivity(intent);
//            }
//        });

        this.roomTitle=findViewById(R.id.textView);
        this.btnAddDevice=findViewById(R.id.button);
        this.btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDeviceForRoom();
//                Intent intent=new Intent(this,AddEditDeviceActivity.class);
//                intent.putExtra("room",this.room);
//                startActivityForResult(intent,MY_REQUEST_CODE);
            }
        });



        // Get ListView object from xml
        this.listView = (ListView) findViewById(R.id.listView);

        MyDatabaseHelper db = new MyDatabaseHelper(this);

        Intent intent = this.getIntent();
        this.room = (Room) intent.getSerializableExtra("room");

        this.roomTitle.setText(this.room.getDescription());

        List<Device> list=  db.getAllDevByRoom(this.room);
        this.linkList.addAll(list);


        // Define a new Adapter
        // 1 - Context
        // 2 - Layout for the row
        // 3 - ID of the TextView to which the data is written
        // 4 - the List of data

        this.listViewAdapter = new ArrayAdapter<Device>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, this.linkList);

        // Assign adapter to ListView
        this.listView.setAdapter(this.listViewAdapter);


        // Register the ListView for Context menu
        registerForContextMenu(this.listView);
    }

    public void addDeviceForRoom(){
        Intent intent = new Intent(this, AddEditDeviceActivity.class);
        intent.putExtra("room",this.room);
        // Start AddEditNoteActivity, (with feedback).
        this.startActivityForResult(intent, MY_REQUEST_CODE);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo)    {

        super.onCreateContextMenu(menu, view, menuInfo);
        menu.setHeaderTitle("Select The Action");

        // groupId, itemId, order, title
        menu.add(0, MENU_ITEM_LAUNCH , 0, "Device control");
        menu.add(0, MENU_ITEM_CREATE , 1, "Create Device");
        menu.add(0, MENU_ITEM_EDIT , 2, "Edit Device");
        menu.add(0, MENU_ITEM_DELETE, 4, "Delete Device");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Device selectedDevice = (Device) this.listView.getItemAtPosition(info.position);

        if(item.getItemId() == MENU_ITEM_LAUNCH){
//            Intent intent = new Intent(this,Tutorial5.class);
//            intent.putExtra("device",selectedDevice);
//            //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            this.startActivity(intent);
        }
        else if(item.getItemId() == MENU_ITEM_CREATE){
            Intent intent = new Intent(this, AddEditDeviceActivity.class);
            intent.putExtra("room",this.room);
            // Start AddEditNoteActivity, (with feedback).
            this.startActivityForResult(intent, MY_REQUEST_CODE);
        }
        else if(item.getItemId() == MENU_ITEM_EDIT ){
            Intent intent = new Intent(this, AddEditDeviceActivity.class);
            intent.putExtra("device", selectedDevice);
            // Start AddEditNoteActivity, (with feedback).
            this.startActivityForResult(intent,MY_REQUEST_CODE);
        }
        else if(item.getItemId() == MENU_ITEM_DELETE){
            // Ask before deleting.
            new AlertDialog.Builder(this)
                    .setMessage(selectedDevice.getDeviceId()+":"+selectedDevice.getDescription()+". Are you sure you want to delete?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteDevice(selectedDevice);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        else {
            return false;
        }
        return true;
    }
    // Delete a record
    private void deleteDevice(Device device)  {
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        db.deleteDevice(device);
        this.linkList.remove(device);
        // Refresh ListView.
        this.listViewAdapter.notifyDataSetChanged();
    }

    // When AddEditNoteActivity completed, it sends feedback.
    // (If you start it using startActivityForResult ())
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == MY_REQUEST_CODE ) {
            boolean needRefresh = data.getBooleanExtra("needRefresh", true);
            // Refresh ListView
            if (needRefresh) {
                this.linkList.clear();
                MyDatabaseHelper db = new MyDatabaseHelper(this);
                List<Device> list = db.getAllDevByRoom(this.room);
                this.linkList.addAll(list);
                // Notify the data change (To refresh the ListView).
                this.listViewAdapter.notifyDataSetChanged();
            }
        }
    }
}