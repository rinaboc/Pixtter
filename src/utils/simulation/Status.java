package utils.simulation;

import utils.exceptions.EntryNotFound;

import java.util.HashMap;

public class Status {

    /**
     * Status entry to store values. Can check for limits when changing values.
     */
    public static class Entry {
        private double value;
        private final double[] limits = new double[2];

        public Entry(double initVal){
            this.value = initVal;
        }

        public void setBottomLimit(double bottom) {
            limits[0] = bottom;
        }

        public void setTopLimit(double top) {
            limits[1] = top;
        }

        public synchronized void addValue(double value) {
            this.value += value;
            checkLimits();
        }

        public synchronized void multiplyValue(double value) {
            this.value *= value;
            checkLimits();
        }

        private void checkLimits() {
            if (this.value < limits[0]) {
                this.value = limits[0];
                return;
            }

            if (this.value > limits[1]) this.value = limits[1];
        }

        public double getValue(){
            return value;
        }
    }

    private final HashMap<String, Entry> entries = new HashMap<>();

    /**
     * Creates a new status entry.
     * @param name of the entry
     * @param initVal initial value
     */
    public void newEntry(String name, Entry initVal) {
        if(entries.containsKey(name)){
            System.out.println("There's already a status with the same name.");
            return;
        }

        entries.put(name, initVal);
    }

    public Entry getEntry(String name) throws EntryNotFound {
        if (!entries.containsKey(name)) {
            System.out.println("Cannot find entry.");
            throw new EntryNotFound();
        }

        return entries.get(name);
    }

}
