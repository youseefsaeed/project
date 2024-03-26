package eu.tutorials.shaproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.tutorials.shaproject.db.StudentEntity
import kotlinx.android.synthetic.main.course_style.view.*
import kotlinx.android.synthetic.main.student_style_in_metrics.view.*


class AdapterStudent_metrics(
    private val context: Context,
    private var items: List<StudentEntity>
) : RecyclerView.Adapter<AdapterStudent_metrics.ViewHolder>(), Filterable {

    private var filteredList: List<StudentEntity> = items
    interface CourseClickListener {
        fun onCourseClicked(studentid: Int,studentname:String,studentgrade:Int,studentfaculty:String,studentattended:Int)
    }
    var courseClickListener: CourseClickListener? = null

    class ViewHolder(itemView: View, private val items: List<StudentEntity>, private val courseClickListener:CourseClickListener ):
        RecyclerView.ViewHolder(itemView){
        var student_name: TextView = itemView.student_name

        init {

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val course = items?.get(position)
                    val student_id = course?.studentId
                    val studentname = course?.name
                    val studentgrade=course?.grade
                    val studentfaculty=course?.faculty
                    val studentattended=course?.lectures
                    if (student_id != null) {
                        if (studentname != null) {
                            if (studentgrade != null) {
                                if (studentfaculty != null) {
                                    if (studentattended != null) {
                                        courseClickListener.onCourseClicked(student_id,studentname,studentgrade,studentfaculty,studentattended)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView= LayoutInflater.from(context).inflate(R.layout.student_style_in_metrics,parent,false)
        return ViewHolder(itemView,items,courseClickListener!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = filteredList?.get(position)
        val studentNumber = "${position + 1}. ${student?.name}"
        holder.student_name.text = studentNumber
    }

    override fun getItemCount(): Int {
        return filteredList?.size ?: 0
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchQuery = constraint.toString().toLowerCase().trim()
                filteredList = if (searchQuery.isEmpty()) {
                    items
                } else {
                    items?.filter { it.name.toLowerCase().startsWith(searchQuery) }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as? List<StudentEntity>)!!
                notifyDataSetChanged()
            }
        }
    }

    // Search function to filter data

    fun sortByName() {
        filteredList = filteredList?.sortedBy { it.name }
        notifyDataSetChanged()
    }

    fun sortByHigherAttendance(courseId: Int) {
        filteredList = filteredList?.sortedByDescending { it.lectures }
        notifyDataSetChanged()
    }

    fun sortByLowerAttendance(courseId: Int) {
        filteredList = filteredList?.sortedBy { it.lectures }
        notifyDataSetChanged()
    }
    fun search(query: String) {
        filter.filter(query)
    }

}