package com.krishna.studentattendance

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.krishna.studentattendance.adapter.StudentsListAdapter
import com.krishna.studentattendance.realm.Attendance_Reports
import com.krishna.studentattendance.realm.Attendance_Students_List
import com.krishna.studentattendance.realm.StudentsList
import com.yarolegovich.lovelydialog.LovelyCustomDialog
import io.realm.*
import java.text.SimpleDateFormat
import java.util.*

class ClassDetailActivity : AppCompatActivity(R.layout.activity_class_details) {

    private var themeImage: ImageView? = null
    private var className: TextView? = null
    private var total_students: TextView? = null
    private var place_holder: TextView? = null
    private var addStudent: CardView? = null
    private var reports_open: CardView? = null
    private var submit_btn: Button? = null
    private var student_name: EditText? = null
    private var reg_no: EditText? = null
    private var mobile_no: EditText? = null
    private var layout_attendance_taken: LinearLayout? = null
    private var mRecyclerview: RecyclerView? = null
    private var room_ID: String? = null
    private var subject_Name: String? = null
    private var class_Name: String? = null
    var realm: Realm? = null
    var transaction: RealmAsyncTask? = null
    var realmChangeListener: RealmChangeListener<*>? = null
    private val handler = Handler(Looper.getMainLooper())
    var mAdapter: StudentsListAdapter? = null
    var progressBar: ProgressBar? = null
    var lovelyCustomDialog: Dialog? = null


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.exitTransition = null

        Realm.init(this)

        val theme = intent.getStringExtra("theme")
        class_Name = intent.getStringExtra("className")
        subject_Name = intent.getStringExtra("subjectName")
        room_ID = intent.getStringExtra("classroom_ID")

        val toolbar = findViewById<Toolbar>(R.id.toolbar_class_detail)
        setSupportActionBar(toolbar)

        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        val collapsingToolbarLayout =
            findViewById<CollapsingToolbarLayout>(R.id.collapsing_disease_detail)
        collapsingToolbarLayout.title = subject_Name

        themeImage = findViewById(R.id.image_disease_detail)
        className = findViewById(R.id.classname_detail)
        total_students = findViewById(R.id.total_students_detail)
        layout_attendance_taken = findViewById(R.id.attendance_taken_layout)
        layout_attendance_taken?.visibility = View.GONE
        addStudent = findViewById(R.id.add_students)
        reports_open = findViewById(R.id.reports_open_btn)
        className?.text = class_Name
        mRecyclerview = findViewById(R.id.recyclerView_detail)
        progressBar = findViewById(R.id.progressbar_detail)
        place_holder = findViewById(R.id.placeholder_detail)
        place_holder?.visibility = View.GONE
        submit_btn = findViewById(R.id.submit_attendance_btn)
        submit_btn?.visibility = View.GONE

        when (theme) {
            "0" -> themeImage?.setImageResource(R.drawable.math)
            "1" -> themeImage?.setImageResource(R.drawable.physics)
            "2" -> themeImage?.setImageResource(R.drawable.chemistry)
            "3" -> themeImage?.setImageResource(R.drawable.biology)
            "4" -> themeImage?.setImageResource(R.drawable.sanskrit)
            "5" -> themeImage?.setImageResource(R.drawable.general_knowledge)
        }

        val r = Runnable {
            RealmInit()
            progressBar?.visibility = View.GONE
        }
        handler.postDelayed(r, 500)

