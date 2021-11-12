package com.example.habrrss
import android.os.Build
import android.text.Html
import androidx.recyclerview.widget.RecyclerView
import com.example.habrrss.databinding.FeedItemBinding
import android.text.Editable
import org.xml.sax.XMLReader
import android.text.method.LinkMovementMethod
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip

class FeedViewHolder(
    private val binding: FeedItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: FeedItem) {
            bindRichText(item.description)
            bindChips(item.categories)
            bindSimpleViews(item)
    }

    private fun bindSimpleViews(item: FeedItem) {
        with(binding) {
            itemTitle.text = item.title
            itemAuthor.text = item.creator
            itemDatePublished.text = item.date
        }
    }

    private fun bindRichText(description_nullable: String?) {
        val description = description_nullable ?: ""
        with(binding) {
            itemDescription.movementMethod = LinkMovementMethod.getInstance()
            itemDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                Html.fromHtml(
                    description,
                    Html.FROM_HTML_MODE_COMPACT,
                    URLImageParser(itemDescription),
                    { _: Boolean, _: String, _: Editable, _: XMLReader ->

                    }
                )
            else
                Html.fromHtml(description)
        }
    }

    private fun bindChips(
        categories: List<Category>?
    ) {
        with(binding) {
            itemChipContainer.removeAllViews()
            if (categories != null && categories.isNotEmpty()) {
                categories.forEach {
                    val chip = Chip(root.context)
                    chip.text = it.name
                    chip.setChipBackgroundColorResource(R.color.design_default_color_primary)
                    chip.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.design_default_color_on_primary
                        )
                    )
                    itemChipContainer.addView(chip)
                }
            }
        }
    }
}