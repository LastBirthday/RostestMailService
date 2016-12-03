class Queue {

    private int size;
    public int top;
    public int bottom;
    private int queue[];
    private int maxSize;

    public Queue (int size) {
        maxSize = size;
        queue = new int[maxSize];
        this.size = 0;
        top = bottom = 0;
    }

    public boolean push (int val) {
        if (maxSize == size) return false;
        if (size == 0) {
            queue[top] = val;
            size++;
            return true;
        }
        if (top == maxSize - 1) {
            top = 0;
            queue[top] = val;
            size++;
            return true;
        }
        queue[top+1] = val;
        top++;
        size++;
        return true;
    }

    public int pop () {
        if (size <= 0) return 0;
        if (bottom == top) {
            size--;
            return queue[bottom];
        }
        if (bottom == maxSize - 1) {
            bottom = 0;
            size--;
            System.out.println(bottom + " " +  top);
            return queue[maxSize-1];
        }
        bottom++;
        size--;
        return queue[bottom-1];
    }

}

public class Main {

    public static void main(String[] args) {

        Queue queue = new Queue(5);

        queue.push(1);
        System.out.println(queue.bottom + "||" + queue.top);
        queue.push(2);
        System.out.println(queue.bottom + "||" + queue.top);
        queue.push(3);
        System.out.println(queue.bottom + "||" + queue.top);
        queue.push(4);
        System.out.println(queue.bottom + "||" + queue.top);
        queue.push(5);
        System.out.println(queue.bottom + "||" + queue.top);

        System.out.println(queue.pop());
        System.out.println(queue.bottom + "||" + queue.top);
        System.out.println(queue.pop());
        System.out.println(queue.bottom + "||" + queue.top);
        System.out.println(queue.pop());
        System.out.println(queue.bottom + "||" + queue.top);
        System.out.println(queue.pop());
        System.out.println(queue.bottom + "||" + queue.top);
        System.out.println(queue.pop());
        System.out.println(queue.bottom + "||" + queue.top);

        queue.push(1);
        System.out.println(queue.bottom + "||" + queue.top);
        queue.push(2);
        System.out.println(queue.bottom + "||" + queue.top);
        queue.push(3);
        System.out.println(queue.bottom + "||" + queue.top);
        queue.push(4);
        System.out.println(queue.bottom + "||" + queue.top);
        queue.push(5);
        System.out.println(queue.bottom + "||" + queue.top);

        System.out.println(queue.pop());
        System.out.println(queue.bottom + "||" + queue.top);
        System.out.println(queue.pop());
        System.out.println(queue.bottom + "||" + queue.top);
        System.out.println(queue.pop());
        System.out.println(queue.bottom + "||" + queue.top);
        System.out.println(queue.pop());
        System.out.println(queue.bottom + "||" + queue.top);
        System.out.println(queue.pop());
        System.out.println(queue.bottom + "||" + queue.top);

    }
}
