/**
 * SQLiteOpenHelper API for Android
 *
 * API Written by: Magarzo, Randolf Josef V.
 * Source: https://github.com/idclxvii/SharpFixAndroid/blob/master/src/tk/idclxvii/sharpfixandroid/SQLiteHelper.java
 *
 * This API can be reused as long as you edit the parts such as the OnCreate, OnUpgrade and OnDowngrade methods
 * since these methods use string literals for creating or updating the database. Also, check the constants and change
 * them according to your need
 *
 * How to use:
 *      Create your data models with the following pattern:
 *
 *          All data models should have this enum, listing all the data of the model
 *
 *              public enum fields(){
 *                  Field1, Field2, Field3
 *             }
 *
 *          Although you can name your field pretty much anything, please do strictly note that listing the names of each
 *          field your model has inside the enum should start with a Caps. If for example your field's name is student_name,
 *          you should list it inside the enum as Student_name
 *
 *          All data models should have this method, exactly as it is written below:
 *
 *              public String[] getFields(){
                    String[] f = new String[fields.values().length];
                    for(int x=0; x < f.length; x++ ){
                        f[x] = fields.values()[x].toString();

                    }
                    return f;
                }

 *          This method is needed by the API in order to define the fields independently without the user explicitly providing it
 *
 *          Create setters and getters following the pattern get + field name. For example:
 *
 *              public Integer getId(){ return this.id}
 *              public void setId(Integer num) { this.id = num; }
 *
 *              The setter and getter above can be understood as methods related to field:
 *
 *                  private Integer id = null;
 *
 *              which has been declared in model's enum as:
 *
 *                  public enum fields(){
 *                      Id
 *                  }
 *
 *          Also note that always use Wrappers (Integer for int, Long for long, etc) when specifying field members of your
 *          data model.
 *
 *          Create an enum containing the list of tables as they are nemed in your database. Example:
 *
 *              public enum Tables {
                    config,
                    contacts,
                    conversation,
                    sms

                }

 *          This enum will be the argument you will provide on methods inside this API that asks for Table, for instance
 *          insert(Tables.config, ....) will insert a data to table named 'config'.
 *
 *
 *
 }
 */

package ph.edu.sscrmnl.privatesms.util;

