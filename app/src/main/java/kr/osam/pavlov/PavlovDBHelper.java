package kr.osam.pavlov;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PavlovDBHelper extends SQLiteOpenHelper {


    // 데이터베이스
    private static final String DATABASE_NAME      = "missions.db";
    private static final int DATABASE_VERSION      = 1;

    // 테이블

    public static final String TABLE_NAME       = "MISSIONS";
    public static final String COLUMN_ID        = "ID";
    public static final String COLUMN_TYPE      = "TYPE";
    public static final String COLUMN_JSON      = "JSON";

    private static final String DATABASE_CREATE_TABLE = "create table "
            + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key, "
            + COLUMN_TYPE + " integer, "
            + COLUMN_JSON + " text);";

    public PavlovDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 앱을 삭제후 앱을 재설치하면 기존 DB파일은 앱 삭제시 지워지지 않기 때문에
        // 테이블이 이미 있다고 생성 에러남
        // 앱을 재설치시 데이터베이스를 삭제해줘야함.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(DATABASE_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}