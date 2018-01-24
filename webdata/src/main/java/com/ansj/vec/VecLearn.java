package com.ansj.vec;
import com.ansj.vec.domain.WordEntry;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class VecLearn {

	public static void main(String[] args) throws IOException {

		VecLearn learn = new VecLearn();
//        learn.loadJavaModel("./files/javaVector.model");
        learn.loadJavaModel("./files/vec.mod");
		System.out.println(learn.distance("中国"));
		System.exit(-1);

//		learn.trainRawText("./log/segmerge.txt","./files/vec.mod");
//		System.exit(-1);

//		System.out.println(vec.analogy("男子", "女子", "男孩子"));
//		System.out.println("山西" + "\t" +
//		Arrays.toString(vec.getWordVector("山西")));
		// ;
		// System.out.println("毛泽东" + "\t" +
		// Arrays.toString(vec.getWordVector("毛泽东")));
		// ;
		// System.out.println("足球" + "\t" +
		// Arrays.toString(vec.getWordVector("足球")));

		// Word2VEC vec2 = new Word2VEC();
		// vec2.loadGoogleModel("InputFiles/vectors.bin") ;
		//
		//
//		String str = "拘留";
//		long start = System.currentTimeMillis();
//		//22779
//		for (int i = 0; i < 100; i++) {vec.distance(str);
//			//System.out.println(vec.distance(str));
//		}
//		System.out.println(System.currentTimeMillis() - start);

		// System.out.println(vec2.distance(str));
		//
		//
		// //男人 国王 女人
		// System.out.println(vec.analogy("邓小平", "毛泽东思想", "毛泽东"));
		// System.out.println(vec2.analogy("毛泽东", "毛泽东思想", "邓小平"));
}

	private  void trainRawText(String midPath,String outPath){
		Learn mylearn = new Learn();
		try {
            mylearn.learnFile(new File(midPath));
            mylearn.saveModel(new File(outPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private  void Transform(String path,String outPath) throws IOException {
		FileWriter writer = new FileWriter(outPath);
		BufferedWriter bw = new BufferedWriter(writer);
		try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)))) {
			int _words = dis.readInt();
			int _size = dis.readInt();
			float vector = 0;

			String key = null;
			float[] value = null;
			for (int i = 0; i < _words; i++) {
				double len = 0;
				key = dis.readUTF();
				bw.write(key);
//				System.out.print(key);
				value = new float[_size];
				for (int j = 0; j < _size; j++) {
					vector = dis.readFloat();
					len += vector * vector;
					value[j] = vector;
				}

				len = Math.sqrt(len);
				//归一化
				for (int j = 0; j < _size; j++) {
					value[j] /= len;
					bw.write(" "+value[j]);
//					System.out.print(" " + value[j]);
				}
				bw.write("\n");
//				System.out.println();
			}
		}
		bw.close();
		writer.close();
		System.exit(-1);
	}

	private static void Transform2(String path,String outPath) throws IOException {
		HashMap<String, float[]> wordMapNew = new HashMap<>();
		float[] value = null;
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream("InputFiles/typical_vectors.bin")))) {
			br.readLine();
			String temp = null,word = null;
			while ((temp = br.readLine()) != null) {
				String[] split = temp.split("\\s+");
				word = split[0];
				value = new float[300];
				for (int k = 0;k < 300;++k) {
					value[k] = Float.parseFloat(split[k+1]);
				}
				wordMapNew.put(word,value);
			}
		}
		int mapSize = wordMapNew.size();
		int cnt  = 0;

		FileWriter writer = new FileWriter(outPath);
		BufferedWriter bw = new BufferedWriter(writer);
		try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)))) {
			int _words = dis.readInt();
			int _size = dis.readInt();
			bw.write((_words+45693)+" "+_size+"\n");
			float vector = 0;

			String key = null;

			for (Entry<String, float[]> element :wordMapNew.entrySet()) {
				bw.write(element.getKey()+" ");
				value = element.getValue();
				for (int j = 0; j < _size; j++) {
					bw.write(value[j] + " ");
				}
				bw.write("\n");
//				System.out.println();
			}
			for (int i = 0; i < _words; i++) {
				double len = 0;
				key = dis.readUTF();
				bw.write(key+" ");

				boolean flag = false;
				if(wordMapNew.containsKey(key)) {
					flag = true;
					value = wordMapNew.get(key);
				}
				for (int j = 0; j < _size; j++) {
					vector = dis.readFloat();
					if(flag)
						vector = (float)((vector+value[j])/2.0);
					bw.write(vector + " ");
				}
				bw.write("\n");
				if(wordMapNew.containsKey(key)){
					cnt++;
					wordMapNew.remove(key);
				}
//				System.out.println();
			}

		}
		bw.close();
		writer.close();
		System.out.println(mapSize - cnt);
		System.exit(-1);
	}

	private static void Transform3(String path,String outPath) throws IOException {
		FileWriter writer = new FileWriter(outPath);
		BufferedWriter bw = new BufferedWriter(writer);
		try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)))) {
			int _words = dis.readInt();
			int _size = dis.readInt();
			bw.write((_words)+" "+_size+"\n");
			float vector = 0;

			String key = null;
			for (int i = 0; i < _words; i++) {
				double len = 0;
				key = dis.readUTF();
				bw.write(key+" ");

				for (int j = 0; j < _size; j++) {
					vector = dis.readFloat();
					bw.write(vector + " ");
				}
				bw.write("\n");
			}

		}
		bw.close();
		writer.close();
		System.exit(-1);
	}


	private HashMap<String, float[]> wordMap = new HashMap<String, float[]>();

	private int words;
	private int size;
	private int topNSize = 30;

	public void testVec(String path,boolean getVec) throws IOException {
		String[] strs = {"台风", "地震", "战争", "谋杀", "霾", "疫情", "受贿", "习近平", "火灾", "紧急状态"};
		Word2VEC vec = new Word2VEC();
		vec.loadJavaModel(path);
		for (String str : strs) {
			if (getVec) {
				float[] tmp = vec.getWordVector(str);
				System.out.print(str + ":[");//distance(str));
				for (float each : tmp) {
					System.out.print(each + ",");
				}
				System.out.println("]");
			}
			else
				System.out.println(str + ":" + vec.distance(str));
		}
		//System.out.println("distance of \"男人\" & \"女人\" is " + vec.distanceOfWord("男人", "女人"));
		System.exit(-1);
	}

	public void printModel(String wordPath , String floatPath) throws IOException {
        FileWriter writer = new FileWriter(floatPath);
        BufferedWriter bw = new BufferedWriter(writer);
		List<String> wordList = new ArrayList<>();
		for(Entry<String, float[]> entry: wordMap.entrySet()){
            String w = entry.getKey();
            float[] vector = entry.getValue();
            wordList.add(w);
            for (int k = 0; k < vector.length; ++k)
                bw.write(vector[k] + " ");
            bw.write("\r\n");
        }
        bw.close();
        writer.close();
        FileUtils.writeLines(new File(wordPath),wordList);
	}

	public void getMSE(String path,String path2,String wordPath) throws IOException {
		Word2VEC vec = new Word2VEC(),vec2 = new Word2VEC();
		vec.loadJavaModel(path);
		vec2.loadJavaModel(path2);
		double err = 0,errSum = 0;
		float[] vector = null,vector2 = null;
		int cnt = 0;
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(wordPath)))) {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				vector = vec.getWordVector(temp);
				vector2 = vec2.getWordVector(temp);
				if(vector != null && vector2 != null){
					if(Double.isNaN(vector[0]) || Double.isNaN(vector2[0]) ) {
						System.out.println(temp +" "+ vector[0] + " "+vector2[0]);
						continue;
					}
							err = 0;
					for(int i = 0;i < vector.length;++i)
						err += (vector[i] - vector2[i])*(vector[i] - vector2[i]);
					err = err / vector.length;
					errSum += err;
					cnt++;
//					System.out.print(temp + " " + err);
				}
//				System.out.println();
			}
		}
		double x = errSum / cnt;
		Random random = new Random();
		if(cnt < 4000)
			System.out.print("!"+cnt+"!");
		System.out.print(x+"\t");
	}

	public void getMinkowski(String path,String path2,String wordPath) throws IOException {
		Word2VEC vec = new Word2VEC(),vec2 = new Word2VEC();
		vec.loadJavaModel(path);
		vec2.loadJavaModel(path2);
		double err = 0,errSum = 0;
		float[] vector = null,vector2 = null;
		int cnt = 0;
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(wordPath)))) {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				vector = vec.getWordVector(temp);
				vector2 = vec2.getWordVector(temp);
				if(vector != null && vector2 != null){
					err = 0;
					for(int i = 0;i < vector.length;++i)
						err += Math.abs((vector[i] - vector2[i])*(vector[i] - vector2[i])*(vector[i] - vector2[i]));
					err = Math.pow(err,1/3.0);
					errSum += err;
					cnt++;
				}
			}
		}
		double x = errSum / cnt;
		System.out.print(x+"\t");
	}


	public void getCosine(String path,String path2,String wordPath) throws IOException {
		Word2VEC vec = new Word2VEC(),vec2 = new Word2VEC();
		vec.loadJavaModel(path);
		vec2.loadJavaModel(path2);
		double err_x = 0,err_y = 0,err_xy = 0,errSum = 0;
		float[] vector = null,vector2 = null;
		int cnt = 0;
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(wordPath)))) {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				vector = vec.getWordVector(temp);
				vector2 = vec2.getWordVector(temp);
				if(vector != null && vector2 != null){
					err_x = 0;
					err_y = 0;
					err_xy = 0;
					for(int i = 0;i < vector.length;++i){
						err_x += vector[i]*vector[i];
						err_y += vector2[i]*vector2[i];
						err_xy += Math.abs(vector[i]*vector2[i]);
					}
					errSum += err_xy / (Math.sqrt(err_x)*Math.sqrt(err_y));
					cnt++;
				}
			}
		}
		double x = errSum / cnt;
		System.out.print(x+"\t");
	}

	public void getMAE(String path,String path2,String wordPath) throws IOException {
		Word2VEC vec = new Word2VEC(),vec2 = new Word2VEC();
		vec.loadJavaModel(path);
		vec2.loadJavaModel(path2);
		double err = 0,errSum = 0;
		float[] vector = null,vector2 = null;
		int cnt = 0;
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(wordPath)))) {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				vector = vec.getWordVector(temp);
				vector2 = vec2.getWordVector(temp);
				if(vector != null && vector2 != null){
					err = 0;
					for(int i = 0;i < vector.length;++i)
						err += Math.abs(vector[i] - vector2[i]);
					err = err / vector.length;
					errSum += err;
					cnt++;
				}
			}
		}
		double x = errSum / cnt;
		System.out.print(x+"\t");
	}

	public void getEuclidean(String path,String path2,String wordPath) throws IOException {
		Word2VEC vec = new Word2VEC(),vec2 = new Word2VEC();
		vec.loadJavaModel(path);
		vec2.loadJavaModel(path2);
		double err = 0,errSum = 0;
		float[] vector = null,vector2 = null;
		int cnt = 0;
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(wordPath)))) {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				vector = vec.getWordVector(temp);
				vector2 = vec2.getWordVector(temp);
				if(vector != null && vector2 != null){
					err = 0;
					for(int i = 0;i < vector.length;++i)
						err += (vector[i] - vector2[i])*(vector[i] - vector2[i]);
					err = Math.sqrt(err);
					errSum += err;
					cnt++;
				}
			}
		}
		double x = errSum / cnt;
		System.out.print(x+"\t");
	}

	public void getDelta(String path,String path2,String wordPath,String outPath) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath)));//输出文件

		Word2VEC vec = new Word2VEC(),vec2 = new Word2VEC();
		vec.loadJavaModel(path);
		vec2.loadJavaModel(path2);
		double err = 0;
		float[] vector = null,vector2 = null;
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(wordPath)))) {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				vector = vec.getWordVector(temp);
				vector2 = vec2.getWordVector(temp);
				if(vector != null && vector2 != null){
					err = 0;
					for(int i = 0;i < vector.length;++i)
						err += (vector[i] - vector2[i])*(vector[i] - vector2[i]);
					err = err / vector.length;
					System.out.print(temp + " " + err);
					out.write(temp + " " + err);
					System.out.print(" [");
					out.write(" [");
					for(int i = 0;i < vector.length;++i) {
//						System.out.print((vector2[i] - vector[i]) + ",");
						out.write((vector2[i] - vector[i]) + ",");
					}
					System.out.print("]");
					out.write("]");
					}
					out.write("\r\n");
				System.out.println();
			}
		}
		out.close();
		System.exit(-1);
	}

	public void getDelta(String path,String path2,String wordPath) throws IOException {
		Word2VEC vec = new Word2VEC(),vec2 = new Word2VEC();
		vec.loadJavaModel(path);
		vec2.loadJavaModel(path2);
		double err = 0,plus = 0;
		float[] vector = null,vector2 = null;
		int cnt = 0,cnt2 = 0;
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(wordPath)))) {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				vector = vec.getWordVector(temp);
				vector2 = vec2.getWordVector(temp);
				if(vector != null && vector2 != null){
					err = 0;
//					cnt = 0;
					for(int i = 0;i < vector2.length;++i)
//						if(Math.abs(vector2[i]-vector[i]) > 0.01 || Double.isNaN(vector2[i]-vector[i]))
//							cnt++;
						err += Math.abs(vector2[i]-vector[i]);
//					err = err / vec.getSize();
					if(!Double.isNaN(err)) {
						plus += err;
						cnt++;
					}
//					System.out.print(temp + " " + err);
//					cnt2 ++;
//					plus +=cnt;
				}
			}
