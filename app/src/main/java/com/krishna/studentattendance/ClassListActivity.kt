package com.krishna.studentattendance

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.krishna.studentattendance.adapter.ClassListAdapter
import android.os.Bundle
import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import io.realm.RealmResults
import com.krishna.studentattendance.realm.Class_Names
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm

class ClassListActivity : AppCompatActivity() {

    var bottomAppBar: BottomAppBar? = null
    var fab_main: FloatingActionButton? = null
    var recyclerView: RecyclerView? = null
    var sample: TextView? = null
    var mAdapter: ClassListAdapter? = null
    var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students_class_dashboard)

        Realm.init(this)

        bottomAppBar = findViewById(R.id.bottomAppBar)
        fab_main = findViewById(R.id.fab_main)

        fab_main?.setOnClickListener(View.OnClickListener { view: View? ->
            val intent = Intent(this@ClassListActivity, CreateClassActivity::class.java)
            resultLauncher.launch(intent)
        })
        realm = Realm.getDefaultInstance()

        val results: RealmResults<Class_Names> = realm?.where(Class_Names::class.java)?.findAll()!!

        sample = findViewById(R.id.classes_sample)
        recyclerView = findViewById(R.id.recyclerView_main)
        recyclerView?.setHasFixedSize(true)

        val staggeredGridLayoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        recyclerView?.layoutManager = staggeredGridLayoutManager
        mAdapter = ClassListAdapter(results, this@ClassListActivity)
        recyclerView?.adapter = mAdapter
    }

    override fun onResume() {
        super.onResume()
        realm?.refresh()
        realm?.isAutoRefresh = true
    }

    var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            realm?.refresh()
            mAdapter?.notifyDataSetChanged()
        }
    }
}