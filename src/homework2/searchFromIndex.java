package homework2;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class searchFromIndex {

	public static void main(String args[]) {

		Path path = Paths.get("indexedFiles"); 
		try (Directory directory = FSDirectory.open(path)) {
			try (IndexReader reader = DirectoryReader.open(directory)) {
				IndexSearcher searcher = new IndexSearcher(reader);

				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(System.in);
				System.out.print("Inserisci la query (f per terminare): ");
				String query = scanner.nextLine();

				while(!query.contentEquals("f")) {
					runQuery(query, searcher);
					System.out.print("Inserisci la query: ");
					query = scanner.nextLine();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}



	}

	private static void runQuery(String stringaQuery, IndexSearcher searcher) throws Exception {		

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		System.out.print("Ricerca su titolo o su contenuto? ");
		String scelta = scanner.nextLine();

		if(scelta.contentEquals("titolo") || scelta.contentEquals("contenuto")) {
			CharArraySet stopWords = new CharArraySet(Arrays.asList(), true);
			Analyzer analyzer = new StopAnalyzer(stopWords);
			QueryParser parser = new QueryParser(scelta, analyzer);
			Query query = parser.parse(stringaQuery);
			runQuery(searcher, query, false);
		} else {
			return;
		}
	}

	private static void runQuery(IndexSearcher searcher, Query query, boolean explain) throws IOException {
		
		TopDocs hits = searcher.search(query, 10);
        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("doc"+scoreDoc.doc + ": "+ doc.get("titolo") + " (" + scoreDoc.score +")");
            if (explain) {
                Explanation explanation = searcher.explain(query, scoreDoc.doc);
                System.out.println(explanation);
            }
        }

	}

}