        submit_btn?.setOnClickListener {
            val count = realm!!.where(StudentsList::class.java)
                .equalTo("class_id", room_ID)
                .count()
            val size: String
            val preferences =
                PreferenceManager.getDefaultSharedPreferences(this@ClassDetailActivity)
            size = preferences.all.size.toString()
            val size2: String = count.toString()
            if (size == size2) {
                submitAttendance()
            } else {
                Toast.makeText(this@ClassDetailActivity, "Select all........", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        reports_open?.setOnClickListener { view: View? ->
            val intent = Intent(this@ClassDetailActivity, Reports_Activity::class.java)
            intent.putExtra("class_name", class_Name)
            intent.putExtra("subject_name", subject_Name)
            intent.putExtra("room_ID", room_ID)
            startActivity(intent)
        }
        addStudent?.setOnClickListener { view: View? ->
            val inflater = LayoutInflater.from(this@ClassDetailActivity)
            val view1 = inflater.inflate(R.layout.popup_add_student, null)
            student_name = view1.findViewById(R.id.name_student_popup)
            reg_no = view1.findViewById(R.id.regNo_student_popup)
            mobile_no = view1.findViewById(R.id.mobileNo_student_popup)
            lovelyCustomDialog = LovelyCustomDialog(this@ClassDetailActivity)
                .setView(view1)
                .setTopColorRes(R.color.theme_light)
                .setTitle("Add Student")
                .setIcon(R.drawable.ic_baseline_person_add_24)
                .setCancelable(false)
                .setListener(R.id.add_btn_popup) {
                    val name = student_name?.text.toString()
                    val regNo = reg_no?.text.toString()
                    val mobNo = mobile_no?.text.toString()

                    if (isValid) {
                        addStudentMethod(name, regNo, mobNo)
                    } else {
                        Toast.makeText(
                            this@ClassDetailActivity,
                            "Please fill all the details..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setListener(R.id.cancel_btn_popup) { lovelyCustomDialog!!.dismiss() }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    fun RealmInit() {
        Realm.init(this)
        realm = Realm.getDefaultInstance()
        val date = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date())
        realmChangeListener = RealmChangeListener { o: Any? ->
            val count = realm?.where(StudentsList::class.java)
                ?.equalTo("class_id", room_ID)
                ?.count()
            total_students!!.text = "Total Students : $count"
            val reports_size = realm?.where(Attendance_Reports::class.java)
                ?.equalTo("date_and_classID", date + room_ID)
                ?.count()
            if (reports_size != 0L) {
                layout_attendance_taken!!.visibility = View.VISIBLE
                submit_btn!!.visibility = View.GONE
            } else {
                layout_attendance_taken!!.visibility = View.GONE
                submit_btn!!.visibility = View.VISIBLE
                if (count != 0L) {
                    submit_btn!!.visibility = View.VISIBLE
                    place_holder!!.visibility = View.GONE
                } else if (count == 0L) {
                    submit_btn!!.visibility = View.GONE
                    place_holder!!.visibility = View.VISIBLE
                }
            }
        }
        realm?.addChangeListener(realmChangeListener as RealmChangeListener<Realm>)
        val students: RealmResults<StudentsList> = realm?.where(StudentsList::class.java)
            ?.equalTo("class_id", room_ID)
            ?.sort("name_student", Sort.ASCENDING)
            ?.findAllAsync() as RealmResults<StudentsList>
        val count = realm?.where(StudentsList::class.java)
            ?.equalTo("class_id", room_ID)
            ?.count()
        val reports_size = realm?.where(Attendance_Reports::class.java)
            ?.equalTo("date_and_classID", date + room_ID)
            ?.count()
        if (reports_size != 0L) {
            layout_attendance_taken!!.visibility = View.VISIBLE
            submit_btn!!.visibility = View.GONE
        } else if (reports_size == 0L) {
            layout_attendance_taken!!.visibility = View.GONE
            submit_btn!!.visibility = View.VISIBLE
            if (count != 0L) {
                submit_btn!!.visibility = View.VISIBLE
                place_holder!!.visibility = View.GONE
            } else if (count == 0L) {
                submit_btn!!.visibility = View.GONE
                place_holder!!.visibility = View.VISIBLE
            }
        }
        total_students!!.text = "Total Students : $count"
        mRecyclerview!!.layoutManager = LinearLayoutManager(this)
        val extraClick = ""
        mAdapter =
            StudentsListAdapter(students, this@ClassDetailActivity, date + room_ID, extraClick)
        mRecyclerview!!.adapter = mAdapter
    }

    fun submitAttendance() {
        val progressDialog = ProgressDialog(this@ClassDetailActivity)
        progressDialog.setMessage("Please wait..")
        progressDialog.show()
        val date = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date())
        val list_students: RealmResults<Attendance_Students_List>
        list_students = realm!!.where(Attendance_Students_List::class.java)
            .equalTo("date_and_classID", date + room_ID)
            .sort("studentName", Sort.ASCENDING)
            .findAllAsync()
        val list = RealmList<Attendance_Students_List>()
        list.addAll(list_students)
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val dateOnly = calendar[Calendar.DATE].toString()
        @SuppressLint("SimpleDateFormat") val monthOnly =
            SimpleDateFormat("MMM").format(calendar.time)
        try {
            realm!!.executeTransaction { realm: Realm ->
                val attendance_reports = realm.createObject(
                    Attendance_Reports::class.java
                )
                attendance_reports.classId = room_ID
                attendance_reports.attendance_students_lists = list
                attendance_reports.date = date
                attendance_reports.dateOnly = dateOnly
                attendance_reports.monthOnly = monthOnly
                attendance_reports.date_and_classID = date + room_ID
                attendance_reports.classname = class_Name
                attendance_reports.subjName = subject_Name
            }
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = preferences.edit()
            editor.clear()
            editor.commit()
            Toast.makeText(this@ClassDetailActivity, "Attendance Submitted", Toast.LENGTH_SHORT)
                .show()
            progressDialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
            progressDialog.dismiss()
            Toast.makeText(this@ClassDetailActivity, "Error Occurred", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.clear()
        editor.commit()
        super.onDestroy()
    }

    fun addStudentMethod(studentName: String, regNo: String, mobileNo: String?) {
        val progressDialog = ProgressDialog(this@ClassDetailActivity)
        progressDialog.setMessage("Creating class..")
        progressDialog.show()
        transaction = realm!!.executeTransactionAsync({ realm: Realm ->
            val students_list = realm.createObject(
                StudentsList::class.java
            )
            val id = studentName + regNo
            students_list.id = id
            students_list.name_student = studentName
            students_list.regNo_student = regNo
            students_list.mobileNo_student = mobileNo
            students_list.class_id = room_ID
        }, {
            progressDialog.dismiss()
            lovelyCustomDialog!!.dismiss()
            realm!!.refresh()
            realm!!.isAutoRefresh = true
            Toast.makeText(this@ClassDetailActivity, "Student Added", Toast.LENGTH_SHORT).show()
        }) { error: Throwable? ->
            progressDialog.dismiss()
            lovelyCustomDialog!!.dismiss()
            Toast.makeText(this@ClassDetailActivity, "Error!", Toast.LENGTH_SHORT).show()
        }
    }

    val isValid: Boolean
        get() = !(student_name!!.text.toString().isEmpty() || reg_no!!.text.toString()
            .isEmpty() || mobile_no!!.text.toString().isEmpty())

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_class_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val TAG = "ClassDetail_Activity"
    }
}