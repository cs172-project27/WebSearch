package org.cs172;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;

public class Indexer {
    void buildIndex() {
        System.out.println("Starting index process... ");
        try {
            Directory indexDirectory = FSDirectory.open(Path.of("tweet_index"));
            IndexWriter indexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig(new StandardAnalyzer()));
            indexWriter.deleteAll();

            InputStream inputStream = Files.newInputStream(Path.of("tweets_new.json"));
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));

            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                Tweet tweet = new Gson().fromJson(jsonReader, Tweet.class);
                Document document = new Document();

                document.add(new TextField("text", tweet.data.text, Field.Store.YES));

                Instant created_at = Instant.parse(tweet.data.created_at);
                document.add(new StringField("created_at", created_at.toString(), Field.Store.YES));

                for (int i = 0; i < tweet.urls.size(); i++) {
                    document.add(new StringField("url" + (i + ""), tweet.urls.get(i), Field.Store.YES));
                }

                for (int i = 0; i < tweet.includes.users.size(); i++) {
                    document.add(new StringField("username" + (i + ""), tweet.includes.users.get(i).username, Field.Store.YES));
                    document.add(new StringField("name" + (i + ""), tweet.includes.users.get(i).name, Field.Store.YES));
                }

                indexWriter.addDocument(document);
            }

            indexWriter.commit();
            indexWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Finished indexing!");
    }

    public static void main(String[] args) {
        new Indexer().buildIndex();
    }
}