import java.util.*;
import java.io.*;
class treeCreator
{
    Node root;
    StringTokenizer st;
    ArrayList<Node> allNodes=new ArrayList<Node>();
    public String tok()
    {
        return st.nextToken();
    }
    public treeCreator(String srcFilePath)
    {
        String document="";
        try(FileReader fr=new FileReader(new File(srcFilePath)))
        {
            
            BufferedReader br=new BufferedReader(fr);
            String s="";
            //boolean ignore=false;
            while((s=br.readLine())!=null)
            {
                document+=s+"\n";
            }
        }
        catch(Exception e){}
        document+="}";
        st=new StringTokenizer(document," \t\n");
        root=new Node("$ROOT",-1);
        scanChildren(root);
        //root=root.childList.get(0);
    }
    public void log(String s)
    {
        //System.out.println(s);
    }
    public void readVariable(Node n)
    {
        String line="";
        while(true)
        {
            line+=tok();
            if(line.charAt(line.length()-1)==';')
            break;
        }
        log(n.name+" variable found is "+line);
        int eq=line.indexOf("=");
        String key=line.substring(0,eq);
        String value=line.substring(eq+1,line.length()-1);
        n.instanceVariables.put(key,value);
    }
    public void scanChildren(Node n)
    {
        while(true)
        {
            String token=tok();
            log("processing "+token);
            switch(token)
            {
                case "node":
                {
                    String name=tok();
                    Node child=new Node(name,n.generation+1);
                    allNodes.add(child);
                    n.setChild(child);
                    if(tok().equals("{"))
                    scanChildren(child);
                    break;
                }
                case "{"://should never be found like this
                {
                    log("ERROR in parsing");
                    break;
                }
                case "}":
                {
                    return;
                }
                case "def":
                {
                    readVariable(n);
                    break;
                }
            }
        }
    }
}