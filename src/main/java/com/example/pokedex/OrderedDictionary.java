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
    private int smallInd = 0;

    private TreeNode largest = null;

    private int largeInd = 0;

    public ObjectRecord find(DataKey k) {
        return find(root, k);
    }

    private ObjectRecord find(TreeNode node, DataKey k) {
        if (node == null) return null;

        if (k.compareTo(node.data.key) == 0) return node.data;
        if (k.compareTo(node.data.key) < 0) return find(node.left, k);
        else return find(node.right, k);
    }

    private TreeNode findNode(DataKey k) {
        return findNode(root, k);
    }
    private TreeNode findNode(TreeNode node, DataKey k) {
        if (node == null) return null;

        if (k.compareTo(node.data.key) == 0) return node;
        if (k.compareTo(node.data.key) < 0) return findNode(node.left, k);
        else return findNode(node.right, k);
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

    public void remove(DataKey k) {
        root = remove(root, k);

        // Update smallest or largest if deleted
        if (k.compareTo(smallest.data.key) == 0){
            TreeNode node = getNodeByIndex(0);
            smallest = node;
            findSmallest();

        } else if (k.compareTo(largest.data.key) == 0){
            TreeNode node = getNodeByIndex(0);
            largest = node;
            findLargest();
        }
    }

    private void findSmallest(){
        findSmallest(getNodeByIndex(0));
    }

    private void findSmallest(TreeNode node){
        if (node.data.key.height < smallest.data.key.height) {
            smallest = node;
        }
        if (node.right != null) { findSmallest(node.right); }
    }
    private void findLargest(){
        findLargest(getNodeByIndex(0));
    }

    private void findLargest(TreeNode node){
        if (node.data.key.height > largest.data.key.height) {
            largest = node;
        }
        if (node.right != null) { findLargest(node.right); }
    }

    private TreeNode remove(TreeNode node, DataKey k) {
        if (node == null) return null; // Item not found, do nothing

        if (k.compareTo(node.data.key) < 0) {
            node.left = remove(node.left, k);
            node.leftSubtreeSize--;
        } else if (k.compareTo(node.data.key) > 0) {
            node.right = remove(node.right, k);
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

                node.right = remove(node.right, node.data.key);
            }
        }

        return node;
    }

    private TreeNode successor(DataKey k){
        TreeNode node = findNode(k);
        return (node.right == null) ? null : node.right;
    }
    private TreeNode predecessor(DataKey k){
        TreeNode node = findNode(k);
        return (node.left == null) ? null : node.left;
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

    private TreeNode getNodeByIndex(int index) {
        return getNodeByIndex(root, index);
    }
    private TreeNode getNodeByIndex(TreeNode node, int index) {
        if (node == null) return null;

        if (node.leftSubtreeSize == index) return node;
        if (index < node.leftSubtreeSize) return getNodeByIndex(node.left, index);
        return getNodeByIndex(node.right, index - node.leftSubtreeSize - 1);
    }

    public ObjectRecord getSmallest() {
        return (smallest == null) ? null : smallest.data;
    }

    public ObjectRecord getLargest() {
        return (largest == null) ? null : largest.data;
    }

    public int getSmallestIndex(){
        //nonfunctioning
        return (smallest == null) ? null : findNode(smallest.data.key).leftSubtreeSize;
    }
    public int getLargestIndex(){
        //nonfunctioning
        return (largest == null) ? null : findNode(largest.data.key).leftSubtreeSize;
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
