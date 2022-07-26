package ge.gmikeladze.messenger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.gmikeladze.messenger.R
import ge.gmikeladze.messenger.databinding.SearchUserBinding
import ge.gmikeladze.messenger.model.User

class SearchItemAdapter : RecyclerView.Adapter<SearchItemAdapter.SearchItemViewHolder>() {

    private var searchItems: List<User> = listOf()
    var onItemClickListener: SearchItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        return SearchItemViewHolder(
            SearchUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        holder.bind(searchItems[position])
        holder.binding.root.setOnClickListener {
            onItemClickListener!!.onItemClicked(searchItems[position])
        }
    }

    override fun getItemCount(): Int {
        return searchItems.size
    }

    fun updateSearchItems(searchItems: List<User>) {
        this.searchItems = searchItems
        notifyDataSetChanged()
    }

    inner class SearchItemViewHolder(val binding: SearchUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.searchItemNickname.text = user.nickname
            binding.searchItemProfession.text = user.profession
            Glide.with(binding.root).load(user.avatarUrl)
                .placeholder(R.drawable.avatar_image_placeholder).circleCrop()
                .into(binding.searchItemAvatar)
        }

    }
}

interface SearchItemClickListener {
    fun onItemClicked(user: User)
}