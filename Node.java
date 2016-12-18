public class Node {
  protected Node prev;
  protected Node next;
  protected int data;
 
  // Constructor initializes data members.
  // Target Complexity: O(1)
  public Node(int data, Node prev, Node next) {
    this.data=data;
    this.prev=prev;
    this.next=next;
  }
 
  // Create a pretty representation of the Node
  // Format: [data]. Example: [3]
  // Target Complexity: O(1)
  public String toString() {
    return "["+data+"]";
  }
} 