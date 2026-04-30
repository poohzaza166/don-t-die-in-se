#!/usr/bin/env bash
# Build the project, run all JUnit tests, and write a CSV report you can open in Excel.
# Uses plain javac + java because maven-compiler-plugin cannot do --release 21 in this setup.
# Usage:  ./run-tests.sh           -> writes test-report.csv
#         ./run-tests.sh out.csv   -> writes out.csv
set -e

OUT="${1:-test-report.csv}"

# JUnit "console standalone" is a single fat jar containing JUnit + Platform + Launcher.
JUNIT_JAR="$HOME/.m2/repository/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar"

# If the jar isn't there, ask Maven to fetch it once (this works even when compile is broken).
if [ ! -f "$JUNIT_JAR" ]; then
    echo "Downloading JUnit standalone jar..."
    mvn -q dependency:get -Dartifact=org.junit.platform:junit-platform-console-standalone:1.10.2
fi

# Make sure main classes are compiled. Maven javafx:run (or IntelliJ) does this for us;
# if target/classes is missing, we bail out with a clear message.
if [ ! -d target/classes ] || [ -z "$(ls -A target/classes 2>/dev/null)" ]; then
    echo "ERROR: target/classes is empty. Build the main code first by running:"
    echo "       mvn javafx:run    (close the window once it opens)"
    echo "       or build the project from IntelliJ."
    exit 1
fi

# Compile test sources using plain javac (not the maven plugin).
mkdir -p target/test-classes
echo "Compiling tests..."
javac --release 21 -d target/test-classes -cp "target/classes:$JUNIT_JAR" test/*.java

# Run the report runner. It executes every test and writes the CSV.
echo "Running tests..."
java -cp "target/classes:target/test-classes:$JUNIT_JAR" TestReportRunner "$OUT"

echo
echo "Report saved to: $OUT"
