package com.theayushyadav11.messease.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.models.Option
import com.theayushyadav11.messease.models.Poll
class PollsAdapter(private val pollsList: MutableList<Poll>,private val context: Context) : RecyclerView.Adapter<PollsAdapter.PollViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.poll_layout, parent, false)
        return PollViewHolder(view)
    }

    override fun onBindViewHolder(holder: PollViewHolder, position: Int) {
        val poll = pollsList[position]
        holder.bind(poll)
        holder.deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Alert!")
            builder.setCancelable(false)
            builder.setMessage("Are you sure you want to delete? \n This cannot be undone!.")
            builder.setPositiveButton("Ok") { dialog, which ->
                deleteItem(position,poll.uid)
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        }


    override fun getItemCount(): Int {
        return pollsList.size
    }

    class PollViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val question: TextView = itemView.findViewById(R.id.tvQuestion)
        private val name: TextView = itemView.findViewById(R.id.tvname)
        private val linearLayout: LinearLayout = itemView.findViewById(R.id.radioGroup)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete)

        fun bind(poll: Poll) {
            question.text = poll.question
            name.text = poll.creater
            linearLayout.removeAllViews()
            addOptions(linearLayout, poll.options, poll)
        }

        private fun addOptions(linearLayout: LinearLayout, options: MutableList<String>, poll:Poll) {
            for (option in options) {
                val view = LayoutInflater.from(linearLayout.context).inflate(R.layout.poll_layout_element, linearLayout, false)
                val optTitle = view.findViewById<TextView>(R.id.title)
                val optRb = view.findViewById<RadioButton>(R.id.rb)
                optRb.visibility=View.INVISIBLE
                val optNop = view.findViewById<TextView>(R.id.nop)
                val optPb = view.findViewById<ProgressBar>(R.id.ProgressBar)
                var count = 0
                FirebaseDatabase.getInstance().reference.child("pollResult").child(poll.uid)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            count = 0
                            var tv = 0
                            for (child in snapshot.children) {
                                tv++
                                if (child.value.toString() == option)
                                    count++
                            }
                            val optPb = view.findViewById<ProgressBar>(R.id.ProgressBar)
                            var progress = 0
                            if (tv > 0) {
                                progress = ((count) * 100) / tv
                            }
                            optTitle.text = option
                            optNop.text = (count).toString()
                            optPb.progress = progress
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })


            // Update progress calculation
                linearLayout.addView(view)
            }
        }
    }
    fun deleteItem(position: Int,uid:String) {

        FirebaseDatabase.getInstance().reference.child("polls").child(pollsList[position].uid).removeValue()
        FirebaseDatabase.getInstance().reference.child("pollResult").child(pollsList[position].uid).removeValue()
        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("polls").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(p in snapshot.children)
                {
                    try {
                        if(p.value.toString()==uid)
                        {
                            FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("polls").child(p.key.toString()).removeValue()
                            break
                        }
                    } catch (e: Exception) {
                    
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        pollsList.removeAt(position)
        notifyItemRemoved(position)
    }
}
