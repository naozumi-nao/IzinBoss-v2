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
import com.naozumi.izinboss.util.GenericUtils

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
                        tvItemTitle.text = leave.id
                        tvItemDescription.text = leave.reason
                        tvItemDate.text = leave.timeStamp
                        when (leave.status) {
                            Leave.Status.APPROVED -> {
                                tvItemStatus.text = itemView.context.getString(R.string.approved)
                                ivItemPhoto.setImageResource(R.drawable.baseline_check_24)
                            }
                            Leave.Status.PENDING -> {
                                tvItemStatus.text = itemView.context.getString(R.string.pending)
                                ivItemPhoto.setImageResource(R.drawable.outline_pending_24)
                            }
                            Leave.Status.REJECTED -> {
                                tvItemStatus.text = itemView.context.getString(R.string.rejected)
                                ivItemPhoto.setImageResource(R.drawable.baseline_close_24)
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
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Leave, newItem: Leave): Boolean {
                    return oldItem == newItem
                }
            }
    }

}