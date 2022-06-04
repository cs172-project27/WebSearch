package org.cs172;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Indexer {
    void buildIndex() {
        try {
            Directory indexDirectory = FSDirectory.open(Path.of("indexes"));
            Analyzer standardAnalyzer = new StandardAnalyzer();

            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(standardAnalyzer);
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);

            InputStream inputStream = Files.newInputStream(Path.of("tweets_new.json"));
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));

            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                Tweet tweet = new Gson().fromJson(jsonReader, Tweet.class);
                System.out.println(tweet.data.text);
            }

            jsonReader.endArray();
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