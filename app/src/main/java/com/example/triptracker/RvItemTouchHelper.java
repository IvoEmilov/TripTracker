package com.example.triptracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class RvItemTouchHelper extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter ithAdapter;
    private Boolean drag, swipe;

    public RvItemTouchHelper(ItemTouchHelperAdapter ithAdapter, Boolean drag, Boolean swipe) {

        this.ithAdapter = ithAdapter;
        this.drag = drag;
        this.swipe = swipe;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        if(drag) return true;
        else return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        if(swipe) return true;
        else return false;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //set color back to original
        viewHolder.itemView.findViewById(R.id.bg_img_gradient).setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.gradient_minty));
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            //sets some color when dragged - just to test
            viewHolder.itemView.findViewById(R.id.bg_img_gradient).setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.color.gray));
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        System.out.println("MOVEMENT");
        ithAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        System.out.println("DRAGGING");
        ithAdapter.onItemSwiped(viewHolder.getAdapterPosition());

    }
}