/**
 * Created by IDcLxViI on 2/7/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ph.edu.sscrmnl.privatesms.databasemodel.Models;
import ph.edu.sscrmnl.privatesms.databasemodel.Tables;


public class SQLiteHelper extends SQLiteOpenHelper {

    private class SQLiteAsyncTask<Params, Progress, Result>
            extends AsyncTask<Params, Progress, Result>{

        /*
         * AsyncTask < Params, Progress, Result >
          *
          *
          *	This method runs the task in parallel. This method must be
         *	invoked in the UI - thread.
         *	this.executeOnExecutor(THREAD_POOL_EXECUTOR, Params toinks);
         */
        protected SQLiteDatabase db;
        protected Tables table;
        protected Class<?> cls;

        SQLiteAsyncTask(){

        }

        SQLiteAsyncTask(Tables table, Class<?> cls, SQLiteDatabase db){
            this.table = table;
            this.cls = cls;
            this.db =  db;

        }


        @Override
        protected void onCancelled(Result result){
            // shit happens, learn to adapt
        }

        @Override
        protected Result doInBackground(Params... params) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected void onPostExecute(Result returnedResult){

			/*
			 * This runs on the UI thread and is called after doInBackground finishes,
			 * 	doInBackground passes its return value to this method after finishing its task
			 */


        }

        @Override
        protected void onProgressUpdate(Progress... value){
			/*
			 * invoked on the UI thread after a call to publishProgress. The timing of the execution is undefined.
			 * This method is used to display any form of progress in the user interface while the background computation
			 *  is still executing. For instance, it can be used to animate a progress bar or show logs in a text field.
			 */

        }

        @Override
        protected void onPreExecute(){
			/*
			 *  runs on the UI thread before doInBackground is called
			 *	generally used to prepare for progress bars, etc.
			 */
        }


    }


    // Logcat tag and switch
    private final String TAG = this.getClass().getSimpleName();
    private static boolean LOGCAT = true;

    // Database Version
    private static final int DATABASE_VERSION = 3;
    private static final double DATABASE_VERSION_REVISION = 0.0;




    // Database Name
    private static final String DATABASE_NAME = "privatesms.db";

    public static String getDbName(){
        return DATABASE_NAME;
    }


    public static String getDatabaseVersion(){
        return (DATABASE_NAME + " v "
                + DATABASE_VERSION + "." + DATABASE_VERSION_REVISION);
    }

    // Table Names
    private static final String[]  TABLES = {"config", "contacts", "conversation", "sms"};

    // config
    private static final String CREATE_CONFIG = "CREATE TABLE" +
            " config ( pin TEXT PRIMARY KEY )";

    // contacts
    private static final String CREATE_CONTACTS = "CREATE TABLE" +
            " contacts ( address TEXT PRIMARY KEY, name TEXT NOT NULL )";

    // conversation
    private static final String CREATE_CONVERSATION = "CREATE TABLE" +
            " conversation ( id INTEGER PRIMARY KEY, address TEXT NOT NULL, count INTEGER DEFAULT 0, "+
            " lastmod BIGINT NOT NULL)";

    // sms
    private static final String CREATE_SMS = "CREATE TABLE sms (" +
            "id INTEGER PRIMARY KEY, " +
            "conversation INTEGER NOT NULL, " +
            "address TEXT, " +
            "body TEXT NOT NULL, " +
            "type INTEGER, " +
            "status INTEGER DEFAULT - 1, " +
            "datetime BIGINT NOT NULL, " +
            //"PRIMARY KEY (id, conversation), " +
            "FOREIGN KEY (address) REFERENCES contacts(address), " +
            "FOREIGN KEY (conversation) REFERENCES conversation(id) )";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables

        db.execSQL(CREATE_CONFIG);
        db.execSQL(CREATE_CONTACTS);
        db.execSQL(CREATE_CONVERSATION);
        db.execSQL(CREATE_SMS);

        if(LOGCAT){
            Log.d(TAG, "Required database tables successfully created.");
            Log.d(TAG, "Recovering previous database data . . .");
        }

        try{
            // default insertions
        	/*
        	this.insert(Tables.accounts_info, new ModelAccountsInfo("idclxvii","6582ceacd9ae19c9147b72e12ea344a1"),db);
        	ModelAccountsInfo mai =
        			(ModelAccountsInfo)this.select(Tables.accounts_info, ModelAccountsInfo.class,
        					new Object[][]{{"login","idclxvii"},{"password", "6582ceacd9ae19c9147b72e12ea344a1"}},db);
        	this.insert(Tables.preferences, new ModelPreferences(mai.getId(), 1,1,1,1,0),db);
        	*/
            Object[][] res = new Object[Tables.values().length][];
            this.result.toArray(res);
            for(int x = 0; x < Tables.values().length; x++){
                for(int y = 0; y < res[x].length; y++){
                    try{
                        this.insert(Tables.values()[x], res[x][y],db);
                        if(LOGCAT){
                            Log.d(TAG, "Successfully recovered data of " + Tables.values()[x].toString());
                        }
                    }catch(Exception ee){
                        if(LOGCAT){
                            StackTraceElement[] st = ee.getStackTrace();
                            for(int z= 0; z <st.length; z++){
                                Log.w(TAG, st[z].toString());

                            }
                        }
                    }

                }
            }

        }catch(Exception e){
            if(LOGCAT){
                try{
                    StackTraceElement[] st = e.getStackTrace();
                    for(int y= 0; y < st.length; y++){
                        Log.w(TAG, st[y].toString());
                    }
                }catch(Exception ee){
                    Log.e(TAG, "2nd Level Exception Caught!\nThe system cannot get the Stacktrace requested at:\n"
                            +"onCreate(SQLiteDatabase)");
                }
            }
        }
    }

    private List<Object[]> result = new ArrayList<Object[]>();

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(LOGCAT){
            Log.d(TAG, "Database Upgrade required!\nOld Version: " + oldVersion + " New Version: " +newVersion);
            Log.d(TAG, "Recovering old database contents . . .");
        }

        for(int x = 0; x < Tables.values().length; x++){
            try{
                //ModelAccountsInfo[] mai = (ModelAccountsInfo[]) this.selectAll(Tables.accounts_info, ModelAccountsInfo.class);
                    Object[] res =  this.selectAll(Tables.values()[x],
                            Class.forName("ph.edu.sscrmnl.privatesms.databasemodel."+ Models.values()[x].toString()), db);
                    //List<Object> r = new ArrayList<Object>();
                    //r.add(res);
                    this.result.add(res);
                    if(LOGCAT){
                        Log.d(TAG, (res.length > 0) ? "Successfully fetched recoverable data from " +Tables.values()[x].toString() :
                                        "No recoverable data from " +Tables.values()[x].toString()
                        );
                    }

            }catch(SQLiteException sqle){
                if(LOGCAT){
                    StackTraceElement[] st = sqle.getStackTrace();
                    for(int y= 0; y <st.length; y++){
                        Log.w(TAG, st[y].toString());

                    }
                }

            }catch(Exception e){
                if(LOGCAT){
                    try{
                        StackTraceElement[] st = e.getStackTrace();
                        for(int y= 0; y <st.length; y++){
                            Log.w(TAG, st[y].toString());

                        }
                    }catch(Exception ee){
                        if(LOGCAT){
                            Log.e(TAG, "2nd Level Exception Caught!\nThe system cannot get the Stacktrace requested at:\n"
                                    +"OnUpgrade(int,int)");
                        }
                    }
                }

            }
        }



        // on upgrade drop older tables
        if(LOGCAT){
            Log.d(TAG, "Dropping previous database tables . . .");
        }
        for(int x = TABLES.length-1; x > -1; x--){

            db.execSQL("DROP TABLE IF EXISTS "+TABLES[x]);

        }
        // create new tables
        if(LOGCAT){
            Log.d(TAG, "Creating new database tables . . .");
        }
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(LOGCAT){
            Log.d(TAG, "Database Downgrade required!\nOld Version: " + oldVersion + " New Version: " +newVersion);
            Log.d(TAG, "Recovering old database contents . . .");
        }

        for(int x = 0; x < Tables.values().length; x++){
            try{
                //ModelAccountsInfo[] mai = (ModelAccountsInfo[]) this.selectAll(Tables.accounts_info, ModelAccountsInfo.class);
                Object[] res =  this.selectAll(Tables.values()[x],
                        Class.forName("ph.edu.sscrmnl.privatesms.databasemodel."+ Models.values()[x].toString()), db);
                //List<Object> r = new ArrayList<Object>();
                //r.add(res);
                this.result.add(res);
                if(LOGCAT){
                    Log.d(TAG, (res.length > 0) ? "Successfully fetched recoverable data from " + Tables.values()[x].toString() :
                                    "No recoverable data from " + Tables.values()[x].toString()
                    );
                }

            }catch(SQLiteException sqle){
                if(LOGCAT){
                    StackTraceElement[] st = sqle.getStackTrace();
                    for(int y= 0; y <st.length; y++){
                        Log.w(TAG, st[y].toString());

                    }
                }

            }catch(Exception e){
                if(LOGCAT){
                    try{
                        StackTraceElement[] st = e.getStackTrace();
                        for(int y= 0; y <st.length; y++){
                            Log.w(TAG, st[y].toString());

                        }
                    }catch(Exception ee){
                        if(LOGCAT){
                            Log.e(TAG, "2nd Level Exception Caught!\nThe system cannot get the Stacktrace requested at:\n"
                                    +"OnUpgrade(int,int)");
                        }
                    }
                }

            }
        }
        // on upgrade drop older tables
        if(LOGCAT){
            Log.d(TAG, "Dropping previous database tables . . .");
        }
        for(int x = TABLES.length-1; x > -1; x--){

            db.execSQL("DROP TABLE IF EXISTS "+TABLES[x]);

        }
        // create new tables
        if(LOGCAT){
            Log.d(TAG, "Creating new database tables . . .");
        }
        onCreate(db);
    }


    /*
     * Returns a list of Object containing the result set of the
     * query "SELECT * FROM <Table_Name>;"
     *
     * Parameters:
     * 	table - the name of the query's target table
     * 	cls - the table model where the result set bases the table fields
     * 	db - the SQLiteDatabase instance where this query is being done.
     * 		If db is null, the method uses the current instance (this.getReadableDatabase())
     */
    public Object[] selectAll(final Tables table, final Class<?> cls, SQLiteDatabase db)
            throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException {


        // class manipulation

        List<Object> resultSet = new ArrayList<Object>();
        Object cl =  cls.newInstance();
        Method m = cls.getMethod("getFields");
        String[] fields = (String[]) m.invoke(cl, null);

        String sql = "SELECT * FROM " +table.toString();
        if(LOGCAT){
            Log.d(TAG,"Select All SQL: " +sql);

        }
        if(db != null){

        }else{
            db = this.getReadableDatabase();
        }
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                cl = cls.newInstance();
                for(String field : fields){
                    String methodName = "set"+field;
                    try{
                        // try if the current method is of type Integer
                        Integer arg = c.getInt((c.getColumnIndex(field.toLowerCase())));
                        m = cls.getMethod(methodName, Integer.class);
                        m.invoke(cl,arg);
                    }catch(Exception e){
                        try{
                            // try if the current method is of type Long
                            Long arg = c.getLong((c.getColumnIndex(field.toLowerCase())));
                            m = cls.getMethod(methodName, Long.class);
                            m.invoke(cl,arg);
                        }catch(Exception ee){
                            try{
                                // try if the current method is of type String
                                String arg = c.getString((c.getColumnIndex(field.toLowerCase())));
                                m = cls.getMethod(methodName, String.class);
                                m.invoke(cl,arg);
                            }catch(Exception eee){
                                if(LOGCAT){
                                    StackTraceElement[] st = e.getStackTrace();
                                    for(int y= 0; y <st.length; y++){
                                        Log.w(TAG, st[y].toString());

                                    }
                                }
                            }
                        }
                    }
                }
                resultSet.add(cl);
            } while (c.moveToNext());
        }
        c.close();
        try{
            this.close();
        }catch(Exception e){
            if(LOGCAT){
                Log.w(TAG, "Closing the current instance of SQLiteHelper failed at: SelectAll()");
            }
        }
        return resultSet.toArray();
    }


    public Object[] selectMulti(Tables table, Class<?> cls, Object[][] params, SQLiteDatabase db)
            throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException{

        String sql = "SELECT * FROM "+table.toString()+" WHERE ";
        String[] args = new String[params.length];

        for(int x =0; x < params.length; x++){
            if(x == params.length - 1){
                //sql+= (params[x][0].toString()+" = '" + AndroidUtils.sanitizeSQL(params[x][1].toString())+"';");
                sql+= (params[x][0].toString()+" = ? ;");
                args[x] = params[x][1].toString();
            }else{
                // sql+= (params[x][0].toString()+" = '" + AndroidUtils.sanitizeSQL(params[x][1].toString())+"' AND ");
                sql+= (params[x][0].toString()+" = ? AND ");
                args[x] = params[x][1].toString();
            }
        }
        if(LOGCAT){
            Log.d(TAG,"Select Multi SQL: " +sql);
        }
        if(db != null){

        }else{
            db = this.getReadableDatabase();
        }
        // class manipulation
        List<Object> resultSet = new ArrayList<Object>();
        Object cl =  cls.newInstance();
        Method m = cls.getMethod("getFields");
        String[] fields = (String[]) m.invoke(cl, null);
        Cursor c = db.rawQuery(sql, args);
        if (c.moveToFirst()) {
            do{
                cl = cls.newInstance();
                for(String field : fields){
                    String methodName = "set"+field;
                    try{
                        // try if the current method is of type Integer
                        Integer arg = c.getInt((c.getColumnIndex(field.toLowerCase())));
                        m = cls.getMethod(methodName, Integer.class);
                        m.invoke(cl,arg);
                    }catch(Exception e){
                        try{
                            // try if the current method is of type Long
                            Long arg = c.getLong((c.getColumnIndex(field.toLowerCase())));
                            m = cls.getMethod(methodName, Long.class);
                            m.invoke(cl,arg);
                        }catch(Exception ee){
                            try{
                                // try if the current method is of type String
                                String arg = c.getString((c.getColumnIndex(field.toLowerCase())));
                                m = cls.getMethod(methodName, String.class);
                                m.invoke(cl,arg);
                            }catch(Exception eee){
                                if(LOGCAT){
                                    StackTraceElement[] st = e.getStackTrace();
                                    for(int y= 0; y <st.length; y++){
                                        Log.w(TAG, st[y].toString());

                                    }
                                }
                            }
                        }
                    }
                }
                resultSet.add(cl);
            }while (c.moveToNext());
        }
        c.close();
        try{
            this.close();
        }catch(Exception e){
            if(LOGCAT){
                Log.w(TAG, "Closing the current instance of SQLiteHelper failed at: SelectMulti()");
            }
        }
        return resultSet.toArray();
    }


    public Object[] selectConditional(Tables table, Class<?> cls, Object[][] params, SQLiteDatabase db)
            throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException{




        // String sql = "SELECT * FROM "+table.toString()+" WHERE " + AndroidUtils.sanitizeSQL(conditionalSql);
        String sql = "SELECT * FROM "+table.toString()+" WHERE ";
        String[] args = new String[params.length];

        for(int x =0; x < params.length; x++){
            if(x == params.length - 1){
                //sql+= (params[x][0].toString()+" = '" + AndroidUtils.sanitizeSQL(params[x][1].toString())+"';");
                sql+= (params[x][0].toString()+" = ? ;");
                args[x] = params[x][1].toString();
            }else{
                // sql+= (params[x][0].toString()+" = '" + AndroidUtils.sanitizeSQL(params[x][1].toString())+"' AND ");
                sql+= (params[x][0].toString()+" = ? " + params[x][2]);
                args[x] = params[x][1].toString();
            }
        }
        if(LOGCAT){
            Log.d(TAG,"Select Conditional SQL: " +sql);
        }
        if(db != null){

        }else{
            db = this.getReadableDatabase();
        }
        // class manipulation
        List<Object> resultSet = new ArrayList<Object>();
        Object cl =  cls.newInstance();
        Method m = cls.getMethod("getFields");
        String[] fields = (String[]) m.invoke(cl, null);
        Cursor c = db.rawQuery(sql, args);
        if (c.moveToFirst()) {
            do{
                cl = cls.newInstance();
                for(String field : fields){
                    String methodName = "set"+field;
                    try{
                        // try if the current method is of type Integer
                        Integer arg = c.getInt((c.getColumnIndex(field.toLowerCase())));
                        m = cls.getMethod(methodName, Integer.class);
                        m.invoke(cl,arg);
                    }catch(Exception e){
                        try{
                            // try if the current method is of type Long
                            Long arg = c.getLong((c.getColumnIndex(field.toLowerCase())));
                            m = cls.getMethod(methodName, Long.class);
                            m.invoke(cl,arg);
                        }catch(Exception ee){
                            try{
                                // try if the current method is of type String
                                String arg = c.getString((c.getColumnIndex(field.toLowerCase())));
                                m = cls.getMethod(methodName, String.class);
                                m.invoke(cl,arg);
                            }catch(Exception eee){
                                if(LOGCAT){
                                    StackTraceElement[] st = e.getStackTrace();
                                    for(int y= 0; y <st.length; y++){
                                        Log.w(TAG, st[y].toString());

                                    }
                                }
                            }
                        }
                    }
                }
                resultSet.add(cl);
            }while (c.moveToNext());
        }
        c.close();
        try{
            this.close();
        }catch(Exception e){
            if(LOGCAT){
                Log.w(TAG, "Closing the current instance of SQLiteHelper failed at: SelectMulti()");
            }
        }
        return resultSet.toArray();
    }

    public Object select(Tables table, Class<?> cls, Object[][] params, SQLiteDatabase db)
            throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException{

        String sql = "SELECT * FROM "+table.toString()+" WHERE ";
        String[] args = new String[params.length];
        for(int x =0; x < params.length; x++){
            if(x == params.length - 1){
                // sql+= (params[x][0].toString()+" = '" + params[x][1].toString()+"';");
                sql+= (params[x][0].toString()+" = ? ;");
                args[x] = params[x][1].toString();
            }else{
                // sql+= (params[x][0].toString()+" = '" + params[x][1].toString()+"' AND ");

                sql+= (params[x][0].toString()+" = ? AND ");
                args[x] = params[x][1].toString();
            }
        }
        if(LOGCAT){
            Log.d(TAG,"Select SQL: " +sql);

        }
        if(db != null){

        }else{
            db = this.getReadableDatabase();
        }// class manipulation
        Object resultSet =  cls.newInstance();
        Method m = cls.getMethod("getFields");
        String[] fields = (String[]) m.invoke(resultSet, null);
        Cursor c = db.rawQuery(sql, args);

        if (c != null){
            c.moveToFirst();
            for(String field : fields){
                String methodName = "set"+field;
                try{
                    // try if the current method is of type Integer
                    Integer arg = c.getInt((c.getColumnIndex(field.toLowerCase())));
                    m = cls.getMethod(methodName, Integer.class);
                    m.invoke(resultSet,arg);
                }catch(Exception e){
                    try{
                        // try if the current method is of type Long
                        Long arg = c.getLong((c.getColumnIndex(field.toLowerCase())));
                        m = cls.getMethod(methodName, Long.class);
                        m.invoke(resultSet,arg);
                    }catch(Exception ee){
                        try{
                            // try if the current method is of type String
                            String arg = c.getString((c.getColumnIndex(field.toLowerCase())));
                            m = cls.getMethod(methodName, String.class);
                            m.invoke(resultSet,arg);
                        }catch(Exception eee){
                            if(LOGCAT){
                                StackTraceElement[] st = e.getStackTrace();
                                for(int y= 0; y <st.length; y++){
                                    Log.w(TAG, st[y].toString());

                                }
                            }
                        }
                    }
                }
            }
        }
        c.close();
        try{
            this.close();
        }catch(Exception e){
            if(LOGCAT){
                Log.w(TAG, "Closing the current instance of SQLiteHelper failed at: Select()");
            }
        }
        return resultSet;
    }

    public boolean insert(Tables table, Object params, SQLiteDatabase db)
            throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException, SQLiteConstraintException{
        if(db != null){

        }else{
            db = this.getWritableDatabase();
        }
        // class manipulation
        Method m = params.getClass().getMethod("getFields");
        String[] fields = (String[]) m.invoke(params, null);
        ContentValues values = new ContentValues();
        for(String field : fields){
            String methodName = "get"+field;
            try{
                // try if the current method is of type Integer
                m = params.getClass().getMethod(methodName);
                values.put(new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")),
                        (Integer)m.invoke(params));
            }catch(Exception e){
                try{
                    // try if the current method is of type Long
                    m = params.getClass().getMethod(methodName);
                    values.put(new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")),
                            (Long)m.invoke(params));
                }catch(Exception ee){
                    try{
                        // try if the current method is of type String
                        m = params.getClass().getMethod(methodName);
                        values.put(new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")),
                                (String)m.invoke(params));
                    }catch(Exception eee){
                        if(LOGCAT){
                            StackTraceElement[] st = e.getStackTrace();
                            for(int y= 0; y <st.length; y++){
                                Log.w(TAG, st[y].toString());

                            }
                        }
                    }
                }
            }
        }
        if(LOGCAT){
            Log.d(TAG,"INSERT INTO "+table.toString()+" " + values);

        }
        return ((db.insert(table.toString(), null, values) == -1) ? false : true);

    }


    /**
     * Updates a record that is already existing in the database
     * If a record is not yet existing, use insert()
     *
     * NOTE:
     * This method takes the oldParams as its WHERE clause and args
     * Be sure that you have explicitly provided a newParams and that
     * its instance is not taken or copied from oldParams' instance.
     * Doing so may produced several bugs when updating database contents
     *
     * TIP:
     * You can use the following example to make sure you are not using
     * the oldParams' instance
     *
     * db.update(Tables.files_info, oldParams, new ModelFilesInfo(arg.getPath(), ...), null);
     *
     *
     */
    public boolean update(Tables table, Object oldParams, Object newParams, SQLiteDatabase db)throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException{

        if(db != null){

        }else{
            this.getWritableDatabase().close();
            db = this.getWritableDatabase();
        }
        // class manipulation
        Method m = newParams.getClass().getMethod("getFields");
        String[] fields = (String[]) m.invoke(newParams, null);
        ContentValues values = new ContentValues();
        String whereSql = "";
        List<String> whereParams = new ArrayList<String>();

        for(String field : fields){
            String methodName = "get"+field;
            String res = null;
            try{
                // try if the current method is of type Integer
                m = newParams.getClass().getMethod(methodName);
                if((Integer)m.invoke(newParams) != null){
                    values.put(new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")),
                            (Integer)m.invoke(newParams));

                }
                m = oldParams.getClass().getMethod(methodName);
                res = Integer.toString((Integer) m.invoke(oldParams));
            }catch(Exception e){
                try{
                    // try if the current method is of type Long
                    m = newParams.getClass().getMethod(methodName);
                    if((Long)m.invoke(newParams) != null){
                        values.put(new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")),
                                (Long)m.invoke(newParams));

                    }
                    m = oldParams.getClass().getMethod(methodName);
                    res = Long.toString((Long) m.invoke(oldParams));
                }catch(Exception ee){
                    try{
                        // try if the current method is of type String
                        m = newParams.getClass().getMethod(methodName);
                        if((String)m.invoke(newParams) != null){
                            values.put(new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")),
                                    (String)m.invoke(newParams));

                        }
                        m = oldParams.getClass().getMethod(methodName);
                        res = (String) m.invoke(oldParams);
                    }catch(Exception eee){
                        if(LOGCAT){
                            StackTraceElement[] st = e.getStackTrace();
                            for(int y= 0; y <st.length; y++){
                                Log.w(TAG, st[y].toString());

                            }
                        }
                    }
                }
            }
            // String res = (String) m.invoke(oldParams);
            if (res != null){
                whereParams.add(res);
                whereSql += (new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")) +
                        " = ? AND ");
            }


        }
        whereSql = whereSql.substring(0,whereSql.length()-5) + ";";
        String[] finalwhereParams = whereParams.toArray(new String[whereParams.size()]);
        if(LOGCAT){
            Log.d(TAG,"UPDATE FROM " +table.toString() + " " + whereSql +", VALUES: " + values );

        }
        return (db.update(table.toString(), values, whereSql,
                finalwhereParams)) > 0;
    }

    public boolean delete(Tables table, Object params, SQLiteDatabase db)throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        if(db != null){

        }else{

            db = this.getWritableDatabase();
        }
        // class manipulation
        Method m = params.getClass().getMethod("getFields");
        String[] fields = (String[]) m.invoke(params, null);
        String whereSql = "";
        List<String> whereParams = new ArrayList<String>();
        for(String field : fields){
            String methodName = "get"+field;
            String res = null;
            try{
                // try if the current method is of type Integer
                m = params.getClass().getMethod(methodName);
                if((Integer)m.invoke(params) != null){
                    m = params.getClass().getMethod(methodName);
                    res = Integer.toString((Integer)m.invoke(params));
                }
            }catch(Exception e){
                try{
                    // try if the current method is of type Long
                    m = params.getClass().getMethod(methodName);
                    if((Long)m.invoke(params) != null){
                        m = params.getClass().getMethod(methodName);
                        res = Long.toString((Long)m.invoke(params));
                    }

                }catch(Exception ee){
                    try{
                        // try if the current method is of type String
                        m = params.getClass().getMethod(methodName);
                        if((String)m.invoke(params) != null){
                            m = params.getClass().getMethod(methodName);
                            res = (String)m.invoke(params);
                        }
                    }catch(Exception eee){
                        if(LOGCAT){
                            StackTraceElement[] st = e.getStackTrace();
                            for(int y= 0; y <st.length; y++){
                                Log.w(TAG, st[y].toString());

                            }
                        }
                    }
                }
            }
            //String res = (String) m.invoke(params);
            if (res != null){
                whereParams.add(res);
                whereSql += (new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")) +
                        " = ? AND ");
            }
        }
        whereSql = whereSql.substring(0,whereSql.length()-5) + ";";
        String[] finalwhereParams = whereParams.toArray(new String[whereParams.size()]);

        return (db.delete(table.toString(), whereSql,
                finalwhereParams)) > 0;


        /*
        return ((db.delete(table.toString(), whereSql,
                finalwhereParams)) > 0);
                */

    }

    public void closeConnection() {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        if (dbRead != null && dbRead.isOpen()){
            dbRead.close();
            Log.i(TAG, "R-- Database connection successfully closed.");
        }



        SQLiteDatabase dbWrite = this.getWritableDatabase();
        if (dbWrite != null && dbWrite.isOpen()){
            dbWrite.close();
            Log.i(TAG, "RW- Database connection successfully closed.");
        }


    }

}