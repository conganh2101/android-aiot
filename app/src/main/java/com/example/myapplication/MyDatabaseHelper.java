package com.example.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.FontsContract;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "Device_Manager";

    // Table name: Room.
    private static final String TABLE_ROOM = "Room";

    private static final String COLUMN_ROOM_ID ="Room_Id";
    private static final String COLUMN_ROOM_DESCRIPTION ="Description";


    //Table name: Device
    private static final String TABLE_DEVICE="DEVICE";

    private static final String COLUMN_DEVICE_ID ="Device_Id";
    private static final String COLUMN_FK_ROOM_ID="Link_Id";
    private static final String COLUMN_DEVICE_DESCRIPTION="Description";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "MyDatabaseHelper.onCreate ... ");
        // Script.
        String script_room_table = "CREATE TABLE " + TABLE_ROOM + "("
                + COLUMN_ROOM_ID + " INTEGER PRIMARY KEY ,"
                + COLUMN_ROOM_DESCRIPTION + " TEXT" + ")";
        String script_device_table = "CREATE TABLE " + TABLE_DEVICE + "("
                + COLUMN_DEVICE_ID + " INTEGER PRIMARY KEY ,"
                + COLUMN_FK_ROOM_ID + " INTEGER REFERENCES " + TABLE_ROOM + "(" +COLUMN_ROOM_ID+ ")" +","
                + COLUMN_DEVICE_DESCRIPTION + " TEXT" + ")";
        // Execute Script.
        db.execSQL(script_room_table);
        db.execSQL(script_device_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "MyDatabaseHelper.onUpgrade ... ");
        // Drop older table if existed
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOM);
            // Create tables again
            onCreate(db);
        }
    }

    public void createDefaultRoomIfNeed()  {
        int count = this.getRoomsCount();
        if(count ==0 ) {
            Room room1 = new Room("Bed Room");
            Room room2 = new Room("Living Room");
            Room room3 = new Room("Dining Room");
            Room room4 = new Room("Bath Room");
            this.addRoom(room1);
            this.addRoom(room2);
            this.addRoom(room3);
            this.addRoom(room4);
        }
    }

//    public void createDefaultFormationIfNeed(){
//        int count = this.getFormationsCount();
//        if(count==0){
//            Formation formation =new Formation("Demo DASCAM mobile");
//            formation.setFormation_Id((int) this.addFormation(formation));
//
//            Camera camera1 = new Camera("rtsp://192.168.1.15:8555/play-file?/home/map/10.mp4",
//                    "View 1");
//            Camera camera2 = new Camera("rtsp://192.168.1.15:8555/play-file?/home/map/0event.mp4",
//                    "View 2");
//            Camera camera3 = new Camera("rtsp://192.168.1.15:8555/play-file?/home/map/1event.mp4",
//                    "View 3");
//            Camera camera4 = new Camera("rtsp://192.168.1.15:8555/play-file?/home/map/2event.mp4",
//                    "View 4");
//            camera1.setLink_Id((int) this.addLink(camera1));
//            camera2.setLink_Id((int) this.addLink(camera2));
//            camera3.setLink_Id((int) this.addLink(camera3));
//            camera4.setLink_Id((int) this.addLink(camera4));
//
//            Position position1=new Position(camera1,formation,1);
//            Position position2=new Position(camera2,formation,2);
//            Position position3=new Position(camera3,formation,3);
//            Position position4=new Position(camera4,formation,4);
//
//            this.addPosition(position1);
//            this.addPosition(position2);
//            this.addPosition(position3);
//            this.addPosition(position4);
//        }
//    }

    public long addRoom(Room room) {//can have error when return row_id
        Log.i(TAG, "MyDatabaseHelper.addNote ... " + room.getDescription());

        long new_Id;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_DESCRIPTION, room.getDescription());

        // Inserting Row
        new_Id=db.insert(TABLE_ROOM, null, values);

        // Closing database connection
        db.close();
        return new_Id;
    }


    public void addDevice(Device device){
        Log.i(TAG, "MyDatabaseHelper.addDevice ... " + device.getDescription());
        SQLiteDatabase db =this.getWritableDatabase();

        ContentValues values =new ContentValues();
        values.put(COLUMN_DEVICE_ID,device.getDeviceId());
        values.put(COLUMN_FK_ROOM_ID,device.getRoom().getRoomId());
        values.put(COLUMN_DEVICE_DESCRIPTION,device.getDescription());
        try {
            db.insert(TABLE_DEVICE, null, values);
        }catch (SQLException e){
            Log.i(TAG, String.valueOf(e));
        }
        db.close();
    }

    public Room getRoom(int id) {
        Log.i(TAG, "MyDatabaseHelper.getNote ... " + id);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ROOM, new String[] { COLUMN_ROOM_ID,
                        COLUMN_ROOM_DESCRIPTION }, COLUMN_ROOM_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Room room = new Room(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));
        // return room
        return room;
    }

    public List<Device> getAllDevByRoom(Room room) {
        Log.i(TAG, "MyDatabaseHelper.getAllNotes ... " );

        List<Device> linkList = new ArrayList<Device>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DEVICE  + " WHERE " + TABLE_DEVICE + "." + COLUMN_FK_ROOM_ID + " = " + room.getRoomId();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Device device = new Device();
                device.setDeviceId(cursor.getInt(0));
                Room room1=new Room();
                room1.setRoomId(cursor.getInt(1));
                device.setRoom(room1);
                device.setDescription(cursor.getString(2));
//                camera.setLink_Id(Integer.parseInt(cursor.getString(0)));
//                camera.setRTSP_Link(cursor.getString(1));
//                camera.setView_Id(cursor.getString(2));
                // Adding note to list
                linkList.add(device);
            } while (cursor.moveToNext());
        }

        // return note list
        return linkList;
    }

    public List<Device> getAllDevs() {
        Log.i(TAG, "MyDatabaseHelper.getAllNotes ... " );

        List<Device> linkList = new ArrayList<Device>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DEVICE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Device device = new Device();
                device.setDeviceId(Integer.parseInt(cursor.getString(0)));
                device.setDescription(cursor.getString(2));
                // Adding note to list
                linkList.add(device);
            } while (cursor.moveToNext());
        }

        // return note list
        return linkList;
    }
    public List<Room> getAllRooms() {
        Log.i(TAG, "MyDatabaseHelper.getAllNotes ... " );

        List<Room> linkList = new ArrayList<Room>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ROOM;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Room room = new Room();
                room.setRoomId(Integer.parseInt(cursor.getString(0)));
                room.setDescription(cursor.getString(1));
                // Adding note to list
                linkList.add(room);
            } while (cursor.moveToNext());
        }

        // return note list
        return linkList;
    }

