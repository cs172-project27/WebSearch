package com.cs172.backend;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class Controller {
    @GetMapping("/test")
    public String test(@RequestParam(required=false,defaultValue="") String q1) {
        System.out.println(q1);

        // 2. query
        String querystr = "lucene";

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
        return Integer.toString(hits.length);
    }
}
