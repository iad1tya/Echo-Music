package com.alexmercerind.audire.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.CachePolicy
import com.alexmercerind.audire.R
import com.alexmercerind.audire.databinding.HistoryItemBinding
import com.alexmercerind.audire.mappers.toMusic
import com.alexmercerind.audire.models.HistoryItem
import com.alexmercerind.audire.ui.HistoryViewModel
import com.alexmercerind.audire.ui.MusicActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HistoryItemAdapter(
    var items: List<HistoryItem>,
    private val historyViewModel: HistoryViewModel,
) : RecyclerView.Adapter<HistoryItemAdapter.HistoryItemViewHolder>() {

    inner class HistoryItemViewHolder(val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HistoryItemViewHolder(
        HistoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.binding.apply {
            val context = root.context
            coverImageView.load(
                items[position].cover,
                ImageLoader.Builder(context).memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED).build()
            ) {
                crossfade(true)
            }
            titleTextView.text = items[position].title
            artistTextView.text = items[position].artists
            root.isLongClickable = true
            root.setOnClickListener {
                Intent(context, MusicActivity::class.java).also {
                    it.putExtra(MusicActivity.MUSIC, items[position].toMusic())
                    context.startActivity(it)
                }
            }
            root.setOnLongClickListener {
                MaterialAlertDialogBuilder(
                    root.context, R.style.Base_Theme_Audire_MaterialAlertDialog
                ).setTitle(R.string.remove_history_item_title).setMessage(
                    context.getString(
                        R.string.remove_history_item_message, items[position].title
                    )
                ).setPositiveButton(R.string.yes) { dialog, _ ->
                    dialog.dismiss()
                    historyViewModel.delete(items[position])
                }.setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }.show()
                true
            }

            likedImageView.visibility = if (items[position].liked) View.VISIBLE else View.GONE
            unlikedImageView.visibility = if (!items[position].liked) View.VISIBLE else View.GONE

            likeFrameLayout.setOnClickListener {
                if (items[position].liked) {
                    historyViewModel.unlike(items[position])
                } else {
                    historyViewModel.like(items[position])
                }
            }
        }

    }
}
