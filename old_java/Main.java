import java.io.*;
import java.util.*;

public class Main {
	public static void main(String args[]){
		ArrayList<course> courseData = new ArrayList<course>();
		ArrayList<String> parsedtitle = new ArrayList<String>();
		ArrayList<String> tabooList = new ArrayList<String>();
		ArrayList<String> tabooList2 = new ArrayList<String>();
		ArrayList<String> rareList = new ArrayList<String>();
		
		int[] searchNum;//�������ꂽ�R�[�X�ԍ�
		
		try{
			BufferedReader rd1 = new BufferedReader(new FileReader("mitCourseDataNoCapital.csv"));
			BufferedReader rd2 = new BufferedReader(new FileReader("ParsedCourseTitle.txt"));
			
			
			while(true){
				String line = rd2.readLine();
				if(line==null) break;
				parsedtitle.add(line);
			}
			
			while(true){
				String line = rd1.readLine();
				if(line == null) break;
				int start = line.indexOf(",");
				int end = line.indexOf(",", start+1);
				String title = " "+ line.substring(0,start)+" ";
				String syl = line.substring(start+1, end);
				start = end+1;
				end = line.indexOf(",",start);
				String dep = line.substring(start, end);
				start = end+1;
				end = line.indexOf(",",start);
				String url=line.substring(start, end);
				ArrayList<String> keywords = new ArrayList<String>();
				String allinfo = line;
				
				for(String parsed: parsedtitle){
					if(title.contains(" "+parsed+" ")||title.contains(" "+parsed+"s")||
							title.contains(" "+parsed.substring(0, parsed.length()-1)+"ies")||title.contains(" "+parsed+"?")||
							title.contains(" "+parsed+":")||title.contains(" "+parsed+";")) keywords.add(parsed);
				}
				
				course c = new course(title, keywords, syl, allinfo, dep,url);
				courseData.add(c);
				//System.out.println(c.toString());
			}
		} catch(IOException e){ System.out.println("bad file2");}
		
		for(String parsed: parsedtitle){
			int count = 0;
			for(course c: courseData){
				String title = c.getTitle();
				if(title.contains(" "+parsed+" ")||title.contains(" "+parsed+"s")||
						title.contains(" "+parsed.substring(0, parsed.length()-1)+"ies")||title.contains(" "+parsed+"?")) count++;
			}
			if(count>20) {
				tabooList2.add(parsed);
			}
			if(count>15||count==1) {
				tabooList.add(parsed);
				//System.out.println(parsed);
			}
			if(1<=count&&count<4){
				rareList.add(parsed);
				//System.out.println(parsed);
			}
		}
		Graph g = new Graph(courseData, tabooList, tabooList2, rareList,"GraphMat.txt");
		//Graph g = new Graph(courseData, tabooList, tabooList2, rareList);
		int num = args.length;
		searchNum = new int[num];
		for(int i=0; i<num; i++){
			searchNum[i] = Integer.parseInt(args[i]);
		}
		
		HashMap<String, ArrayList<course>> map = g.searchKey2(searchNum, 100);
		
		/*
		for(ArrayList<course> list: map.values()){
			for(course c: list){
				System.out.println(c.getAllinfo());
			}
		}
		*/
		
		try{
			PrintWriter wd = new PrintWriter(new FileWriter("output.csv"));
			for(ArrayList<course> list: map.values()){
				for(int i=0; i<list.size();i++){
					course c = list.get(i);
					ArrayList<String> relatedurl = c.getRelatedurl();
					ArrayList<String> relatedcourse = c.getRelatedCourse();
					String line = c.getClieque()+","+c.getAllinfo();
					int size = relatedurl.size();
					
					for(int j=0; j<size; j++){
						line = line + ","+relatedurl.get(j)+","+relatedcourse.get(j);
					}
					
					wd.println(line);
				}
			}
			wd.close();
		}catch(IOException e){System.out.println("bad file");}
		
		
		//ArrayList<ArrayList<course>> scc = g.scc();
		/*
		ArrayList<String> searchKey = new ArrayList<String>();
				searchKey.add("");
				searchKey.add("programming language");
				searchKey.add("");
		ArrayList<ArrayList<course>> neighbor = g.searchKey(searchKey,50);
		
		if(neighbor==null){System.out.println("no neighbor found");}else{
		for(ArrayList<course> list: neighbor){
			for(course c: list){
				System.out.println(c.toString());
			}
			System.out.println("----------------------------------");
		}
		}
		
		/*
		for(ArrayList<course> list: scc){
			for(course c: list){
				System.out.println(c.toString());
			}
			System.out.println("----------------------------------");
		}*/
		
		//csv file�̍쐬
		/*
		try{
			PrintWriter wd = new PrintWriter(new FileWriter("clusteredData.csv"));
			int i =0;
			for(ArrayList<course> list: scc){
				for(course c: list){
					String line = "";
					line = line + i + ","+c.getAllinfo();
					wd.println(line);
				}
				i++;
			}
			
			
		}catch(IOException e){}
		*/
		
	}
	
	
	private void parseTitle(){
	try{
		BufferedReader rd = new BufferedReader(new FileReader("Coursetitle.txt"));
		PrintWriter wd = new PrintWriter(new FileWriter("CoursetitlewithPeriod.txt"));
		
		while(true){
			String line = rd.readLine();
			if(line == null) break;
			line = line.replaceAll(" of ", ": ");
			line = line.replaceAll(" and ", ": ");
			line = line.replaceAll(" ii ", "");
			line = line.replaceAll(" i ", "");
			line = line.replaceAll(" iii ", "");
			line = line.replaceAll("introduction", "");
			line = line.replaceAll("introductory", "");
			line = line.replaceAll("intro","");
			line = line + ":";
			line = line.replaceAll(" iii:", ":");
			line = line.replaceAll(" ii:", ":");
			line = line.replaceAll(" i:", ":");
			wd.println(line);
		}
		rd.close();
		wd.close();
	}catch (IOException e){System.out.println("no file");}
	}
}

