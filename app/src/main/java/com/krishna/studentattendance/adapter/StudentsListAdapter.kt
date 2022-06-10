package com.krishna.studentattendance.adapter

import io.realm.RealmResults
import com.krishna.studentattendance.realm.StudentsList
import android.app.Activity
import io.realm.RealmRecyclerViewAdapter
import com.krishna.studentattendance.viewholders.ViewHolder_students
import android.view.ViewGroup
import android.view.LayoutInflater
import com.krishna.studentattendance.R
import android.preference.PreferenceManager
import io.realm.Realm

class StudentsListAdapter(
    var mList: RealmResults<StudentsList>,
    private val mActivity: Activity,
    var mroomID: String,
    extraClick: String?
) : RealmRecyclerViewAdapter<StudentsList?, ViewHolder_students>(
    mList, true
) {
    var stuID: String? = null
    var realm: Realm = Realm.getDefaultInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_students {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_attendance_adapter, parent, false)
        return ViewHolder_students(itemView, mActivity, mList, mroomID)
    }

    override fun onBindViewHolder(holder: ViewHolder_students, position: Int) {
        val temp = getItem(position)
        holder.student_name.text = temp!!.name_student
        holder.student_regNo.text = temp.regNo_student

        val preferences = PreferenceManager.getDefaultSharedPreferences(mActivity)
        stuID = temp.regNo_student

        val value = preferences.getString(stuID, null)
        if (value != null) {
            if (value == "Present") {
                holder.radioButton_present.isChecked = true
            } else {
                holder.radioButton_absent.isChecked = true
            }
        }
    }
}