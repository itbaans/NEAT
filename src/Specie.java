import java.util.ArrayList;
import java.util.List;

public class Specie {

    List<DNA> list = new ArrayList<>();
    DNA representative;

    public void setReprentative() {
        representative = list.get(0);
    }


}