import java.util.Scanner;
public class Number {
  private Node high, low;
  private int digitCount=0;
  private int decimalPlaces=0;
  private boolean negative=false;
  
  //constructor 
  public Number() {
    high=new Node(0, null, null);
    low=new Node(0, high, null);
    high.next=low;
  }
  
  //construct a number from a string
  public Number(String str) throws NotANumberException {
    if (validate(str)) {
      accept(str);
    }
    else {
      throw new NotANumberException();
    }
  }
  
  //does the work for the previous constructor 
  public void accept(String str) {
    //make the string a new char[] to make things easier
    char[] array=str.toCharArray();
    int i=0;
    //is it negative
    if (array[i]=='-') {
      negative=true;
      i++;
    }
    //has there been a decimal point
    boolean isDecimal=false;
    if (array[i]=='.') {
      isDecimal=true;
      i++;
    }
    //set high
    high=new Node(Character.getNumericValue(array[i]), null, null);
    i++;
    digitCount++;
    if (isDecimal==true) {
      decimalPlaces++;
    }
    Node prev=high;
    Node current;
    //set all nodes but high and low
    for (int j=i; j<array.length; j++) {
      if (array[j]=='.') {
        isDecimal=true;
      }
      else {
        current=new Node(Character.getNumericValue(array[j]), prev, null);
        prev.next=current;
        prev=current;
        digitCount++;
        if (isDecimal==true) {
          decimalPlaces++;
        }
      }
    }
    //set low
    if (digitCount>1) {
      low=prev;
    }
    else {
      low=new Node(0,prev,null);
    }
  }
  
  //add together two numbers
  public Number add(Number n) {
    Number answer;
    Number larger=this.compareToAbsolute(n);
    Number smaller=(larger==this) ? n : this;
    if (larger.negative && smaller.negative) {
      answer=larger.addAbsolute(smaller);
      answer.negative=true;
    }
    else if (larger.negative && !smaller.negative) {
      answer=larger.subtractAbsolute(smaller);
      answer.negative=true;
    }
    else if (!larger.negative && smaller.negative) {
      answer=larger.subtractAbsolute(smaller);
    }
    else {
      answer=larger.addAbsolute(smaller);
    }
    return answer;
  }
  
  //adds without regard to sign
  private Number addAbsolute(Number n) {
    Number sum=new Number();
    //value to help align the decimal points
    int trailingZeros=this.decimalPlaces-n.decimalPlaces;
    Node current=(this.digitCount<2) ? this.high : this.low;
    Node current2=(n.digitCount<2) ? n.high : n.low;
    //allign the decimal points by appending the decimal places that the other number doesn't have to the end
    if (trailingZeros!=0) {
      boolean use2=false;
      //find which number has more decimal points
      if (trailingZeros<0) {
        use2=true;
      }
      trailingZeros=Math.abs(trailingZeros);
      while (trailingZeros>0) {
        if (use2) {
          sum.high=new Node(current2.data, null, sum.high);
          sum.high.next.prev=sum.high;
          current2=current2.prev;
          sum.digitCount++;
        }
        else {
          sum.high=new Node(current.data, null, sum.high);
          sum.high.next.prev=sum.high;
          current=current.prev;
          sum.digitCount++;
        }
        trailingZeros--;
      }
    }
    //add the numbers after they are aligned
    int carry=0;
    while (current!=null&&current2!=null) {
      //find the number to add and if carrying is needed
      current.data+=carry;
      carry=0;
      int localSum=current.data+current2.data;
      if (localSum>9) {
        carry=1;
        localSum-=10;
      }
      //create a node with the new number
      sum.high=new Node(localSum, null, sum.high);
      if (sum.high.next!=null) {
        sum.high.next.prev=sum.high;
      }
      current=current.prev;
      current2=current2.prev;
      sum.digitCount++;
    }
    //if one number has more higher order digits, append those on the end
    while(current!=null) {
      sum.high=new Node(current.data+carry, null, sum.high);
      sum.high.next.prev=sum.high;
      sum.digitCount++;
      current=current.prev;
      carry=0;
    }
    while(current2!=null) {
      sum.high=new Node(current2.data+carry, null, sum.high);
      sum.high.next.prev=sum.high;
      sum.digitCount++;
      current2=current2.prev;
      carry=0;
    }
    if (carry!=0) {
      sum.high=new Node(carry, null, sum.high);
      sum.high.next.prev=sum.high;
      sum.digitCount++;
    }
    //sum.decimalPlaces is the max of the other two Number's decimal places
    sum.decimalPlaces=(this.decimalPlaces>n.decimalPlaces) ? this.decimalPlaces : n.decimalPlaces;
    sum.trim();
    return sum;
  }
  
