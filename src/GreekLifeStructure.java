import java.util.Arrays;
import java.util.List;

public class GreekLifeStructure {

    public final int x;
    public final int y;

    public final int width;
    public final int height;

    public final int gender;

    public final String displayName;

    public final List<String> names;

    boolean isCorrect = false;
    boolean isActive = false;
    boolean hasAnotherAttempt = true;

    GreekLifeStructure(String databaseRow) {
        List<String> list = Arrays.asList(databaseRow.split(","));
        x = Integer.parseInt(list.get(0));
        y = Integer.parseInt(list.get(1));
        width = Integer.parseInt(list.get(2));
        height = Integer.parseInt(list.get(3));
        gender = Integer.parseInt(list.get(4));
        displayName = list.get(6);
        System.out.println("found " + displayName);
        names = list.subList(5, list.size());
    }
}
