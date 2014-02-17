import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLProcessor {

	ArrayList<String> var_decld = new ArrayList<String>();
	ArrayList<String> fun_decld = new ArrayList<String>();

	FileWriter fstream; 
	BufferedWriter out;
	int c =0, i =0;

	public void ProcessXmlsFromDirectory(String files, String DirName)
	{		
		try
		{
			String wholepath = DirName + files;
			//System.out.println("The whole path is "+wholepath);

			File file = new File(wholepath);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringElementContentWhitespace(true);
			dbf.setNamespaceAware(true);
			dbf.setIgnoringComments(true);

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);

			doc = db.parse(file);
			doc.normalizeDocument();

			Node root = doc.getDocumentElement();
			// remove whitespace nodes
			root.normalize();
			removeWhitespace(root);

			//open file for writing

			//fstream = new FileWriter("C:\\MY_STUFF\\research\\syrus_Dafny\\dafny\\" + files.substring(0, (files.length() - 4)) + ".dfy");
			fstream = new FileWriter("C:\\MY_STUFF\\research\\syrus_Dafny\\SampleStringFormulas\\problems_from_diego\\all\\all\\dafny\\" + files.substring(0, (files.length() - 4)) + ".dfy");
			out = new BufferedWriter(fstream);


			Node formula = root.getLastChild();		

			//Node implies = formula.getFirstChild();
			//			System.out.println(implies.toString());
			//			System.out.println(implies.getFirstChild().toString()); 

			String cName = doc.getDocumentElement().getAttribute("cName");
			out.write("class Client<T>{");
			out.newLine();
			declare_vars(doc);
			declare_funs(doc);
			
			//out.write("function formula() :bool {");
			out.write("method formula() {");
			out.newLine();
						
			declare_lemmas(doc);
			
			//out.write("assert (!(");
			out.write("assert ");
			traverseTree(formula.getFirstChild(), doc);
			//out.write("));");
			out.write(";");
			out.newLine();
			out.write("} }");
			out.newLine();
			out.close();

		}
		catch (Exception e) 
		{
			e.printStackTrace();  
		}
	}//end ProcessXMLsFromDirectory

	public void declare_vars(Document doc) throws IOException
	{

		NodeList vars = doc.getElementsByTagName("symbol");
		int numSymbols = vars.getLength();

		out.newLine();	

		for (int i = 0; i < numSymbols; i++) 
		{
			//System.out.println(vars.item(i).getFirstChild().toString()); 
			String symbol = vars.item(i).getFirstChild().getTextContent();
			String type = vars.item(i).getAttributes().getNamedItem("type").getNodeValue();

			if (!(var_decld.contains(symbol)))
			{
				var_decld.add(symbol);

				if(type.equals("finiteset(object)"))
				{

					out.write("var "+ symbol + " : set<T>;");
					out.newLine();
				}
				
				if(type.equals("finiteset(integer)"))
				{

					out.write("var "+ symbol + " : set<int>;");
					out.newLine();
				}

				else if (type.equals("object"))
				{
					out.write("var "+ symbol + " : T;");
					out.newLine();
				}

				else if (type.equals("integer"))
				{
					out.write("var "+ symbol + " : int;");
					out.newLine();
				}
				else if (type.equals("string(object)"))
				{
					out.write("var "+ symbol + " : seq<T>;");
					out.newLine();
				}
			}

		}//end for

		out.newLine();	

	}//end declare_vars(Document doc)	


	public static void removeWhitespace(Node n) {
		NodeList nl = n.getChildNodes();
		for (int pos = 0, c = nl.getLength(); pos < c; pos++) {
			Node child = nl.item(pos);
			if (child.getNodeType() != Node.TEXT_NODE) {
				removeWhitespace(child);
			}
		}

		// count backwards so that pos is correct even if nodes are removed
		for (int pos = nl.getLength() - 1; pos >= 0; pos--) {
			Node child = nl.item(pos);
			if (child.getNodeType() == Node.TEXT_NODE) {
				// if node's text is made up only of whitespace characters
				if (child.getTextContent().trim().equals("")) {
					n.removeChild(child);
				}
			}
		}
	}

	public void traverseTree(Node node, Document doc) throws Exception
	{
		NodeList card = null;
		card = doc.getElementsByTagName("bar");
		String cName = doc.getDocumentElement().getAttribute("cName");
		// Extract node info:
		String elementName = node.getNodeName();
		//System.out.println("(DEBUG)-"+ elementName);
		String val = node.getNodeValue();


		if(elementName.equals("neq"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" != ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if(elementName.equals("eq"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" == ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");

		}					 

		else if(elementName.equals("geq"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" >= ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if(elementName.equals("leq"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" <= ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if(elementName.equals("gt"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" > ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if(elementName.equals("lt"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" < ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}
		else if(elementName.equals("and"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" && ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}
		else if(elementName.equals("or"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" || ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}
		else if(elementName.equals("add")) // for integers
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" + ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if(elementName.equals("subtract")) // for integers
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" - ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if(elementName.equals("star")) // for integers
		{	
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			if(cName.contains("String") || cName.contains("string"))
			{
				out.write(" + ");
			}
			else
				out.write(" * ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if(elementName.equals("union"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" + ");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}

		else if(elementName.equals("intersection"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" * ");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}
		
		else if(elementName.equals("subset"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" <= ");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}
		else if(elementName.equals("propersubset"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" < ");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}

		else if(elementName.equals("bar"))
		{
			if(cName.contains("set"))
			{
				out.write("(card (");
				traverseTree(node.getFirstChild(), doc);
				out.write(")) ");
			}
			else if(cName.contains("string") )
			{
				out.write("(|");
				traverseTree(node.getFirstChild(), doc);
				out.write("|) ");
			}
		}

		else if(elementName.equals("negate"))
		{
			out.write("(- ");
			traverseTree(node.getFirstChild(), doc);
			out.write(") ");
		}
		else if(elementName.equals("difference"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" - ");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}
		else if(elementName.equals("implies"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" ==> ");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}
		else if(elementName.equals("iff"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" <==> ");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}
		else if(elementName.equals("singleton"))
		{
			out.write("({ ");
			traverseTree(node.getFirstChild(), doc);
			out.write("}) ");
		}
		else if(elementName.equals("stringleton"))
		{
			out.write("([ ");
			traverseTree(node.getFirstChild(), doc);
			out.write("]) ");
		}
		else if(elementName.equals("symbol"))
		{
			//op = read_text_set(node);
			String symbol = node.getFirstChild().getTextContent();

			//check whether the symbol has been declared or not

			out.write(symbol);
			//traverseTree(node.getFirstChild());
		}

		else if(elementName.equals("constant"))
		{
			//op = read_text_set(node);
			String constant = node.getFirstChild().getTextContent();

			//check whether the symbol has been declared or not

			out.write(" " +constant + " ");
			//traverseTree(node.getFirstChild());
		}

		else if(elementName.equals("emptyset"))
		{
			out.write(" {}");
			//traverseTree(node.getFirstChild());
		}
		else if(elementName.equals("emptystring"))
		{
			out.write(" []");
			//traverseTree(node.getFirstChild());
		}
		else if(elementName.equals("zero"))
		{
			out.write(" 0");
			//  traverseTree(node.getFirstChild());
		}

		else if(elementName.equals("is_initial"))
		{

			out.write(" (is_initial (");
			traverseTree(node.getFirstChild(), doc);
			out.write(")) ");
			/*out.write("i"+ i);
			i++;*/
		}
		
		else if(elementName.equals("min"))
		{

			out.write(" (min (");
			traverseTree(node.getFirstChild(), doc);
			out.write(",");
			traverseTree(node.getLastChild(), doc);
			out.write(")) ");
			/*out.write("i"+ i);
			i++;*/
		}
		
		else if(elementName.equals("max"))
		{

			out.write(" (max (");
			traverseTree(node.getFirstChild(), doc);
			out.write(",");
			traverseTree(node.getLastChild(), doc);
			out.write(")) ");
			/*out.write("i"+ i);
			i++;*/
		}

		else if(elementName.equals("element"))
		{
			out.write(" ( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" in ");
			traverseTree(node.getLastChild(), doc);
		
			out.write(") ");
		}

		else if(elementName.equals("notelement"))
		{
			out.write(" ( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" !in ");
			traverseTree(node.getLastChild(), doc);
		
			out.write(") ");
		}

		else if(elementName.equals("not"))
		{

			out.write("( ! ");
			traverseTree(node.getFirstChild(), doc);
			out.write(")  ");
		}
		
		else if(elementName.equals("thereexists") || elementName.equals("exists"))
		{
			//hack to prove more nested quantifiers: remove the next 2 lines
			//out.write("exists ");			
			//traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
		}
		
		else if(elementName.equals("forall"))
		{
			out.write("forall ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
		}

		else if(elementName.equals("vars"))
		{
			//op = read_text_set(node);
			String type = node.getAttributes().getNamedItem("type").getNodeValue();
			String dfny_type= "";

			//check whether the symbol has been declared or not

			if(type.equals("finiteset(object)"))
			{

				dfny_type = "set<T>";
			}
			
			if(type.equals("finiteset(integer)"))
			{

				dfny_type = "set<int>";
			}
			
			if(type.equals("string(object)") || type.equals("string of item") || type.equals("string"))
			{

				dfny_type = "seq<T>";
			}

			else if (type.equals("object"))
			{
				dfny_type = "T";
			}

			else if (type.equals("integer"))
			{
				dfny_type = "int" ;
			}

			NodeList var = node.getChildNodes();
			int v = var.getLength();
			//if there are multiple variables, we need to add commas between their names
			if(v>1)
			{
			for (int j =0; j<v-1; j++)
			{
			String vars = var.item(j).getTextContent();
			out.write(vars + " :" + dfny_type + ",");
			}
			}
			String vars = var.item(v-1).getTextContent();
			
			out.write(vars + " :" + dfny_type + ":: ");
			//out.write(":"+dfny_type+":: ");
		}
		
		else if(elementName.equals("body"))
		{
			//System.out.println("found body");
			traverseTree(node.getFirstChild(), doc);
			
		}
		
		else if(elementName.equals("substring"))
		{
			out.write("substring( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(",");
			traverseTree(node.getFirstChild().getNextSibling(), doc);
			out.write(",");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}
		else if(elementName.equals("function"))
		{
			String name = node.getAttributes().getNamedItem("name").getNodeValue();
			String type = node.getAttributes().getNamedItem("type").getNodeValue();

			out.write(name+ "(");
			
			NodeList args = node.getChildNodes();
			int a = args.getLength();

			//findMethods(doc);

			if (a> 1) 
			{
				for (int j = 0; j < a-1; j++)
				{
					traverseTree(args.item(j).getFirstChild(), doc);
					out.write(",");
				}
			}
			traverseTree(args.item(a-1).getFirstChild(), doc);
			out.write(")  ");
		}
		
		else if(elementName.equals("reverse"))
		{
			out.write("reverse( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(")");
		}

	}//end traverseTree(Node node)

	public void declare_funs(Document doc) throws IOException
	{
		//find emptyset
		NodeList emptyset = doc.getElementsByTagName("emptyset");
		NodeList substring = doc.getElementsByTagName("substring");

		//find card

		NodeList card = doc.getElementsByTagName("bar");
		NodeList min = doc.getElementsByTagName("min");
		NodeList max = doc.getElementsByTagName("max");
		NodeList reverse = doc.getElementsByTagName("reverse");
		
		String cName = doc.getDocumentElement().getAttribute("cName");

		if(cName.contains("set"))
		{

			if(card.getLength() > 0)
			{
				out.newLine();	
				//	out.write("(declare-fun card (Set) Int)");
				//out.write("method card (s: set<T>) returns (y:int)");
				out.write("function card (s: set<int>) :int");
				out.newLine();	
			}
		}
		
		if(cName.contains("Integer"))
		{
			if(card.getLength() > 0)
			{
				out.newLine();
				out.write("function card (x: int):int");
				out.newLine();
				out.write("{ if (x >= 0) then x else  -x }");
				out.newLine();

			}
		}

		//find is_initial

		NodeList is_initial = doc.getElementsByTagName("is_initial");

		if(is_initial.getLength() > 0)
		{
			out.newLine();	
			out.write("function is_initial(x: T) : bool");
			out.newLine();
			out.newLine();
		}
		
		if(substring.getLength() > 0)
		{
			out.newLine();	
			out.write("function substring(s: seq<T>, start : int, finish: int) : seq<T>");
			out.newLine();
			out.newLine();
		}
		
		if(min.getLength() > 0)
		{
			out.newLine();	
			out.write("function min(x: int, y:int) : int");
			out.newLine();
			out.write("{   if (x < y) then x else y }");
			out.newLine();
		}
		
		if(max.getLength() > 0)
		{
			out.newLine();	
			out.write("function max(x: int, y:int) : int");
			out.newLine();
			out.write("{   if (x > y) then x else y }");
			out.newLine();
		}
		
		if(reverse.getLength() > 0)
		{
			out.newLine();	
			out.write("function reverse(s: seq<T>): seq<T> ");
			out.newLine();
			out.newLine();
		}
		NodeList fn = doc.getElementsByTagName("function");

		int numfuns = fn.getLength();

		out.newLine();	

		for (int i = 0; i < numfuns; i++) 
		{
			//System.out.println(vars.item(i).getFirstChild().toString()); 
			String fn_name = fn.item(i).getAttributes().getNamedItem("name").getNodeValue();

			//System.out.println(fn_name);
			if (!(var_decld.contains(fn_name)))
			{
				var_decld.add(fn_name);

				if(fn_name.equals("ARE_IN_ORDER"))
				{

					out.write("function "+ fn_name + " (x : T, y : T) : bool");
					out.newLine();
				}

				else if(fn_name.equals("OCCURS_COUNT"))
				{

					out.write("function "+ fn_name + " (s : seq<T>, i :T): int");
					out.newLine();
				}
				else if(fn_name.equals("ARE_PERMUTATIONS"))
				{

					out.write("function "+ fn_name + " (s1 :seq<T>, s2: seq<T>): bool");
					out.newLine();
				}
				else if(fn_name.equals("PRECEDES"))
				{

					out.write("function "+ fn_name + "  (s1: seq<T>, s2: seq<T>): bool");
					out.newLine();
				}
				else if(fn_name.equals("IS_NONDECREASING"))
				{

					out.write("function "+ fn_name + " (s: seq<T>): bool ");
					out.newLine();
				}
			}//end if


		}//end for

		out.newLine();	
	}
	
	public void declare_lemmas(Document doc) throws IOException
	{
		NodeList card = doc.getElementsByTagName("bar");
		NodeList reverse = doc.getElementsByTagName("reverse");
		
		String cName = doc.getDocumentElement().getAttribute("cName");

		if(cName.contains("set"))
		{

			if(card.getLength() > 0)
			{
				out.newLine();	
				out.write("assume (forall s: set<int> :: card(s) >= 0);");
				out.newLine();	
				out.write("assume (forall s: set<int> :: s == {} ==> card(s) == 0);");
				out.newLine();	
				out.write("assume (forall s: set<int>, x: int :: x in s ==> card(s-{x}) == (card(s) - 1 ));");
				out.newLine();
				out.write("assume (forall s: set<int>, t: set<int> :: card(s+t) <= card(s) + card(t));");
				out.newLine();				

			}
		}
		
		if(reverse.getLength() > 0)
		{
			out.newLine();	
			out.write("//reverse definition");
			out.newLine();
			out.write("assume (forall s:seq<T> :: if s ==[] then reverse(s) == [] else (exists t: seq<T>, x: T :: s == [x] + t && reverse(s) == reverse(t) + [x]));");
			out.newLine();	
			out.write("//reverse lemmas");
			out.newLine();
			//out.write("assume (reverse([]) == []);");
			out.newLine();
			//out.write("assume (forall x: T :: reverse([x]) == [x]);");
			out.newLine();
			//out.write("assume (forall s1: seq<T>, s2: seq<T> :: s1 == reverse(s2) ==> s2 == reverse(s1));");
			out.newLine();
			//out.write("assume (forall s1: seq<T>, s2: seq<T> :: reverse(s2 + s1) == reverse(s1) + reverse(s2));");
			out.newLine();
			//out.write("assume (forall s1: seq<T>, s2: seq<T>, s3: seq<T>, s4: seq<T> :: ((reverse(s1) + s2) == (reverse(s3) + s4) && |s3| == |s1|)==> (s1 == s3 && s2 == s4));");
			//out.newLine();
			//out.write("assume (forall s1: seq<T> :: |reverse(s1)| == |s1|);");
			out.newLine();
			//out.write("assume (forall s1: seq<T>, s2: seq<T> :: (reverse(s1) == reverse(s2)) ==> (s1 == s2));");
			out.newLine();
			out.newLine();
		}
		
		NodeList substring = doc.getElementsByTagName("substring");
		if(substring.getLength() > 0)
		{
		out.newLine();	
		out.write("assume ( forall s: seq<T>, start : int, finish: int ::(start < 0 || start > finish || finish > |s| ==> substring(s, start, finish) == []) && (!(start < 0 || start > finish || finish > |s|) ==> (exists a:seq<T>, b:seq<T> :: s == a + substring(s, start, finish) + b && |a| == start&& |b| == |s| - finish)));");
		out.newLine();	
		
		//reading the lemmas
		try{
			  // Open the file that is the first 
			  // command line parameter
			  FileInputStream fstream = new FileInputStream("C:\\MY_STUFF\\research\\syrus_Dafny\\lemmas\\substring_lemmas.thy");
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  //Read File Line By Line
			  while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
			  out.write (strLine);
			  out.newLine();	
			  }
			  //Close the input stream
			  in.close();
			    }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }
		}
		
		if(cName.contains("String"))
		{
			if(var_decld.contains("ARE_IN_ORDER"))
			{
				out.newLine();	
				out.write("assume (forall x, y  :: ARE_IN_ORDER(x, y) || ARE_IN_ORDER(y, x));");
				out.newLine();	
				out.write("assume (forall x, y, z :: (ARE_IN_ORDER(x, y) && ARE_IN_ORDER(y, z) ==> ARE_IN_ORDER(x, z)));");		
				out.newLine();
			}

			if(var_decld.contains("OCCURS_COUNT"))
			{
				out.newLine();	
				out.write("assume ( forall s:seq<T>, i :T :: if s == [] then OCCURS_COUNT(s, i) == 0 else (exists x: T, r: seq<T> :: s == [x] + r && (if x == i then OCCURS_COUNT(s, i) == OCCURS_COUNT(r, i) +1 else OCCURS_COUNT(s, i) == OCCURS_COUNT(r, i))));	");		
				out.newLine();
			}
			if(var_decld.contains("PRECEDES"))
			{
				out.newLine();	
				out.write("assume (forall s1: seq<T>, s2: seq<T>:: PRECEDES(s1, s2) == (forall i, j :: (OCCURS_COUNT(s1, i) > 0 && OCCURS_COUNT(s2, j) > 0 ==> ARE_IN_ORDER(i, j)))) ;	");		
				out.newLine();
			}
			if(var_decld.contains("IS_NONDECREASING"))
			{
				out.newLine();	
				out.write("assume (forall s: seq<T>:: IS_NONDECREASING(s) == (forall a: seq<T> , b: seq<T> :: s == a + b ==> PRECEDES(a, b))); ");		
				out.newLine();
			}
			if(var_decld.contains("ARE_PERMUTATIONS"))
			{
				out.newLine();	
				out.write("assume (forall s1: seq<T>, s2: seq<T>:: ARE_PERMUTATIONS(s1, s2) == (forall i :: OCCURS_COUNT(s1, i) == OCCURS_COUNT(s2, i))) ;");		
				out.newLine();
			}
		}

	}//end declare_lemmas
}
