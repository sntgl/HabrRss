package com.example.habrrss
import android.os.Build
import android.text.Html
import androidx.recyclerview.widget.RecyclerView
import com.example.habrrss.databinding.FeedItemBinding
import android.text.Editable
import org.xml.sax.XMLReader
import android.text.method.LinkMovementMethod

class FeedViewHolder(
    private val binding: FeedItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: FeedItem) {
        with(binding) {
            itemDescription.movementMethod = LinkMovementMethod.getInstance()
            itemDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                Html.fromHtml(
                    item.description,
                    Html.FROM_HTML_MODE_COMPACT,
                    URLImageParser(itemDescription),
                    { _: Boolean, _: String, _: Editable, _: XMLReader ->

                    }
                )
            else
                Html.fromHtml(item.description)
            itemTitle.text = item.title
        }
    }
}