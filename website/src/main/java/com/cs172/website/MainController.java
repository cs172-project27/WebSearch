package com.cs172.website;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

@Controller
public class MainController {
    @GetMapping("/results")
    public String results(@RequestParam(name="query", required=false, defaultValue="World") String query, Model model) {
        // 2. query
        String querystr = query;

        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        Query q = null;
        try {
            q = new QueryParser("title", new StandardAnalyzer()).parse(querystr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // 3. search
        int hitsPerPage = 10;
        IndexReader reader = null;
        try {
            reader = DirectoryReader.open(FSDirectory.open(Path.of("C:/Users/mayur/Desktop/Coding/CS172/WebSearch/backend/tweet_index")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = null;
        try {
            docs = searcher.search(q, hitsPerPage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ScoreDoc[] hits = docs.scoreDocs;
        System.out.println(hits.length);
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // print to local host page
        model.addAttribute("query", query);
        return "results";
        }

    }