package ru.hixon;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static final List<String> mailingListArchives = List.of(
            "https://mail.openjdk.org/pipermail/valhalla-dev/",
            "https://mail.openjdk.org/pipermail/amber-dev/"
    );

    public static void main(String[] args) throws Exception {
        new Main().run("jdbc:sqlite:C:/Users/Hixon/Desktop/java-mailing-lists-search/indexer/src/main/java/ru/hixon/test.db");


//        Indexer indexer = new Indexer();
//        indexer.index();
    }

    public void run(String dbUrl) throws IOException {
        LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
        System.setProperty("jdk.httpclient.maxstreams", "500");

        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .connectTimeout(Duration.ofSeconds(3))
                    .build();

            Database database = new Database(dbUrl);
            database.executeDatabaseMigrations();

            Indexer indexer = new Indexer(database, httpClient, mailingListArchives);
            indexer.index();
        } catch (Throwable th) {
            logger.log(Level.SEVERE, "Got unhandled exception", th);
        }
    }
}