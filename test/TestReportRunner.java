import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.engine.TestExecutionResult;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Runs all JUnit tests and writes a CSV that opens cleanly in Excel.
// Run with:  mvn test-compile exec:java -Dexec.classpathScope=test -Dexec.mainClass=TestReportRunner
public class TestReportRunner {

    public static void main(String[] args) throws Exception {
        String outPath = args.length > 0 ? args[0] : "test-report.csv";

        // Tell JUnit to look at every test class in the project.
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        DiscoverySelectors.selectClass(CardTest.class),
                        DiscoverySelectors.selectClass(HandTest.class),
                        DiscoverySelectors.selectClass(DeckTest.class),
                        DiscoverySelectors.selectClass(RoomTest.class),
                        DiscoverySelectors.selectClass(TileTest.class),
                        DiscoverySelectors.selectClass(PlayerTest.class),
                        DiscoverySelectors.selectClass(NotepadTest.class),
                        DiscoverySelectors.selectClass(RandomBrainTest.class),
                        DiscoverySelectors.selectClass(GameSetupTest.class)
                )
                .build();

        CsvListener listener = new CsvListener();
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        listener.write(outPath);
        System.out.println("Report written to " + outPath);
        System.out.println("Passed: " + listener.passed
                         + "  Failed: " + listener.failed
                         + "  Skipped: " + listener.skipped);
    }

    // Captures each test result and writes them out as CSV rows.
    static class CsvListener implements TestExecutionListener {
        // Map from unique id to the row data we want to write.
        private final Map<String, Row> rows = new HashMap<>();
        int passed = 0, failed = 0, skipped = 0;

        @Override
        public void executionStarted(TestIdentifier id) {
            if (id.isTest()) {
                rows.put(id.getUniqueId(), new Row(id, System.currentTimeMillis()));
            }
        }

        @Override
        public void executionFinished(TestIdentifier id, TestExecutionResult result) {
            if (!id.isTest()) return;
            Row r = rows.get(id.getUniqueId());
            if (r == null) return;
            r.duration = System.currentTimeMillis() - r.start;
            r.status = result.getStatus().name();
            r.message = result.getThrowable().map(Throwable::getMessage).orElse("");
            switch (result.getStatus()) {
                case SUCCESSFUL -> passed++;
                case FAILED, ABORTED -> failed++;
            }
        }

        @Override
        public void executionSkipped(TestIdentifier id, String reason) {
            if (!id.isTest()) return;
            Row r = new Row(id, System.currentTimeMillis());
            r.status = "SKIPPED";
            r.message = reason;
            rows.put(id.getUniqueId(), r);
            skipped++;
        }

        // Write the CSV. Excel handles this fine with default settings.
        void write(String path) throws Exception {
            try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
                pw.println("timestamp,test_class,test_name,status,duration_ms,message");
                String now = LocalDateTime.now().toString();
                for (Row r : rows.values()) {
                    pw.printf("%s,%s,%s,%s,%d,%s%n",
                            csv(now),
                            csv(r.className),
                            csv(r.testName),
                            csv(r.status),
                            r.duration,
                            csv(r.message));
                }
            }
        }

        // Wrap fields in quotes and escape inner quotes; needed for safe CSV.
        private String csv(String s) {
            if (s == null) return "";
            String escaped = s.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
    }

    static class Row {
        final String className;
        final String testName;
        final long start;
        long duration;
        String status = "UNKNOWN";
        String message = "";

        Row(TestIdentifier id, long start) {
            // Parent identifier holds the class name in the source segment.
            this.className = id.getParentId()
                    .map(p -> p.replaceAll(".*class:([^\\]]+).*", "$1"))
                    .orElse("?");
            this.testName = id.getDisplayName();
            this.start = start;
        }
    }
}
