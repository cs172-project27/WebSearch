package org.example;

import com.google.gson.Gson;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Indexer {
    IndexWriter indexWriter = null;

    void parseJSON() {
        Gson gson = new Gson();

        Reader reader;
        try {
            reader = Files.newBufferedReader(Paths.get("tweets.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<?, ?> jsonMap = gson.fromJson(reader, Map.class);
        for (Map.Entry<?, ?> entry : jsonMap.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }

        // close reader
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    void buildIndex() {
        Directory indexDirectory;
        try {
            indexDirectory = FSDirectory.open(Path.of("indexes"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Analyzer standardAnalyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(standardAnalyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        try {
            indexWriter = new IndexWriter (indexDirectory, indexWriterConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        parseJSON();

        try {
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        new Indexer().buildIndex();
    }
}