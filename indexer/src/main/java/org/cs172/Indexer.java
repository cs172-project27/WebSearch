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

            InputStream inputStream = Files.newInputStream(Path.of("tweets_new.json"));
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));

            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                Tweet tweet = new Gson().fromJson(jsonReader, Tweet.class);
                Document document = new Document();

                document.add(new StringField("author_id", tweet.data.author_id, Field.Store.YES));
                document.add(new StringField("id", tweet.data.id, Field.Store.YES));
                document.add(new TextField("text", tweet.data.text, Field.Store.YES));

                document.add(new StringField("name", tweet.includes.users.get(0).name, Field.Store.YES));
                document.add(new StringField("name", tweet.includes.users.get(0).username, Field.Store.YES));
                document.add(new StringField("user_id", tweet.includes.users.get(0).id, Field.Store.YES));

                Instant created_at = Instant.parse(tweet.data.created_at);
                document.add(new StringField("time_created", created_at.atZone(ZoneOffset.UTC).toLocalTime().toString(), Field.Store.YES));
                document.add(new StringField("day_created", created_at.atZone(ZoneOffset.UTC).getDayOfWeek().toString(), Field.Store.YES));
                document.add(new StringField("month_created", created_at.atZone(ZoneOffset.UTC).getMonth().toString(), Field.Store.YES));
                document.add(new NumericDocValuesField("year_created", created_at.atZone(ZoneOffset.UTC).getYear()));

                for (int i = 0; i < tweet.urls.size(); i++) {
                    document.add(new StringField("url" + (i + ""), tweet.urls.get(i), Field.Store.YES));
                }

                for (int i = 0; i < tweet.urls.size(); i++) {
                    document.add(new StringField("url" + (i + ""), tweet.urls.get(i), Field.Store.YES));
                }

                indexWriter.addDocument(document);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Finished indexing!");
    }

    public static void main(String[] args) {
        new Indexer().buildIndex();
    }
}