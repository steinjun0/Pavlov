package kr.osam.pavlov;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import kr.osam.pavlov.Missons.Mission;

public class PavlovDBParser extends SQLiteOpenHelper {

    public PavlovDBParser(Context context){
        super(context, "Mission.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table Missions("
                +"idx integer primary key autoincrement, "
                +"title text not null,"
                +"serviceID integer not null,"
                +"isWorking integer not null,"
                +"JSONText text not null"
                +")";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public static List<Mission> readMission()
    {
        List<Mission> tmpList = new ArrayList<Mission>() {};



        return tmpList;
    }
}