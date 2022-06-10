package com.krishna.studentattendance.bottomSheet

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.EditText
import androidx.cardview.widget.CardView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.krishna.studentattendance.R
import android.content.Intent
import android.net.Uri
import android.view.View

class StudentBottomSheet(var _name: String, var _regNo: String, var _mobNo: String) :
    BottomSheetDialogFragment() {

    private var name_student: EditText? = null
    private var regNo_student: EditText? = null
    private var mobNo_student: EditText? = null
    var call: CardView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottomsheet_student_edit, container, false)
        name_student = v.findViewById(R.id.stu_name_edit)
        regNo_student = v.findViewById(R.id.stu_regNo_edit)
        mobNo_student = v.findViewById(R.id.stu_mobNo_edit)
        call = v.findViewById(R.id.call_edit)
        name_student?.setText(_name)
        regNo_student?.setText(_regNo)
        mobNo_student?.setText(_mobNo)

        call?.setOnClickListener {
            val uri = "tel:" + _mobNo.trim { it <= ' ' }
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(uri)
            startActivity(intent)
        }
        return v
    }
}