//    public List<Camera> findLinkByPosition(Formation formation){
//        List<Camera> linkList = new ArrayList<Camera>();
//        String findLinkByFormation= "SELECT "
//                + TABLE_CAM + "." + COLUMN_LINK_ID + ", "
//                + TABLE_CAM + "." + COLUMN_RTSP_LINK + ", "
//                + TABLE_CAM + "." + COLUMN_VIEW_ID + ", "
//                + TABLE_POSITION + "." + COLUMN_POSITION
//                + " FROM "
//                + TABLE_CAM
//                + " INNER JOIN " + TABLE_POSITION +" ON " + TABLE_CAM + "." + COLUMN_LINK_ID + " = " + TABLE_POSITION + "." + COLUMN_F_LINK_ID
//                + " WHERE " + TABLE_POSITION + "." + COLUMN_F_FORMATION_ID + " = " + formation.getFormation_Id()
//                + " ORDER BY " + TABLE_POSITION + "." + COLUMN_POSITION ;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor =db.rawQuery(findLinkByFormation,null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                Camera camera = new Camera();
//                camera.setLink_Id(Integer.parseInt(cursor.getString(0)));
//                camera.setRTSP_Link(cursor.getString(1));
//                camera.setView_Id(cursor.getString(2));
//                // Adding note to list
//                linkList.add(camera);
//            } while (cursor.moveToNext());
//        }
//
//
//        return linkList;
//    }


    public int getRoomsCount() {
        Log.i(TAG, "MyDatabaseHelper.getNotesCount ... " );

        String countQuery = "SELECT  * FROM " + TABLE_ROOM;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    public int getDeviceCount(Room room) {
        Log.i(TAG, "MyDatabaseHelper.getNotesCount ... " );

        String countQuery  = "SELECT  * FROM " + TABLE_DEVICE  + " WHERE " + TABLE_DEVICE + "." + COLUMN_FK_ROOM_ID + " = " + room.getRoomId();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }


    public int updateRoom(Room room) {
        Log.i(TAG, "MyDatabaseHelper.updateNote ... "  + room.getDescription());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_DESCRIPTION, room.getDescription());


        // updating row
        return db.update(TABLE_ROOM, values, COLUMN_ROOM_ID + " = ?",
                new String[]{String.valueOf(room.getRoomId())});
    }


    public int updateDevice(Device device){
        Log.i(TAG, "MyDatabaseHelper.updatePosition ... " +device.getDescription() );
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(COLUMN_FORMATION_ID, formation.getFormation_Id());
       // values.put(COLUMN_DEVICE_ID, device.getDeviceId());
        values.put(COLUMN_DEVICE_DESCRIPTION,device.getDescription());

        // updating row
        return db.update(TABLE_DEVICE,
                values,
                COLUMN_DEVICE_ID + " = ?",
                new String[]{String.valueOf(device.getDeviceId())});
    }
    public void deleteRoom(Room room) {
        Log.i(TAG, "MyDatabaseHelper.updateNote ... " + room.getDescription() );

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DEVICE, COLUMN_FK_ROOM_ID + " = ?",
                new String[] { String.valueOf(room.getRoomId()) });
        db.delete(TABLE_ROOM, COLUMN_ROOM_ID + " = ?",
                new String[] { String.valueOf(room.getRoomId()) });
        db.close();
    }

    public void deleteDevice(Device device){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DEVICE, COLUMN_DEVICE_ID + " = ?",
                new String[] { String.valueOf(device.getDeviceId()) });
        db.close();

    }
}

