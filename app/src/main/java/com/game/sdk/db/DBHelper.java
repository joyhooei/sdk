package com.game.sdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.util.Logger;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 保存登录的用户信息
		db.execSQL("create table if not exists " + UserLoginInfodao.TABLENAME
				+ "(_id integer primary key autoincrement,"
				+ UserLoginInfodao.USERNAME + " String,"
				+ UserLoginInfodao.PASSWORD + " String)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
