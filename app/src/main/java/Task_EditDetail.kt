//package com.example.newevent2
//
//import android.app.DatePickerDialog
//import android.os.Bundle
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.newevent2.Model.Task
//import com.example.newevent2.Model.TaskModel
//import com.example.newevent2.ui.dialog.DatePickerFragment
//import com.google.android.material.chip.Chip
//import com.google.android.material.chip.ChipGroup
//import com.google.firebase.database.FirebaseDatabase
//import kotlinx.android.synthetic.main.new_task_taskdetail.*
//import kotlinx.android.synthetic.main.new_task_taskdetail.view.*
//import kotlinx.android.synthetic.main.task_editdetail.*
//import java.util.*
//import kotlin.collections.ArrayList
//import com.example.newevent2.ui.Functions.showDatePickerDialog
//
//class Task_EditDetail : AppCompatActivity() {
//
//    var task = Task()
//    var userid = ""
//    var eventid = ""
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.task_editdetail)
//
//        task = intent.getParcelableExtra("task")!!
//        userid = intent.getStringExtra("userid").toString()
//        eventid = intent.getStringExtra("eventid").toString()
//
////        val chipgroupedit = findViewById<ChipGroup>(R.id.groupedit)
////        chipgroupedit.isSingleSelection = true
////
////        // Create chips and select the one matching the category
////        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
////        for (category in list) {
////            val chip = Chip(this)
////            chip.text = category.en_name
////            chipgroupedit.addView(chip)
////            if (task.category == category.code) {
////                chip.isSelected = true
////                chipgroupedit.check(chip.id)
////            }
////        }
//
//        //---------------------------------------------------------------------------------//
//        //setSupportActionBar(findViewById(R.id.toolbar))
//        //supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
//        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//
//        val apptitle = findViewById<TextView>(R.id.appbartitle)
//        apptitle.text = "Task Detail"
//        //-------------------------------------------------------------------------------//
//
//        val tasktitle = findViewById<TextView>(R.id.tkname)
//        tasktitle.text = task.name
//
//        val taskdate = findViewById<TextView>(R.id.tkdate)
//        taskdate.text = task.date
//
////        val selectedchip = when (taskcategory) {
////            "flowers" -> findViewById<TextView>(R.id.chip)
////            "venue" -> findViewById<TextView>(R.id.chip2)
////            "photo" -> findViewById<TextView>(R.id.chip3)
////            "entertainment" -> findViewById<TextView>(R.id.chip4)
////            "transport" -> findViewById<TextView>(R.id.chip5)
////            "ceremony" -> findViewById<TextView>(R.id.chip6)
////            "accesories" -> findViewById<TextView>(R.id.chip7)
////            "beauty" -> findViewById<TextView>(R.id.chip8)
////            "food" -> findViewById<TextView>(R.id.chip9)
////            "guests" -> findViewById<TextView>(R.id.chip10)
////            else -> findViewById<TextView>(R.id.chip)
////        }
//
////        selectedchip.isSelected = true
////        groupedit.check(selectedchip.id)
////        groupedit.isSingleSelection = true
//
//        val taskbudget = findViewById<TextView>(R.id.tkbudget)
//        taskbudget.text = task.budget
//
//        tkname.setOnClickListener {
//            tkname.error = null
//        }
//
//        tkdate.setOnClickListener {
//            tkdate.error = null
//            tkdate.setText(showDatePickerDialog(supportFragmentManager))
//        }
//
//        button2.setOnClickListener()
//        {
//            var inputvalflag = true
//            if (tkname.text.toString().isEmpty()) {
//                tkname.error = "Task name is required!"
//                inputvalflag = false
//            }
//            if (tkdate.text.toString().isEmpty()) {
//                tkdate.error = "Task due date is required!"
//                inputvalflag = false
//            }
////            if (groupedit.checkedChipId == -1) {
////                Toast.makeText(this, "Category is required!", Toast.LENGTH_SHORT).show()
////                inputvalflag = false
////            }
//            if (inputvalflag) {
//                saveTask()
//                onBackPressed()
//            }
//        }
//    }
//
//
//    private fun saveTask() {
//        task.name = tkname.text.toString()
//        task.date = tkdate.text.toString()
//        task.budget = tkbudget.text.toString()
//
////        val chipselected = groupedit.findViewById<Chip>(groupedit.checkedChipId)
////        val chiptextvalue = chipselected.text.toString()
////
////        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
////        for (category in list) {
////            if(chiptextvalue == category.en_name){
////                task.category = category.code
////            }
////        }
//////
////        taskcategory = when (chiptextvalue) {
////            "Flowers & Deco" -> "flowers"
////            "Venue" -> "venue"
////            "Photo & Video" -> "photo"
////            "Entertainment" -> "entertainment"
////            "Transportation" -> "transport"
////            "Ceremony" -> "ceremony"
////            "Attire & Accessories" -> "accesories"
////            "Health & Beauty" -> "beauty"
////            "Food & Drink" -> "food"
////            "Guests" -> "guests"
////            else -> "none"
////        }
//
//        val taskmodel = TaskModel()
//        taskmodel.editTask(userid, eventid, task, object: TaskModel.FirebaseAddEditTaskSuccess {
//            override fun onTaskAddedEdited(flag: Boolean) {
//                TODO("Not yet implemented")
//            }
//        })
//        }
//    }