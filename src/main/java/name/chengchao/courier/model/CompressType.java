package name.chengchao.courier.model;

public enum CompressType {

    none(1),
    snappy(2);

    private int value;

    private CompressType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CompressType valueOfInt(int v) {
        switch (v) {
            case 1:
                return none;
            case 2:
                return snappy;

            default:
                return none;
        }
    }

}
