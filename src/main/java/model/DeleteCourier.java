package model;

public class DeleteCourier {
    private String id;

    public DeleteCourier() {
    }

    public DeleteCourier(int num) {
        StringBuilder sb = new StringBuilder();
        sb.append(num);
        String id = sb.toString();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
