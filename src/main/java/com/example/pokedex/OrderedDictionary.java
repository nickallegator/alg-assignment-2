package com.example.pokedex;

public class OrderedDictionary {

    private int size = 0;

    private class TreeNode {
        ObjectRecord data;
        TreeNode left, right;

        int leftSubtreeSize;

        TreeNode(ObjectRecord r) {
            this.data = r;
            left = right = null;
        }
    }

    private TreeNode root;

    private TreeNode smallest = null;

    private TreeNode largest = null;

    public ObjectRecord find(DataKey k) {
        return find(root, k);
    }

    private ObjectRecord find(TreeNode node, DataKey k) {
        if (node == null) return null;

        if (k.compareTo(node.data.key) == 0) return node.data;
        if (k.compareTo(node.data.key) < 0) return find(node.left, k);
        else return find(node.right, k);
    }

    public void insert(ObjectRecord r) {
        root = insert(root, r);
        size++;
        // Check and set smallest based on height
        if (smallest == null || r.key.height < smallest.data.key.height) {
            smallest = new TreeNode(r);
        }

        // Check and set largest based on height
        if (largest == null || r.key.height > largest.data.key.height) {
            largest = new TreeNode(r);
        }
    }

    private TreeNode insert(TreeNode node, ObjectRecord r) {
        if (node == null) return new TreeNode(r);

        if (r.key.compareTo(node.data.key) < 0) {
            node.left = insert(node.left, r);
            node.leftSubtreeSize++;
        } else if (r.key.compareTo(node.data.key) > 0) {
            node.right = insert(node.right, r);
        }

        return node;
    }

    public void delete(DataKey k) {
        root = delete(root, k);
    }

    private TreeNode delete(TreeNode node, DataKey k) {
        if (node == null) return null; // Item not found, do nothing

        if (k.compareTo(node.data.key) < 0) {
            node.left = delete(node.left, k);
            node.leftSubtreeSize--;
        } else if (k.compareTo(node.data.key) > 0) {
            node.right = delete(node.right, k);
        } else {
            // node with only one child or no child
            if ((node.left == null) || (node.right == null)) {
                TreeNode temp = null;
                if (temp == node.left)
                    temp = node.right;
                else
                    temp = node.left;

                // No child
                if (temp == null) {
                    temp = node;
                    node = null;
                } else {
                    node = temp;
                }
                size--;
            } else {
                // node with two children
                node.data = minValue(node.right);

                node.right = delete(node.right, node.data.key);
            }
        }

        // Ensure smallest or largest was not deleted
        if (node != null && (smallest == null || node.data.key.height < smallest.data.key.height)) {
            smallest = node;
        }

        if (node != null && (largest == null || node.data.key.height > largest.data.key.height)) {
            largest = node;
        }

        return node;
    }

    private ObjectRecord minValue(TreeNode node) {
        ObjectRecord minValue = node.data;
        while (node.left != null) {
            minValue = node.left.data;
            node = node.left;
        }
        return minValue;
    }

    public void clear() {
        root = null;
        size = 0;
    }



    public ObjectRecord getByIndex(int index) {
        return getByIndex(root, index);
    }

    private ObjectRecord getByIndex(TreeNode node, int index) {
        if (node == null) return null;

        if (node.leftSubtreeSize == index) return node.data;
        if (index < node.leftSubtreeSize) return getByIndex(node.left, index);
        return getByIndex(node.right, index - node.leftSubtreeSize - 1);
    }

    public ObjectRecord getSmallest() {
        return (smallest == null) ? null : smallest.data;
    }

    public ObjectRecord getLargest() {
        return (largest == null) ? null : largest.data;
    }

    public int size() {
        return size;
    }


}

class ObjectRecord {
    DataKey key;
    Object value;

    ObjectRecord(DataKey k, Object v) {
        key = k;
        value = v;
    }
}

class DataKey implements Comparable<DataKey> {
    String name;

    int height;

    DataKey(String n, int h) {
        name = n;
        height = h;
    }

    @Override
    public int compareTo(DataKey other) {
        return this.name.compareTo(other.name);
    }
}
