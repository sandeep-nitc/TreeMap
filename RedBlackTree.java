import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
class RedBlackTree {
    //private static final String FILENAME = "C:\\Users\\sandeep kumar\\Desktop\\MAP\\KEY_VALUE.txt";

    public enum Color {
        RED,
        BLACK
    }

    public static class Node {
        int key;
        int value;
        Color color;
        Node left;
        Node right;
        Node parent;
        boolean isNullLeaf;
    }

    private static Node createBlackNode(int data,int value) {
        Node node = new Node();
        node.key = data;
        node.value=value;
        node.color = Color.BLACK;
        node.left = createNullLeafNode(node);
        node.right = createNullLeafNode(node);
        return node;
    }

    private static Node createNullLeafNode(Node parent) {
        Node leaf = new Node();
        leaf.color = Color.BLACK;
        leaf.isNullLeaf = true;
        leaf.parent = parent;
        return leaf;
    }

    private static Node createRedNode(Node parent, int data,int value) {
        Node node = new Node();
        node.key = data;
        node.value=value;
        node.color = Color.RED;
        node.parent = parent;
        node.left = createNullLeafNode(node);
        node.right = createNullLeafNode(node);
        return node;
    }

    synchronized public Node put(Node root, int data,int value) {
        return insert(null, root, data,value);
    }

    public void printRedBlackTree(Node root) {
        if(root!=null)
        {
        printRedBlackTree(root.left);
        if(root.key!=0)
            System.out.print(root.key+" ");
        printRedBlackTree(root.right);
        }
        //printRedBlackTree(root, 0);
    }
    public boolean validateRedBlackTree(Node root) {

        if(root == null) {
            return true;
        }
        if(root.color != Color.BLACK) {
            System.out.print("Root is not black");
            return false;
        }
        AtomicInteger blackCount = new AtomicInteger(0);
        return checkBlackNodesCount(root, blackCount, 0) && noRedRedParentChild(root, Color.BLACK);
    }

    private void rightRotate(Node root, boolean changeColor) {
        Node parent = root.parent;
        root.parent = parent.parent;
        if(parent.parent != null) {
            if(parent.parent.right == parent) {
                parent.parent.right = root;
            } else {
                parent.parent.left = root;
            }
        }
        Node right = root.right;
        root.right = parent;
        parent.parent = root;
        parent.left = right;
        if(right != null) {
            right.parent = parent;
        }
        if(changeColor) {
            root.color = Color.BLACK;
            parent.color = Color.RED;
        }
    }

    private void leftRotate(Node root, boolean changeColor) {
        Node parent = root.parent;
        root.parent = parent.parent;
        if(parent.parent != null) {
            if(parent.parent.right == parent) {
                parent.parent.right = root;
            } else {
                parent.parent.left = root;
            }
        }
        Node left = root.left;
        root.left = parent;
        parent.parent = root;
        parent.right = left;
        if(left != null) {
            left.parent = parent;
        }
        if(changeColor) {
            root.color = Color.BLACK;
            parent.color = Color.RED;
        }
    }

    private Optional<Node> findSiblingNode(Node root) {
        Node parent = root.parent;
        if(isLeftChild(root)) {
            return Optional.ofNullable(parent.right.isNullLeaf ? null : parent.right);
        } else {
            return Optional.ofNullable(parent.left.isNullLeaf ? null : parent.left);
        }
    }

    private boolean isLeftChild(Node root) {
        Node parent = root.parent;
        if(parent.left == root) {
            return true;
        } else {
            return false;
        }
    }

