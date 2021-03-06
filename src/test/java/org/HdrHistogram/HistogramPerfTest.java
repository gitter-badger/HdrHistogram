/**
 * HistogramPerfTest.java
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

package org.HdrHistogram;

import org.junit.*;

/**
 * JUnit test for {@link Histogram}
 */
public class HistogramPerfTest {
    static final long highestTrackableValue = 3600L * 1000 * 1000; // e.g. for 1 hr in usec units
    static final int numberOfSignificantValueDigits = 3;
    static final long testValueLevel = 12340;
    static final long warmupLoopLength = 50000;
    static final long rawTimingLoopCount = 500000000L;
    static final long rawDoubleTimingLoopCount = 300000000L;
    static final long singleWriterIntervalTimingLoopCount = 100000000L;
    static final long singleWriterDoubleIntervalTimingLoopCount = 100000000L;
    static final long intervalTimingLoopCount = 40000000L;
    static final long synchronizedTimingLoopCount = 180000000L;
    static final long atomicTimingLoopCount = 80000000L;
    static final long concurrentTimingLoopCount = 50000000L;

    void recordLoopWithExpectedInterval(AbstractHistogram histogram, long loopCount, long expectedInterval) {
        for (long i = 0; i < loopCount; i++)
            histogram.recordValueWithExpectedInterval(testValueLevel + (i & 0x8000), expectedInterval);
    }

    void recordLoopWithExpectedInterval(IntervalHistogramRecorder histogram, long loopCount, long expectedInterval) {
        for (long i = 0; i < loopCount; i++)
            histogram.recordValueWithExpectedInterval(testValueLevel + (i & 0x8000), expectedInterval);
    }

    void recordLoopWithExpectedInterval(SingleWriterIntervalHistogramRecorder histogram, long loopCount, long expectedInterval) {
        for (long i = 0; i < loopCount; i++)
            histogram.recordValueWithExpectedInterval(testValueLevel + (i & 0x8000), expectedInterval);
    }

    void recordLoopWithExpectedInterval(IntervalDoubleHistogramRecorder histogram, long loopCount, long expectedInterval) {
        for (long i = 0; i < loopCount; i++)
            histogram.recordValueWithExpectedInterval(testValueLevel + (i & 0x8000), expectedInterval);
    }

    void recordLoopWithExpectedInterval(SingleWriterIntervalDoubleHistogramRecorder histogram, long loopCount, long expectedInterval) {
        for (long i = 0; i < loopCount; i++)
            histogram.recordValueWithExpectedInterval(testValueLevel + (i & 0x8000), expectedInterval);
    }

    void recordLoopDoubleWithExpectedInterval(DoubleHistogram histogram, long loopCount, double expectedInterval) {
        for (long i = 0; i < loopCount; i++)
            histogram.recordValueWithExpectedInterval(testValueLevel + (i & 0x8000), expectedInterval);
    }

    long LeadingZerosSpeedLoop(long loopCount) {
        long sum = 0;
        for (long i = 0; i < loopCount; i++) {
            // long val = testValueLevel + (i & 0x8000);
            long val = testValueLevel;
            sum += Long.numberOfLeadingZeros(val);
            sum += Long.numberOfLeadingZeros(val);
            sum += Long.numberOfLeadingZeros(val);
            sum += Long.numberOfLeadingZeros(val);
            sum += Long.numberOfLeadingZeros(val);
            sum += Long.numberOfLeadingZeros(val);
            sum += Long.numberOfLeadingZeros(val);
            sum += Long.numberOfLeadingZeros(val);
        }
        return sum;
    }

