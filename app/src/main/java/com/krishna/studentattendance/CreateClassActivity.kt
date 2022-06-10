package com.krishna.studentattendance

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.krishna.studentattendance.customradiobutton.RadioRealButton
import com.krishna.studentattendance.customradiobutton.RadioRealButtonGroup
import com.krishna.studentattendance.realm.Class_Names
import io.realm.Realm
import io.realm.RealmAsyncTask
import java.util.*

class CreateClassActivity : AppCompatActivity(R.layout.activity_create_class) {

    private var createButton: Button? = null
    private var _className: EditText? = null
    private var _subjectName: EditText? = null
    var realm: Realm? = null
    var transaction: RealmAsyncTask? = null
    private var position_bg = "0"

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_insert_class)
        setSupportActionBar(toolbar)

        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        createButton = findViewById(R.id.button_create_student_class)
        _className = findViewById(R.id.className_createClass)
        _subjectName = findViewById(R.id.subjectName_createClass)

        Realm.init(this)
        realm = Realm.getDefaultInstance()

        val button1 = findViewById<View>(R.id.button1) as RadioRealButton
        val button2 = findViewById<View>(R.id.button2) as RadioRealButton
        val button3 = findViewById<View>(R.id.button3) as RadioRealButton
        val button4 = findViewById<View>(R.id.button4) as RadioRealButton
        val button5 = findViewById<View>(R.id.button5) as RadioRealButton
        val button6 = findViewById<View>(R.id.button6) as RadioRealButton
        val group = findViewById<View>(R.id.group) as RadioRealButtonGroup

        group.setOnClickedButtonListener { button: RadioRealButton?, position: Int ->
            position_bg = position.toString()
        }

        createButton?.setOnClickListener { view: View? ->

            if (isValid) {

                val progressDialog = ProgressDialog(this@CreateClassActivity)
                progressDialog.setMessage("Creating class..")
                progressDialog.show()

                transaction = realm?.executeTransactionAsync({ realm ->
                    val className = realm.createObject(Class_Names::class.java)
                    val id = _className?.text.toString() + _subjectName?.text.toString()
                    className.id = id
                    className.name_class = _className?.text.toString()
                    className.name_subject = _subjectName?.text.toString()
                    className.position_bg = position_bg
                }, {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@CreateClassActivity,
                        "Successfully created",
                        Toast.LENGTH_SHORT
                    ).show()

                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()

                }, {
                    progressDialog.dismiss()
                    Toast.makeText(this@CreateClassActivity, "Error!", Toast.LENGTH_SHORT).show()
                })
            } else {
                Toast.makeText(this@CreateClassActivity, "Fill all details", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val isValid: Boolean
        get() = _className!!.text.toString().isNotEmpty() && _subjectName!!.text.toString().isNotEmpty()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}