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
    private var unfilteredList: List<StudentEntity> = items

    interface CourseClickListener {
        fun onCourseClicked(student: StudentEntity)
    }

    var courseClickListener: CourseClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var student: StudentEntity? = null
        var student_name: TextView = itemView.student_name

        init {
            itemView.setOnClickListener {
                student?.let { courseClickListener?.onCourseClicked(it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.student_style_in_metrics, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = filteredList[position]
        holder.student = student
        val studentNumber = "${position + 1}. ${student.name}"
        holder.student_name.text = studentNumber
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchQuery = constraint.toString().toLowerCase().trim()
                filteredList = if (searchQuery.isEmpty()) {
                    unfilteredList
                } else {
                    unfilteredList.filter {
                        it.name.toLowerCase().contains(searchQuery) ||
                                it.studentId.toString().contains(searchQuery)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as? List<StudentEntity>) ?: emptyList()
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
    fun updateData(newItems: List<StudentEntity>) {
        items = newItems
        unfilteredList = newItems
        notifyDataSetChanged()
    }
}