package com.naozumi.izinboss.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.ItemRowLeaveBinding
import com.naozumi.izinboss.model.local.Leave

class LeaveListAdapter : ListAdapter<Leave, LeaveListAdapter.ListViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LeaveListAdapter.ListViewHolder {
        val binding = ItemRowLeaveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class ListViewHolder(private var binding: ItemRowLeaveBinding) :
            RecyclerView.ViewHolder(binding.root) {
                fun bind(leave: Leave) {
                    with(binding) {
                        tvItemTitle.text = leave.title
                        tvItemDescription.text = leave.description
                        if (leave.timeStamp != null) {
                            binding.tvItemDate.text = DateUtils.getRelativeTimeSpanString(leave.timeStamp)
                        }
                        when {
                            leave.status.equals("0") -> {
                                tvItemStatus.text = "Approved"
                                ivItemPhoto.setImageResource(R.drawable.baseline_check_24)
                            }
                            leave.status.equals("1") -> {
                                tvItemStatus.text = "Rejected"
                                ivItemPhoto.setImageResource(R.drawable.baseline_close_24)
                            }
                            else -> {
                                tvItemStatus.text = "Pending"
                                ivItemPhoto.setImageResource(R.drawable.outline_pending_24)
                            }
                        }
                        itemView.setOnClickListener {
                            onItemClickCallback.onItemClicked(leave)
                        }
                    }
                }
            }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Leave)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Leave> =
            object : DiffUtil.ItemCallback<Leave>() {
                override fun areItemsTheSame(oldItem: Leave, newItem: Leave): Boolean {
                    return oldItem.title == newItem.title
                }

                override fun areContentsTheSame(oldItem: Leave, newItem: Leave): Boolean {
                    return oldItem == newItem
                }
            }
    }

}