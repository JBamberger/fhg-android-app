package de.jbamberger.fhgapp.ui.components

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class DataBindingAdapter
constructor(private val items: MutableList<Item> = ArrayList<Item>()) : DataBindingBaseAdapter() {

    fun replaceAll(items: List<Item>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun add(item: Item) {
        this.items.add(item)
        notifyDataSetChanged()
    }

    fun add(index: Int, item: Item) {
        this.items.add(index, item)
        notifyItemInserted(index)
    }

    fun remove(index: Int) {
        this.items.removeAt(index)
        notifyItemRemoved(index)
    }

    fun update(index: Int, item: Item) {
        this.items.removeAt(index)
        this.items.add(index, item)
        notifyItemChanged(index)
    }


    override fun getObjForPosition(position: Int): Any? {
        return items[position].obj
    }

    override fun getListenerForPosition(position: Int): Any? {
        return items[position].listener
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return items[position].layoutId
    }

    override fun getItemCount(): Int {
        return items.size
    }

    data class Item(val layoutId: Int, val obj: Any?, val listener: Any?)
}