class course{
	private String courseTitle;
	private ArrayList<String> titleKey;
	private String syllabus;
	private String allinfo;
	private int courseNum;
	private String dep;
	private String clieque;
	private String url;
	private ArrayList<String> relatedurl;
	private ArrayList<String> relatedcourse;
	
	//constructor
	public course(String title, ArrayList<String> keywords, String syl, String allinfo, String dep,String url){
		courseTitle = title;
		titleKey = keywords;
		syllabus = syl;
		this.allinfo = allinfo;
		this.dep = dep;
		this.url = url;
	}
	
	//getters
	public String getTitle(){
		return courseTitle;
	}
	
	public ArrayList<String> getRelatedurl(){
		return relatedurl;
	}
	
	public String geturl(){
		return url;
	}
	
	public String getDep(){
		return dep;
	}
	
	public ArrayList<String> getKeywords(){
		return titleKey;
	}
	
	public String getSyl(){
		return syllabus;
	}
	
	public String getAllinfo(){
		return allinfo;
	}
	
	public int getCourseNum(){
		return courseNum;
	}
	
	public ArrayList<String> getRelatedCourse(){
		return relatedcourse;
	}
	
	public String toString(){
		String line = "title: "+ courseTitle; //+ "/ keywords: ";
		/*
		for(String key: titleKey){
			line += key + ", ";
		}*/
		line += " |  department: "+dep;
		return line;
	}
	
	public void setCourseNum(int n){
		courseNum = n;
	}
	
	public void setClieque(String c){
		this.clieque = c;
	}
	
	public void setRelatedurl(ArrayList<String> r){
		this.relatedurl = r;
	}
	
	public void setRelatedCourse(ArrayList<String> r){
		this.relatedcourse = r;
	}
	
	public String getClieque(){
		return clieque;
	}
	
}