  //return the largest number without regard to sign
  private Number compareToAbsolute(Number n) {
    if ((this.digitCount-this.decimalPlaces)-(n.digitCount-n.decimalPlaces)!=0) {
      Number larger=((this.digitCount-this.decimalPlaces)>(n.digitCount-n.decimalPlaces)) ? this : n;
      return larger;
    }
    else {
      int i=(this.digitCount>n.digitCount) ? n.digitCount : this.digitCount;
      Node current=this.high;
      Node current2=n.high;
      while (i>0) {
        if (current.data!=current2.data) {
          Number larger=(current.data>current2.data) ? this : n;
          return larger;
        }
        i--;
      }
      return this;
    }
  }
  
  public Number subtract(Number n) {
    Number answer;
    Number larger=this.compareToAbsolute(n);
    Number smaller=(larger==this) ? n : this;
    if (larger==this) {
      if (larger.negative && smaller.negative) {
        answer=larger.subtractAbsolute(smaller);
        answer.negative=true;
      }
      else if (!larger.negative && smaller.negative) {
        answer=larger.addAbsolute(smaller);
      }
      else if (larger.negative && !smaller.negative) {
        answer=larger.addAbsolute(smaller);
        answer.negative=true;
      }
      else {
        answer=larger.subtractAbsolute(smaller);
      }
    }
    else {
      if (smaller.negative && larger.negative) {
        answer=larger.subtractAbsolute(smaller);
      }
      else if (!smaller.negative && larger.negative) {
        answer=larger.addAbsolute(smaller);
      }
      else if (smaller.negative && !larger.negative) {
        answer=larger.addAbsolute(smaller);
        answer.negative=true;
      }
      else {
        answer=larger.subtractAbsolute(smaller);
        answer.negative=true;
      }
    }
    return answer;
  }
  
  //subtracts without regard to sign
  private Number subtractAbsolute(Number n) {
    Number diff=new Number();
    //find the larger number
    Number larger=this.compareToAbsolute(n);
    Number smaller=(larger==this) ? n : this;
    Node currentL=(larger.digitCount<2) ? larger.high : larger.low;
    Node currentS=(smaller.digitCount<2) ? smaller.high : smaller.low;
    int borrow=0;
    //int used for aligning decimal points
    int trailingZeros=larger.decimalPlaces-smaller.decimalPlaces;
    int top;
    int bottom;
    while (currentL!=null) {
      //if the smaller number has more decimal points:
      //treat the larger digit as if it was zero
      //use the smaller digit
      //increment trailingZeros to keep track of the decimal point
      if (trailingZeros<0) {
        top=0;
        trailingZeros++;
        bottom=currentS.data;
        currentS=currentS.prev;
      }
      //if the larger number has more decimal points:
      //use the larger digit
      //treat the smaller digit as if it was zero
      //decrement trailingZeros to keep track of the decmial point
      else if (trailingZeros>0) {
        top=currentL.data;
        currentL=currentL.prev;
        bottom=0;
        trailingZeros--;
      }
      //if trailingZeros==0:
      //use the larger value, the loop makes it so it will never be null
      //use the smaller value if not null, if null, treat it as zero
      else {
        top=currentL.data;
        currentL=currentL.prev;
        if (currentS!=null) {
          bottom=currentS.data;
          currentS=currentS.prev;
        }
        else {
          bottom=0;
        }
      }
      //apply the borrow value
      top=top-borrow;
      //calculate newDigit and the borrow value
      int newDigit=top-bottom;
      if (newDigit<0) {
        newDigit+=10;
        borrow=1;
      }
      else {
        borrow=0;
      }
      //insert the new value 
      diff.high=new Node(newDigit, null, diff.high);
      diff.high.next.prev=diff.high;
      diff.digitCount++;
    }
    //diff.decimalPlaces is the larger of the two Numbers' decimal places 
    diff.decimalPlaces=(this.decimalPlaces>n.decimalPlaces) ? this.decimalPlaces : n.decimalPlaces;
    diff.trim();
    return diff;
  }
  
