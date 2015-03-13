import java.io.*;
import java.util.*;

public class Graph {
	//�אڍs��@A->B��A�����ۂ�B�����
	private ArrayList<ArrayList<Integer>> GraphMat;
	//GraphMat�̓]�u�s��
	private ArrayList<ArrayList<Integer>> GraphMatTranspose;
	ArrayList<course> courseData;
	private int N; //course�̐�
	
	private ArrayList<String> tabooList;
	private ArrayList<String> tabooList2;
	private ArrayList<String> rareList;
	
	//scc�p
	//dfs1�̊e�I�������ɑ�������R�[�X�ԍ����L�^���邽�߂̔z��
	private int[] stack;
	//���̒��_�ɖK�ꂽ���Ƃ����邩�ǂ������L�^�B�K�ꂽ=1,�K��Ă��Ȃ�=0
	private int[] flag;
	
	//���A�������̔z��(�e�R�[�X�����Ԗڂ̋��A�������ɑ����邩��Ԃ�)
	private int[] component;
	
	//dfs1�ɂ�����I������
	private int fin;
	
	//dfs2�ɂ����Č��ݒT������component
	private int co;
	
	//�[���D��T���̐[������
	private int depth;
	
	//constructor 1
	public Graph(ArrayList<course> courseData, ArrayList<String> taboo, ArrayList<String> taboo2, ArrayList<String> rare){
		this.courseData = courseData;
		N = courseData.size();
		tabooList = taboo;
		tabooList2 = taboo2;
		rareList = rare;
		//�אڍs��Ƃ��̓]�u�s���set����
		setGraph();
		printGraphMat("GraphMat.txt");
		
		for(int i=0; i<N; i++){
			System.out.println(nearCourse(courseData.get(i)).size());
		}
	}	
	
	//constructor 2
	public Graph(ArrayList<course> courseData, ArrayList<String> taboo, ArrayList<String> taboo2, ArrayList<String> rare, String filename){
		this.courseData = courseData;
		N = courseData.size();
		tabooList = taboo;
		tabooList2 = taboo2;
		rareList = rare;
		//�t�@�C����GraphMat�Ƃ��̓]�u�s��ɓǂݍ���
		readIntoGraphMat(filename);
		printGraphMat("GraphMat2.txt");
	}	
	
	private void readIntoGraphMat(String filename){
		int q=0;
		//course�ɔԍ�������U��
		for(course c: courseData){
			c.setCourseNum(q);
			q++;
		}
		
		GraphMat = new ArrayList<ArrayList<Integer>>();
		try{
		BufferedReader rd = new BufferedReader(new FileReader(filename));
		while(true){
			String line = rd.readLine();
			if(line==null) break;
			ArrayList<Integer> list = new ArrayList<Integer>();
			int end;
			int start=0;
			if(line.equals("")){
				GraphMat.add(list); //���������Ă��Ȃ������ꍇ
			}else{
				int num = line.length()-line.replaceAll(" ", "").length();//�����̐�
				for(int i=0; i<num; i++){
					end = line.indexOf(" ", start);
					String number = line.substring(start, end);
					start=end+1;
					list.add(Integer.parseInt(number));
				}
				GraphMat.add(list);
			}
		}
		}catch(IOException e){System.out.println("bad file");}
		
		//�]�u�O���t�̌v�Z
		GraphMatTranspose = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<N; i++){
			ArrayList<Integer> list = new ArrayList<Integer>();
			GraphMatTranspose.add(list);
		}
		
