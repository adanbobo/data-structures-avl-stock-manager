
public class AVLTree<T extends Comparable<T>, D> {
    static public class Node<T, D> {
        T key;
        D data; // additional data
        int height, subtreeSize;
        Node<T, D> left, right, pred, succ;

        Node(T key, D data) {
            this.key = key;
            this.data = data;
            this.height = 1;
            this.subtreeSize = 1; // leaf
        }
    }

    private Node<T, D> root;

    private int height(Node<T, D> node) {
        return node == null ? 0 : node.height;
    }

    private int getSubtreeSize(Node<T, D> node) {
        return node == null ? 0 : node.subtreeSize;
    }

    private void updateNode(Node<T, D> node) {
        if (node != null) {
            node.height = Math.max(height(node.left), height(node.right)) + 1;
            node.subtreeSize = 1 + getSubtreeSize(node.left) + getSubtreeSize(node.right);
        }
    }

    private int getBalance(Node<T, D> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private Node<T, D> rightRotate(Node<T, D> y) {
        Node<T, D> x = y.left;
        Node<T, D> T2 = x.right;

        // rotations
        x.right = y;
        y.left = T2;
        updateNode(y);
        updateNode(x);

        return x; // new root
    }

    private Node<T, D> leftRotate(Node<T, D> x) {
        Node<T, D> y = x.right;
        Node<T, D> T2 = y.left;

        y.left = x;
        x.right = T2;
        updateNode(x);
        updateNode(y);

        return y;
    }

    private Node<T, D> balance(Node<T, D> node) {
        int balance = getBalance(node);

        // left heavy
        if (balance > 1) {
            if (getBalance(node.left) < 0) {
                node.left = leftRotate(node.left);
            }
            return rightRotate(node);
        }

        // right heavy
        if (balance < -1) {
            if (getBalance(node.right) > 0) {
                node.right = rightRotate(node.right);
            }
            return leftRotate(node);
        }

        return node;
    }

    public Node<T, D> find(T key) {
        Node<T, D> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) current = current.left;
            else if (cmp > 0) current = current.right;
            else return current;
        }

        return null;
    }

    public void insert(T key, D data) {
        root = insert(root, key, data, null, null);
    }

    private Node<T, D> insert(Node<T, D> node, T key, D data, Node<T, D> pred, Node<T, D> succ) {
        if (node == null) {
            Node<T, D> newNode = new Node<>(key, data);
            newNode.pred = pred;
            newNode.succ = succ;
            if (pred != null) pred.succ = newNode;
            if (succ != null) succ.pred = newNode;
            return newNode;
        }

        if (key.compareTo(node.key) < 0) {
            node.left = insert(node.left, key, data, pred, node);
        } else if (key.compareTo(node.key) > 0) {
            node.right = insert(node.right, key, data, node, succ);
        } else {
            // update existing node
            node.data = data;
        }

        updateNode(node);
        return balance(node);
    }

    public void delete(T key) {
        root = delete(root, key);
    }

    private Node<T, D> delete(Node<T, D> node, T key) {
        if (node == null) return null;

        if (key.compareTo(node.key) < 0) {
            node.left = delete(node.left, key);
        } else if (key.compareTo(node.key) > 0) {
            node.right = delete(node.right, key);
        } else {
            // update pred/succ pointers
            if (node.pred != null) node.pred.succ = node.succ;
            if (node.succ != null) node.succ.pred = node.pred;

            // node with only one child or no child
            if (node.left == null || node.right == null) {
                return (node.left != null) ? node.left : node.right;
            }

            // node with two children
            Node<T, D> temp = minValueNode(node.right);
            node.key = temp.key;
            node.data = temp.data;
            node.right = delete(node.right, temp.key);
        }

        updateNode(node);
        return balance(node);
    }

    private Node<T, D> minValueNode(Node<T, D> node) {
        Node<T, D> current = node;
        while (current.left != null) current = current.left;
        return current;
    }

    public Object[] getNodesInRange(T low, T high) {
        int size = countInRange(low, high);
        Object[] result = new Object[size];

        Node<T, D> smallest = findFirstGreaterOrEqual(root, low);
        int index = 0;

        while (smallest != null && smallest.key.compareTo(high) <= 0) {
            result[index++] = smallest.data;
            smallest = smallest.succ;
        }

        return result;
    }

    private Node<T, D> findFirstGreaterOrEqual(Node<T, D> node, T key) {
        Node<T, D> candidate = null;
        while (node != null) {
            if (node.key.compareTo(key) >= 0) {
                candidate = node;
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return candidate;
    }

    public int countInRange(T low, T high) {
        if (low.compareTo(high) > 0) return 0;

        // find rank of first element > high and first element >= low
        int highRank = getRankOfFirstGreater(root, high);
        int lowRank = getRankOfFirstGreaterOrEqual(root, low);

        return highRank - lowRank;
    }

    private int getRankOfFirstGreater(Node<T, D> node, T key) {
        if (node == null) return 0;

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            // node.key > key: so look only in the left subtree
            return getRankOfFirstGreater(node.left, key);
        } else {
            // node.key <= key: so count everything in left + this node + go right
            return getSubtreeSize(node.left) + 1 + getRankOfFirstGreater(node.right, key);
        }
    }

    private int getRankOfFirstGreaterOrEqual(Node<T, D> node, T key) {
        if (node == null) return 0;

        int cmp = key.compareTo(node.key);
        if (cmp <= 0) {
            // current node is >= key, look in left subtree
            return getRankOfFirstGreaterOrEqual(node.left, key);
        } else {
            // current node is < key, count left subtree + current node + look in right
            return getSubtreeSize(node.left) + 1 + getRankOfFirstGreaterOrEqual(node.right, key);
        }
    }
}
