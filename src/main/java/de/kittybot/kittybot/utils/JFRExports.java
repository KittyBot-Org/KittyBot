package de.kittybot.kittybot.utils;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

import java.util.concurrent.atomic.AtomicBoolean;

public class JFRExports {

	// ty Natan 👀 https://github.com/Mantaro/MantaroBot/blob/master/src/main/java/net/kodehawa/mantarobot/utils/exporters/JFRExports.java
	private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);
	private static final double NANOSECONDS_PER_SECOND = 1E9;
	//jdk.SafepointBegin, jdk.SafepointStateSynchronization, jdk.SafepointEnd
	private static final Histogram SAFEPOINTS = Histogram.build()
			.name("jvm_safepoint_pauses_seconds")
			.help("Safepoint pauses by buckets")
			.labelNames("type") // ttsp, operation
			.buckets(0.005, 0.010, 0.025, 0.050, 0.100, 0.200, 0.400, 0.800, 1.600, 3, 5, 10)
			.create();

	private static final Histogram.Child SAFEPOINTS_TTSP = SAFEPOINTS.labels("ttsp");
	private static final Histogram.Child SAFEPOINTS_OPERATION = SAFEPOINTS.labels("operation");
	//jdk.GarbageCollection
	private static final Histogram GC_PAUSES = Histogram.build()
			.name("jvm_gc_pauses_seconds")
			.help("Longest garbage collection pause per collection")
			.labelNames("name", "cause")
			.buckets(0.005, 0.010, 0.025, 0.050, 0.100, 0.200, 0.400, 0.800, 1.600, 3, 5, 10)
			.create();

	//jdk.GarbageCollection
	private static final Histogram GC_PAUSES_SUM = Histogram.build()
			.name("jvm_gc_sum_of_pauses_seconds")
			.help("Sum of garbage collection pauses per collection")
			.labelNames("name", "cause")
			.buckets(0.005, 0.010, 0.025, 0.050, 0.100, 0.200, 0.400, 0.800, 1.600, 3, 5, 10)
			.create();

	//jdk.GCReferenceStatistics
	private static final Counter REFERENCE_STATISTICS = Counter.build()
			.name("jvm_reference_statistics")
			.help("Number of java.lang.ref references by type")
			.labelNames("type")
			.create();

	//jdk.ExecuteVMOperation
	private static final Counter VM_OPERATIONS = Counter.build()
			.name("jvm_vm_operations")
			.help("Executed VM operations")
			.labelNames("operation", "safepoint")
			.create();

	//jdk.NetworkUtilization
	private static final Gauge NETWORK_READ = Gauge.build()
			.name("jvm_network_read")
			.help("Bits read from the network per second")
			.labelNames("interface")
			.create();

	//jdk.NetworkUtilization
	private static final Gauge NETWORK_WRITE = Gauge.build()
			.name("jvm_network_write")
			.help("Bits written to the network per second")
			.labelNames("interface")
			.create();

	//jdk.JavaThreadStatistics
	private static final Gauge THREADS_CURRENT = Gauge.build()
			.name("jvm_threads_current")
			.help("Current thread count of the JVM")
			.create();

	//jdk.JavaThreadStatistics
	private static final Gauge THREADS_DAEMON = Gauge.build()
			.name("jvm_threads_daemon")
			.help("Daemon thread count of the JVM")
			.create();

	//jdk.CPULoad
	private static final Gauge CPU_USER = Gauge.build()
			.name("jvm_cpu_user")
			.help("User CPU usage of the JVM")
			.create();

	//jdk.CPULoad
	private static final Gauge CPU_SYSTEM = Gauge.build()
			.name("jvm_cpu_system")
			.help("System CPU usage of the JVM")
			.create();

	//jdk.CPULoad
	private static final Gauge CPU_MACHINE = Gauge.build()
			.name("jvm_cpu_machine")
			.help("CPU usage of the machine the JVM is running on")
			.create();

	//jdk.GCHeapSummary, jdk.MetaspaceSummary
	private static final Gauge MEMORY_USAGE = Gauge.build()
			// remove _jfr suffix if we remove the standard exports
			.name("jvm_memory_bytes_used_jfr")
			.help("Bytes of memory used by the JVM")
			.labelNames("area") //heap, nonheap
			.create();


	public static void register(){
		if(!REGISTERED.compareAndSet(false, true)){
			return;
		}

		SAFEPOINTS.register();
		GC_PAUSES.register();
		GC_PAUSES_SUM.register();
		REFERENCE_STATISTICS.register();
		VM_OPERATIONS.register();
		NETWORK_READ.register();
		NETWORK_WRITE.register();
		THREADS_CURRENT.register();
		THREADS_DAEMON.register();
		CPU_USER.register();
		CPU_SYSTEM.register();
		CPU_MACHINE.register();
		MEMORY_USAGE.register();
	}

}