//			plus /= (cnt2);
			plus/=(300*cnt);
			System.out.println(plus);
		}
//		System.exit(-1);
	}

	public void getDeltaCount(String path,String path2,String wordPath) throws IOException {
		Word2VEC vec = new Word2VEC(),vec2 = new Word2VEC();
		vec.loadJavaModel(path);
		vec2.loadJavaModel(path2);
		double err = 0,plus = 0;
		float[] vector = null,vector2 = null;
		int cnt = 0,cnt2 = 0;
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(wordPath)))) {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				vector = vec.getWordVector(temp);
				vector2 = vec2.getWordVector(temp);
				if(vector != null && vector2 != null){
					err = 0;
					cnt = 0;
					for(int i = 0;i < vector2.length;++i)
						if(Math.abs(vector2[i]-vector[i]) > 0.02 || Double.isNaN(vector2[i]-vector[i]))
							cnt++;
//						err += Math.abs(vector2[i]-vector[i]);
//					err = err / vec.getSize();
//					if(!Double.isNaN(err)) {
//						plus += err;
//						cnt++;
//					}
//					System.out.print(temp + " " + err);
					cnt2 ++;
					plus +=cnt;
				}
			}
			plus /= (cnt2);
//			plus/=(300*cnt);
			System.out.println(plus);
		}
