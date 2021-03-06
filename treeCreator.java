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
        root=new Node("$ROOT",-1,this);
        scanChildren(root);
        //root=root.childList.get(0);
    }
    public void log(String s)
    {
        //System.out.println(s);
    }
    public void readVariable(Node n,StringTokenizer stt)
    {
        String line="";
        while(stt.hasMoreTokens())
        {
            line+=stt.nextToken();
            if(line.charAt(line.length()-1)==';')
            {
                line=line.substring(0, line.length()-1);
                break;
            }
        }
        log(n.name+" variable found is "+line);
        int eq=line.indexOf("=");
        String key=line.substring(0,eq);
        String value=line.substring(eq+1,line.length());
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
                    Node child=new Node(name,n.generation+1,this);
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
                    readVariable(n,st);
                    break;
                }
                default:
                    System.out.println("Unknown: "+token);
                    break;
            }
        }
    }
    Node getNodeByName(String name)
    {
        for(Node n:allNodes)
        {
            if(n.name.equals(name))
            return n;
        }
        return null;
    }
    String readJSON(String nam)
    {
        String out="";
        try(FileReader fr=new FileReader(nam+".json"))
        {
            BufferedReader br=new BufferedReader(fr);
            String s="";
            while((s=br.readLine())!=null)
            {
                out+=s+"\n";
            }
        }
        catch(Exception e){}
        return out;
    }
    void makeJSON(Node start,String name)
    {
        try(FileWriter fw=new FileWriter(name+".json");)
        {
            BufferedWriter bw=new BufferedWriter(fw);
            PrintWriter pw=new PrintWriter(bw);
            pw.println("{");
            json(root,"",pw);
            pw.print("\n}");
            pw.flush();
            System.out.println("File created with name "+name+".json");
        }
        catch(Exception e)
        {
            System.out.println("File creation failed "+e.getMessage());
        }
    }
    static final String DQ="\"",ONETAB="    ";
    private void json(Node n,String space,PrintWriter p)
    {
        p.print(space+DQ+n.getName()+DQ+":\n");
        p.print(space+"{\n");
        
        int k=0;
        for(Map.Entry<String,Object> s:n.instanceVariables.entrySet())
        {
            k++;
            if(k!=n.instanceVariables.size())
            p.println(space+ONETAB+DQ+s.getKey()+DQ+":"+DQ+s.getValue()+DQ+",");
            else
            p.println(space+ONETAB+DQ+s.getKey()+DQ+":"+DQ+s.getValue()+DQ);

        }
        if(n.childList.size()!=0  &&  k!=0)
        p.println(space+ONETAB+",");
        for(int i=0;i<n.childList.size();i++)
        {
            json(n.childList.get(i),space+ONETAB,p);
            if(i!=n.childList.size()-1)
            p.println(space+ONETAB+",");
        }
        p.println("\n"+space+"}");
        
    }
}