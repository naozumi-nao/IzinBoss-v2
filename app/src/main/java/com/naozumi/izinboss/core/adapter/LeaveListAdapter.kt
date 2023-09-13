package com.naozumi.izinboss.core.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.ItemRowLeaveBinding
import com.naozumi.izinboss.core.model.local.LeaveRequest

class LeaveListAdapter : ListAdapter<LeaveRequest, LeaveListAdapter.ListViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val binding = ItemRowLeaveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class ListViewHolder(private var binding: ItemRowLeaveBinding) :
            RecyclerView.ViewHolder(binding.root) {
                fun bind(leaveRequest: LeaveRequest) {
                    with(binding) {
                        tvItemName.text = leaveRequest.employeeName
                        tvItemDescription.text = leaveRequest.reason
                        tvItemDate.text = leaveRequest.timeStamp
                        when (leaveRequest.status) {
                            LeaveRequest.Status.APPROVED -> {
                                tvItemStatus.text = itemView.context.getString(R.string.approved)
                                ivItemPhoto.setImageResource(R.drawable.baseline_check_24)
                            }
                            LeaveRequest.Status.PENDING -> {
                                tvItemStatus.text = itemView.context.getString(R.string.pending)
                                ivItemPhoto.setImageResource(R.drawable.outline_pending_24)
                            }
                            LeaveRequest.Status.REJECTED -> {
                                tvItemStatus.text = itemView.context.getString(R.string.rejected)
                                ivItemPhoto.setImageResource(R.drawable.baseline_close_24)
                            }
                        }
                        itemView.setOnClickListener {
                            onItemClickCallback.onItemClicked(leaveRequest)
                        }
                    }
                }
            }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: LeaveRequest)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<LeaveRequest> =
            object : DiffUtil.ItemCallback<LeaveRequest>() {
                override fun areItemsTheSame(oldItem: LeaveRequest, newItem: LeaveRequest): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: LeaveRequest, newItem: LeaveRequest): Boolean {
                    return oldItem == newItem
                }
            }
    }

}