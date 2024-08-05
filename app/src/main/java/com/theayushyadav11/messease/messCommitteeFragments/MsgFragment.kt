package com.theayushyadav11.messease.messCommitteeFragments

import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.FragmentMsg2Binding
import com.theayushyadav11.messease.models.Msg
import com.theayushyadav11.messease.utils.Mess

class MsgFragment : Fragment() {
  private lateinit var binding:FragmentMsg2Binding
  val auth= FirebaseAuth.getInstance()
    val database= FirebaseDatabase.getInstance().reference
    val storage= FirebaseStorage.getInstance().reference
    private lateinit var mess:Mess
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
     binding=FragmentMsg2Binding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
mess=Mess(requireContext())

        database.child("Messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<Msg>()

                for (data in snapshot.children) {
                    val message = data.getValue(Msg::class.java)
                    if (message != null&&message.creater==auth.currentUser?.displayName){
                        addMsg(message)
                        list.add(message)
                    }

                }
                if(list.isEmpty())
                {
                    binding.msg.visibility=View.VISIBLE
                }
                else
                {
                    binding.msg.visibility=View.GONE
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun addMsg(msg: Msg) {
        val layout=LayoutInflater.from(requireContext()).inflate(R.layout.msg_layout,binding.msgAdder,false)
        val title=layout.findViewById<TextView>(R.id.title)
        val body=layout.findViewById<TextView>(R.id.body)
        val time=layout.findViewById<TextView>(R.id.time)
        val creator=layout.findViewById<TextView>(R.id.creater)
        val adder=layout.findViewById<LinearLayout>(R.id.adder)
        val delete=layout.findViewById<ImageView>(R.id.delete)
        delete.visibility=View.VISIBLE
        title.text=msg.title
        body.text=msg.body
        time.text=msg.time
        creator.text=msg.creater
        Log.d("maneesh",msg.photos.toString())
        for( url in msg.photos)
        {

            val img=LayoutInflater.from(requireContext()).inflate(R.layout.img,adder,false)
            Picasso.get()
                .load(url)
                .into(img.findViewById<ImageView>(R.id.img))

            adder.addView(img)
        }
         delete.setOnClickListener{

             val dialog= AlertDialog.Builder(requireContext())
             dialog.setTitle("Alert")
             dialog.setMessage("Are you sure you want to delete this message?")
             dialog.setPositiveButton("Yes"){dialog,_->
                 mess.addPb("Deleting message...")
                 database.child("Messages").child(msg.uid).removeValue().addOnCompleteListener {
                     if(it.isSuccessful)
                     {

                         for(i in msg.photos)
                         {
                             val ref=FirebaseStorage.getInstance().getReferenceFromUrl(i)
                             ref.delete().addOnCompleteListener {
                                 if(it.isSuccessful)
                                 {
                                     if(i==msg.photos.last())
                                     {
                                         mess.toast("Message deleted")
                                         binding.msgAdder.removeView(layout)
                                         mess.pbDismiss()
                                     }
                                 }else
                                 {
                                     mess.toast(it.exception?.message.toString())
                                     mess.pbDismiss()
                                 }
                             }
                         }
                         if(msg.photos.size==0)
                         mess.pbDismiss()

                     }
                     else
                     {
                         mess.toast(it.exception?.message.toString())
                         mess.pbDismiss()
                     }
                 }

                 dialog.dismiss()
             }
             dialog.setNegativeButton("No"){dialog,_->
                 dialog.dismiss()
             }
             dialog.show()




         }
        binding.msgAdder.addView(layout)
    }
}