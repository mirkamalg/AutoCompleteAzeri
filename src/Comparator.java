public class Comparator implements java.util.Comparator<String> {

    public Comparator() {
        super();
    }

    public int compare(String s1, String s2) {
        int dist1 = s1.length();
        int dist2 = s2.length();

        return dist1 - dist2;
    }
}
