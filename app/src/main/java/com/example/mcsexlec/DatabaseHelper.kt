package com.example.mcsexlec

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "shabymovie.db"
        private const val DATABASE_VERSION = 1

        // Users Table
        const val TABLE_USERS = "users"
        const val KEY_USER_ID = "user_id"
        const val KEY_PHONE = "phone_number"
        const val KEY_PASSWORD = "password"

        // Films Table
        const val TABLE_FILMS = "films"
        const val KEY_FILM_ID = "film_id"
        const val KEY_FILM_DESCRIPTION = "film_description"
        const val KEY_FILM_TITLE = "film_title"
        const val KEY_FILM_IMAGE = "film_image"
        const val KEY_FILM_PRICE = "film_price"

        // Transactions Table
        const val TABLE_TRANSACTIONS = "transactions"
        const val KEY_TRANSACTION_ID = "id"
        const val KEY_USER_ID_FK = "user_id" // Foreign key for users
        const val KEY_FILM_ID_FK = "film_id" // Foreign key for films
        const val KEY_QUANTITY = "quantity"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create Users Table
        val CREATE_USERS_TABLE = """
            CREATE TABLE $TABLE_USERS (
                $KEY_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_PHONE TEXT NOT NULL,
                $KEY_PASSWORD TEXT NOT NULL
            )
        """

        // Create Films Table
        val CREATE_FILMS_TABLE = """
            CREATE TABLE $TABLE_FILMS (
                $KEY_FILM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_FILM_TITLE TEXT NOT NULL,
                $KEY_FILM_DESCRIPTION TEXT,
                $KEY_FILM_IMAGE TEXT,
                $KEY_FILM_PRICE INTEGER
            )
        """

        // Create Transactions Table
        val CREATE_TRANSACTIONS_TABLE = """
            CREATE TABLE $TABLE_TRANSACTIONS (
                $KEY_TRANSACTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_USER_ID_FK INTEGER,
                $KEY_FILM_ID_FK INTEGER,
                $KEY_QUANTITY INTEGER,
                FOREIGN KEY ($KEY_USER_ID_FK) REFERENCES $TABLE_USERS($KEY_USER_ID),
                FOREIGN KEY ($KEY_FILM_ID_FK) REFERENCES $TABLE_FILMS($KEY_FILM_ID)
            )
        """

        // Execute the queries
        db?.execSQL(CREATE_USERS_TABLE)
        db?.execSQL(CREATE_FILMS_TABLE)
        db?.execSQL(CREATE_TRANSACTIONS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop old tables if they exist and recreate them
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FILMS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        onCreate(db)
    }
    // Fungsi untuk mendapatkan user berdasarkan nomor telepon
    fun getUserByPhone(phone: String): Cursor? {
        val db = this.readableDatabase
        return db.query(
            TABLE_USERS,  // Nama tabel
            null,  // Ambil semua kolom
            "$KEY_PHONE = ?",  // Kondisi WHERE untuk mencari berdasarkan nomor telepon
            arrayOf(phone),  // Nilai untuk placeholder ?
            null,
            null,
            null
        )
    }

    // CRUD Operations for Users

    // Insert a new user
    fun addUser(phone: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(KEY_PHONE, phone)
        values.put(KEY_PASSWORD, password)

        return db.insert(TABLE_USERS, null, values).also { db.close() }
    }

    // Check if user is registered
    fun isUserRegistered(phone: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(TABLE_USERS, null, "$KEY_PHONE = ?", arrayOf(phone), null, null, null)
        return cursor.count > 0
    }

    // Validate user login
    fun validateUser(phone: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_USERS,
            null,
            "$KEY_PHONE = ? AND $KEY_PASSWORD = ?",
            arrayOf(phone, password),
            null,
            null,
            null
        )
        return cursor.count > 0
    }

    // CRUD Operations for Films

    // Insert a new film
    fun addFilm(title: String,description: String, image: String, price: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(KEY_FILM_TITLE, title)
        values.put(KEY_FILM_DESCRIPTION, description)
        values.put(KEY_FILM_IMAGE, image)
        values.put(KEY_FILM_PRICE, price)

        return db.insert(TABLE_FILMS, null, values).also { db.close() }
    }

    // Get film by ID
    fun getFilmById(filmId: Int): Cursor {
        val db = this.readableDatabase
        return db.query(
            TABLE_FILMS,
            null,
            "$KEY_FILM_ID = ?",
            arrayOf(filmId.toString()),
            null,
            null,
            null
        )
    }

    fun getTransactionWithFilmByUserId(userId: Int): Cursor {
        val db = this.readableDatabase
        val query = """
        SELECT t.$KEY_QUANTITY, f.$KEY_FILM_TITLE, f.$KEY_FILM_PRICE
        FROM $TABLE_TRANSACTIONS t
        JOIN $TABLE_FILMS f ON t.$KEY_FILM_ID_FK = f.$KEY_FILM_ID
        WHERE t.$KEY_USER_ID_FK = ?
    """
        return db.rawQuery(query, arrayOf(userId.toString()))
    }



    // CRUD Operations for Transactions

    // Insert a new transaction
    fun addTransaction(userId: Int, filmId: Int, quantity: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(KEY_USER_ID_FK, userId)
        values.put(KEY_FILM_ID_FK, filmId)
        values.put(KEY_QUANTITY, quantity)

        return db.insert(TABLE_TRANSACTIONS, null, values).also { db.close() }
    }

    // Get transactions by user ID
    fun getTransactionsByUserId(userId: Int): Cursor {
        val db = this.readableDatabase
        return db.query(
            TABLE_TRANSACTIONS,
            null,
            "$KEY_USER_ID_FK = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )
    }
    // Method to get user by ID
    fun getUserById(userId: Int): Cursor {
        val db = this.readableDatabase
        return db.query(
            TABLE_USERS,
            null,
            "$KEY_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )
    }

}
