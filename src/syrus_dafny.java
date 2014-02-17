import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class syrus_dafny {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//first I will delete the old dfy files
/*
		String DirName1 = ("C:\\MY_STUFF\\research\\syrus_Dafny\\dafny\\");
		File[] listOfFiles1 =  GetFilesFromDirectory(DirName1);

		int numofFiles1 = listOfFiles1.length;
		String files1; 

		for(int i=0; i< numofFiles1; i++)
		{
			if (listOfFiles1[i].isFile()) 
			{
				listOfFiles1[i].delete();
			}
		}
		//now delete the old answer
		String DirName2 = ("C:\\MY_STUFF\\research\\syrus_Dafny\\answer\\");
		File[] listOfFiles2 =  GetFilesFromDirectory(DirName2);

		int numofFiles2 = listOfFiles2.length;
		String files2; 

		for(int i=0; i< numofFiles2; i++)
		{
			if (listOfFiles2[i].isFile()) 
			{
				listOfFiles2[i].delete();
			}
		}*/
		
		//retreive the problems
		//String DirName = ("C:\\MY_STUFF\\research\\syrus_Dafny\\problems\\");
		//String DirName = ("C:\\MY_STUFF\\research\\syrus_Dafny\\SampleStringFormulas\\problems_from_diego\\4th\\SetFormula3\\SetFormula3\\");
		String DirName = ("C:\\MY_STUFF\\research\\syrus_Dafny\\SampleStringFormulas\\problems_from_diego\\all\\all\\all\\");
		File[] listOfFiles = GetFilesFromDirectory(DirName);

		int numofFiles = listOfFiles.length;
		String files;

		for(int i=0; i< numofFiles; i++)
		{
			if (listOfFiles[i].isFile()) 
			{
				files = listOfFiles[i].getName();
				XMLProcessor xml_processor = new XMLProcessor();
				xml_processor.ProcessXmlsFromDirectory(files, DirName);
			}
		}
		//pass the XML file list to pct
		pct(listOfFiles);

	}//end main

	public static File[] GetFilesFromDirectory(String path)
	{

		String files;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles(); 

		for (int i = 0; i < listOfFiles.length; i++) 
		{

			if (listOfFiles[i].isFile()) 
			{
				files = listOfFiles[i].getName();
				//  System.out.println(files);
			}
		}

		return listOfFiles;
	}//end GetFilesFromDirectory(String path)

	public static void pct(File[] listOfFilesXML)
	{
		
		try {
			String line;
			//String DirName = ("C:\\MY_STUFF\\research\\syrus_Dafny\\dafny\\");
			//String DirName = ("C:\\MY_STUFF\\research\\syrus_Dafny\\SampleStringFormulas\\problems_from_diego\\4th\\SetFormula3\\dafny\\");
			String DirName = ("C:\\MY_STUFF\\research\\syrus_Dafny\\SampleStringFormulas\\problems_from_diego\\all\\all\\dafny\\");
			File[] listOfFiles =  GetFilesFromDirectory(DirName);
			
			FileWriter fstream; 
			BufferedWriter out;

			int numofFiles = listOfFiles.length;
			int numofFilesXML = listOfFilesXML.length;
			
			System.out.println(numofFiles+","+numofFilesXML);
			String files; int verified = 0, error = 0, timeout = 0;
			
			//fstream = new FileWriter("C:\\MY_STUFF\\research\\syrus_Dafny\\answer\\result.txt");
			//fstream = new FileWriter("C:\\MY_STUFF\\research\\syrus_Dafny\\SampleStringFormulas\\problems_from_diego\\4th\\SetFormula3\\answer\\result.txt");
			
			fstream = new FileWriter("C:\\MY_STUFF\\research\\syrus_Dafny\\SampleStringFormulas\\problems_from_diego\\all\\all\\answer\\result.txt");
			out = new BufferedWriter(fstream);

			for(int i=0; i< numofFiles; i++)
			{
				if (listOfFiles[i].isFile()) 
				{
					files = listOfFiles[i].getName();
					//System.out.print(""+files + "-"); 
					out.write(""+files + "-"); 

					verified = 0; error = 0; timeout = 0;
					//String[] cmd = {"C:\\Users\\adititagore\\Desktop\\research\\dafny_25_May_2012\\dafny.exe", "/timeLimit:1", "/compile:0","/nologo", "C:\\MY_STUFF\\research\\syrus_Dafny\\dafny\\"+files};
					String[] cmd = {"C:\\Users\\adititagore\\Desktop\\research\\dafny_25_May_2012\\dafny.exe", "/timeLimit:1", "/compile:0","/nologo", "C:\\MY_STUFF\\research\\syrus_Dafny\\SampleStringFormulas\\problems_from_diego\\all\\all\\dafny\\"+files};
					Process p = Runtime.getRuntime().exec(cmd);



					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

					
					
					while ((line = input.readLine()) != null)
					{
						if(line.contains("errors detected") || line.contains("assertion violation"))
						{
							error = 1;
							//System.out.print("Error detected : " );
						//	System.out.println(line);
						}
						else if (line.contains("time out"))
						{
							timeout = 1;
						}
						else if(line.contains("0 errors") && !(line.contains("time out") ))
						{
							//System.out.println("(DEBUG)- verified is "+verified);
							verified = 1;
							//System.out.print("Verified" );
							//System.out.println(line);
						}
						//System.out.print(line+"-");
						out.write(line+"-");
					}

					if (verified == 1)
					{
					//	System.out.print("VERIFIED" );
						
						out.write("VERIFIED" );
						//File file = new File("C:\\MY_STUFF\\research\\syrus_Dafny\\problems\\"+listOfFilesXML[i]);
						//File file = new File("C:\\MY_STUFF\\research\\syrus_Dafny\\SampleStringFormulas\\problems_from_diego\\4th\\SetFormula3\\SetFormula3\\"+listOfFilesXML[i]);
					//	System.out.println(file.getName());
						//File dir = new File("C:\\MY_STUFF\\research\\syrus_Dafny\\true\\");
						File dir = new File("C:\\MY_STUFF\\research\\syrus_Dafny\\SampleStringFormulas\\problems_from_diego\\all\\all\\answer\\true\\");

						// Move file to new directory
						String name = listOfFilesXML[i].getName();
						boolean success = listOfFilesXML[i].renameTo(new File(dir, listOfFilesXML[i].getName()));
						if (!success) {
						   System.out.println(" File was not successfully moved: "+name);
						}
						listOfFilesXML[i].delete();
						
						//System.out.println();
					}
					if (error == 1)
					{
					//	System.out.print("ERROR" );
						out.write("ERROR" );
						//System.out.println();
					}
					if (timeout == 1)
					{
						//System.out.print("TIMEOUT" );
						out.write("TIMEOUT" );
						//System.out.println();
					}
				//	System.out.println();
					out.newLine();
					while ((line = stdError.readLine()) != null) {
					//	System.out.println(line);
						out.write(line);
						//System.out.println();
					}

					input.close();
					stdError.close();
					p.destroy();
					

				}
			}//end for
			out.close();
			System.out.println("DONE");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


}
