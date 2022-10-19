package homework2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class writeIndex {

	public static void main(String args[]) throws Exception {

		Path path = Paths.get("indexedFiles");
		File folder = new File("inputFiles");
		try (Directory directory = FSDirectory.open(path)) {
			indexDocs(directory, new SimpleTextCodec(), folder);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private static void indexDocs(Directory directory, Codec codec, File folder) throws IOException {
		Analyzer defaultAnalyzer = new StandardAnalyzer();
		CharArraySet stopWords = new CharArraySet(Arrays.asList(), true);
		Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
		perFieldAnalyzers.put("contenuto", new StopAnalyzer(stopWords));
		perFieldAnalyzers.put("titolo", new StopAnalyzer(stopWords));

		Analyzer analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, perFieldAnalyzers);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		if (codec != null) {
			config.setCodec(codec);
		}
		IndexWriter writer = new IndexWriter(directory, config);
		writer.deleteAll();

		File[] listOfFiles = folder.listFiles();
		
		int i = 0;
		long start = System.currentTimeMillis();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				i++;
				Document doc = new Document();
				doc.add(new TextField("titolo", file.getName(), Field.Store.YES));
				doc.add(new TextField("contenuto", readFile(file), Field.Store.YES));
				writer.addDocument(doc);
			}
		}
		writer.commit();
		writer.close();
		long end = System.currentTimeMillis();
		System.out.print("File indicizzati = " + i + "\n");
		System.out.print("Tempo impiegato = " + (end-start) + " millisecondi\n");
	}

	public static String readFile(File file) throws IOException {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader buffer = new BufferedReader(new FileReader(file))) {
           String str;
           
           while ((str = buffer.readLine()) != null) {
               builder.append(str).append("\n");
           }
       }
       catch (IOException e) {
           e.printStackTrace();
       }

       return builder.toString();
	}

}
