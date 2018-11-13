package name.chengchao.courier.model;

public enum BodyType {

    json(1);

    private int value;

    private BodyType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BodyType valueOfInt(int v) {
        switch (v) {
            case 1:
                return json;

            default:
                return json;
        }
    }

}
