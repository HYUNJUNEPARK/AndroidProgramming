package com.example.sqlite

import android.graphics.Color
import java.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlite.databinding.ItemRecyclerBinding

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.Holder>() {
    var helper:SQLiteHelper? = null
    var listData = mutableListOf<Memo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val memo = listData.get(position)
        holder.setMemo(memo)
    }

    inner class Holder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        var mMemo:Memo? = null

        init {
            binding.buttonDelete.setOnClickListener {
                helper?.deleteMemo(mMemo!!)
                listData.remove(mMemo)
                notifyDataSetChanged()
            }
            binding.buttonUpdate.setOnClickListener {
                if(binding.buttonUpdate.text == "수정"){
                    binding.textEditor.visibility = View.VISIBLE
                    binding.buttonUpdate.text = "저장"
                    binding.buttonUpdate.setTextColor(Color.RED)
                    binding.textEditor.setText("${mMemo?.content}")
                } else {
                    binding.textEditor.visibility = View.INVISIBLE
                    binding.buttonUpdate.text = "수정"
                    binding.buttonUpdate.setTextColor(Color.BLACK)
                    var content = binding.textEditor.text.toString()
                    val memo = Memo(mMemo?.no, content, System.currentTimeMillis())
                    helper?.updateMemo(memo)
                    listData.clear()
                    listData.addAll(helper!!.selectMemo())
                    notifyDataSetChanged()
                }
            }
        }

        /*
          Memo 데이터를 item_recycler UI 에 세팅해줌
          Holder 내부 변수 mMemo 에 position 에 해당하는 Memo 데이터 객체를 저장해 삭제 준비
          삭제 버튼이 눌리면 listData 에서 mMemo 와 같은 데이터를 삭제
          데이터가 변할 수 있기 때문에 mutableListOf<> 를 사용
        */
        fun setMemo(memo:Memo) {
            binding.textNo.text = "${memo.no}"
            binding.textContent.text = memo.content
            val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm")
            binding.textDatetime.text = "${sdf.format(memo.datetime)}"
            this.mMemo = memo
        }
    }
}

