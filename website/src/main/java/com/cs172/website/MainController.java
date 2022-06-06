package com.cs172.website;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

@Controller
public class MainController {
    @GetMapping("/results")
    public String results(@RequestParam(name="query", required=false, defaultValue="World") String query, Model model) {
        // query
        Query q = null;
        try {
            q = new QueryParser("text", new StandardAnalyzer()).parse(query);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // search
        try {
            int hitsPerPage = 10000;

            IndexReader reader = DirectoryReader.open(FSDirectory.open(Path.of("tweet_index")));
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);

            ScoreDoc[] hits = docs.scoreDocs;
            ArrayList<String> results = new ArrayList<>();
            for (ScoreDoc hit: hits) {
                Document document = searcher.doc(hit.doc);
                results.add(document.get("text"));
            }

            reader.close();

            // print to local host page @ http://localhost:8080/
            model.addAttribute("query", query);
            model.addAttribute("hitCount", hits.length);
            model.addAttribute("results", results);
            return "results";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}