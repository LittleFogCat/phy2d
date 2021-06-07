package top.littlefogcat.bouncedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main2.*
import java.lang.IllegalStateException

class MainActivity2 : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity2"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        rv.layoutManager = LinearLayoutManager(this)
        val adapter = A()
        rv.adapter = adapter

        adapter.submitList(
            mutableListOf(
                "alice", "bob", "cindy", "doge", "edge", "frank", "grey", "holy", "ires",
                "alice", "bob", "cindy", "doge", "edge", "frank", "grey", "holy", "ires",
                "alice", "bob", "cindy", "doge", "edge", "frank", "grey", "holy", "ires",
                "alice", "bob", "cindy", "doge", "edge", "frank", "grey", "holy", "ires",
                "alice", "bob", "cindy", "doge", "edge", "frank", "grey", "holy", "ires",
                "alice", "bob", "cindy", "doge", "edge", "frank", "grey", "holy", "ires",
                "alice", "bob", "cindy", "doge", "edge", "frank", "grey", "holy", "ires",
                "alice", "bob", "cindy", "doge", "edge", "frank", "grey", "holy", "ires",
            )
        )

        Handler(Looper.getMainLooper()).postDelayed({
            rv.smoothScrollToPosition(14)
        }, 2000)

        rv.post {
            rv.post {
                rv.post {
                    Log.d(TAG, "onCreate: Post!")
                }
            }
        }
    }

    inner class A : ListAdapter<String, A.VH>(object : DiffUtil.ItemCallback<String?>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }) {
        inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(TextView(this@MainActivity2).also {
                it.setPadding(60, 60, 60, 60)
                it.textSize = 30f
            })
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val tv = holder.itemView as TextView
            tv.text = getItem(position)
        }
    }

}