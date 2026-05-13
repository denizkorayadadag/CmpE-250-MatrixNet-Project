/**
 * A Stack implementation
 */
public class Stack {
    private Node top;
    private int size;

    private static class Node {
        RouteState data;
        Node next;

        Node(RouteState data) {
            this.data = data;
        }
    }

    public Stack() {
        this.top = null;
        this.size = 0;
    }

    public void push(RouteState item) {
        Node newNode = new Node(item);
        newNode.next = top;
        top = newNode;
        size++;
    }

    public RouteState pop() {
        if (isEmpty()) {
            return null;
        }
        RouteState data = top.data;
        top = top.next;
        size--;
        return data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }
}
