package eu.tutorials.shaproject

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.course_style.view.*

class Adapter_coursesscreen (val context: Context, val items: List<CoursesX>) :
    RecyclerView.Adapter<Adapter_coursesscreen.ViewHolder>() {
    interface CourseClickListener {
        fun onCourseClicked(courseId: Int,coursename:String)
    }
    var courseClickListener: CourseClickListener? = null

    class ViewHolder(itemView:View,private val items: List<CoursesX>,private val courseClickListener:CourseClickListener ):
        RecyclerView.ViewHolder(itemView){
        var course_name: TextView = itemView.course_1

        init {

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val course = items[position]
                    val courseId = course.course_id
                    val coursename = course.name
                    courseClickListener?.onCourseClicked(courseId,coursename)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView=LayoutInflater.from(context).inflate(R.layout.course_style,parent,false)
        return ViewHolder(itemView,items,courseClickListener!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.course_name.text=items[position].name.toString()

    }

    override fun getItemCount(): Int {
        return items.size
    }

}