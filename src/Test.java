import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

class Node_T {
    boolean isHiddenInput;
    boolean isHiddenOutput;

    public Node_T(boolean isHiddenInput, boolean isHiddenOutput) {
        this.isHiddenInput = isHiddenInput;
        this.isHiddenOutput = isHiddenOutput;
    }

    @Override
    public String toString() {
        return "Node_N{" +
                "isHiddenInput=" + isHiddenInput +
                ", isHiddenOutput=" + isHiddenOutput +
                '}';
    }
}

public class Test {

    public static void main(String[] args) {
        // Create your linked list of Node_N
        LinkedList<Node_T> nodeList = new LinkedList<>();

        // Add some test data
        nodeList.add(new Node_T(false, true)); // Non-hidden input, hidden output
        nodeList.add(new Node_T(true, false)); // Hidden input, non-hidden output
        nodeList.add(new Node_T(true, true)); // Hidden input, hidden output
        nodeList.add(new Node_T(true, false)); // Non-hidden input, non-hidden output

        // Print the original list
        System.out.println("Original list:");
        for (Node_T node : nodeList) {
            System.out.println(node.toString());
        }

        // Sort the list
        sortNodeList(nodeList);

        // Print the sorted list
        System.out.println("\nSorted list:");
        for (Node_T node : nodeList) {
            System.out.println(node);
        }
    }

    public static void sortNodeList(LinkedList<Node_T> nodeList) {
        // Define custom comparator to sort the list
        Comparator<Node_T> comparator = new Comparator<Node_T>() {
            @Override
            public int compare(Node_T node1, Node_T node2) {
                // First, check if both nodes are hidden inputs or hidden outputs
                if ((node1.isHiddenInput && node1.isHiddenOutput) && (node2.isHiddenInput && node2.isHiddenOutput)) {
                    // If both are hidden inputs or hidden outputs, maintain their order
                    return 0;
                } else if (node1.isHiddenInput && !node2.isHiddenInput) {
                    // If only node1 is hidden input, it should come later in the list
                    return 1;
                } else if (!node1.isHiddenInput && node2.isHiddenInput) {
                    // If only node2 is hidden input, it should come later in the list
                    return -1;
                } else if (node1.isHiddenOutput && !node2.isHiddenOutput) {
                    // If only node1 is hidden output, it should come earlier in the list
                    return -1;
                } else if (!node1.isHiddenOutput && node2.isHiddenOutput) {
                    // If only node2 is hidden output, it should come earlier in the list
                    return 1;
                } else {
                    // If neither node is hidden input or hidden output, maintain their order
                    return 0;
                }
            }
        };

        // Sort the list using the custom comparator
        Collections.sort(nodeList, comparator);
    }
}
