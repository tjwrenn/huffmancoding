/*
 * @author Tj Wrenn
 * @project PriorityQueue.java
 */
import java.util.*;

/**
 * Creates a priority Queue where "smaller" object are given priority.
 */
public class PriorityQueue
{
    private LinkedList myLinkedList;
    private int iMySize;

	//added by tj
	/**
	 * Returns the number of elements in the Queue
	 * Time: O(1)
	 * Space: O(1)
	 */
	public int size(){ return iMySize;	}
	
    /**
     * Creates priority queue with no elements
     * Space: O(1)
     * Time: O(1)
     */
    public PriorityQueue()
    {
        myLinkedList = new LinkedList();
        iMySize = 0;
    }
    
    /**
     * Insert object into the queue dependent upon its priority
     * Time: O(N) where N is the number of objects in the queue
     * Space: O(1)
     * Parameters: Object to be inserted into the queue
     */
    public void put(Object o)
    {
        int temp = iMySize;
        if(iMySize == 0)
        {   myLinkedList.addFirst(o);
            iMySize++;
            return;
        }
        ListIterator iter = myLinkedList.listIterator(0);
        while(iter.hasNext())
        {   if((((Comparable)o).compareTo((Comparable)iter.next())) < 1)
            {   iter.previous();
                iter.add(o);
                iMySize++;
                break;
            }
        }
        if(iMySize == temp)
        {   myLinkedList.addLast(o);
            iMySize++;
        }
      //  System.out.println(toString());
    }
    
    /**
     * Removes object from queue at specified index
     * Time: O(N), where N is the number of elements in the internal linked list
     * Space: O(1)
     * Paramters: index of object to be removed
     */
    public Object pop(int index)
    {
        if(iMySize != 0)
        {   Object temp = myLinkedList.remove(index);
            iMySize--;
            return temp;
        }
        return null;
    }
    
    /**
     * Removes object from the front of the queue
     * Time: O(1)
     * Space: O(1)
     */
    public Object pop()
    {
        if(iMySize !=0)
        {   Object temp = myLinkedList.remove(0);
            iMySize--;
            return temp;
        }
        return null;
    }
    
    /**
     * Converts queue into a visually exciting String
     * Time: O(N) where N is the number of elements in the queue
     * Space: O(1), one string
     */
    public String toString()
    {   String temp = "";
        ListIterator iter = myLinkedList.listIterator(0);
        while(iter.hasNext())
            temp += iter.next().toString() + " ";
        return temp;
    }
}