    public void testRawRecordingSpeedAtExpectedInterval(String label, AbstractHistogram histogram,
                                                        long expectedInterval, long timingLoopCount) throws Exception {
        System.out.println("\nTiming recording speed with expectedInterval = " + expectedInterval + " :");
        // Warm up:
        long startTime = System.nanoTime();
        recordLoopWithExpectedInterval(histogram, warmupLoopLength, expectedInterval);
        long endTime = System.nanoTime();
        long deltaUsec = (endTime - startTime) / 1000L;
        long rate = 1000000 * warmupLoopLength / deltaUsec;
        System.out.println(label + "Warmup: " + warmupLoopLength + " value recordings completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
        histogram.reset();
        // Wait a bit to make sure compiler had a cache to do it's stuff:
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        startTime = System.nanoTime();
        recordLoopWithExpectedInterval(histogram, timingLoopCount, expectedInterval);
        endTime = System.nanoTime();
        deltaUsec = (endTime - startTime) / 1000L;
        rate = 1000000 * timingLoopCount / deltaUsec;
        System.out.println(label + "Hot code timing:");
        System.out.println(label + timingLoopCount + " value recordings completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
        rate = 1000000 * histogram.getTotalCount() / deltaUsec;
        System.out.println(label + histogram.getTotalCount() + " raw recorded entries completed in " +
                deltaUsec + " usec, rate = " + rate + " recorded values per sec.");
    }

    public void testRawRecordingSpeedAtExpectedInterval(String label, IntervalHistogramRecorder intervalHistogram,
                                                        long expectedInterval, long timingLoopCount) throws Exception {
        System.out.println("\nTiming recording speed with expectedInterval = " + expectedInterval + " :");
        // Warm up:
        long startTime = System.nanoTime();
        recordLoopWithExpectedInterval(intervalHistogram, warmupLoopLength, expectedInterval);
        long endTime = System.nanoTime();
        long deltaUsec = (endTime - startTime) / 1000L;
        long rate = 1000000 * warmupLoopLength / deltaUsec;
        System.out.println(label + "Warmup: " + warmupLoopLength + " value recordings completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
        intervalHistogram.reset();
        // Wait a bit to make sure compiler had a cache to do it's stuff:
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        startTime = System.nanoTime();
        recordLoopWithExpectedInterval(intervalHistogram, timingLoopCount, expectedInterval);
        endTime = System.nanoTime();
        deltaUsec = (endTime - startTime) / 1000L;
        rate = 1000000 * timingLoopCount / deltaUsec;
        System.out.println(label + "Hot code timing:");
        System.out.println(label + timingLoopCount + " value recordings completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
        Histogram histogram = intervalHistogram.getIntervalHistogram();
        rate = 1000000 * histogram.getTotalCount() / deltaUsec;
        System.out.println(label + histogram.getTotalCount() + " raw recorded entries completed in " +
                deltaUsec + " usec, rate = " + rate + " recorded values per sec.");
    }

    public void testRawRecordingSpeedAtExpectedInterval(String label, SingleWriterIntervalHistogramRecorder intervalHistogram,
                                                        long expectedInterval, long timingLoopCount) throws Exception {
        System.out.println("\nTiming recording speed with expectedInterval = " + expectedInterval + " :");
        // Warm up:
        long startTime = System.nanoTime();
        recordLoopWithExpectedInterval(intervalHistogram, warmupLoopLength, expectedInterval);
        long endTime = System.nanoTime();
        long deltaUsec = (endTime - startTime) / 1000L;
        long rate = 1000000 * warmupLoopLength / deltaUsec;
        System.out.println(label + "Warmup: " + warmupLoopLength + " value recordings completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
        intervalHistogram.reset();
        // Wait a bit to make sure compiler had a cache to do it's stuff:
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        startTime = System.nanoTime();
        recordLoopWithExpectedInterval(intervalHistogram, timingLoopCount, expectedInterval);
        endTime = System.nanoTime();
        deltaUsec = (endTime - startTime) / 1000L;
        rate = 1000000 * timingLoopCount / deltaUsec;
        System.out.println(label + "Hot code timing:");
        System.out.println(label + timingLoopCount + " value recordings completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
        Histogram histogram = intervalHistogram.getIntervalHistogram();
        rate = 1000000 * histogram.getTotalCount() / deltaUsec;
        System.out.println(label + histogram.getTotalCount() + " raw recorded entries completed in " +
                deltaUsec + " usec, rate = " + rate + " recorded values per sec.");
    }

    public void testRawRecordingSpeedAtExpectedInterval(String label, SingleWriterIntervalDoubleHistogramRecorder intervalHistogram,
                                                        long expectedInterval, long timingLoopCount) throws Exception {
        System.out.println("\nTiming recording speed with expectedInterval = " + expectedInterval + " :");
        // Warm up:
        long startTime = System.nanoTime();
        recordLoopWithExpectedInterval(intervalHistogram, warmupLoopLength, expectedInterval);
        long endTime = System.nanoTime();
        long deltaUsec = (endTime - startTime) / 1000L;
        long rate = 1000000 * warmupLoopLength / deltaUsec;
        System.out.println(label + "Warmup: " + warmupLoopLength + " value recordings completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
        intervalHistogram.reset();
        // Wait a bit to make sure compiler had a cache to do it's stuff:
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        startTime = System.nanoTime();
        recordLoopWithExpectedInterval(intervalHistogram, timingLoopCount, expectedInterval);
        endTime = System.nanoTime();
        deltaUsec = (endTime - startTime) / 1000L;
        rate = 1000000 * timingLoopCount / deltaUsec;
        System.out.println(label + "Hot code timing:");
        System.out.println(label + timingLoopCount + " value recordings completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
        DoubleHistogram histogram = intervalHistogram.getIntervalHistogram();
        rate = 1000000 * histogram.getTotalCount() / deltaUsec;
        System.out.println(label + histogram.getTotalCount() + " raw recorded entries completed in " +
                deltaUsec + " usec, rate = " + rate + " recorded values per sec.");
    }

    public void testRawRecordingSpeedAtExpectedInterval(String label, IntervalDoubleHistogramRecorder intervalHistogram,
                                                        long expectedInterval, long timingLoopCount) throws Exception {
        System.out.println("\nTiming recording speed with expectedInterval = " + expectedInterval + " :");
        // Warm up:
        long startTime = System.nanoTime();
        recordLoopWithExpectedInterval(intervalHistogram, warmupLoopLength, expectedInterval);
        long endTime = System.nanoTime();
        long deltaUsec = (endTime - startTime) / 1000L;
        long rate = 1000000 * warmupLoopLength / deltaUsec;
        System.out.println(label + "Warmup: " + warmupLoopLength + " value recordings completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
        intervalHistogram.reset();
        // Wait a bit to make sure compiler had a cache to do it's stuff:
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        startTime = System.nanoTime();
        recordLoopWithExpectedInterval(intervalHistogram, timingLoopCount, expectedInterval);
        endTime = System.nanoTime();
        deltaUsec = (endTime - startTime) / 1000L;
        rate = 1000000 * timingLoopCount / deltaUsec;
        System.out.println(label + "Hot code timing:");
        System.out.println(label + timingLoopCount + " value recordings completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
        DoubleHistogram histogram = intervalHistogram.getIntervalHistogram();
        rate = 1000000 * histogram.getTotalCount() / deltaUsec;
        System.out.println(label + histogram.getTotalCount() + " raw recorded entries completed in " +
                deltaUsec + " usec, rate = " + rate + " recorded values per sec.");
    }

    public void testRawDoubleRecordingSpeedAtExpectedInterval(String label, DoubleHistogram histogram,
                                                        long expectedInterval, long timingLoopCount) throws Exception {
        System.out.println("\nTiming recording speed with expectedInterval = " + expectedInterval + " :");
        // Warm up:
        long startTime = System.nanoTime();
        recordLoopDoubleWithExpectedInterval(histogram, warmupLoopLength, expectedInterval);
        long endTime = System.nanoTime();
        long deltaUsec = (endTime - startTime) / 1000L;
        long rate = 1000000 * warmupLoopLength / deltaUsec;
        System.out.println(label + "Warmup: " + warmupLoopLength + " value recordings completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
        histogram.reset();
        // Wait a bit to make sure compiler had a cache to do it's stuff:
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        startTime = System.nanoTime();
        recordLoopDoubleWithExpectedInterval(histogram, timingLoopCount, expectedInterval);
        endTime = System.nanoTime();
        deltaUsec = (endTime - startTime) / 1000L;
        rate = 1000000 * timingLoopCount / deltaUsec;
        System.out.println(label + "Hot code timing:");
        System.out.println(label + timingLoopCount + " value recordings completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
        rate = 1000000 * histogram.getTotalCount() / deltaUsec;
        System.out.println(label + histogram.getTotalCount() + " raw recorded entries completed in " +
                deltaUsec + " usec, rate = " + rate + " recorded values per sec.");
    }

    @Test
    public void testRawRecordingSpeed() throws Exception {
        AbstractHistogram histogram;
        histogram = new Histogram(highestTrackableValue, numberOfSignificantValueDigits);
        System.out.println("\n\nTiming Histogram:");
        testRawRecordingSpeedAtExpectedInterval("Histogram: ", histogram, 1000000000, rawTimingLoopCount);
    }

    @Test
    public void testSingleWriterIntervalRecordingSpeed() throws Exception {
        SingleWriterIntervalHistogramRecorder histogramRecorder;
        histogramRecorder = new SingleWriterIntervalHistogramRecorder(highestTrackableValue, numberOfSignificantValueDigits);
        System.out.println("\n\nTiming SingleWriterIntervalHistogramRecorder:");
        testRawRecordingSpeedAtExpectedInterval("SingleWriterIntervalHistogramRecorder: ", histogramRecorder, 1000000000, singleWriterIntervalTimingLoopCount);
    }

    @Test
    public void testIntervalRecordingSpeed() throws Exception {
        IntervalHistogramRecorder histogramRecorder;
        histogramRecorder = new IntervalHistogramRecorder(highestTrackableValue, numberOfSignificantValueDigits);
        System.out.println("\n\nTiming IntervalHistogramRecorder:");
        testRawRecordingSpeedAtExpectedInterval("IntervalHistogramRecorder: ", histogramRecorder, 1000000000, intervalTimingLoopCount);
    }

    @Test
    public void testRawDoubleRecordingSpeed() throws Exception {
        DoubleHistogram histogram;
        histogram = new DoubleHistogram(highestTrackableValue, numberOfSignificantValueDigits);
        System.out.println("\n\nTiming DoubleHistogram:");
        testRawDoubleRecordingSpeedAtExpectedInterval("DoubleHistogram: ", histogram, 1000000000, rawDoubleTimingLoopCount);
    }

    @Test
    public void testDoubleIntervalRecordingSpeed() throws Exception {
        IntervalDoubleHistogramRecorder histogramRecorder;
        histogramRecorder = new IntervalDoubleHistogramRecorder(highestTrackableValue, numberOfSignificantValueDigits);
        System.out.println("\n\nTiming IntervalDoubleHistogramRecorder:");
        testRawRecordingSpeedAtExpectedInterval("IntervalDoubleHistogramRecorder: ", histogramRecorder, 1000000000, intervalTimingLoopCount);
    }

    @Test
    public void testSingleWriterDoubleIntervalRecordingSpeed() throws Exception {
        SingleWriterIntervalDoubleHistogramRecorder histogramRecorder;
        histogramRecorder = new SingleWriterIntervalDoubleHistogramRecorder(highestTrackableValue, numberOfSignificantValueDigits);
        System.out.println("\n\nTiming SingleWriterIntervalDoubleHistogramRecorder:");
        testRawRecordingSpeedAtExpectedInterval("SingleWriterIntervalDoubleHistogramRecorder: ", histogramRecorder, 1000000000, singleWriterDoubleIntervalTimingLoopCount);
    }

    @Test
    public void testRawSyncronizedRecordingSpeed() throws Exception {
        AbstractHistogram histogram;
        histogram = new SynchronizedHistogram(highestTrackableValue, numberOfSignificantValueDigits);
        System.out.println("\n\nTiming SynchronizedHistogram:");
        testRawRecordingSpeedAtExpectedInterval("SynchronizedHistogram: ", histogram, 1000000000, synchronizedTimingLoopCount);
    }

    @Test
    public void testRawAtomicRecordingSpeed() throws Exception {
        AbstractHistogram histogram;
        histogram = new AtomicHistogram(highestTrackableValue, numberOfSignificantValueDigits);
        System.out.println("\n\nTiming AtomicHistogram:");
        testRawRecordingSpeedAtExpectedInterval("AtomicHistogram: ", histogram, 1000000000, atomicTimingLoopCount);
    }


    @Test
    public void testRawConcurrentRecordingSpeed() throws Exception {
        AbstractHistogram histogram;
        histogram = new ConcurrentHistogram(highestTrackableValue, numberOfSignificantValueDigits);
        System.out.println("\n\nTiming ConcurrentHistogram:");
        testRawRecordingSpeedAtExpectedInterval("AtomicHistogram: ", histogram, 1000000000, concurrentTimingLoopCount);
    }

    public void testLeadingZerosSpeed() throws Exception {
        System.out.println("\nTiming LeadingZerosSpeed :");
        long startTime = System.nanoTime();
        LeadingZerosSpeedLoop(warmupLoopLength);
        long endTime = System.nanoTime();
        long deltaUsec = (endTime - startTime) / 1000L;
        long rate = 1000000 * warmupLoopLength / deltaUsec;
        System.out.println("Warmup:\n" + warmupLoopLength + " Leading Zero loops completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
        // Wait a bit to make sure compiler had a cache to do it's stuff:
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        startTime = System.nanoTime();
        LeadingZerosSpeedLoop(rawTimingLoopCount);
        endTime = System.nanoTime();
        deltaUsec = (endTime - startTime) / 1000L;
        rate = 1000000 * rawTimingLoopCount / deltaUsec;
        System.out.println("Hot code timing:");
        System.out.println(rawTimingLoopCount + " Leading Zero loops completed in " +
                deltaUsec + " usec, rate = " + rate + " value recording calls per sec.");
    }

    public static void main(String[] args) {
        try {
            HistogramPerfTest test = new HistogramPerfTest();
            test.testLeadingZerosSpeed();
            Thread.sleep(1000000);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

}
