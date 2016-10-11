import java.io.*;
import java.util.*;
import java.util.Queue;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class TreeSearch {

    private static String algorithm = null;
    private static String startingNodeName = null;
    private static String endingNodeName = null;
    private static int numberOfTrafficLines = 0;
    private static int sundayTrafficLines = 0;
    private static ArrayList<String[]> connections = new ArrayList<String[]>();
    static Queue<Node> explored = new LinkedList<Node>();

    public static void main(String[] args) {

        BufferedReader br=null;
        HashMap<String, Map> parent = new HashMap<>();
        HashMap<String, Node> nodesStorage = new HashMap<>();

        try{
            br=new BufferedReader(new FileReader("input.txt"));
            String contentLine = br.readLine();
            contentLine.trim(); //get rid of trailing and leading white spaces
            algorithm= contentLine;
            startingNodeName=br.readLine();
            endingNodeName=br.readLine();
            numberOfTrafficLines=Integer.parseInt(br.readLine());
            for (int i=0; i < numberOfTrafficLines; i++){
                String line = br.readLine();
                String[] splited = line.split("\\s+"); //splited is the array
                connections.add(splited);
                //([a,b,5], [a,c,3], [b,d,1], [c,d,2])
            }

            //creating the hashmap with {parent=> {child=>distance} }
            for (int i = 0; i < numberOfTrafficLines; i++) {
                String[] currentArray = connections.get(i);
                String head = currentArray[0];
                String tail = currentArray[1];
                Map value = parent.get(head);
                if (value == null) {
                    value = new LinkedHashMap<String, Integer>();  //ordered
                    value.put(tail, Integer.parseInt(currentArray[2]));
                } else { //if the key already exists
                    value.put(tail, Integer.parseInt(currentArray[2]));
                }
                parent.put(currentArray[0], value);
            }

            sundayTrafficLines = Integer.parseInt(br.readLine());
            for (int i = 0; i < sundayTrafficLines; i++) {
                String line = br.readLine();
                String[] splited = line.split("\\s+");  //splited is the array

                String node_name = splited[0];
                int g_dist = 0;
                String goal_distance = splited[1]; //string, need to be int
                g_dist = Integer.parseInt(goal_distance);

                nodesStorage.put(node_name, new Node(node_name,g_dist));
                //Node
                /*{"A"= Node_Object
                 "B"= Node_Object
                 ...
                 "G"= Node_Object} */
            }
        }catch (IOException ioe)
        {
            ioe.printStackTrace();
        }


        switch (algorithm) {
            case "BFS":
                //System.out.println("Starting BFS");
                bfs(parent, nodesStorage);
                break;
            case "DFS":
                //System.out.println("Starting DFS");
                dfs(parent, nodesStorage);
                break;
            case "UCS":
                //System.out.println("Starting UCS");
                ucs(parent, nodesStorage);
                break;
            case "A*":
                //System.out.println("Starting A*");
                aStar(parent, nodesStorage);
                break;
            default:
                System.out.println("Input Error. Search Type not found");
        }
    }

    private static void bfs(Map<String, Map> graph, Map<String, Node> allNodes) {

        Queue<Node> toBeExplored = new LinkedList<Node>();  //we will load this with Nodes we have yet to open
        toBeExplored.add(allNodes.get(startingNodeName));
        Node currentNode=null;

        while(!toBeExplored.isEmpty()){
            currentNode= toBeExplored.remove();
            currentNode.isVisited=true;
            //System.out.println("\nExploring current node: " + currentNode.name);
            Map distances = graph.get(currentNode.name);  //getting children of currentNode(we are expanding this)
            if(currentNode.name.equals(endingNodeName)){
                //System.out.println("Current node is: " + currentNode.name +" WE FOUND THE GOAL");  //ONLY BREAK WHEN YOU FOUND THE GOAL
                break;
            }
            //from now, if the currentNode is not the goal node name!
            else if(distances == null) {  //if the node is not the goal and it has no children, do nothing
                //System.out.println(currentNode.name + " has no children RIP ;_;7");
            }
            else {  //if the node is not the goal, but it has children to enqueue, add them..
                //System.out.println("distances of node "+ currentNode.name + " is " + distances);
                Set<String> childrenNames = distances.keySet();  //{B, C}
                //System.out.println(currentNode.name + " set of child names " + childrenNames);

                for (String childName : childrenNames) {
                    Node checkNode = allNodes.get(childName);
                    if (checkNode.isVisited == false) {
                        toBeExplored.add(checkNode);  //add the Node to the Queue toBeExplored
                        checkNode.isVisited = true;
                        checkNode.parent = currentNode;
                        checkNode.g_value += 1+currentNode.g_value;
                    }
                }
            }
        }
        recoverPath(currentNode, allNodes );
        recoverPath(currentNode, allNodes );
    }

    //tie breaking works
    private static void dfs(Map<String, Map> graph, Map<String, Node> allNodes){

        Stack<Node> toBeExplored = new Stack<>();  //we will load this with Nodes we have yet
        toBeExplored.add(allNodes.get(startingNodeName));
        Node currentNode=null;

        while(!toBeExplored.isEmpty()){
            currentNode= toBeExplored.pop();
            currentNode.isVisited=true;
            Map distances = graph.get(currentNode.name);  //getting children of currentNode(we are expanding this)
            System.out.println("\nExploring node: " + currentNode.name);

            if(currentNode.name.equals(endingNodeName)){  //if the current node popped is the goal
                System.out.println("WE FOUND THE GOAL");
                break;
            }
            else if(distances == null) { //if the node does not have any children, don't add "null" children to queue
                System.out.println("...{This node has no children} ;_;7 ");
            }
            else {
                Set<String> childrenNames = distances.keySet();  //{B, C, D}
                System.out.println("set of child names " + childrenNames);

                Stack<Node> loadBack = new Stack<>();  //we will load this with Nodes to preserve order

                for (String childName : childrenNames) {
                    System.out.println("Child: " + childName);
                    Node checkNode = allNodes.get(childName);
                    //System.out.println(checkNode.name);
                    if (checkNode.isVisited == false) {
                        loadBack.add(checkNode);
                        checkNode.isVisited = true;
                        checkNode.parent = currentNode;
                        checkNode.g_value += 1+currentNode.g_value;
                    }
                }

                while(!loadBack.isEmpty()){
                    Node a = loadBack.pop();
                    toBeExplored.add(a);
                }
            }
        }
        recoverPath(currentNode, allNodes );
        recoverPath(allNodes.get(endingNodeName), allNodes);
    }

    //check tie breaking
    private static void ucs(Map<String, Map> graph, Map<String, Node> allNodes){
        PriorityQueue<Node> open = new PriorityQueue<>();
        //Map<String, Node> closed = new LinkedHashMap<>();

        int order=0;

        open.add(allNodes.get(startingNodeName));  //add the initial node
        allNodes.get(startingNodeName).nodeOrder=order;
        Node currentNode = null;

        while(!open.isEmpty()){
            currentNode = open.remove();
            currentNode.isVisited = true;
            System.out.println("\nEvaluating node: " + currentNode.name + " Node Order: " + currentNode.nodeOrder);


            Map distancesToChildren = graph.get(currentNode.name); //getting Hashmap single name: Distances is the gval between current and potential node

            if(currentNode.name.equals(endingNodeName)){  //if node is goal
                //find parent g value and add to the g value
                //NEED TO CHECK FOR NODE ORDER THOUGH

                System.out.println("We found the goal!!!!!");
                System.out.println("The parent of this node is currently: " + currentNode.parent.name);
                //the parent is now the latest one that has encountered the goal

                Node parentNode= currentNode.parent;
                if (parentNode == null) { //it's a starting node
                    currentNode.g_value = 0;
                    //closed.put(currentNode.name, currentNode);  //PUT THE NODE the close LINKED HASH MAP
                    break;
                }
            }
            else if( distancesToChildren == null){  //else if node is a dead end and no goal, we assign a g val anyways
                System.out.println("The current node has no children RIP");
                Node parentNode = currentNode.parent;
                System.out.println("The parent of node " + currentNode.name + " is " + parentNode.name);
                /*
                int sum=0;
                while(parentNode != null) {
                    sum += parentNode.g_value;
                    parentNode = currentNode.parent;
                }
                */
            }
            //else if the node is not a dead end or goal,
            else {
                Set<String> children = distancesToChildren.keySet();
                for (String child : children) {
                    if(allNodes.get(child).nodeOrder == 0) {
                        order += 1;  //each encountered node will get an order number
                    }
                    Node testChild = allNodes.get(child);  //we get the node from allNodes
                    testChild.nodeOrder= order;

                    //situation 1) node never encountered before
                    if (testChild.isVisited == false) {
                        System.out.println("Node " + testChild.name + " never encountered before");
                        testChild.isVisited=true;
                        testChild.parent =currentNode;
                        int gValuetoAdd = (int) distancesToChildren.get(child);
                        //updating the g value of the testNode
                        System.out.println("Adding " + gValuetoAdd + " to " + testChild.name + "\'s gval of " + testChild.g_value + " plus its parent\'s g values");
                        Node parentNode= testChild.parent; // parentNode is now added value
                        System.out.println("ParentNode G Val: " + parentNode.name +" is " + parentNode.g_value);
                        int pastGVal= parentNode.g_value;

                        int sum=0;
                        while(parentNode != null){
                            testChild.g_value += parentNode.g_value;
                            parentNode = parentNode.parent;
                        }
                        sum=testChild.g_value;
                        System.out.println(sum + "g val of the parent before");
                        testChild.g_value =+(pastGVal+gValuetoAdd); //we add the g value from the currentNode to childNode
                        System.out.println("G value of " + testChild.name + " is now " + testChild.g_value);
                        open.add(testChild);

                    }
                    //situation2. Node isVisited before, but we now have a shorter path
                    else if(testChild.isVisited ==true){
                        System.out.println("We\'ve seen this before: " + testChild.name);
                        /*if the g val of the current Node to here is less than the current g val, then we got to change parents
                        and update the priority queue
                         */
                        /*if its the same g val, then we have to
                        chose the one that has a higher order
                         */
                        System.out.println("Current G value of " + testChild.name + " is " + testChild.g_value);
                        int gValuetoAdd = (int) distancesToChildren.get(child);
                        System.out.println("gValtoAdd:"+gValuetoAdd);
                        //G value to add is from the node we are at now, and we are checking the distance to G from currentNode

                        int compareG= testChild.g_value;
                        System.out.println("Current node gVal: " + "of " +currentNode.name +" is " + currentNode.g_value);

                        if(compareG > (currentNode.g_value + gValuetoAdd)){
                            System.out.println("We\'re gonna change parents!");
                            testChild.parent=currentNode;
                            testChild.g_value=currentNode.g_value + gValuetoAdd;
                            System.out.println("Now the node " + testChild.name + " has a g value of " + testChild.g_value);
                        }

                        if(compareG == currentNode.g_value + gValuetoAdd){
                            System.out.println("We have to compare the order numbers!");

                        }

                    } //end of situation 2) Node is Visited before but we have a shorter path
                }
            }
        }
        recoverPath(allNodes.get(endingNodeName), allNodes );
    }

    //need to implement this
    private static void aStar(Map<String, Map> graph, Map<String, Node> allNodes){
        //System.out.println(allNodes);
        PriorityQueue<Node> open = new PriorityQueue<>();
        int order=0;
        open.add(allNodes.get(startingNodeName));  //add the initial node
        allNodes.get(startingNodeName).nodeOrder=order;

        allNodes.get(startingNodeName).h_value= allNodes.get(startingNodeName).estimate_to_goal;
        allNodes.get(startingNodeName).f_value= allNodes.get(startingNodeName).estimate_to_goal;

        //System.out.println(allNodes.get(startingNodeName).h_value);
        Node currentNode = null;

        while(!open.isEmpty()){
            currentNode = open.remove();
            currentNode.isVisited = true;
            //System.out.println("\nEvaluating node: " + currentNode.name + " Node Order: " + currentNode.nodeOrder);

            Map distancesToChildren = graph.get(currentNode.name);
            //getting Hashmap single name: Distances is the gval between current and potential node

            if(currentNode.name.equals(endingNodeName)){  //if node is goal

               // System.out.println("We found the goal!!!!!");
                //System.out.println("The parent of this node is currently: " + currentNode.parent.name);
                //the parent is now the latest one that has encountered the goal
                Node parentNode= currentNode.parent;

                if (parentNode == null) { //it's a starting node if there is no parent since all nodes must have parents
                    currentNode.g_value = 0;
                    currentNode.h_value = currentNode.estimate_to_goal;
                    //System.out.println("F val at start: " + currentNode.f_value);
                    break;
                }
            }
            else if( distancesToChildren == null){  //else if node is a dead end and no goal, we assign a g val anyways
                //System.out.println("The current node has no children RIP ;_;7");
                Node parentNode = currentNode.parent;
                //System.out.println("The parent of node " + currentNode.name + " is " + parentNode.name);
            }
            //else if the node is not a dead end or goal,
            else {
                Set<String> children = distancesToChildren.keySet();
                for (String child : children) {
                    if(allNodes.get(child).nodeOrder == 0) {
                        order += 1;  //each encountered node will get an order number
                    }
                    Node testChild = allNodes.get(child);  //we get the node from allNodes

                    testChild.nodeOrder= order;

                    testChild.h_value=allNodes.get(child).estimate_to_goal;
                    //System.out.println("The h val of child " + testChild.name + " is " + testChild.h_value);

                    //situation 1) node never encountered before
                    if (testChild.isVisited == false) {
                        //System.out.println("Node " + testChild.name + " never encountered before");
                        testChild.isVisited=true;
                        testChild.parent =currentNode;
                        int gValuetoAdd = (int) distancesToChildren.get(child);
                        //System.out.println("gval to add: " + gValuetoAdd);

                        //updating the g value of the testNode
                        Node parentNode= testChild.parent; // parentNode is now added value

                        //System.out.println("ParentNode g Val of "+ testChild.name + " is " + parentNode.name +" is " + parentNode.g_value);
                        //System.out.println("ParentNode f Val of "+ testChild.name + " is " + parentNode.name + " is " + parentNode.f_value);
                        int pastGVal= parentNode.g_value;
                        //System.out.println("past G val: " + pastGVal);
                        int pastFVal= parentNode.f_value;
                        //System.out.println("Parent node " + parentNode.name + " is fVAL: " + parentNode.f_value);

                        int sum=0;
                        while(parentNode != null){
                            testChild.g_value += parentNode.g_value;
                            parentNode = parentNode.parent;
                        }

                        sum=testChild.g_value;
                        //System.out.println(sum + ": is G val of the parent before");
                        testChild.g_value =(pastGVal+gValuetoAdd); //we add the g value from the currentNode to childNode

                        testChild.f_value =(gValuetoAdd+ pastFVal +testChild.h_value);
                        //System.out.println("g value of " + testChild.name + " is now " + testChild.g_value); //correct
                        //System.out.println("f value before was: " + pastFVal); //correct
                        //System.out.println("f val of the node now: " + testChild.f_value);
                        //testChild.f_value= (pastFVal + testChild.h_value);

                        open.add(testChild);

                    }



                    //situation2. Node isVisited before, but we now have a shorter path
                    else if(testChild.isVisited ==true){
                        //System.out.println("We\'ve seen this before!!!: " + testChild.name);
                        /*if the g val of the current Node to here is less than the current g val, then we got to change parents
                        and update the priority queue.
                        If its the same g val, then we have to
                        chose the one that has a lower order
                         */
                        //System.out.println("Current g value of " + testChild.name + " is " + testChild.g_value);
                        //System.out.println("Current f val of " +testChild.name + " is " + testChild.f_value);
                        int gValuetoAdd = (int) distancesToChildren.get(child);
                        int hValuetoAdd = testChild.estimate_to_goal;
                        //System.out.println("gValtoAdd:"+gValuetoAdd);
                        //System.out.println("hValtoAdd:"+hValuetoAdd);
                        //G value to add is from the node we are at now, and we are checking the distance to G from currentNode

                        int compareG= testChild.g_value;
                        int compareF= testChild.f_value;
                        //System.out.println("compareF of " + testChild.name + " is " +compareF);
                        //System.out.println("Current node gVal: " + "of " +currentNode.name +" is " + currentNode.g_value);
                        //int compareF = testChild.f_value;
                        //System.out.println("Current node fVal: " + "of " +currentNode.name +" is " + currentNode.f_value);

                        if(compareF > (currentNode.f_value + gValuetoAdd + testChild.h_value)){
                            //System.out.println("switch parents");
                            testChild.parent=currentNode;
                            testChild.f_value =currentNode.f_value + gValuetoAdd + testChild.h_value;
                            //System.out.println("Now the node " + testChild.name + " has a f value of " + testChild.f_value);
                        }

                        if (compareG > (currentNode.g_value + gValuetoAdd)){
                            //System.out.println("We\'re gonna change parents!");
                            testChild.parent=currentNode;
                            testChild.g_value=currentNode.g_value + gValuetoAdd;
                            //testChild.f_value=currentNode.f_value +
                            //System.out.println("Now the node " + testChild.name + " has a g value of " + testChild.g_value);
                        }


                        if(compareG == currentNode.g_value + gValuetoAdd){
                            System.out.println("We have to compare the order numbers!");

                        }

                    } //end of situation 2) Node is Visited before but we have a shorter path
                }
            }
        }
        recoverPath(allNodes.get(endingNodeName), allNodes );
    }

    private static void recoverPath(Node node, Map<String, Node> nodes){
        ArrayList<String> sequenceOfNodesVisited = new ArrayList<>();
        FileWriter fw = null;
        try {
            fw = new FileWriter("output.txt");
            PrintWriter pw = new PrintWriter(fw);
            Node nodePrint = node ;

            if(nodePrint.parent == null){
                pw.println("No path found!");
            }
            else {

                do {
                    String nodeName = nodePrint.name;
                    String nodeParent = nodePrint.parent.name;
                    int nodeGVal = nodePrint.g_value;
                    sequenceOfNodesVisited.add(nodeName + " " + nodeGVal);
                    // System.out.println(nodeName + " " + nodeGVal);
                    nodePrint = nodes.get(nodeParent);
                }
                while (nodePrint.parent != null);
                sequenceOfNodesVisited.add(nodes.get(startingNodeName).name + " " + nodes.get(startingNodeName).g_value);

                for (int i = 0; i < sequenceOfNodesVisited.size(); i++) {
                    System.out.println(sequenceOfNodesVisited.get(sequenceOfNodesVisited.size() - 1 - i));
                    pw.println(sequenceOfNodesVisited.get(sequenceOfNodesVisited.size() - 1 - i));
                }
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("Error: File not written");
            e.printStackTrace();
        }
    }