		for(int i=0; i<N; i++){
			for(Integer n: GraphMat.get(i)){
				GraphMatTranspose.get(n).add(i);
			}
		}
	}
	
	private void printGraphMat(String filename){
		try {
			PrintWriter wd = new PrintWriter(new FileWriter(filename));
			for(ArrayList<Integer> list: GraphMatTranspose){
				for(Integer n: list){
					wd.print(n+" ");
				}
				wd.println("");
			}
			wd.close();
		} catch (IOException e) {
			System.out.println("bad file");
		}
	}
	
	private void setGraph(){
		GraphMat = new ArrayList<ArrayList<Integer>>();
		GraphMatTranspose = new ArrayList<ArrayList<Integer>>();
		int i=0;
		//course�ɔԍ�������
		for(course c: courseData){
			c.setCourseNum(i);
			ArrayList<Integer> cc = new ArrayList<Integer>();
			ArrayList<Integer> cct = new ArrayList<Integer>();
			GraphMat.add(cc);
			GraphMatTranspose.add(cct);
			i++;
		}
		
		int p = 0;
		int q = 0;
		for(course c: courseData){
			ArrayList<String> keywords = c.getKeywords();
			for(course compare: courseData){
				if(c==compare) continue;
				if(hasSimilarTitle(keywords, compare)){
					addEdge(c,compare, GraphMat);
					addEdge(compare,c, GraphMat);
					addEdge(c,compare, GraphMatTranspose);
					addEdge(compare,c, GraphMatTranspose);
					p++;
				} else if(hasRelatedContent(keywords, compare)){
					addEdge(compare,c, GraphMat);
					addEdge(c,compare, GraphMatTranspose);
					q++;
				}
			}
		}
		System.out.println("p: "+p);
		System.out.println("q: "+q);
	}
	
	//title���߂��Ɣ��f���ꂽ�ꍇ��true keywords��0.7�ȏ��v
	private boolean hasSimilarTitle(ArrayList<String> keywords, course compare){
		int n = 0;
		int similarity=0;
		boolean hasRareInCommon = false;
		String title = compare.getTitle();
		for(String word: keywords){
			if(!tabooList.contains(word)){
				if(title.contains(" "+word+" ")||title.contains(" "+word+"s")||
					title.contains(" "+word.substring(0, word.length()-1)+"ies")||title.contains(" "+word+"?")||
					title.contains(" "+word+":")||title.contains(" "+word+";")){
					similarity++;
					if(rareList.contains(word)){
						similarity++; //rare�ȒP�ꂪ�����Ă���ƃ{�[�i�X�|�C���g
						hasRareInCommon = true;
					}
				}
					n++;
			}
		}
		//double rate = (double)similarity/n;
		if((similarity>2 && n>1)||hasRareInCommon) return true;
		return false;
	}
	
	//title��compare��syllabus���߂��Ɣ��f���ꂽ�ꍇ��true ��v���������ȏ�
	private boolean hasRelatedContent(ArrayList<String> keywords, course compare){
		int n = 0;
		double similarity=0;
		boolean hasRareInCommon = false;
		String syl = " "+compare.getSyl()+" ";
		for(String word: keywords){
			if(!tabooList2.contains(word)){
			if(syl.contains(" "+word+" ")||syl.contains(" "+word+"s")||
					syl.contains(" "+word.substring(0, word.length()-1)+"ies")||syl.contains(" "+word+"?")
					||syl.contains(" "+word+".")||syl.contains(" "+word+":")||syl.contains(" "+word+";")||
					syl.contains("\""+word+" ")||syl.contains("\""+word+".")||syl.contains("-"+word+" ")||
					syl.contains(" "+word+"-")||syl.contains("-"+word+" ")){
				similarity+=0.9;
				if(rareList.contains(word)) {
					similarity+=0.9; //�{�[�i�X
					hasRareInCommon = true;
				}
			}
				n++;
			}
		}
		//double rate = (double)similarity/n;
		if((n==1&&hasRareInCommon)||3<=similarity||(hasRareInCommon&&2<=similarity)) return true;
		return false;
	}
	
	//form����to�փG�b�W�������킦��B�������ɑ��݂��Ă����炻�̂܂܂ɂ���B
	private void addEdge(course from, course to,ArrayList<ArrayList<Integer>> Mat){
		ArrayList<Integer> list = Mat.get(from.getCourseNum());
		if(!list.contains(to.getCourseNum())) list.add(to.getCourseNum());
	}
	
	public void printMat(){
		for(int i=0; i<N; i++){
			ArrayList<Integer> list = GraphMat.get(i);
			String line = "";
			for(Integer n: list){
				line = line + " " + n;
			}
			System.out.println(i +" --> "+line);
		}
	}
	
	public HashMap<String, ArrayList<course>> searchKey2(int[] list, int n){
		int num = list.length;
		if(num==0) return null;
		int[] nearnum = new int[num];
		//�P�ꂪ����Ȃ�
		for(int i=0; i<num; i++){
			nearnum[i] = nearCourse(courseData.get(list[i])).size();
		}
		
		int[] array = new int[num];
		for(int i=0; i<num; i++){
			array[i] = i;
		}
		quicksort(nearnum, array, 0, num-1);
		
		HashMap<String, ArrayList<course>> courseSorted=new HashMap<String, ArrayList<course>>();
		ArrayList<course> added = new ArrayList<course>();//���łɒǉ����ꂽ�R�[�X
	
		int addednum=0; //�ǉ����ꂽ�R�[�X�̔ԍ�
		for(int i=0; i<num; i++){
				course c = courseData.get(list[array[i]]);
				boolean t = true;
				for(course d: added){
					if(d.geturl().equals(c.geturl())) t = false;
				}
				if(t){
				added.add(c);
				addednum++;
				}
		}		
		for(int i=0; i<num; i++){
			if(nearnum[i]>1){
				ArrayList<course> list1 = nearCourse(courseData.get(list[array[i]]));
				addednum += list1.size()-1;
				if(addednum>n) break;
				for(course c: list1){
					boolean t=true;
					for(course d: added){
						if(d.geturl().equals(c.geturl())) t = false;
					}
					if(t) added.add(c);
				}
			}
		}
		
		//addedに対してクリークを求める
		int numvertex = added.size();
		int numedge=0;
		
		ArrayList<ArrayList<Integer>> mat = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<numvertex; i++){
			ArrayList<Integer> l = new ArrayList<Integer>();
			mat.add(l);
		}
		
		for(int i=0; i<numvertex; i++){
			for(Integer m: GraphMat.get(added.get(i).getCourseNum())){
				int u=added.indexOf(courseData.get(m));
				if(u!=-1 && !mat.get(u).contains(i)){
					mat.get(i).add(u);
					numedge++;
				}
			}
		}
		
		try{
			PrintWriter wd = new PrintWriter(new FileWriter("Clieque.txt"));
			wd.println("1");
			wd.println(numvertex);
			wd.println(numedge);
			for(int i=0; i<numvertex; i++){
				for(Integer m: mat.get(i)){
					wd.println(i+ " " +m);
				}
			}
			wd.close();
		}catch(IOException e){System.out.println("bad file");}
		
		ArrayList<int[]> maxClieque = getMaxClieque("Clieque.txt");
		
		/*for(int[] a:maxClieque){
			for(int i=0; i<a.length; i++){
			System.out.print(a[i]+",");
			}
			System.out.println("");
		}*/
		
		int num1 = maxClieque.size();
		int[] cliequenum = new int[num1]; 
		for(int i=0; i<num1; i++){
			cliequenum[i] = maxClieque.get(i).length;
		}
		
		int[] array1 = new int[num1];
		for(int i=0; i<num1; i++){
			array1[i] = i;
		}
		quicksort(cliequenum, array1, 0, num1-1);
		
		ArrayList<Integer> cliequed = new ArrayList<Integer>();
		ArrayList<int[]> cliequelist = new ArrayList<int[]>();
		//クリークが大きい順に
		for(int i=num1-1; i>=0; i--){
			int[] cli = maxClieque.get(array1[i]);
			if(cli.length==1) break;
			if(cli.length>2){
			boolean t = true;
			for(int j=0; j<cli.length; j++){
				if(cliequed.contains(cli[j])) t = false;
			}
			if(t){
				//System.out.println("hi");
				cliequelist.add(cli);
				for(int k=0; k<cli.length; k++){
					cliequed.add(cli[k]);
				}
			}
			} else if(cli.length==2){
				int from = added.get(cli[0]).getCourseNum();
				int to = added.get(cli[1]).getCourseNum();
				if(!cliequed.contains(cli[0])&&!cliequed.contains(cli[1])){
				if(GraphMat.get(from).contains(to)&&GraphMat.get(to).contains(from)) {
					cliequelist.add(cli);
					for(int k=0; k<cli.length; k++){
						cliequed.add(cli[k]);
					}
				}
				}
			}
		}
		
		
		for(int[] arr: cliequelist){
			ArrayList<course> li = new ArrayList<course>();
			for(int i=0; i<arr.length; i++){
				//System.out.println(added.get(arr[i]).toString());
				li.add(added.get(arr[i]));
			}
			for(int i=0; i<arr.length; i++){
				added.get(arr[i]).setClieque(extractKeyWords(li));
			}
			
			for(int i=0; i<li.size();i++){
				ArrayList<String> relatedurl = new ArrayList<String>();
				ArrayList<String> relatedcourse = new ArrayList<String>();
				course c = li.get(i);
				for(int j=0; j<li.size();j++){
					if(i!=j) {
						relatedurl.add(li.get(j).geturl());
						relatedcourse.add(li.get(j).getTitle());
					}
					
				}
				c.setRelatedCourse(relatedcourse);
				c.setRelatedurl(relatedurl);
				}
			}	
		
		
		for(int i=0; i<added.size(); i++){
			if(cliequed.contains(i)){
			course c = added.get(i);
			String dep = c.getDep();
			if(!courseSorted.containsKey(dep)){
				ArrayList<course> list2 = new ArrayList<course>();
				list2.add(c);
				courseSorted.put(dep, list2);
			} else{
				courseSorted.get(dep).add(c);
			}
			}
		}
		return courseSorted;
	}
	
	private String extractKeyWords(ArrayList<course> list){
		ArrayList<String> titlelist = new ArrayList<String>();
		for(course c: list){
			for(String title: c.getKeywords()){
				if(!titlelist.contains(title)&&!tabooList2.contains(title)) titlelist.add(title);
			}
		}
		int num = titlelist.size();
		if(num==0) return null;
		//各単語の出現回数を数える
		int[] countarray = new int[num];
		
		for(int i=0; i<num; i++){
			String item = " "+titlelist.get(i);
			int count = 0;
			for(course c: list){
				String t = c.getTitle();
				String s = c.getSyl();
				if(t.contains(item)) count++;
				if(s.contains(item)) count++;
			}
			countarray[i] = count;
		}
		
		int[] array = new int[num];
		for(int i=0; i<num; i++){
			array[i] = i;
		}
		quicksort(countarray, array, 0, num-1);
		return titlelist.get(array[num-1]);
	}
	
	
	
	
	private ArrayList<course> nearCourse(course c){
		ArrayList<course> list = new ArrayList<course>();
		for(Integer n:GraphMat.get(c.getCourseNum())){
			list.add(courseData.get(n));
		}
		for(Integer n:GraphMatTranspose.get(c.getCourseNum())){
			if(!list.contains(courseData.get(n))) list.add(courseData.get(n));
		}
		return list;
	}

	private ArrayList<int[]> getMaxClieque(String filename){
		BufferedReader bufReader = null; 
        
        File file = new File("Clieque.txt"); 
        try { 
            bufReader = new BufferedReader(new FileReader(file));
        } catch (Exception e) { 
            e.printStackTrace(); 
            return null; 
        } 
     
    MaximalCliquesWithPivot ff = new MaximalCliquesWithPivot();
    try { 
        int totalGraphs = ff.readTotalGraphCount(bufReader);
        for (int i = 0; i < totalGraphs; i++) {
            ff.readNextGraph(bufReader); 
            ff.Bron_KerboschPivotExecute(); 

        } 
    } catch (Exception e) { 
        e.printStackTrace(); 
        System.err.println("Exiting : " + e); 
    } finally { 
        try { 
            bufReader.close(); 
        } catch (Exception f) { 

        } 
    } 
    return ff.getCliequeList();
} 
	
	
	
	//���A����������scc
	public ArrayList<ArrayList<course>> scc(){
		stack = new int[N];
		flag = new int[N];
		component = new int[N];
		initFlag();
		fin = 0;
		
		int[] sorted = new int[N];
		for(int i=0; i<N; i++){
			sorted[i] = i;
		}
		
		qsort(sorted);
		
		for(int i=0; i<N; i++){
			if(flag[sorted[i]]==0) dfs1(sorted[i]);
		}
		
		co=0;
		initFlag();
		//�I���������x�����Ɍ��Ă���
		depth = 0;
		for(int i=N-1; i>=0; i--){
			if(flag[stack[i]]==0){
				dfs2(stack[i]);
				co++;
			}
		}
		System.out.println("number of components: " +co);
		ArrayList<ArrayList<course>> scc = new ArrayList<ArrayList<course>>();
		for(int i=0; i<co; i++){
			ArrayList<course> list = new ArrayList<course>();
			for(int j=0; j<N; j++){
				if(component[j]==i){
					list.add(courseData.get(j));
				}
			}
			System.out.println(list.size());
			scc.add(list);
		}	
		return scc;
	}
	
	private void initFlag(){
		for(int i=0; i<N; i++){
			flag[i] = 0;
		}
	}
	
	
	private void qsort(int[] array){
		int[] connectNum = new int[N];
		for(int i=0; i<N; i++){
			connectNum[i]=GraphMatTranspose.get(i).size()-GraphMat.get(i).size(); //i�̋�̐�-i�̒��ې������������̂�����ׂĂ���
		}
		quicksort(connectNum, array, 0, N-1);
	}
	
	private void quicksort(int[] connectNum, int[] array, int p, int r){
		int q;
		
		if(p<r){
		q = partition(connectNum, array, p, r);
		quicksort(connectNum, array, p, q);
		quicksort(connectNum, array, q+1, r);
		}
	}
	
	//connectNum��sort����Barray�͑Ή�����悤�ɕ��ёւ���
	private int partition(int[] connectNum, int[] array, int p, int r){
		int i, j;
		int x; //�s�{�b�g
		int tmp;
		x = connectNum[p];
		i = p-1; j = r+1;
		while(true){
			do{j = j-1;} while(connectNum[j]>x);
			do{i = i+1;} while(connectNum[i]<x);
			if(i<j){
				tmp = connectNum[i]; connectNum[i] = connectNum[j]; connectNum[j] = tmp;
				tmp = array[i]; array[i] = array[j]; array[j] = tmp;
			} else{
				return j;
			}
		}
	}
	
	
	//s���n�_�Ƃ���[���D��T��1,GraphMat�ɑ΂��čs��
	private void dfs1(int s){
		flag[s] = 1;
		ArrayList<Integer> list = GraphMat.get(s);
		for(Integer n: list){
			if(flag[n]==0) dfs1(n);
		}
		stack[fin]=s;
		fin++;
	}
	
	//s���n�_�Ƃ���[���D��T��2,GraphMatTranspose�ɑ΂��čs��
	//�[������2
	private void dfs2(int v){
		depth++; //���[����
		if(depth<3){
			flag[v] = 1;
			component[v] = co;
			ArrayList<Integer> list = GraphMatTranspose.get(v);
			for(Integer n:list){
				if(flag[n]==0) dfs2(n);
			}
		}
		depth--; //��
	}
}
