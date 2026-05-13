import java.util.ArrayList;

public class MinHeap {
    private ArrayList<RouteState> heap;

    public MinHeap() {
        this.heap = new ArrayList<>();
    }
    public void insert(RouteState node) {
        heap.add(node);
        percolateUp(heap.size() - 1);
    }

    /**
     * Removes and returns the minimum element (best RouteState)
     */
    public RouteState extractMin() {
        if (heap.isEmpty())
            return null;

        RouteState top = heap.get(0);
        int lastIdx = heap.size() - 1;
        RouteState bottom = heap.remove(lastIdx);

        if (!heap.isEmpty()) {
            heap.set(0, bottom);
            percolateDown(0);
        }

        return top;
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void resetHeap() {
        heap.clear();
    }

    /**
     * Percolates an inserted element from the leaves to the root
     */
    private void percolateUp(int index) {
        if (index == 0)
            return;
        int parentIndex = (index - 1) / 4;
        RouteState current = heap.get(index);
        RouteState parent = heap.get(parentIndex);
        if (compareRoute(current, parent) < 0) {
            heap.set(index, parent);
            heap.set(parentIndex, current);
            percolateUp(parentIndex);
        }
    }

    private void percolateDown(int index) {
        int firstChild = 4 * index + 1;
        int smallest = index;
        int size = heap.size();

        for (int k = 0; k < 4; k++) {
            int childIdx = firstChild + k;
            if (childIdx < size && compareRoute(heap.get(childIdx), heap.get(smallest)) < 0) {
                smallest = childIdx;
            }
        }
        if (smallest != index) {
            RouteState current = heap.get(index);
            RouteState target = heap.get(smallest);
            heap.set(index, target);
            heap.set(smallest, current);
            percolateDown(smallest);
        }
    }

    /**
     * Compares two routes
     */
    private int compareRoute(RouteState a, RouteState b) {
        if (Math.abs(a.totalCost - b.totalCost) > Main.error) {
            if (a.totalCost < b.totalCost) {
                return -1;
            } else {
                return 1;
            }
        }
        if (a.hopCount != b.hopCount) {
            return a.hopCount - b.hopCount;
        }

        return a.getPath().compareTo(b.getPath());
    }
}
