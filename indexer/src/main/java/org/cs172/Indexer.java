package org.cs172;

import com.google.gson.Gson;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Path;

public class Indexer {
    IndexWriter indexWriter = null;

    private void parseJSON() {
        Gson gson = new Gson();
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader("tweets_new.json"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            String currentData = bufferedReader.readLine();
            while (currentData != null) {
                System.out.println(currentData);
                currentData = bufferedReader.readLine();
            }
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
            indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);
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