//		System.exit(-1);
	}

	public void getTheta(String path,String add,String wordPath,String outPath) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath)));//输出文件

		Word2VEC vec = new Word2VEC();
		vec.loadJavaModel(path);
		float[] vector = null;
		double mod = 0;
		int cnt = 1;
		out.write("#x #y #z\r\n");
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(wordPath)))) {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				vector = vec.getWordVector(temp);
				if(vector != null){
					mod = 0;
					for(int i = 0;i < vector.length;++i) {
						mod += vector[i]*vector[i];
//						out.write((vector[i]) + ",");
					}
					if(Double.isNaN(mod))
						continue;
					mod = Math.sqrt(mod);
					out.write(cnt/1000.0 + " " + mod + " " + add + "\r\n");
				}
				cnt ++;
			}
		}
		out.close();
		System.exit(-1);
	}


	public void loadGoogleModel(String path) throws IOException {
		DataInputStream dis = null;
		BufferedInputStream bis = null;
		double len = 0;
		float vector = 0;
		try {
			bis = new BufferedInputStream(new FileInputStream(path));
			dis = new DataInputStream(bis);
			// //读取词数
			words = Integer.parseInt(readString(dis));
			// //大小
			size = Integer.parseInt(readString(dis));
			String word;
			float[] vectors = null;
			for (int i = 0; i < words; i++) {
				word = readString(dis);
				vectors = new float[size];
				len = 0;
				for (int j = 0; j < size; j++) {
					vector = readFloat(dis);
					len += vector * vector;
					vectors[j] = (float) vector;
				}
				len = Math.sqrt(len);

				for (int j = 0; j < size; j++) {
					vectors[j] /= len;
				}

				wordMap.put(word, vectors);
				dis.read();
			}
		} finally {
			bis.close();
			dis.close();
		}
	}

	/**
	 * 加载模型
	 *
	 * @param path
	 *            模型的路径
	 * @throws IOException
	 */
	public void loadJavaModel(String path) throws IOException {
		try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)))) {
			words = dis.readInt();
			size = dis.readInt();

			float vector = 0;

			String key = null;
			float[] value = null;
			for (int i = 0; i < words; i++) {
				double len = 0;
				key = dis.readUTF();
				value = new float[size];
				for (int j = 0; j < size; j++) {
					vector = dis.readFloat();
					len += vector * vector;
					value[j] = vector;
				}

				len = Math.sqrt(len);
				//归一化
				for (int j = 0; j < size; j++) {
					value[j] /= len;
				}
				wordMap.put(key, value);
			}

		}
	}

	private static final int MAX_SIZE = 50;

	/**
	 * 近义词
	 *
	 * @return
	 */
	public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
		float[] wv0 = getWordVector(word0);
		float[] wv1 = getWordVector(word1);
		float[] wv2 = getWordVector(word2);

		if (wv1 == null || wv2 == null || wv0 == null) {
			return null;
		}
		float[] wordVector = new float[size];
		for (int i = 0; i < size; i++) {
			wordVector[i] = wv1[i] - wv0[i] + wv2[i];
		}
		float[] tempVector;
		String name;
		List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
		for (Entry<String, float[]> entry : wordMap.entrySet()) {
			name = entry.getKey();
			if (name.equals(word0) || name.equals(word1) || name.equals(word2)) {
				continue;
			}
			float dist = 0;
			tempVector = entry.getValue();
			for (int i = 0; i < wordVector.length; i++) {
				dist += wordVector[i] * tempVector[i];
			}
			insertTopN(name, dist, wordEntrys);
		}
		return new TreeSet<WordEntry>(wordEntrys);
	}

	private void insertTopN(String name, float score, List<WordEntry> wordsEntrys) {
		// TODO Auto-generated method stub
		if (wordsEntrys.size() < topNSize) {
			wordsEntrys.add(new WordEntry(name, score));
			return;
		}
		float min = Float.MAX_VALUE;
		int minOffe = 0;
		for (int i = 0; i < topNSize; i++) {
			WordEntry wordEntry = wordsEntrys.get(i);
			if (min > wordEntry.score) {
				min = wordEntry.score;
				minOffe = i;
			}
		}

		if (score > min) {
			wordsEntrys.set(minOffe, new WordEntry(name, score));
		}

	}

	public float distanceOfWord(String wordA , String wordB ){
		float dist = 0;
		if(wordA.compareTo(wordB) == 0)
			return 1;
		float[] vectorA = getWordVector(wordA);
		float[] vectorB = getWordVector(wordB);
		if(vectorA == null || vectorB == null)
			return 0;
		for (int i = 0; i < vectorA.length; i++) {
			dist += vectorB[i] * vectorA[i];
		}
		return dist;
	}

	public int getMapsize(){
		return wordMap.size();
	}

	public Set<WordEntry> distance(String queryWord) {

		float[] center = wordMap.get(queryWord);
		if (center == null) {
			return Collections.emptySet();
		}

		int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
		TreeSet<WordEntry> result = new TreeSet<WordEntry>();

		double min = Float.MIN_VALUE;
		for (Entry<String, float[]> entry : wordMap.entrySet()) {
			float[] vector = entry.getValue();
			float dist = 0;
			for (int i = 0; i < vector.length; i++) {
				dist += center[i] * vector[i];
			}

			if (dist > min) {
				result.add(new WordEntry(entry.getKey(), dist));
				if (resultSize < result.size()) {
					result.pollLast();
				}
				min = result.last().score;
			}
		}
		result.pollFirst();

		return result;
	}

	public Set<WordEntry> distance(List<String> words) {

		float[] center = null;
		for (String word : words) {
			center = sum(center, wordMap.get(word));
		}

		if (center == null) {
			return Collections.emptySet();
		}

		int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
		TreeSet<WordEntry> result = new TreeSet<WordEntry>();

		double min = Float.MIN_VALUE;
		for (Entry<String, float[]> entry : wordMap.entrySet()) {
			float[] vector = entry.getValue();
			float dist = 0;
			for (int i = 0; i < vector.length; i++) {
				dist += center[i] * vector[i];
			}

			if (dist > min) {
				result.add(new WordEntry(entry.getKey(), dist));
				if (resultSize < result.size()) {
					result.pollLast();
				}
				min = result.last().score;
			}
		}
		result.pollFirst();

		return result;
	}

	private float[] sum(float[] center, float[] fs) {
		// TODO Auto-generated method stub

		if (center == null && fs == null) {
			return null;
		}

		if (fs == null) {
			return center;
		}

		if (center == null) {
			return fs;
		}

		for (int i = 0; i < fs.length; i++) {
			center[i] += fs[i];
		}

		return center;
	}

	/**
	 * 得到词向量
	 *
	 * @param word
	 * @return
	 */
	public float[] getWordVector(String word) {
		return wordMap.get(word);
	}
	/**
	 * 是否有该词的词向量
	 *
	 * @param word
	 * @return boolean
	 */
	public boolean hasWord(String word) {
		return wordMap.containsKey(word);
	}

	public static float readFloat(InputStream is) throws IOException {
		byte[] bytes = new byte[4];
		is.read(bytes);
		return getFloat(bytes);
	}

	/**
	 * 读取一个float
	 *
	 * @param b
	 * @return
	 */
	public static float getFloat(byte[] b) {
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		return Float.intBitsToFloat(accum);
	}

	/**
	 * 读取一个字符串
	 *
	 * @param dis
	 * @return
	 * @throws IOException
	 */
	private static String readString(DataInputStream dis) throws IOException {
		// TODO Auto-generated method stub
		byte[] bytes = new byte[MAX_SIZE];
		byte b = dis.readByte();
		int i = -1;
		StringBuilder sb = new StringBuilder();
		while (b != 32 && b != 10) {
			i++;
			bytes[i] = b;
			b = dis.readByte();
			if (i == 49) {
				sb.append(new String(bytes));
				i = -1;
				bytes = new byte[MAX_SIZE];
			}
		}
		sb.append(new String(bytes, 0, i + 1));
		return sb.toString();
	}

	public int getTopNSize() {
		return topNSize;
	}

	public void setTopNSize(int topNSize) {
		this.topNSize = topNSize;
	}

	public HashMap<String, float[]> getWordMap() {
		return wordMap;
	}

	public int getWords() {
		return words;
	}

	public int getSize() {
		return size;
	}

}
