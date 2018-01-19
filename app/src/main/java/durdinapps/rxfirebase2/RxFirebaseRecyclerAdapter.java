package durdinapps.rxfirebase2;


import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public abstract class RxFirebaseRecyclerAdapter<ViewHolder extends RecyclerView.ViewHolder, T>
    extends RecyclerView.Adapter<ViewHolder> {
    private Class<T> itemClass;
    private ArrayList<T> items;
    private ArrayList<String> keys;

    public RxFirebaseRecyclerAdapter(Class<T> itemClass) {
        this(itemClass, null, null);
    }

    public RxFirebaseRecyclerAdapter(Class<T> itemClass,
                                     @Nullable ArrayList<T> items,
                                     @Nullable ArrayList<String> keys) {

        if (items != null && keys != null) {
            this.items = items;
            this.keys = keys;
        } else {
            this.items = new ArrayList<T>();
            this.keys = new ArrayList<String>();
        }
        this.itemClass = itemClass;
    }

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(ViewHolder holder, final int position);

    @Override
    public int getItemCount() {
        return (items != null) ? items.size() : 0;
    }

    public void manageChildItem(RxFirebaseChildEvent<DataSnapshot> item) {
        switch (item.getEventType()) {
            case ADDED:
                addItem(item.getValue(), item.getPreviousChildName());
                break;
            case CHANGED:
                changeItem(item.getValue(), item.getPreviousChildName());
                break;
            case REMOVED:
                removeItem(item.getValue());
                break;
            case MOVED:
                onItemMoved(item.getValue(), item.getPreviousChildName());
                break;
        }
    }

    private void addItem(DataSnapshot dataSnapshot, String previousChildName) {
        String key = dataSnapshot.getKey();

        if (!keys.contains(key)) {
            T item = dataSnapshot.getValue(RxFirebaseRecyclerAdapter.this.itemClass);
            int insertedPosition;
            if (previousChildName == null) {
                items.add(0, item);
                keys.add(0, key);
                insertedPosition = 0;
            } else {
                int previousIndex = keys.indexOf(previousChildName);
                int nextIndex = previousIndex + 1;
                if (nextIndex == items.size()) {
                    items.add(item);
                    keys.add(key);
                } else {
                    items.add(nextIndex, item);
                    keys.add(nextIndex, key);
                }
                insertedPosition = nextIndex;
            }
            notifyItemInserted(insertedPosition);
            itemAdded(item, key, insertedPosition);
        }
    }

    public void changeItem(DataSnapshot dataSnapshot, String previousChildName) {
        String key = dataSnapshot.getKey();

        if (keys.contains(key)) {
            int index = keys.indexOf(key);
            T oldItem = items.get(index);
            T newItem = dataSnapshot.getValue(RxFirebaseRecyclerAdapter.this.itemClass);

            items.set(index, newItem);

            notifyItemChanged(index);
            itemChanged(oldItem, newItem, key, index);
        }
    }

    public void removeItem(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();

        if (keys.contains(key)) {
            int index = keys.indexOf(key);
            T item = items.get(index);

            keys.remove(index);
            items.remove(index);

            notifyItemRemoved(index);
            itemRemoved(item, key, index);
        }
    }

    public void onItemMoved(DataSnapshot dataSnapshot, String previousChildName) {
        String key = dataSnapshot.getKey();

        int index = keys.indexOf(key);
        T item = dataSnapshot.getValue(RxFirebaseRecyclerAdapter.this.itemClass);
        items.remove(index);
        keys.remove(index);
        int newPosition;
        if (previousChildName == null) {
            items.add(0, item);
            keys.add(0, key);
            newPosition = 0;
        } else {
            int previousIndex = keys.indexOf(previousChildName);
            int nextIndex = previousIndex + 1;
            if (nextIndex == items.size()) {
                items.add(item);
                keys.add(key);
            } else {
                items.add(nextIndex, item);
                keys.add(nextIndex, key);
            }
            newPosition = nextIndex;
        }
        notifyItemMoved(index, newPosition);
        itemMoved(item, key, index, newPosition);
    }

    ;


    /**
     * Returns the list of items of the adapter: can be useful when dealing with a configuration
     * change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of items of the adapter
     */
    public ArrayList<T> getItems() {
        return items;
    }

    /**
     * Returns the list of keys of the items of the adapter: can be useful when dealing with a
     * configuration change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of keys of the items of the adapter
     */
    public ArrayList<String> getKeys() {
        return keys;
    }

    /**
     * Returns the item in the specified position
     *
     * @param position Position of the item in the adapter
     * @return the item
     */
    public T getItem(int position) {
        return items.get(position);
    }

    /**
     * Returns the item in the specified position
     *
     * @param key Key of the item in the adapter
     * @return the item
     */
    public T getItemByKey(String key) {
        return items.get(keys.indexOf(key));
    }


    /**
     * Returns the position of the item in the adapter
     *
     * @param item Item to be searched
     * @return the position in the adapter if found, -1 otherwise
     */
    public int getPositionForItem(T item) {
        return items != null && items.size() > 0 ? items.indexOf(item) : -1;
    }

    /**
     * Returns the position of the item in the adapter
     *
     * @param key Key to be searched
     * @return the position in the adapter if found, -1 otherwise
     */
    public int getPositionForKey(String key) {
        return keys != null && keys.size() > 0 ? keys.indexOf(key) : -1;
    }

    /**
     * Check if the searched item is in the adapter
     *
     * @param item Item to be searched
     * @return true if the item is in the adapter, false otherwise
     */
    public boolean contains(T item) {
        return items != null && items.contains(item);
    }

    /**
     * ABSTRACT METHODS THAT MUST BE IMPLEMENTED BY THE EXTENDING ADAPTER.
     */


    /**
     * Called after an item has been added to the adapter
     *
     * @param item     Added item
     * @param key      Key of the added item
     * @param position Position of the added item in the adapter
     */
    protected abstract void itemAdded(T item, String key, int position);

    /**
     * Called after an item changed
     *
     * @param oldItem  Old version of the changed item
     * @param newItem  Current version of the changed item
     * @param key      Key of the changed item
     * @param position Position of the changed item in the adapter
     */
    protected abstract void itemChanged(T oldItem, T newItem, String key, int position);

    /**
     * Called after an item has been removed from the adapter
     *
     * @param item     Removed item
     * @param key      Key of the removed item
     * @param position Position of the removed item in the adapter
     */
    protected abstract void itemRemoved(T item, String key, int position);

    /**
     * Called after an item changed position
     *
     * @param item        Moved item
     * @param key         Key of the moved item
     * @param oldPosition Old position of the changed item in the adapter
     * @param newPosition New position of the changed item in the adapter
     */
    protected abstract void itemMoved(T item, String key, int oldPosition, int newPosition);

}