    private Node insert(Node parent, Node root, int data,int value) {
        if(root  == null || root.isNullLeaf) {
            if(parent != null) {
                return createRedNode(parent, data,value);
            } else {
                return createBlackNode(data,value);
            }
        }
        if(root.key == data) {
            throw new IllegalArgumentException("Duplicate KEY " + data);
        }
        boolean isLeft;
        if(root.key > data) {
            Node left = insert(root, root.left, data,value);
            if(left == root.parent) {
                return left;
            }
            root.left = left;
            isLeft = true;
        } else {
            Node right = insert(root, root.right, data,value);
            if(right == root.parent) {
                return right;
            }
            root.right = right;
            isLeft = false;
        }

        if(isLeft) {
            if(root.color == Color.RED && root.left.color == Color.RED) {
                Optional<Node> sibling = findSiblingNode(root);
                if(!sibling.isPresent() || sibling.get().color == Color.BLACK) {
                    if(isLeftChild(root)) {
                        rightRotate(root, true);
                    } else {
                        rightRotate(root.left, false);
                        root = root.parent;
                        leftRotate(root, true);
                    }

                } else {
                    root.color = Color.BLACK;
                    sibling.get().color = Color.BLACK;
                    if(root.parent.parent != null) {
                        root.parent.color = Color.RED;
                    }
                }
            }
        } else {
            if(root.color == Color.RED && root.right.color == Color.RED) {
                Optional<Node> sibling = findSiblingNode(root);
                if(!sibling.isPresent() || sibling.get().color == Color.BLACK) {
                    if(!isLeftChild(root)) {
                        leftRotate(root, true);
                    } else {
                        leftRotate(root.right, false);
                        root = root.parent;
                        rightRotate(root, true);
                    }
                } else {
                    root.color = Color.BLACK;
                    sibling.get().color = Color.BLACK;
                    if(root.parent.parent != null) {
                        root.parent.color = Color.RED;
                    }
                }
            }
        }
        return root;
    }
    private Node findSmallest(Node root) {
        Node prev = null;
        while(root != null && !root.isNullLeaf) {
            prev = root;
            root = root.left;
        }
        return prev != null ? prev : root;
    }
    private void replaceNode(Node root, Node child, AtomicReference<Node> rootReference) {
        child.parent = root.parent;
        if(root.parent == null) {
            rootReference.set(child);
        }
        else {
            if(isLeftChild(root)) {
                root.parent.left = child;
            } else {
                root.parent.right = child;
            }
        }
    }
    private void printRedBlackTree(Node root, int space) {
        if(root == null || root.isNullLeaf) {
            return;
        }
        printRedBlackTree(root.right, space + 5);
        for(int i=0; i < space; i++) {
            System.out.print(" ");
        }
        System.out.println(root.key + " " + (root.color == Color.BLACK ? "B" : "R"));
        printRedBlackTree(root.left, space + 5);
    }

    private boolean noRedRedParentChild(Node root, Color parentColor) {
        if(root == null) {
            return true;
        }
        if(root.color == Color.RED && parentColor == Color.RED) {
            return false;
        }

        return noRedRedParentChild(root.left, root.color) && noRedRedParentChild(root.right, root.color);
    }

    private boolean checkBlackNodesCount(Node root, AtomicInteger blackCount, int currentCount) {

        if(root.color == Color.BLACK) {
            currentCount++;
        }

        if(root.left == null && root.right == null) {
            if(blackCount.get() == 0) {
                blackCount.set(currentCount);
                return true;
            } else {
                return currentCount == blackCount.get();
            }
        }
        return checkBlackNodesCount(root.left, blackCount, currentCount) && checkBlackNodesCount(root.right, blackCount, currentCount);
    }
    synchronized public int get(Node root,int data)
    {
       if(root==null)
           return -1;
       if(data<root.key)
           return get(root.left,data);
       if(data>root.key)
           return get(root.right,data);
       return root.value;
    }
    public void CopyToFile(Node root,FileWriter fw, BufferedWriter bwk)
    {
        try
        {
            if(root!=null)
            {
                CopyToFile(root.left,fw,bwk);
                bwk.write(root.key+"->"+root.value);
                CopyToFile(root.right,fw,bwk);
            }
        }
        catch(Exception e){}
    }

    public static void main(String args[]) {
        Node root = null;
        RedBlackTree rbt = new RedBlackTree();
        Scanner scan=new Scanner(System.in);
        System.out.println("initial size");
        int n=scan.nextInt();
        int i=0;
        while(true)
        {
            System.out.println("1. PUT <KEY,VALUE>");
            System.out.println("2. GET<KEY>");
            System.out.println("3. PRINT");
            System.out.println("4. EXIT");
            int ch=scan.nextInt();
            switch(ch)
            {
                case 1:int key=scan.nextInt();
                       int value=scan.nextInt();
                       try
                       {
                            if(i<n)
                            {
                                root = rbt.put(root, key, value);
                                i++;
                            }
                            else
                            {
                                FileBack f=new FileBack();
                                f.get(root);
                                root=null;
                                root = rbt.put(root, key, value);
                                i=0;
                            }
                       }
                       catch(Exception ie)
                       {System.out.println(ie.getMessage());}
                       break;
                case 2: int findkey=scan.nextInt();
                        int val=rbt.get(root, findkey);
                        if(val!=-1)
                            System.out.println("VALUE :- "+val);
                        else
                            System.out.println("no key found");
                        break;
                case 3: rbt.printRedBlackTree(root);System.out.println();break;
                case 4:System.exit(0);break;
            }
        }
    }
}