  public Number multiply(Number n) {
    Number answer=new Number();
    Node current=n.high;
    while (current!=null) {
      Number partialProduct=new Number();
      int carry=0;
      Node current2=(this.digitCount<2) ? this.high : this.low;
      while (current2!=null) {
        int newDigit=current.data * current2.data + carry;
        carry=newDigit/10;
        newDigit%=10;
        partialProduct.high=new Node(newDigit, null, partialProduct.high);
        partialProduct.high.next.prev=partialProduct.high;
        partialProduct.digitCount++;
        current2=current2.prev;
      }
      if (carry!=0) {
        partialProduct.high=new Node(carry, null, partialProduct.high);
        partialProduct.high.next.prev=partialProduct.high;
        partialProduct.digitCount++;
      }
      answer.low=new Node(0, answer.low, null);
      answer.low.prev.next=answer.low;
      answer.digitCount++;
      answer.trim();
      partialProduct.trim();
      answer=answer.addAbsolute(partialProduct);
      current=current.next;
    }
    answer.decimalPlaces=this.decimalPlaces+n.decimalPlaces;
    if (this.negative!=n.negative) {
      answer.negative=true;
    }
    return answer;
  }
    
  //reverse the sign of the Number
  public void reverseSign() {
    negative=!negative;
  }

  //returns a String representation of the Number
  public String toString() {
    String repr="";
    if (negative) {
      repr+="-";
    }
    Node current=high;
    //location of the decimal point
    int decimal=digitCount-decimalPlaces;
    for (int i=0; i<digitCount; i++) {
      if (i==decimal) {
        repr+=".";
      }
      repr+=Integer.toString(current.data);
      current=current.next;
    }
    return repr;
  }
  
  //determines if a string is a valid Number
  private boolean validate(String str) {
    char[] array=str.toCharArray();
    for (int i=0; i<array.length; i++) {
      if (!(Character.isDigit(array[i])||array[i]=='.'||array[i]=='-')) {
        return false;
      }
    }
    return true;
  }
  
  private void trim() {
    Node current=this.high;
    int actualDigits=0;
    while (current!=null) {
      actualDigits++;
      current=current.next;
    }
    for (int i=0; i<actualDigits-digitCount; i++) {
      this.low=this.low.prev;
      this.low.next=null;
    } 
  }
  
  public static void main(String[] args) throws NotANumberException {
    Number current=new Number();
    String menu="\nCurrent Value: "+current+"\n\nEnter a value: e     Add: a\nSubtract: s          Multiply: m\nReverse sign: r      Clear: c\nQuit: q";
    String c="";
    Scanner sc=new Scanner(System.in);
    while (!c.equals("q")) {
      System.out.println(menu);
      c=sc.nextLine();
      if (c.equals("e")) {
        System.out.println("Value: ");
        try {
          current=new Number(sc.nextLine());
        }
        catch (NotANumberException e) {
          System.out.println("Not a valid number!");
        }
        menu="\nCurrent Value: "+current+"\n\nEnter a value: e     Add: a\nSubtract: s          Multiply: m\nReverse sign: r      Clear: c\nQuit: q";
      }
      else if (c.equals("a")) {
        System.out.println("Value: ");
        try {
          current=current.add(new Number(sc.nextLine()));
        }
        catch (NotANumberException e) {
          System.out.println("Not a valid number!");
        }
        menu="\nCurrent Value: "+current+"\n\nEnter a value: e     Add: a\nSubtract: s          Multiply: m\nReverse sign: r      Clear: c\nQuit: q";
      }
      else if (c.equals("s")) {
        System.out.println("Value: ");
        try {
          current=current.subtract(new Number(sc.nextLine()));
        }
        catch (NotANumberException e) {
          System.out.println("Not a valid number!");
        }
        menu="\nCurrent Value: "+current+"\n\nEnter a value: e     Add: a\nSubtract: s          Multiply: m\nReverse sign: r      Clear: c\nQuit: q";
      }
      else if (c.equals("m")) {
        System.out.println("Value: ");
        try {
          current=current.multiply(new Number(sc.nextLine()));
        }
        catch (NotANumberException e) {
          System.out.println("Not a valid number!");
        }
        menu="\nCurrent Value: "+current+"\n\nEnter a value: e     Add: a\nSubtract: s          Multiply: m\nReverse sign: r      Clear: c\nQuit: q";
      }
      else if (c.equals("r")) {
        current.reverseSign();
        menu="\nCurrent Value: "+current+"\n\nEnter a value: e     Add: a\nSubtract: s          Multiply: m\nReverse sign: r      Clear: c\nQuit: q";
      }
      else if (c.equals("c")) {
        current=new Number();
        menu="\nCurrent Value: "+current+"\n\nEnter a value: e     Add: a\nSubtract: s          Multiply: m\nReverse sign: r      Clear: c\nQuit: q";
      }
      else if (!c.equals("q")) {
        System.out.println("Not a valid selection");
      }
    }
  }
}