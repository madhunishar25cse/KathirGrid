import java.util.*;

/**
 * KathirGrid Java OOP allocation engine (academic companion source).
 * The browser website is served by the Node.js/MongoDB app in this ZIP.
 * This file demonstrates the same priority allocation rules in Java.
 * Compile: javac KathirGrid.java
 * Run:     java KathirGrid
 */
public class KathirGrid {
    enum Priority { CRITICAL, ESSENTIAL, DEFERRABLE }
    enum AllocationMode { DIVISIBLE, INDIVISIBLE }

    static abstract class ConsumerLoad {
        private final String id, name;
        private final double demand, minimumService;
        private final Priority priority;
        private final AllocationMode mode;
        ConsumerLoad(String id, String name, double demand, double minimumService,
                     Priority priority, AllocationMode mode) {
            if (demand < 0 || minimumService < 0 || minimumService > demand)
                throw new IllegalArgumentException("Invalid demand/minimum service for " + name);
            this.id=id; this.name=name; this.demand=demand;
            this.minimumService=minimumService; this.priority=priority; this.mode=mode;
        }
        public String getId(){ return id; }
        public String getName(){ return name; }
        public double getDemand(){ return demand; }
        public double getMinimumService(){ return minimumService; }
        public Priority getPriority(){ return priority; }
        public AllocationMode getMode(){ return mode; }
        abstract boolean accepts(double available);
    }
    static final class DivisibleLoad extends ConsumerLoad {
        DivisibleLoad(String id,String name,double demand,double min,Priority p){
            super(id,name,demand,min,p,AllocationMode.DIVISIBLE);
        }
        @Override boolean accepts(double available){ return available >= getMinimumService(); }
    }
    static final class IndivisibleLoad extends ConsumerLoad {
        IndivisibleLoad(String id,String name,double demand,double min,Priority p){
            super(id,name,demand,min,p,AllocationMode.INDIVISIBLE);
        }
        @Override boolean accepts(double available){ return available >= getDemand(); }
    }
    static final class AllocationRecord {
        final ConsumerLoad consumer; final double allocated, unmet;
        AllocationRecord(ConsumerLoad c,double a){consumer=c;allocated=a;unmet=c.getDemand()-a;}
    }
    static final class IntervalAllocation {
        final int interval; final double generation, allocated, unmet, curtailed;
        final List<AllocationRecord> records;
        IntervalAllocation(int i,double g,double a,double u,double c,List<AllocationRecord> r){
            interval=i;generation=g;allocated=a;unmet=u;curtailed=c;records=r;
        }
    }
    static final class Microgrid {
        private final List<ConsumerLoad> loads = new ArrayList<>();
        private final PriorityQueue<ConsumerLoad> queue = new PriorityQueue<>(
            Comparator.comparing(ConsumerLoad::getPriority)
                      .thenComparing(ConsumerLoad::getId));
        void addLoad(ConsumerLoad load){ loads.add(load); }
        IntervalAllocation allocate(int interval,double generation){
            if(generation<0) throw new IllegalArgumentException("Generation cannot be negative");
            queue.clear(); queue.addAll(loads);
            double remaining=generation, totalAllocated=0, totalUnmet=0;
            List<AllocationRecord> records=new ArrayList<>();
            while(!queue.isEmpty()){
                ConsumerLoad c=queue.poll(); double give=0;
                if(c.accepts(remaining)){
                    give = c instanceof IndivisibleLoad ? c.getDemand()
                          : Math.min(c.getDemand(),remaining);
                }
                remaining=Math.max(0,remaining-give);
                totalAllocated+=give; totalUnmet+=c.getDemand()-give;
                records.add(new AllocationRecord(c,give));
            }
            return new IntervalAllocation(interval,generation,totalAllocated,totalUnmet,remaining,records);
        }
    }
    public static void main(String[] args){
        Microgrid grid=new Microgrid();
        grid.addLoad(new IndivisibleLoad("C-001","Village Clinic",12,12,Priority.CRITICAL));
        grid.addLoad(new IndivisibleLoad("C-002","Water Pumps",18,18,Priority.CRITICAL));
        grid.addLoad(new DivisibleLoad("C-003","Households North",22,5,Priority.ESSENTIAL));
        grid.addLoad(new DivisibleLoad("C-004","Street Lighting",7,2,Priority.DEFERRABLE));
        double[] hourlyGeneration={8,20,35,15};
        System.out.println("KATHIRGRID — JAVA PRIORITY ALLOCATION DEMO");
        for(int i=0;i<hourlyGeneration.length;i++){
            IntervalAllocation a=grid.allocate(i+1,hourlyGeneration[i]);
            System.out.printf(Locale.US,"\nHour %02d | Generation %.2f | Allocated %.2f | Unmet %.2f | Curtailed %.2f kWh%n",
                a.interval,a.generation,a.allocated,a.unmet,a.curtailed);
            for(AllocationRecord r:a.records)
                System.out.printf(Locale.US,"  %-20s %-10s demand=%5.2f allocated=%5.2f unmet=%5.2f%n",
                    r.consumer.getName(),r.consumer.getPriority(),r.consumer.getDemand(),r.allocated,r.unmet);
        }
    }
}
