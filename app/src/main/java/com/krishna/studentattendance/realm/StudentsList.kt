package com.krishna.studentattendance.realm

import io.realm.RealmObject

open class StudentsList : RealmObject() {
    var id: String? = null
    var name_student: String? = null
    var regNo_student: String? = null
    var mobileNo_student: String? = null
    var class_id: String? = null
}