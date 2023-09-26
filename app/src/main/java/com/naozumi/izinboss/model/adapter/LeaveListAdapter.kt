package com.naozumi.izinboss.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.ItemRowLeaveBinding
import com.naozumi.izinboss.model.datamodel.LeaveRequest

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
                tvItemDate.text = leaveRequest.timeStamp
                tvItemType.text = leaveRequest.type.toString()
                tvItemStartDate.text = leaveRequest.startDate
                tvItemEndDate.text = leaveRequest.endDate
                tvItemReason.text = leaveRequest.reason
                btnItemStatus.isClickable = false
                when (leaveRequest.status) {
                    LeaveRequest.Status.APPROVED -> {
                        btnItemStatus.text = itemView.context.getString(R.string.approved)
                        btnItemStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
                        btnItemStatus.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.background_green))
                        ivItemPhoto.setImageResource(R.drawable.baseline_check_circle_outline_24)
                        ivItemPhoto.setColorFilter(ContextCompat.getColor(itemView.context, R.color.green))
                    }

                    LeaveRequest.Status.PENDING -> {
                        btnItemStatus.text = itemView.context.getString(R.string.pending)
                        btnItemStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.yellowish_orange))
                        btnItemStatus.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.background_yellow))
                        ivItemPhoto.setImageResource(R.drawable.baseline_access_time_24)
                        ivItemPhoto.setColorFilter(ContextCompat.getColor(itemView.context, R.color.yellowish_orange))
                    }

                    LeaveRequest.Status.REJECTED -> {
                        btnItemStatus.text = itemView.context.getString(R.string.rejected)
                        btnItemStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                        btnItemStatus.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.background_red))
                        ivItemPhoto.setImageResource(R.drawable.baseline_close_24)
                        ivItemPhoto.setColorFilter(ContextCompat.getColor(itemView.context, R.color.red))
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