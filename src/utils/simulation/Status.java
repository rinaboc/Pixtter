package utils.simulation;

import utils.exceptions.EntryNotFound;

import java.util.HashMap;

public class Status {

    /**
     * Status entry to store values. Can check for limits when changing values.
     */
    public static class Entry {
        private double value;

        private double bottomLimit;
        private boolean bottomLimitSet = false;

        private double topLimit;
        private boolean topLimitSet = false;

        public Entry(double initVal){
            this.value = initVal;
        }

        //
        // limit setting
        //
        public Entry(double initVal, double bottomLimit, double topLimit){
            this.value = initVal;
            this.bottomLimit = bottomLimit; this.bottomLimitSet = true;
            this.topLimit = topLimit; this.topLimitSet = true;
            checkLimits();
        }
        public void setBottomLimit(double bottom) {
            bottomLimit = bottom; bottomLimitSet = true;
        }
        public void setTopLimit(double top) {
            topLimit = top; topLimitSet = true;
        }
        public void releaseLimits(){
            bottomLimitSet = false; topLimitSet = false;
        }

        //
        // manipulate value
        //
        public synchronized void addValue(double value) {
            this.value += value;
            checkLimits();
        }

        public synchronized void multiplyValue(double value) {
            this.value *= value;
            checkLimits();
        }

        /**
         * Checks if the value is between the set limits if they are enabled.
         */
        private void checkLimits() {
            if (bottomLimitSet && this.value < bottomLimit) {
                this.value = bottomLimit;
                return;
            }

            if (topLimitSet && this.value > topLimit) this.value = topLimit;
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
